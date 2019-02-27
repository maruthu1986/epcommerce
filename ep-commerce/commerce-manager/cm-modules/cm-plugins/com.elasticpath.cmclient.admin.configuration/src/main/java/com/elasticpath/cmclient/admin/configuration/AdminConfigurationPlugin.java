/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.configuration;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
@SuppressWarnings("PMD.UseUtilityClass")
public class AdminConfigurationPlugin extends AbstractEpUIPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.admin.configuration"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public AdminConfigurationPlugin() {
		// empty
	}

	/**
	 * Checks whether a user is authorized to manage customer profiles.
	 *
	 * @return true if authorized, false if not.
	 */
	public static boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(AdminConfigurationPermissions.ADMIN_CONFIGURATION_MANAGE);
	}


	@Override
	protected void loadLocalizedMessages() {
		AdminConfigurationMessages.get();
	}
}
