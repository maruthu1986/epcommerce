/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.warehouses.actions;

import com.elasticpath.cmclient.admin.warehouses.AdminWarehousesPlugin;
import com.elasticpath.cmclient.admin.warehouses.views.WarehouseListView;
import com.elasticpath.cmclient.core.EpAuthorizationException;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedShowViewAction;

/**
 * Shows warehouses list view.
 */
public class ShowWarehousesViewAction extends AbstractAuthorizedShowViewAction {

	@Override
	protected String getViewId() {
		if (!isAuthorized()) {
			throw new EpAuthorizationException(WarehouseListView.VIEW_ID);
		}
		return WarehouseListView.VIEW_ID;
	}

	@Override
	public boolean isAuthorized() {
		return AdminWarehousesPlugin.isAuthorized();
	}
}
