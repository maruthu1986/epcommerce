/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
public class AdminDataPoliciesPlugin extends AbstractEpUIPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.admin.datapolicies"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public AdminDataPoliciesPlugin() {
		// empty
	}

	/**
	 * Checks whether a user is authorized to manage data policies.
	 *
	 * @return true if authorized, false if not.
	 */
	public static boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(AdminDataPoliciesPermissions.ADMIN_DATA_POLICIES_MANAGE);
	}

	@Override
	protected void loadLocalizedMessages() {
		AdminDataPoliciesMessages.get();
	}
}
