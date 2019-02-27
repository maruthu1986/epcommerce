/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog;

import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
public class CatalogPlugin extends AbstractPolicyAwareUIPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.catalog"; //$NON-NLS-1$

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			CatalogImageRegistry.disposeAllImages();
		} catch (ExceptionInInitializerError | IllegalStateException e) {
			// Do nothing.
		}
		super.stop(context);
	}

	@Override
	protected String getPluginId() {
		return PLUGIN_ID;
	}

	@Override
	protected void loadLocalizedMessages() {
		CatalogMessages.get();
	}
}
