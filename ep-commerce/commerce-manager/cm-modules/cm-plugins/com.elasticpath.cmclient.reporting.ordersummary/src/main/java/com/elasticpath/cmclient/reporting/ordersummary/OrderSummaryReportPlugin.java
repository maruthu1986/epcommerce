/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */

package com.elasticpath.cmclient.reporting.ordersummary;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class OrderSummaryReportPlugin extends AbstractUIPlugin {
	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.reporting.ordersummary"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public OrderSummaryReportPlugin() {
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
	public static OrderSummaryReportPlugin getDefault() {
		return CmSingletonUtil.getSessionInstance(OrderSummaryReportPlugin.class);
	}
}
