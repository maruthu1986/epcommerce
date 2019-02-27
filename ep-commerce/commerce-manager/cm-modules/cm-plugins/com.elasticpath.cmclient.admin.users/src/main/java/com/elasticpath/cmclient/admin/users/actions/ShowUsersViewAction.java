/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.users.actions;

import com.elasticpath.cmclient.admin.users.AdminUsersPlugin;
import com.elasticpath.cmclient.admin.users.views.RoleListView;
import com.elasticpath.cmclient.admin.users.views.UserSearchView;
import com.elasticpath.cmclient.core.EpAuthorizationException;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedShowViewAction;

/**
 * Shows users view.
 */
public class ShowUsersViewAction extends AbstractAuthorizedShowViewAction {

	@Override
	protected String getViewId() {
		if (!isAuthorized()) {
			throw new EpAuthorizationException(RoleListView.VIEW_ID);
		}
		return UserSearchView.VIEW_ID;
	}

	@Override
	public boolean isAuthorized() {
		return AdminUsersPlugin.isAuthorized();
	}
}
