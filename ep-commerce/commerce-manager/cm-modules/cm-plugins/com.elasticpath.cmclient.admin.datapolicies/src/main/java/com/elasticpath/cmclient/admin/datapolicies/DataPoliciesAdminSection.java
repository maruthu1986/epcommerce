/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.AbstractAdminSection;
import com.elasticpath.cmclient.admin.datapolicies.views.DataPolicyListView;

/**
 * Data policies admin section.
 */
public class DataPoliciesAdminSection extends AbstractAdminSection {

	@Override
	public void createItems(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		createItem(toolkit, parent, site, DataPolicyListView.VIEW_ID, AdminDataPoliciesMessages.get().DataPoliciesAdminSection_DataPolicies,
				AdminDataPoliciesImageRegistry.getImage(AdminDataPoliciesImageRegistry.IMAGE_DATA_POLICIES));
	}

	@Override
	public boolean isAuthorized() {
		return AdminDataPoliciesPlugin.isAuthorized();
	}
}
