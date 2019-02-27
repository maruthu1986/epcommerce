/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.configuration.actions;

import com.elasticpath.cmclient.admin.configuration.AdminConfigurationPlugin;
import com.elasticpath.cmclient.admin.configuration.views.SystemConfigurationView;
import com.elasticpath.cmclient.core.EpAuthorizationException;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedShowViewAction;

/**
 * Shows the system administration view for authorized users.
 */
public class ShowSystemAdminViewAction extends AbstractAuthorizedShowViewAction {

	@Override
	protected String getViewId() {
		if (!isAuthorized()) {
			throw new EpAuthorizationException(SystemConfigurationView.VIEW_ID);
		}
		return SystemConfigurationView.VIEW_ID;
	}

	@Override
	public boolean isAuthorized() {
		return AdminConfigurationPlugin.isAuthorized();
	}
}
