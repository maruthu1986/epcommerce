/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;

/**
 * Reauthorization wizard.
 */
public class ReAuthWizard extends AbstractEpWizard<Order> {

	private static final String PAGE_CARD_INFO_REAUTH = "PageCardInfoReauth"; //$NON-NLS-1$

	private static final String PAGE_SUMMARY_REAUTH = "PageSummaryReauth"; //$NON-NLS-1$

	private final ReAuthInitPage initPage;

	private final ReAuthSummaryPage summaryPage;

	private final Order order;

	private final List<ReAuthorizationItem> reAuthorizationList;

	private boolean allReAuthsSuccessful;

	/**
	 * The constructor.
	 *
	 * @param order the order for which reauthorization is required.
	 * @param reAuthorizationList list of shipments for for which reauthorization is required.
	 */
	public ReAuthWizard(final Order order, final List<ReAuthorizationItem> reAuthorizationList) {
		super(FulfillmentMessages.get().CaptureWizard_Title, FulfillmentMessages.get().CaptureWizard_Page_Title, null);
		this.order = order;
		this.reAuthorizationList = reAuthorizationList;
		final DataBindingContext dataBindingContext = new DataBindingContext();
		initPage = new ReAuthInitPage(PAGE_CARD_INFO_REAUTH, dataBindingContext, order);
		summaryPage = new ReAuthSummaryPage(PAGE_SUMMARY_REAUTH, dataBindingContext);
	}

	/**
	 * Invokes reauthorization wizard if required for the specified order.
	 *
	 * @param order order for whic reauthorization is requested.
	 * @return if reauthorization was successful on is not required.
	 */
	public static boolean reAuthIfRequired(final Order order) {
		/** ReAuthWizard(final Order order, final Map<OrderShipment, BigDecimal> reauthShipments) */
		final List<ReAuthorizationItem> reAuthorizationList = new ArrayList<>();
		final PaymentService paymentService = ServiceLocator.getService(ContextIdNames.PAYMENT_SERVICE);
		for (final OrderShipment shipment : order.getAllShipments()) {
			BigDecimal additionalAmount = shipment.getTotal();
			OrderPayment originalPayment = paymentService.getLastAuthorizationPayments(shipment);

			if (originalPayment != null) {
				additionalAmount = paymentService.getAdditionalAuthAmount(shipment);
			}

			if (additionalAmount.compareTo(BigDecimal.ZERO) > 0) {
				final ReAuthorizationItem reAuthorizationItem = new ReAuthorizationItem();
				reAuthorizationItem.setShipment(shipment);
				reAuthorizationItem.setOldPayment(originalPayment);
				reAuthorizationList.add(reAuthorizationItem);
			}
		}

		boolean reAuthSuccessfulOrNotRequired = true;
		if (!reAuthorizationList.isEmpty()) {
			final ReAuthWizard reAuthWizard = new ReAuthWizard(order, reAuthorizationList);
			final EpWizardDialog dialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), reAuthWizard);
			dialog.addPageChangingListener(reAuthWizard);
			reAuthSuccessfulOrNotRequired = (dialog.open() == Window.OK);
		}
		return reAuthSuccessfulOrNotRequired;

	}

	@Override
	public void addPages() {
		addPage(initPage);
		addPage(summaryPage);
	}

	@Override
	public boolean performCancel() {
		if (getContainer().getCurrentPage() == initPage || !isAllReAuthsSuccessful()) {
			MessageDialog.openWarning(getShell(), FulfillmentMessages.get().CaptureWizard_Cancel_Title,
					FulfillmentMessages.get().CaptureWizard_Cancel_Message);
			return true;
		}
		return false;
	}

	/**
	 * Execute reauthorization for each shipment from getReauthList().
	 *
	 * @return if there was no gateway errors. Other exceptions are stored in appropriate ReAuthTuple's.
	 */
	public boolean process() {
		allReAuthsSuccessful = false; // burn bridges
		boolean newReAuthStatus = true; // we hope that everything will be fine
		final PaymentService paymentService = ServiceLocator.getService(ContextIdNames.PAYMENT_SERVICE);

		for (final ReAuthorizationItem reAuthorizationItem : getReAuthorizationList()) {
			boolean currentReAuthSuccessful = false; // burn bridges

			try {
				PaymentResult paymentResult = getPaymentResult(paymentService, reAuthorizationItem);
				reinitializeShipments(reAuthorizationItem, paymentResult.getProcessedPayments());

				if (paymentResult.getResultCode() != PaymentResult.CODE_OK && paymentResult.getCause() != null) {
					throw paymentResult.getCause();
				}

				currentReAuthSuccessful = true;
			} catch (final PaymentGatewayException gatewayException) {
				MessageDialog.openError(getShell(), FulfillmentMessages.get().ReAuthWizard_GatewayError_Title,

						NLS.bind(FulfillmentMessages.get().ReAuthWizard_GatewayError_Text,
						gatewayException.getLocalizedMessage()));
				return false;
			} catch (final PaymentProcessingException error) {
				reAuthorizationItem.setError(error);
			}

			newReAuthStatus &= currentReAuthSuccessful; // newReAuthStatus should be true only is all reauths were successful
		}

		allReAuthsSuccessful = newReAuthStatus;
		return true;
	}

	private PaymentResult getPaymentResult(final PaymentService paymentService, final ReAuthorizationItem reAuthorizationItem) {
		if (reAuthorizationItem.getOldPayment() == null) {
			return paymentService.initializeNewShipmentPayment(reAuthorizationItem.getShipment(), reAuthorizationItem.getNewPayment());
		}

		return paymentService.adjustShipmentPayment(reAuthorizationItem.getShipment(), reAuthorizationItem.getNewPayment());
	}

	/**
	 * Re-initialize shipments. This prevents evil JPA exception.
	 * 
	 * @param reAuthorizationItem the reAuth item
	 * @param processedPayments the processed payments
	 */
	private void reinitializeShipments(final ReAuthorizationItem reAuthorizationItem, final Collection<OrderPayment> processedPayments) {
		for (OrderPayment proccesedPayment : processedPayments) {
			// Need to reinit shipment and order. This prevents evil JPA exception.
			proccesedPayment.setOrderShipment(reAuthorizationItem.getShipment());
			proccesedPayment.setOrder(reAuthorizationItem.getShipment().getOrder());
			reAuthorizationItem.getShipment().getOrder().addOrderPayment(proccesedPayment);
			if (OrderPayment.AUTHORIZATION_TRANSACTION.equals(proccesedPayment.getTransactionType())) {
				reAuthorizationItem.setNewPayment(proccesedPayment);
			}
		}
	}

	@Override
	protected Order getModel() {
		return order;
	}

	/**
	 * @return list of tuples containing information about reuthorization process for each shipment.
	 */
	public List<ReAuthorizationItem> getReAuthorizationList() {
		return reAuthorizationList;
	}

	@Override
	public boolean canFinish() {
		if (getContainer().getCurrentPage() == initPage) {
			return false;
		}
		return isAllReAuthsSuccessful() && super.canFinish();
	}

	@Override
	public void onUpdateButtons(final EpWizardDialog dialog) {
		if (dialog.getSelectedPage() == summaryPage) {
			dialog.getWizardButton(IDialogConstants.BACK_ID).setEnabled(!isAllReAuthsSuccessful());
			dialog.getWizardButton(IDialogConstants.CANCEL_ID).setEnabled(!isAllReAuthsSuccessful());
		} else {
			dialog.getWizardButton(IDialogConstants.CANCEL_ID).setEnabled(true);
		}
	}

	/**
	 * @return if all reauthorizations were successful.
	 */
	public boolean isAllReAuthsSuccessful() {
		return allReAuthsSuccessful;
	}

}
