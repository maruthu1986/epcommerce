/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.payment.actions;

import com.elasticpath.cmclient.admin.payment.AdminPaymentPlugin;
import com.elasticpath.cmclient.admin.payment.views.PaymentListView;
import com.elasticpath.cmclient.core.EpAuthorizationException;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedShowViewAction;

/**
 * Opens payment gateway list view.
 */
public class ShowPaymentGatewaysViewAction extends AbstractAuthorizedShowViewAction {

	@Override
	protected String getViewId() {
		if (!isAuthorized()) {
			throw new EpAuthorizationException(PaymentListView.VIEW_ID);
		}
		return PaymentListView.VIEW_ID;
	}

	@Override
	public boolean isAuthorized() {
		return AdminPaymentPlugin.isAuthorized();
	}
}
