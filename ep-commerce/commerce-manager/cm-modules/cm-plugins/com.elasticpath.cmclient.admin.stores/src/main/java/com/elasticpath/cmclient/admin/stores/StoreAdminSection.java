/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.AbstractAdminSection;
import com.elasticpath.cmclient.admin.stores.views.StoreListView;

/**
 * Store admin section.
 */
public class StoreAdminSection extends AbstractAdminSection {

	@Override
	public void createItems(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		createItem(toolkit, parent, site, StoreListView.VIEW_ID, AdminStoresMessages.get().StoreAdminSection_StoreAdmin, AdminStoresImageRegistry
				.getImage(AdminStoresImageRegistry.IMAGE_STORE_ADMIN_SECTION_ITEM));
	}

	@Override
	public boolean isAuthorized() {
		return AdminStoresPlugin.isAuthorized();
	}
}
