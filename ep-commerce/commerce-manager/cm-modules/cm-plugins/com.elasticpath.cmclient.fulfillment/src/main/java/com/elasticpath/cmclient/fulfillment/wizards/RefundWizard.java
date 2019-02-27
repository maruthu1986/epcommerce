/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEventCmHelper;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.service.order.IncorrectRefundAmountException;
import com.elasticpath.service.order.OrderService;

/**
 * Create refund wizard.
 */
public final class RefundWizard extends AbstractEpWizard<Object> {

	private static final String PAGE_CARD_INFO_REFUND = "PageCardInfoRefund"; //$NON-NLS-1$

	private static final String PAGE_SUMMARY_REFUND = "PageSummaryRefund"; //$NON-NLS-1$

	private static final String ERROR_OCCURRED_WHILE_PROCESSING_A_REFUND = "Error occurred while processing a refund.";

	private final Order order;

	private OrderPayment processedPayment;

	private final RefundCardInfoPage cardInfoPage;

	private final RefundSummaryPage summaryPage;

	private static final Logger LOG = Logger.getLogger(RefundWizard.class);

	/**
	 * The constructor.
	 *
	 * @param order order for which the refund should be done.
	 */
	public RefundWizard(final Order order) {
		super(FulfillmentMessages.get().RefundWizard_Title, FulfillmentMessages.get().RefundWizard_Page_Title, FulfillmentImageRegistry
				.getImage(FulfillmentImageRegistry.IMAGE_REFUND_CREATE));
		this.order = order;
		cardInfoPage = new RefundCardInfoPage(PAGE_CARD_INFO_REFUND);
		summaryPage = new RefundSummaryPage(PAGE_SUMMARY_REFUND);
	}

	@Override
	public void addPages() {
		addPage(cardInfoPage);
		addPage(summaryPage);
	}

	@Override
	public void onUpdateButtons(final EpWizardDialog dialog) {
		if (dialog.getSelectedPage() == summaryPage) {
			dialog.getWizardButton(IDialogConstants.BACK_ID).setEnabled(false);
			dialog.getWizardButton(IDialogConstants.CANCEL_ID).setEnabled(false);
		}
	}

	/**
	 * Get order payment that was already processed.
	 *
	 * @return order payment
	 */
	public OrderPayment getProcessedPayment() {
		return processedPayment;
	}

	/**
	 * Set order payment that was already processed.
	 *
	 * @param processedPayment the processed order payment
	 */
	public void setProcessedPayment(final OrderPayment processedPayment) {
		this.processedPayment = processedPayment;
	}

	/**
	 * Process refund.
	 *
	 * @param selectedPayment if null - original payment source will be used.
	 * @param refundNote refund note.
	 * @return if refund was successful.
	 */
	public boolean process(final OrderPayment selectedPayment, final String refundNote) {

		final BigDecimal amount = selectedPayment.getAmount();
		final OrderPayment payment = ServiceLocator.getService(ContextIdNames.ORDER_PAYMENT);

		payment.copyTransactionFollowOnInfo(selectedPayment);
		payment.setRequestToken(selectedPayment.getRequestToken());
		payment.setEmail(getModel().getCustomer().getEmail());

		final OrderService orderService = ServiceLocator.getService(ContextIdNames.ORDER_SERVICE);
		try {
			// refund to the payment provided by the wizard.
			// it could be either the selected payment by user or the new payment populated with the credit card information
			OrderShipment orderShipment = selectedPayment.getOrderShipment();
			String shipmentNumber = null;
			if (orderShipment != null) {
				shipmentNumber = orderShipment.getShipmentNumber();
			}

			Order returnOrder = orderService.refundOrderPayment(getModel().getUidPk(),
																shipmentNumber,
																payment,
																amount,
																getEventOriginator());

			// Logs refund note if necessary
			if (refundNote != null && refundNote.length() > 0) {
				OrderEventCmHelper.initForOrderAuditing(returnOrder);
				OrderEventCmHelper.getOrderEventHelper().logOrderNote(returnOrder, refundNote);
				returnOrder = orderService.update(returnOrder);
			}

			/* Save processed payment to wizard. getWizard().getProcessedPayment() will be used on the summary page. */
			if (!returnOrder.getOrderPayments().isEmpty()) {
				//Find the refunded OrderPayment and set the local reference to it
				//FIXME: It's possible, though very unlikely, that this will return an OrderPayment different than the one
				//that was just refunded. When refactoring this area a better method may be found.
				OrderPayment returnedPaymentWithLatestDate = null;
				for (OrderPayment returnedPayment : returnOrder.getOrderPayments()) {
					if (returnedPaymentWithLatestDate == null) {
						returnedPaymentWithLatestDate = returnedPayment;
					}
					if (returnedPayment.getCreatedDate().after(returnedPaymentWithLatestDate.getCreatedDate())) {
						returnedPaymentWithLatestDate = returnedPayment;
					}
				}
				setProcessedPayment(returnedPaymentWithLatestDate);
			}
		} catch (final PaymentGatewayException gatewayException) {
			LOG.error(ERROR_OCCURRED_WHILE_PROCESSING_A_REFUND, gatewayException); //$NON-NLS-1$
			MessageDialog.openError(getShell(), FulfillmentMessages.get().RefundWizard_PaymentProceedError_Title,

					NLS.bind(FulfillmentMessages.get().RefundWizard_PaymentProceedError_Message,
					gatewayException.getLocalizedMessage()));
			return false;
		} catch (final IncorrectRefundAmountException amountException) {
			LOG.error(ERROR_OCCURRED_WHILE_PROCESSING_A_REFUND, amountException); //$NON-NLS-1$
			MessageDialog.openError(getShell(), FulfillmentMessages.get().RefundWizard_IncorrectRefundAmount_Title,
					FulfillmentMessages.get().RefundWizard_IncorrectRefundAmount_Message);
			return false;
		}
		return true;
	}

	@Override
	protected Order getModel() {
		return order;
	}
	
	private EventOriginator getEventOriginator() {
		EventOriginatorHelper helper = ServiceLocator.getService(
				ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(LoginManager.getCmUser());
	}

}