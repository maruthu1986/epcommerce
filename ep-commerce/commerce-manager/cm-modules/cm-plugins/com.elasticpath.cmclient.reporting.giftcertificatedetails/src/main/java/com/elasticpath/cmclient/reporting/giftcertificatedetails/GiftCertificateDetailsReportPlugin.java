/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.giftcertificatedetails;

import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class GiftCertificateDetailsReportPlugin extends AbstractEpUIPlugin {
	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.reporting.giftcertificatedetails"; //$NON-NLS-1$

	// The shared instance
	private static GiftCertificateDetailsReportPlugin plugin;

	/**
	 * The constructor.
	 */
	public GiftCertificateDetailsReportPlugin() {
		// empty
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	protected void loadLocalizedMessages() {
		GiftCertificateDetailsMessages.load();
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
	public static GiftCertificateDetailsReportPlugin getDefault() {
		return plugin;
	}
}
