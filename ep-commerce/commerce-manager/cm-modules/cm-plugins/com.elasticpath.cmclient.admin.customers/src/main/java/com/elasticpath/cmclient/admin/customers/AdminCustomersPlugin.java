/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.admin.customers;

import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
public class AdminCustomersPlugin extends AbstractEpUIPlugin {

	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.admin.customers"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public AdminCustomersPlugin() {
		// empty
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			AdminCustomersImageRegistry.disposeAllImages();
		} catch (ExceptionInInitializerError | IllegalStateException e) {
			// Do nothing.
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static AdminCustomersPlugin getDefault() {
		return CmSingletonUtil.getSessionInstance(AdminCustomersPlugin.class);
	}

	/**
	 * Checks whether a user is authorized to manage customer profiles.
	 * 
	 * @return true if authorized, false if not.
	 */
	public static boolean isAttributesAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(AdminCustomersPermissions.ADMIN_CUSTOMER_PROFILES_MANAGE);
	}

	/**
	 * Checks whether a user is authorized to manage customer segments.
	 * 
	 * @return true if authorized, false if not.
	 */
	public static boolean isSegmentsAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(AdminCustomersPermissions.ADMIN_CUSTOMER_SEGMENTS_MANAGE);
	}

	@Override
	protected void loadLocalizedMessages() {
		AdminCustomersMessages.get();
	}
}
