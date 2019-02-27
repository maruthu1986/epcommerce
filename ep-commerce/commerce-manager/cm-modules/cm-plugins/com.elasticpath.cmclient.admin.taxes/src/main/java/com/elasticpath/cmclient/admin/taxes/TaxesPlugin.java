/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes;

import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
public class TaxesPlugin extends AbstractEpUIPlugin {
	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.admin.taxes"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public TaxesPlugin() {
		super();
		// default constructor
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			TaxesImageRegistry.disposeAllImages();
		} catch (ExceptionInInitializerError | IllegalStateException e) {
			// Do nothing.
		}
		super.stop(context);
	}

	/**
	 * Determines whether the current user is authorized to use this plugin.
	 * 
	 * @return true if yes, else false
	 */
	public static boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(TaxesPermissions.TAXES_MANAGE);
	}


	@Override
	protected void loadLocalizedMessages() {
		TaxesMessages.get();
	}
}
