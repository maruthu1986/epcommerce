/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.registration;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class ReportingRegistrationPlugin extends AbstractUIPlugin {
	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.reporting.registration"; //$NON-NLS-1$

	// The shared instance
	private static ReportingRegistrationPlugin plugin;

	/**
	 * The constructor.
	 */
	public ReportingRegistrationPlugin() {
		// empty
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static ReportingRegistrationPlugin getDefault() {
		return plugin;
	}
}
