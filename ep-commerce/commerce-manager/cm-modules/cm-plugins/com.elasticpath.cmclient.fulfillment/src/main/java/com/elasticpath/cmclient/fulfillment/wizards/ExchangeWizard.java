/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.InsufficientInventoryException;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;
import com.elasticpath.service.order.IncorrectRefundAmountException;
import com.elasticpath.service.order.OrderReturnOutOfDateException;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.order.ReturnExchangeType;

/**
 * The wizard for creating and editing Exchange.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class ExchangeWizard extends AbstractEpWizard<OrderReturn> {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(ExchangeWizard.class);
	private static final String ERROR_OCCURRED_WHILE_PROCESSING_AN_EXCHANGE = "Error occurred while processing an exchange."; //$NON-NLS-1$

	/**
	 * ExchangeWizardType defines type of the wizard for Exchange.
	 */
	enum ExchangeWizardType {
		/** Create exchange wizard. */
		CREATE_EXCHANGE,
		/** Complete exchange wizard. */
		COMPLETE_EXCHANGE
	}

	private static final String PAGE_SUBJECT_RETURN = "PageSubjectReturn"; //$NON-NLS-1$

	private static final String PAGE_ORDER_EXCHANGE = "PageOrderExchange"; //$NON-NLS-1$

	private static final String PAGE_PAYMENT_EXCHANGE = "PagePaymentExchange"; //$NON-NLS-1$

	private static final String PAGE_SUMMARY_EXCHANGE = "PageSummaryExchange"; //$NON-NLS-1$

	private OrderReturn orderReturn;

	private final ReturnSubjectPage returnSubjectPage;

	private final ExchangeOrderItemsPage exchangeOrderItemsPage;

	private final ExchangePaymentPage paymentPage;

	private final ExchangeSummaryPage exchangeSummaryPage;

	private static ReturnAndExchangeService orderReturnService = ServiceLocator.getService(ContextIdNames.ORDER_RETURN_SERVICE);

	private final ExchangeWizardType exchangeWizardType;

	/**
	 * Returns wizard type for exchange order.
	 *
	 * @return exchange order wizard type.
	 */
	public ExchangeWizardType getExchangeWizardType() {
		return exchangeWizardType;
	}

	/**
	 * Creates instance of exchange wizard. (Factory Method)
	 *
	 * @param orderShipment the order shipment
	 * @return ExchangeWizard
	 */
	public static ExchangeWizard createExchangeWizard(final OrderShipment orderShipment) {

		OrderReturn orderReturn = orderReturnService.getOrderReturnPrototype(orderShipment, OrderReturnType.EXCHANGE);

		return new ExchangeWizard(FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_EXCHANGE_CREATE),
				FulfillmentMessages.get().ExchangeWizard_Create_Title, orderReturn,
				ExchangeWizardType.CREATE_EXCHANGE, orderShipment.isInclusiveTax());
	}

	/**
	 * Creates instance of complete exchange wizard. (Factory Method)
	 *
	 * @param orderReturn order return
	 * @return ExchangeWizard
	 */
	public static ExchangeWizard completeExchangeWizard(final OrderReturn orderReturn) {
		return new ExchangeWizard(FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_EXCHANGE_COMPLETE),
				FulfillmentMessages.get().ExchangeWizard_Complete_Title, orderReturn, ExchangeWizardType.COMPLETE_EXCHANGE,
				orderReturn.isInclusiveTax());
	}

	private ExchangeWizard(final Image wizardImage, final String windowTitle, final OrderReturn orderReturn,
			final ExchangeWizardType exchangeWizardType, final boolean isInclusiveTax) {
		super(windowTitle, windowTitle + FulfillmentMessages.SPACE + FulfillmentMessages.get().ReturnWizard_Step_Info, wizardImage);
		this.orderReturn = orderReturn;
		this.exchangeWizardType = exchangeWizardType;
		switch (exchangeWizardType) {
		case CREATE_EXCHANGE:
			returnSubjectPage = new ReturnSubjectPage(PAGE_SUBJECT_RETURN, FulfillmentMessages.get().ExchangeWizard_ExchangeItemsExchangePage_Message,
					FulfillmentMessages.get().ExchangeWizard_ItemsToBeExchanged_Section, getModel(), isInclusiveTax);
			exchangeOrderItemsPage = new ExchangeOrderItemsPage(PAGE_ORDER_EXCHANGE,
					FulfillmentMessages.get().ExchangeWizard_OrderItemsExchangePage_Message);
			paymentPage = new ExchangePaymentPage(PAGE_PAYMENT_EXCHANGE, FulfillmentMessages.get().ExchangeWizard_PaymentPage_Message);
			exchangeSummaryPage = new ExchangeSummaryPage(PAGE_SUMMARY_EXCHANGE, FulfillmentMessages.get().ExchangeWizard_SummaryPage_Message);
			break;
		case COMPLETE_EXCHANGE:
			returnSubjectPage = null;
			exchangeOrderItemsPage = null;
			paymentPage = new ExchangePaymentPage(PAGE_PAYMENT_EXCHANGE, FulfillmentMessages.get().ExchangeWizard_PaymentPage_Message);
			exchangeSummaryPage = new ExchangeSummaryPage(PAGE_SUMMARY_EXCHANGE, FulfillmentMessages.get().ExchangeWizard_SummaryPage_Message);
			break;
		default:
			returnSubjectPage = null;
			exchangeOrderItemsPage = null;
			paymentPage = null;
			exchangeSummaryPage = null;
			break;
		}

	}

	/**
	 * Completes exchange.
	 * @return true if the operation terminated correctly
	 */
	public boolean completeExchnage() {
		try {
			orderReturn.getOrder().setModifiedBy(getEventOriginator());
			OrderPayment authPayment = ((ExchangePaymentPage) getPage(PAGE_PAYMENT_EXCHANGE)).getPayment();
			ReturnExchangeType type = ((ExchangePaymentPage) getPage(PAGE_PAYMENT_EXCHANGE)).getSelectionResult();
			orderReturn = orderReturnService.completeExchange(orderReturn, type, authPayment);

			// update model by reloading order return, which reloads exchange order payments
			// a new exchange order payment will have been added after exchange completion
			orderReturn = retrieveOrderReturn(orderReturn.getUidPk());

		} catch (OrderReturnOutOfDateException e) {
			LOG.error(ERROR_OCCURRED_WHILE_PROCESSING_AN_EXCHANGE, e); //$NON-NLS-1$
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					FulfillmentMessages.get().OrderReturn_ErrDlgCollisionTitle, FulfillmentMessages.get().OrderReturn_ErrDlgCollisionMessage);
			return false;
		} catch (PaymentProcessingException paymentProcessingException) {
			LOG.error(ERROR_OCCURRED_WHILE_PROCESSING_AN_EXCHANGE, paymentProcessingException); //$NON-NLS-1$
			MessageDialog.openError(getShell(), FulfillmentMessages.get().ExchangeWizard_CardError_Title,
					FulfillmentMessages.get().getCreditCardErrorMessage(paymentProcessingException));
			return false;
		} catch (InsufficientInventoryException insufficientInventoryException) {
			LOG.error(ERROR_OCCURRED_WHILE_PROCESSING_AN_EXCHANGE, insufficientInventoryException); //$NON-NLS-1$
			MessageDialog.openError(getShell(), FulfillmentMessages.get().ExchangeWizard_InsufficientInventory_Title,
					FulfillmentMessages.get().ExchangeWizard_InsufficientInventory_Message);
			return false;
		} catch (PaymentGatewayException gatewayException) {
			LOG.error(ERROR_OCCURRED_WHILE_PROCESSING_AN_EXCHANGE, gatewayException); //$NON-NLS-1$
			MessageDialog.openError(getShell(), FulfillmentMessages.get().RefundWizard_PaymentProceedError_Title,

					NLS.bind(FulfillmentMessages.get().RefundWizard_PaymentProceedError_Message,
					gatewayException.getLocalizedMessage()));
			return false;
		} catch (IncorrectRefundAmountException amountException) {
			LOG.error(ERROR_OCCURRED_WHILE_PROCESSING_AN_EXCHANGE, amountException); //$NON-NLS-1$
			MessageDialog.openError(getShell(), FulfillmentMessages.get().RefundWizard_IncorrectRefundAmount_Title,
					FulfillmentMessages.get().RefundWizard_IncorrectRefundAmount_Message);
			return false;
		}
		return true;
	}

	/**
	 * Retrieves the order return by uid.
	 *
	 * @param orderReturnUid the UID of the order return to be retrieved.
	 * @return the <code>Order</code>
	 */
	private OrderReturn retrieveOrderReturn(final long orderReturnUid) {
		FetchGroupLoadTuner tuner = ServiceLocator.getService(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		tuner.addFetchGroup(FetchGroupConstants.ORDER_INDEX,
			FetchGroupConstants.ORDER_NOTES,
			FetchGroupConstants.ALL);

		return orderReturnService.get(orderReturnUid, tuner);
	}

	/**
	 * Creates exchange.
	 *
	 * @return true if the operation terminated correctly
	 */
	public boolean createExchange() {
		LOG.debug("proceed method called"); //$NON-NLS-1$
		try {
			orderReturn.getOrder().setModifiedBy(getEventOriginator());
			OrderPayment authPayment = ((ExchangePaymentPage) getPage(PAGE_PAYMENT_EXCHANGE)).getPayment();
			ReturnExchangeType type = ((ExchangePaymentPage) getPage(PAGE_PAYMENT_EXCHANGE)).getSelectionResult();
			orderReturn.setCreatedByCmUser(LoginManager.getCmUser());
			orderReturn = orderReturnService.createExchange(orderReturn, type, authPayment);
		} catch (PaymentProcessingException paymentProcessingException) {
			LOG.error(ERROR_OCCURRED_WHILE_PROCESSING_AN_EXCHANGE, paymentProcessingException); //$NON-NLS-1$
			MessageDialog.openError(getShell(), FulfillmentMessages.get().ExchangeWizard_CardError_Title,
					FulfillmentMessages.get().getCreditCardErrorMessage(paymentProcessingException));
			return false;
		} catch (InsufficientInventoryException insufficientInventoryException) {
			LOG.error(ERROR_OCCURRED_WHILE_PROCESSING_AN_EXCHANGE, insufficientInventoryException); //$NON-NLS-1$
			MessageDialog.openError(getShell(), FulfillmentMessages.get().ExchangeWizard_InsufficientInventory_Title,
					FulfillmentMessages.get().ExchangeWizard_InsufficientInventory_Message);
			return false;
		} catch (PaymentGatewayException gatewayException) {
			LOG.error(ERROR_OCCURRED_WHILE_PROCESSING_AN_EXCHANGE, gatewayException); //$NON-NLS-1$
			MessageDialog.openError(getShell(), FulfillmentMessages.get().RefundWizard_PaymentProceedError_Title,

					NLS.bind(FulfillmentMessages.get().RefundWizard_PaymentProceedError_Message,
					gatewayException.getLocalizedMessage()));
			return false;
		} catch (IncorrectRefundAmountException amountException) {
			LOG.error(ERROR_OCCURRED_WHILE_PROCESSING_AN_EXCHANGE, amountException); //$NON-NLS-1$
			MessageDialog.openError(getShell(), FulfillmentMessages.get().RefundWizard_IncorrectRefundAmount_Title,
					FulfillmentMessages.get().RefundWizard_IncorrectRefundAmount_Message);
			return false;
		}

		return true;
	}

	@Override
	public void addPages() {
		addWizardPage(returnSubjectPage);
		addWizardPage(exchangeOrderItemsPage);
		addWizardPage(paymentPage);
		addWizardPage(exchangeSummaryPage);
	}

	private void addWizardPage(final IWizardPage page) {
		if (page != null) {
			super.addPage(page);
		}
	}

	@Override
	public OrderReturn getModel() {
		return this.orderReturn;
	}
	
	@Override
	public void onUpdateButtons(final EpWizardDialog dialog) {
		if (dialog.getCurrentPage() == exchangeSummaryPage) {
			dialog.getWizardButton(IDialogConstants.BACK_ID).setEnabled(false);
			dialog.getWizardButton(IDialogConstants.CANCEL_ID).setEnabled(false);
		}
	}
	
	private EventOriginator getEventOriginator() {
		EventOriginatorHelper helper = ServiceLocator.getService(
				ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(LoginManager.getCmUser());
	}
}
