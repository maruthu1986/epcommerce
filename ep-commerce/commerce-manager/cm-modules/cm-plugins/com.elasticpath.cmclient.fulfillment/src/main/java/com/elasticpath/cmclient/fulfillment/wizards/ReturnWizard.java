/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

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
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.order.IllegalReturnStateException;
import com.elasticpath.service.order.IncorrectRefundAmountException;
import com.elasticpath.service.order.OrderReturnOutOfDateException;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.order.ReturnExchangeType;

/**
 * The wizard for creating and editing an order return.
 */
public final class ReturnWizard extends AbstractEpWizard <OrderReturn> {
	
	/** ReturnWizard types. */
	enum ReturnWizardType {
		/** Create type. */
		CREATE_RETURN,
		/** Edit type. */
		EDIT_RETURN,
		/** Complete type. */
		COMPLETE_RETURN
	}

	private static final String PAGE_SUBJECT_RETURN = ReturnSubjectPage.class.getName();

	private static final String PAGE_METHOD_RETURN = ReturnMethodPage.class.getName();

	private static final String PAGE_SUMMARY_RETURN = ReturnSummaryPage.class.getName();

	private static final ReturnAndExchangeService ORDER_RETURN_SERVICE = 
			(ReturnAndExchangeService) ServiceLocator.getService(ContextIdNames.ORDER_RETURN_SERVICE);
	
	private OrderReturn orderReturn;

	private ReturnSubjectPage returnSubjectPage;

	private ReturnMethodPage returnMethodPage;

	private ReturnSummaryPage returnSummaryPage;

	private final ReturnWizardType returnWizardType;

	private final boolean isInclusiveTax;

	/**
	 * Full type ReturnWizard factory method.
	 * 
	 * @param order The order
	 * @param orderShipment The order shipment
	 * @return ReturnWizard
	 */
	public static ReturnWizard createReturnWizard(final Order order, final OrderShipment orderShipment) {

		OrderReturn orderReturn = ORDER_RETURN_SERVICE.getOrderReturnPrototype(orderShipment, OrderReturnType.RETURN);

		return new ReturnWizard(FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_RETURN_CREATE),
				FulfillmentMessages.get().ReturnWizard_Create_Title, orderReturn, ReturnWizardType.CREATE_RETURN, orderShipment.isInclusiveTax());
	}

	/**
	 * Edit type ReturnWizard factory method.
	 * 
	 * @param orderReturn The order return
	 * @return Return Wizard
	 */
	public static ReturnWizard editReturnWizard(final OrderReturn orderReturn) {
		ReturnWizard returnWizard = new ReturnWizard(FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_RETURN_EDIT),
				FulfillmentMessages.get().ReturnWizard_Edit_Title, orderReturn, ReturnWizardType.EDIT_RETURN, orderReturn.isInclusiveTax());
		orderReturn.updateOrderReturnableQuantity(orderReturn.getOrder(), getProductSkuLookup());
		return returnWizard;
	}

	/**
	 * Complete type ReturnWizard factory method.
	 * 
	 * @param orderReturn The order return
	 * @return Return Wizard
	 */
	public static ReturnWizard completeReturnWizard(final OrderReturn orderReturn) {
		return new ReturnWizard(FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_RETURN_COMPLETE),
				FulfillmentMessages.get().ReturnWizard_Complete_Title, orderReturn, ReturnWizardType.COMPLETE_RETURN, orderReturn.isInclusiveTax());
	}

	private ReturnWizard(final Image wizardImage, final String windowTitle, final OrderReturn orderReturn, 
			final ReturnWizardType returnWizardType, final boolean isInclusiveTax) {

		super(windowTitle, windowTitle + FulfillmentMessages.SPACE + FulfillmentMessages.get().ReturnWizard_Step_Info, wizardImage);

		this.orderReturn = orderReturn;
		this.returnWizardType = returnWizardType;
		this.isInclusiveTax = isInclusiveTax;
		
		returnSubjectPage = null;
		returnMethodPage = null;
		returnSummaryPage = null;

		switch (returnWizardType) {
		case CREATE_RETURN:
			createSubjectPage(FulfillmentMessages.get().ReturnWizard_SubjectPage_Message);
			createMethodPage(FulfillmentMessages.get().ReturnWizard_MethodPage_Message);
			createSummaryPage();
			break;
		case EDIT_RETURN:
			createSubjectPage(FulfillmentMessages.EMPTY_STRING);
			break;
		case COMPLETE_RETURN:
			createMethodPage(FulfillmentMessages.get().ReturnWizard_MethodPage_Message);
			createSummaryPage();
			break;
		default:
			// Do nothing
			break;
		}
	}

	private void createSubjectPage(final String message) {
		returnSubjectPage = new ReturnSubjectPage(PAGE_SUBJECT_RETURN, message,
				FulfillmentMessages.get().ReturnWizard_ItemsToBeReturned_Section, getModel(), isInclusiveTax);
	}

	private void createMethodPage(final String message) {
		returnMethodPage = new ReturnMethodPage(PAGE_METHOD_RETURN, message);
	}

	private void createSummaryPage() {
		returnSummaryPage = new ReturnSummaryPage(PAGE_SUMMARY_RETURN);
	}

	@Override
	public void addPages() {
		addWizardPage(returnSubjectPage);
		addWizardPage(returnMethodPage);
		addWizardPage(returnSummaryPage);
	}

	private void addWizardPage(final IWizardPage page) {
		if (page != null) {
			super.addPage(page);
		}
	}

	@Override
	public boolean performCancel() {		
		orderReturn.recalculateOrderReturn();
		return true;
	}

	@Override
	public boolean performFinish() {
		IWizardPage page = getContainer().getCurrentPage();

		if (page instanceof ReturnSubjectPage && getReturnWizardType() == ReturnWizard.ReturnWizardType.EDIT_RETURN) {
			if (!((ReturnSubjectPage) page).validate()) {
				return false;
			}
			orderReturn.getOrder().setModifiedBy(getEventOriginator());
			try {
				ORDER_RETURN_SERVICE.editReturn(orderReturn);
			} catch (OrderReturnOutOfDateException | IllegalReturnStateException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
						FulfillmentMessages.get().OrderReturn_ErrDlgCollisionTitle, FulfillmentMessages.get().OrderReturn_ErrDlgCollisionMessage);
				return false;
			}
		}

		return true;
	}

	/**
	 * Completes return.
	 * 
	 * @return true if the operation terminated correctly
	 */
	public boolean completeReturn() {		
		try {
			orderReturn.getOrder().setModifiedBy(getEventOriginator());
			ReturnExchangeType type = ((ReturnMethodPage) getPage(PAGE_METHOD_RETURN)).getRefundControlResult();			
			orderReturn = ORDER_RETURN_SERVICE.completeReturn(orderReturn, type);
		} catch (OrderReturnOutOfDateException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					FulfillmentMessages.get().OrderReturn_ErrDlgCollisionTitle, FulfillmentMessages.get().OrderReturn_ErrDlgCollisionMessage);
			return false;		
		} catch (IncorrectRefundAmountException e) {
			MessageDialog.openError(getShell(), FulfillmentMessages.get().RefundWizard_IncorrectRefundAmount_Title,
					FulfillmentMessages.get().RefundWizard_IncorrectRefundAmount_Message);
			return false;
		} catch (PaymentGatewayException gatewayException) {
			MessageDialog.openError(getShell(), FulfillmentMessages.get().ReturnWizard_ProceedError_Title,
					FulfillmentMessages.get().ReturnWizard_ProceedError_Msg);
			return false;
		}

		return true;
	}
	
	/**
	 * Creates return.
	 * 
	 * @return true if the operation terminated correctly
	 */
	public boolean createReturn() {		
		try {
			ReturnExchangeType type = ((ReturnMethodPage) getPage(PAGE_METHOD_RETURN)).getRefundControlResult();
			orderReturn.setCreatedByCmUser(LoginManager.getCmUser());
			orderReturn.normalizeOrderReturn();
			orderReturn = ORDER_RETURN_SERVICE.createShipmentReturn(orderReturn,
					type,
					orderReturn.getOrderShipmentForReturn(),
					getEventOriginator());

		} catch (IncorrectRefundAmountException e) {
			MessageDialog.openError(getShell(), FulfillmentMessages.get().RefundWizard_IncorrectRefundAmount_Title,
					FulfillmentMessages.get().RefundWizard_IncorrectRefundAmount_Message);
			return false;
		} catch (PaymentGatewayException gatewayException) {
			MessageDialog.openError(getShell(), FulfillmentMessages.get().ReturnWizard_ProceedError_Title,
					FulfillmentMessages.get().ReturnWizard_ProceedError_Msg);
			return false;
		}

		return true;
	}

	/**
	 * Getter to get ReturnWizard type.
	 * 
	 * @return The ReturnWizard type.
	 */
	public ReturnWizardType getReturnWizardType() {
		return returnWizardType;
	}

	@Override
	public OrderReturn getModel() {
		return orderReturn;
	}

	@Override
	public void onUpdateButtons(final EpWizardDialog dialog) {
		// Nothing to do if this is a single page edit return wizard
		if (getReturnWizardType() == ReturnWizard.ReturnWizardType.EDIT_RETURN) {
			return;
		}
		IWizardPage currentPage = dialog.getCurrentPage(); 
		if (returnSummaryPage.equals(currentPage)) {
			dialog.getWizardButton(IDialogConstants.BACK_ID).setEnabled(false);
			dialog.getWizardButton(IDialogConstants.CANCEL_ID).setEnabled(false);
		} else if (returnSubjectPage != null && returnSubjectPage.equals(currentPage)) {
			dialog.getWizardButton(IDialogConstants.NEXT_ID).setEnabled(returnSubjectPage.validate());
		}
	}
	
	private EventOriginator getEventOriginator() {
		EventOriginatorHelper helper = ServiceLocator.getService(
				ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(LoginManager.getCmUser());
	}
	
	protected static ProductSkuLookup getProductSkuLookup() {
		return ServiceLocator.getService(ContextIdNames.PRODUCT_SKU_LOOKUP);
	}
}
