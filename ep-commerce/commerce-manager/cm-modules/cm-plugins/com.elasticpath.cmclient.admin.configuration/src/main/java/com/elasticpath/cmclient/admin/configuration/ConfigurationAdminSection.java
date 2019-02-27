/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.admin.configuration;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.AbstractAdminSection;
import com.elasticpath.cmclient.admin.configuration.views.SearchIndexesView;
import com.elasticpath.cmclient.admin.configuration.views.SystemConfigurationView;

/**
 * Configuration admin section.
 */
public class ConfigurationAdminSection extends AbstractAdminSection {

	@Override
	public void createItems(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		createItem(toolkit, parent, site, SystemConfigurationView.VIEW_ID,
				AdminConfigurationMessages.get().ConfigurationAdminSection_SystemConfiguration,
				AdminConfigurationImageRegistry.getImage(AdminConfigurationImageRegistry.IMAGE_SYSTEM_PROPERTIES));
		
		createItem(toolkit, parent, site, SearchIndexesView.VIEW_ID, AdminConfigurationMessages.get().ConfigurationAdminSection_SearchIndexes,
				AdminConfigurationImageRegistry.getImage(AdminConfigurationImageRegistry.IMAGE_SEARCH_INDEXES));
	}

	@Override
	public boolean isAuthorized() {
		return AdminConfigurationPlugin.isAuthorized();
	}
}
