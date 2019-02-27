/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.configuration.actions;

import com.elasticpath.cmclient.admin.configuration.AdminConfigurationPlugin;
import com.elasticpath.cmclient.admin.configuration.views.SearchIndexesView;
import com.elasticpath.cmclient.core.EpAuthorizationException;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedShowViewAction;

/**
 * Shows the search indexes view for authorized users.
 */
public class ShowSearchViewAction extends AbstractAuthorizedShowViewAction {

	@Override
	protected String getViewId() {
		if (!isAuthorized()) {
			throw new EpAuthorizationException(SearchIndexesView.VIEW_ID);
		}
		return SearchIndexesView.VIEW_ID;
	}

	@Override
	public boolean isAuthorized() {
		return AdminConfigurationPlugin.isAuthorized();
	}
	
}
