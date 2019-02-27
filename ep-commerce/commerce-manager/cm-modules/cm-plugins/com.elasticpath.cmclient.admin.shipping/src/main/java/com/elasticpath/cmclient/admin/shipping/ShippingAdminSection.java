/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.shipping;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.AbstractAdminSection;
import com.elasticpath.cmclient.admin.shipping.views.ShippingRegionListView;

/**
 * Shipping admin item extension.
 */
public class ShippingAdminSection extends AbstractAdminSection {

	@Override
	public void createItems(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		createItem(toolkit, parent, site, ShippingRegionListView.VIEW_ID, AdminShippingMessages.get().ShippingAdminItemCompositeFactory_RegionsAdmin,
				AdminShippingImageRegistry.getImage(AdminShippingImageRegistry.IMAGE_SHIPPING));
	}

	@Override
	public boolean isAuthorized() {
		return AdminShippingPlugin.isAuthorized();
	}
}
