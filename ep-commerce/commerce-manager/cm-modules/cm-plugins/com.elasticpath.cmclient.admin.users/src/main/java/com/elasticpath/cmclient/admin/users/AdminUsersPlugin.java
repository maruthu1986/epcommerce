/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.admin.users;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
@SuppressWarnings("PMD.UseUtilityClass")
public class AdminUsersPlugin extends AbstractEpUIPlugin {

	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.admin.users"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public AdminUsersPlugin() {
		//empty
	}

	/**
	 * Determines whether the current user is authorized to use this plugin.
	 * 
	 * @return true if yes, else false
	 */
	public static boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(AdminUsersPermissions.ADMIN_USERS_MANAGE);
	}

	@Override
	protected void loadLocalizedMessages() {
		AdminUsersMessages.get();
	}
}
