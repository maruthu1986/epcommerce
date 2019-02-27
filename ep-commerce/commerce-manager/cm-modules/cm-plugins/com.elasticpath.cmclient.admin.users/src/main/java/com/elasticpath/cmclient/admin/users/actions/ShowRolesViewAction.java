/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.users.actions;

import com.elasticpath.cmclient.admin.users.AdminUsersPlugin;
import com.elasticpath.cmclient.admin.users.views.RoleListView;
import com.elasticpath.cmclient.core.EpAuthorizationException;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedShowViewAction;

/**
 * Shows user roles view.
 */
public class ShowRolesViewAction extends AbstractAuthorizedShowViewAction {

	@Override
	protected String getViewId() {
		if (!isAuthorized()) {
			throw new EpAuthorizationException(RoleListView.VIEW_ID);
		}
		return RoleListView.VIEW_ID;
	}

	@Override
	public boolean isAuthorized() {
		return AdminUsersPlugin.isAuthorized();
	}
}
