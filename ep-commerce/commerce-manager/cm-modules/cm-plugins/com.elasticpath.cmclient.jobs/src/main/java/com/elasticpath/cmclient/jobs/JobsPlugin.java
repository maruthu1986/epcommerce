/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.jobs;

import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
public class JobsPlugin extends AbstractPolicyAwareUIPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.jobs"; //$NON-NLS-1$

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			JobsImageRegistry.disposeAllImages();
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
		JobsMessages.get();
	}
}
