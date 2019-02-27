/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.admin.customers;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.AbstractAdminSection;
import com.elasticpath.cmclient.admin.customers.views.AttributeListView;
import com.elasticpath.cmclient.admin.customers.views.CustomerSegmentListView;

/**
 * Customer admin section.
 */
public class CustomerAdminSection extends AbstractAdminSection {

	/**
	 * Creates the items for the customer admin section.
	 * 
	 * @param toolkit the top level toolkit which contains all admin sections
	 * @param parent the parent section which is the container for this specific admin section
	 * @param site the Workbench site, so that the composite can get a reference to Views that it should open.
	 */
	@Override
	public void createItems(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		if (AdminCustomersPlugin.isAttributesAuthorized()) {
			createItem(toolkit, parent, site, AttributeListView.VIEW_ID, AdminCustomersMessages.get().CustomerAdminSection_ProfileAttributes,
					AdminCustomersImageRegistry.getImage(AdminCustomersImageRegistry.IMAGE_ATTRIBUTE));
		}
		
		if (AdminCustomersPlugin.isSegmentsAuthorized()) {
			createItem(toolkit, parent, site, CustomerSegmentListView.VIEW_ID, AdminCustomersMessages.get().CustomerAdminSection_CustomerSegments,
					AdminCustomersImageRegistry.getImage(AdminCustomersImageRegistry.IMAGE_CUSTOMER_SEGMENT));
		}
	}

	@Override
	public boolean isAuthorized() {
		return AdminCustomersPlugin.isAttributesAuthorized() || AdminCustomersPlugin.isSegmentsAuthorized();
	}
}
