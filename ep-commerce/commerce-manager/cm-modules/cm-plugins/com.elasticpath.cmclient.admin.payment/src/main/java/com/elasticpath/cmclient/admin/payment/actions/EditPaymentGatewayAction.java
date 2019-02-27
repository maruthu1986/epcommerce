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
import com.elasticpath.cmclient.admin.payment.dialogs.AbstractPaymentGatewayDialog;
import com.elasticpath.cmclient.admin.payment.views.PaymentListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.service.payment.PaymentGatewayService;

/**
 * Edit payment gateway action.
 */
public class EditPaymentGatewayAction extends Action {

	private static final Logger LOG = Logger.getLogger(EditPaymentGatewayAction.class);

	private final PaymentListView listView;

	/**
	 * Constructor.
	 *
	 * @param listView PaymentListView this action is associated with
	 * @param text action text
	 * @param imageDescriptor action image
	 */
	public EditPaymentGatewayAction(final PaymentListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("EditPaymentGatewayAction Action called."); //$NON-NLS-1$
		PaymentGateway selectedGateway = listView.getSelectedPaymentGateway();

		PaymentGatewayService gatewayService = ServiceLocator.getService(
				ContextIdNames.PAYMENT_GATEWAY_SERVICE);

		/** Get the most recent version of payment gateway. */
		PaymentGateway gatewayToEdit = gatewayService.getGateway(selectedGateway.getUidPk());

		if (gatewayToEdit == null) {
			MessageDialog.openInformation(listView.getSite().getShell(), AdminPaymentMessages.get().EditPaymentGateway,
				NLS.bind(AdminPaymentMessages.get().PaymentGatewayNoLongerExists,
				selectedGateway.getName()));
			listView.refreshViewerInput();
			return;
		}

		AbstractPaymentGatewayDialog editDialog = AbstractPaymentGatewayDialog.buildEditDialog(listView.getSite().getShell(), gatewayToEdit);

		if (editDialog.openDialog()) {
			gatewayService.saveOrUpdate(gatewayToEdit);
			listView.refreshViewerInput();
		}
	}
}
