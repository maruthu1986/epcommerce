/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.advancedsearch;


import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
public class AdvancedSearchPlugin extends AbstractEpUIPlugin {

	/**
	 * The plug-in ID.
	 */ 
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.advancedsearch"; //$NON-NLS-1$

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			AdvancedSearchImageRegistry.disposeAllImages();
		} catch (ExceptionInInitializerError | IllegalStateException e) {
			// Do nothing.
		}
		super.stop(context);
	}

	/**
	 * Returns the session instance.
	 *
	 * @return the session instance
	 */
	public static AdvancedSearchPlugin getDefault() {
		return CmSingletonUtil.getSessionInstance(AdvancedSearchPlugin.class);
	}

	@Override
	protected void loadLocalizedMessages() {
		AdvancedSearchMessages.get();
	}
}
