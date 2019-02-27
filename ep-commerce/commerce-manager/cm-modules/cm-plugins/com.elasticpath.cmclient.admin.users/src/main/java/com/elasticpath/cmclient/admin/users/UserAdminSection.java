/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.users;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.AbstractAdminSection;
import com.elasticpath.cmclient.admin.users.views.RoleListView;
import com.elasticpath.cmclient.admin.users.views.UserSearchView;

/**
 * User admin section.
 */
public class UserAdminSection extends AbstractAdminSection {

	@Override
	public void createItems(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		this.createItem(toolkit, parent, site, UserSearchView.VIEW_ID, AdminUsersMessages.get().UserAdminSection_UserAdmin, AdminUsersImageRegistry
				.getImage(AdminUsersImageRegistry.IMAGE_USER));
		this.createItem(toolkit, parent, site, RoleListView.VIEW_ID, AdminUsersMessages.get().UserAdminSection_RoleAdmin, AdminUsersImageRegistry
				.getImage(AdminUsersImageRegistry.IMAGE_ROLE));
	}
	@Override
	public boolean isAuthorized() {
		return AdminUsersPlugin.isAuthorized();
	}
}
