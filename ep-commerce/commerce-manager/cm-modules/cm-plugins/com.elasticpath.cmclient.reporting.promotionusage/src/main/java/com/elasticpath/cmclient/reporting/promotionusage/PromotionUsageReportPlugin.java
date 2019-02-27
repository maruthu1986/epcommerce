/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.promotionusage;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class PromotionUsageReportPlugin extends AbstractEpUIPlugin {
	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.reporting.promotionusage"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public PromotionUsageReportPlugin() {
		// empty
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	protected void loadLocalizedMessages() {
		PromotionUsageMessages.get();
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
	public static PromotionUsageReportPlugin getDefault() {
		return CmSingletonUtil.getSessionInstance(PromotionUsageReportPlugin.class);
	}
}
