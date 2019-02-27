/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.customers.actions;

import com.elasticpath.cmclient.admin.customers.AdminCustomersPlugin;
import com.elasticpath.cmclient.admin.customers.views.AttributeListView;
import com.elasticpath.cmclient.core.EpAuthorizationException;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedShowViewAction;

/**
 * Opens Customer Profile Attributes View. 
 */
public class ShowAttributesViewAction extends AbstractAuthorizedShowViewAction {

	@Override
	protected String getViewId() {
		if (!isAuthorized()) {
			throw new EpAuthorizationException(AttributeListView.VIEW_ID);
		}
		return AttributeListView.VIEW_ID;
	}

	@Override
	public boolean isAuthorized() {
		return AdminCustomersPlugin.isAttributesAuthorized();
	}
}
