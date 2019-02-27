/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.admin.customers.actions;

import com.elasticpath.cmclient.admin.customers.AdminCustomersPlugin;
import com.elasticpath.cmclient.admin.customers.views.AttributeListView;
import com.elasticpath.cmclient.admin.customers.views.CustomerSegmentListView;
import com.elasticpath.cmclient.core.EpAuthorizationException;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedShowViewAction;

/**
 * Opens Customer Segment View. 
 */
public class ShowCustomerSegmentViewAction extends AbstractAuthorizedShowViewAction {

	@Override
	protected String getViewId() {
		if (!isAuthorized()) {
			throw new EpAuthorizationException(AttributeListView.VIEW_ID);
		}
		return CustomerSegmentListView.VIEW_ID;
	}

	@Override
	public boolean isAuthorized() {
		return AdminCustomersPlugin.isSegmentsAuthorized();
	}
}
