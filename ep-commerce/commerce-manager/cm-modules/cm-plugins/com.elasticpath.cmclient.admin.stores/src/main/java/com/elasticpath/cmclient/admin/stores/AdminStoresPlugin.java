/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores;

import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
public class AdminStoresPlugin extends AbstractEpUIPlugin {

	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.admin.stores"; //$NON-NLS-1$

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			AdminStoresImageRegistry.disposeAllImages();
		} catch (ExceptionInInitializerError | IllegalStateException e) {
			// Do nothing.
		}
		super.stop(context);
	}

	/**
	 * Returns the session instance.
	 *
	 * @return the session instance.
	 */
	public static AdminStoresPlugin getDefault() {
		return CmSingletonUtil.getSessionInstance(AdminStoresPlugin.class);
	}
	
	/**
	 * Determines whether a user is authorized to use this plugin.
	 * 
	 * @return true if yes, else false
	 */
	public static boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(AdminStoresPermissions.ADMIN_STORES_MANAGE);
	}

	@Override
	protected void loadLocalizedMessages() {
		AdminStoresMessages.get();
	}
}
