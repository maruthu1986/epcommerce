/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.warehouses;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
@SuppressWarnings("PMD.UseUtilityClass")
public final class AdminWarehousesPlugin extends AbstractEpUIPlugin {

	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.admin.warehouses"; //$NON-NLS-1$

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static AdminWarehousesPlugin getDefault() {
		return CmSingletonUtil.getSessionInstance(AdminWarehousesPlugin.class);
	}

	/**
	 * Determines whether the current user is authorized to use this plugin.
	 * 
	 * @return true if yes, else false
	 */
	public static boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(AdminWarehousesPermissions.ADMIN_WAREHOUSES_MANAGE);
	}


	@Override
	protected void loadLocalizedMessages() {
		AdminWarehousesMessages.get();
	}
}