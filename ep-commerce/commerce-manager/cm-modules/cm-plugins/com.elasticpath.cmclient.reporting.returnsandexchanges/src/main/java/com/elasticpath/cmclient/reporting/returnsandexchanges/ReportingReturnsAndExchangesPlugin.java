/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.returnsandexchanges;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.CmSingletonUtil;

/**
 * The activator class controls the plug-in life cycle.
 */
public class ReportingReturnsAndExchangesPlugin extends AbstractUIPlugin {
	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.reporting.returnsandexchanges"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public ReportingReturnsAndExchangesPlugin() {
		// empty
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 * @param context the context
	 * @throws Exception on error
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 * @param context the context
	 * @throws Exception on error
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static ReportingReturnsAndExchangesPlugin getDefault() {
		return CmSingletonUtil.getSessionInstance(ReportingReturnsAndExchangesPlugin.class);
	}
}
