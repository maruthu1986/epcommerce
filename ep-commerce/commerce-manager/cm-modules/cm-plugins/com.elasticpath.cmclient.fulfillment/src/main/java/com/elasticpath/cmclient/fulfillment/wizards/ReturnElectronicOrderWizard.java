/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.service.order.IncorrectRefundAmountException;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.order.ReturnExchangeType;
import com.elasticpath.service.payment.PaymentServiceException;

/**
 * The wizard for creating and editing an order return.
 */
public final class ReturnElectronicOrderWizard extends AbstractEpWizard <OrderReturn> {

	private static final String PAGE_SUBJECT_RETURN = ReturnElectronicOrderSubjectPage.class.getName();

	private static final String PAGE_METHOD_RETURN = ReturnElectronicOrderMethodPage.class.getName();

	private static final String PAGE_SUMMARY_RETURN = ReturnElectronicOrderSummaryPage.class.getName();

	private static final ReturnAndExchangeService ORDER_RETURN_SERVICE =
			(ReturnAndExchangeService) ServiceLocator.getService(ContextIdNames.ORDER_RETURN_SERVICE);

	private OrderReturn orderReturn;

	private ReturnElectronicOrderSubjectPage returnElectronicOrderSubjectPage;

	private ReturnElectronicOrderMethodPage returnElectronicOrderMethodPage;

	private ReturnElectronicOrderSummaryPage returnElectronicOrderSummaryPage;

	private final boolean isInclusiveTax;

	/**
	 * Full type ReturnWizard factory method.
	 *
	 * @param order The order
	 * @param orderShipment The order shipment
	 * @return ReturnWizard
	 */
	public static ReturnElectronicOrderWizard createReturnWizard(final Order order, final OrderShipment orderShipment) {

		OrderReturn orderReturn = ORDER_RETURN_SERVICE.getOrderReturnPrototype(orderShipment, OrderReturnType.RETURN);
		orderReturn.setPhysicalReturn(false);

		return new ReturnElectronicOrderWizard(FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_RETURN_CREATE),
				FulfillmentMessages.get().ReturnWizard_Create_Title,
				orderReturn,
				orderShipment.isInclusiveTax());
	}

	private ReturnElectronicOrderWizard(final Image wizardImage, final String windowTitle, final OrderReturn orderReturn,
			final boolean isInclusiveTax) {

		super(windowTitle, windowTitle + FulfillmentMessages.SPACE + FulfillmentMessages.get().ReturnWizard_Step_Info, wizardImage);

		this.orderReturn = orderReturn;
		this.isInclusiveTax = isInclusiveTax;

		returnElectronicOrderSubjectPage = null;
		returnElectronicOrderMethodPage = null;
		returnElectronicOrderSummaryPage = null;

		createSubjectPage(FulfillmentMessages.get().ReturnWizard_SubjectPage_Message);
		createMethodPage(FulfillmentMessages.get().ReturnWizard_MethodPage_Message);
		createSummaryPage();
	}

	private void createSubjectPage(final String message) {
		returnElectronicOrderSubjectPage = new ReturnElectronicOrderSubjectPage(PAGE_SUBJECT_RETURN, message,
				FulfillmentMessages.get().ReturnWizard_ItemsToBeReturned_Section, getModel(), isInclusiveTax);
	}

	private void createMethodPage(final String message) {
		returnElectronicOrderMethodPage = new ReturnElectronicOrderMethodPage(PAGE_METHOD_RETURN, message);
	}

	private void createSummaryPage() {
		returnElectronicOrderSummaryPage = new ReturnElectronicOrderSummaryPage(PAGE_SUMMARY_RETURN);
	}

	/**
	 * Add all needed pages. Note, that not all pages will be actually added, only those that was really created.
	 */
	@Override
	public void addPages() {
		addWizardPage(returnElectronicOrderSubjectPage);
		addWizardPage(returnElectronicOrderMethodPage);
		addWizardPage(returnElectronicOrderSummaryPage);
	}

	private void addWizardPage(final IWizardPage page) {
		if (page != null) {
			super.addPage(page);
		}
	}

	/**
	 * Restore qty to zero to release order returns in case of cancelation.
	 *
	 * @return always returns true, cause cancelation is not needed to be executed.
	 */
	@Override
	public boolean performCancel() {
		orderReturn.recalculateOrderReturn();
		return true;
	}

	/**
	 * Creates return.
	 *
	 * @return true if the operation terminated correctly
	 */
	public boolean createReturn() {
		try {
			ReturnExchangeType type = ((ReturnElectronicOrderMethodPage) getPage(PAGE_METHOD_RETURN)).getRefundControlResult();
			orderReturn.setCreatedByCmUser(LoginManager.getCmUser()); //order return should be the protoype
			orderReturn.normalizeOrderReturn();
			orderReturn = ORDER_RETURN_SERVICE.createShipmentReturn(orderReturn, type,
				orderReturn.getOrderShipmentForReturn(), getEventOriginator());
		} catch (IncorrectRefundAmountException e) {
			MessageDialog.openError(getShell(), FulfillmentMessages.get().RefundWizard_IncorrectRefundAmount_Title,
					FulfillmentMessages.get().RefundWizard_IncorrectRefundAmount_Message);
			return false;
		} catch (PaymentGatewayException gatewayException) {
			MessageDialog.openError(getShell(), FulfillmentMessages.get().ReturnWizard_ProceedError_Title,
					FulfillmentMessages.get().ReturnWizard_ProceedError_Msg);
			return false;
		} catch (PaymentServiceException psException) {
			MessageDialog.openError(getShell(), FulfillmentMessages.get().ReturnWizard_ProceedError_Title,

					NLS.bind(FulfillmentMessages.get().ReturnWizard_ProceedError_Msg,
					psException.getLocalizedMessage()));
			return false;
		} catch (Exception otherException) {
			MessageDialog.openError(getShell(), FulfillmentMessages.get().ReturnWizard_ProceedError_Title,

					NLS.bind(FulfillmentMessages.get().ReturnWizard_ProceedError_Msg,
					otherException.getLocalizedMessage()));
			return false;
		}

		return true;
	}

	/**
	 * Getter to get order return.
	 * 
	 * @return The order return
	 */
	@Override
	public OrderReturn getModel() {
		return orderReturn;
	}

	/**
	 * Back and Cancel buttons are disabled in the summary page.
	 * 
	 * @param dialog parent EpWizardDialog.
	 */
	@Override
	public void onUpdateButtons(final EpWizardDialog dialog) {
		IWizardPage currentPage = dialog.getCurrentPage(); 
		if (returnElectronicOrderSummaryPage.equals(currentPage)) {
			dialog.getWizardButton(IDialogConstants.BACK_ID).setEnabled(false);
			dialog.getWizardButton(IDialogConstants.CANCEL_ID).setEnabled(false);
		} else if (returnElectronicOrderSubjectPage != null && returnElectronicOrderSubjectPage.equals(currentPage)) {
			dialog.getWizardButton(IDialogConstants.NEXT_ID).setEnabled(returnElectronicOrderSubjectPage.validate());
		}
	}
	
	private EventOriginator getEventOriginator() {
		EventOriginatorHelper helper = ServiceLocator.getService(
				ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(LoginManager.getCmUser());
	}
}
