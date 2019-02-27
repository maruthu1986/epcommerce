/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.order.AbstractOrderPage;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEditor;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.service.order.OrderService;

/**
 * Resend email confirmation Action.
 */
public class ResendConfirmationEmailContributionAction extends Action {

	private static final Logger LOG = Logger.getLogger(ResendConfirmationEmailContributionAction.class);

	private final AbstractOrderPage orderPage;

	/**
	 * Constructor for resend email confirmation action.
	 * 
	 * @param orderPage the AbstractOrderPage
	 */
	public ResendConfirmationEmailContributionAction(final AbstractOrderPage orderPage) {
		super(FulfillmentMessages.get().OrderActionResendConfirmationEmail, FulfillmentImageRegistry.EMAIL_CONFIRMATION_RESEND);
		setToolTipText(FulfillmentMessages.get().OrderActionResendConfirmationEmail);
		this.orderPage = orderPage;
	}

	@Override
	public void run() {

		final Order order = ((OrderEditor) orderPage.getEditor()).getModel();

		final OrderService orderService = ServiceLocator.getService(ContextIdNames.ORDER_SERVICE);

		boolean confirmationEmailSentSuccessfully = false;

		try {
			orderService.resendOrderConfirmationEvent(order.getOrderNumber());
			confirmationEmailSentSuccessfully = true;
		} catch (final Exception e) {
			LOG.error("Exception sending email ", e); //$NON-NLS-1$
		}

		String message;
		if (confirmationEmailSentSuccessfully) {
			message = FulfillmentMessages.get().resendConfirmationEmailSuccess;
		} else {
			message = FulfillmentMessages.get().resendConfirmationEmailFailure;
		}
		MessageDialog.openInformation(orderPage.getSite().getShell(), FulfillmentMessages.get().OrderActionResendConfirmationEmail, message);
	}
}
