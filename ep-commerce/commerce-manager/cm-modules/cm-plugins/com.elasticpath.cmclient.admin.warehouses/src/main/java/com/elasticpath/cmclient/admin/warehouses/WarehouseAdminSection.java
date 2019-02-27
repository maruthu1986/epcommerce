/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.warehouses;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.AbstractAdminSection;
import com.elasticpath.cmclient.admin.warehouses.views.WarehouseListView;

/**
 * Warehouse admin section.
 */
public class WarehouseAdminSection extends AbstractAdminSection {

	@Override
	public void createItems(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		createItem(toolkit, parent, site, WarehouseListView.VIEW_ID, AdminWarehousesMessages.get().UserAdminSection_UserAdmin,
				AdminWarehousesImageRegistry.getImage(AdminWarehousesImageRegistry.IMAGE_WAREHOUSE));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAuthorized() {
		return AdminWarehousesPlugin.isAuthorized();
	}
}
