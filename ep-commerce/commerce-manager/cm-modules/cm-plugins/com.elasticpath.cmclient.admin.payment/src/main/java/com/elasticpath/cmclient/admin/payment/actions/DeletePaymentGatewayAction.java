/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.payment.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.admin.payment.AdminPaymentMessages;
import com.elasticpath.cmclient.admin.payment.views.PaymentListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.service.payment.PaymentGatewayService;

/**
 * Delete payment gateway action.
 */
public class DeletePaymentGatewayAction extends Action {

	private static final Logger LOG = Logger.getLogger(DeletePaymentGatewayAction.class);

	private final PaymentListView listView;

	/**
	 * Constructor.
	 *
	 * @param listView PaymentListView this action is associated with
	 * @param text action text
	 * @param imageDescriptor action image
	 */
	public DeletePaymentGatewayAction(final PaymentListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("DeletePaymentGatewayAction Action called."); //$NON-NLS-1$

		PaymentGatewayService gatewayService = ServiceLocator.getService(
				ContextIdNames.PAYMENT_GATEWAY_SERVICE);

		PaymentGateway paymentGateway = listView.getSelectedPaymentGateway();

		if (gatewayService.getPaymentGatewaysInUse().contains(paymentGateway.getUidPk())) {
			MessageDialog.openInformation(listView.getSite().getShell(), AdminPaymentMessages.get().PaymentGatewayInUseTitle,
				NLS.bind(AdminPaymentMessages.get().PaymentGatewayInUseMessage,
				paymentGateway.getName()));
			return;
		}

		boolean confirmed = MessageDialog.openConfirm(listView.getSite().getShell(), AdminPaymentMessages.get().DeletePaymentGatewayTitle,

				NLS.bind(AdminPaymentMessages.get().DeletePaymentGatewayText,
				paymentGateway.getName()));

		if (confirmed) {
			// TODO: Do I need to check if the gateway isn't in use? If so-do the checkings.
			gatewayService.remove(paymentGateway);
			listView.refreshViewerInput();
		}
	}
}
