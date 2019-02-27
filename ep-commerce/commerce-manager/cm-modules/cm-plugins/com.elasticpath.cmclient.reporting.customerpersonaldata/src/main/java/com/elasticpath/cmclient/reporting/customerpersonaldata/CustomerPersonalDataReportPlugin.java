/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.reporting.customerpersonaldata;

import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

/**
 * The activator class controls the plug-in life cycle.
 */
public class CustomerPersonalDataReportPlugin extends AbstractEpUIPlugin {
	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.reporting.customerpersonaldata"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public CustomerPersonalDataReportPlugin() {
		// empty
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	protected void loadLocalizedMessages() {
		CustomerPersonalDataMessages.get();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static CustomerPersonalDataReportPlugin getDefault() {
		return CmSingletonUtil.getSessionInstance(CustomerPersonalDataReportPlugin.class);
	}
}
