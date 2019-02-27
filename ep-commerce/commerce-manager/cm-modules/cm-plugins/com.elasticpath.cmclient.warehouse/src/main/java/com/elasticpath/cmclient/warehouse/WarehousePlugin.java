/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse;

import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
public class WarehousePlugin extends AbstractEpUIPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.warehouse"; //$NON-NLS-1$

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			WarehouseImageRegistry.disposeAllImages();
		} catch (ExceptionInInitializerError | IllegalStateException e) {
			// Do nothing.
		}

		super.stop(context);
	}

	@Override
	protected void loadLocalizedMessages() {
		WarehouseMessages.get();
	}
}
