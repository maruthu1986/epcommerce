/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.payment.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.admin.payment.dialogs.AbstractPaymentGatewayDialog;
import com.elasticpath.cmclient.admin.payment.views.PaymentListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.payment.PaymentGatewayService;

/**
 * Create payment gateway action.
 */
public class CreatePaymentGatewayAction extends Action {

	private static final Logger LOG = Logger.getLogger(CreatePaymentGatewayAction.class);

	private final PaymentListView listView;

	/**
	 * The constructor.
	 * 
	 * @param listView PaymentListView this action is associated with
	 * @param text action text
	 * @param imageDescriptor action image
	 */
	public CreatePaymentGatewayAction(final PaymentListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("CreatePaymentGatewayAction Action called."); //$NON-NLS-1$

		AbstractPaymentGatewayDialog createDialog = AbstractPaymentGatewayDialog.buildCreateDialog(listView.getSite().getShell());

		if (createDialog.openDialog()) {
			PaymentGatewayService gatewayService = ServiceLocator.getService(
					ContextIdNames.PAYMENT_GATEWAY_SERVICE);
			gatewayService.saveOrUpdate(createDialog.getPaymentGateway());
			listView.refreshViewerInput();
		}
	}
}
