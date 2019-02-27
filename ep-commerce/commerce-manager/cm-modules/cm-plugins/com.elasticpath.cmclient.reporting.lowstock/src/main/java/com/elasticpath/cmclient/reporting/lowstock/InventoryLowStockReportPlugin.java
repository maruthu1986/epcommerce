/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.lowstock;

import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class InventoryLowStockReportPlugin extends AbstractEpUIPlugin {
	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.reporting.lowstock"; //$NON-NLS-1$

	// The shared instance
	private static InventoryLowStockReportPlugin plugin;

	/**
	 * The constructor.
	 */
	public InventoryLowStockReportPlugin() {
		// empty
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	protected void loadLocalizedMessages() {
		InventoryLowStockReportMessages.load();
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
	public static InventoryLowStockReportPlugin getDefault() {
		return plugin;
	}
}

