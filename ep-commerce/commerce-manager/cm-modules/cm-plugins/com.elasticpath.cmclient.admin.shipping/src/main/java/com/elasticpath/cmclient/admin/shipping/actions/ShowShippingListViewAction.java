/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.shipping.actions;

import com.elasticpath.cmclient.admin.shipping.AdminShippingPlugin;
import com.elasticpath.cmclient.admin.shipping.views.ShippingRegionListView;
import com.elasticpath.cmclient.core.EpAuthorizationException;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedShowViewAction;

/**
 * Shows shipping regions list view.
 */
public class ShowShippingListViewAction extends AbstractAuthorizedShowViewAction {

	@Override
	protected String getViewId() {
		if (!isAuthorized()) {
			throw new EpAuthorizationException(ShippingRegionListView.VIEW_ID);
		}
		return ShippingRegionListView.VIEW_ID;
	}

	@Override
	public boolean isAuthorized() {
		return AdminShippingPlugin.isAuthorized();
	}
}
