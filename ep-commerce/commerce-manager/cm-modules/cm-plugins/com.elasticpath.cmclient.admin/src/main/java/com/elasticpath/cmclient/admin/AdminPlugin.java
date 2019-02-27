/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin;

import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
public class AdminPlugin extends AbstractEpUIPlugin {

	/** The plug-in ID. **/
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.admin"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public AdminPlugin() {
		//empty
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		ImageRegistry.disposeAllImages();
	}

	@Override
	protected void loadLocalizedMessages() {
		AdminMessages.get();
	}
}
