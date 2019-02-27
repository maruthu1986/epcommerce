/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.stockallocation;

import com.elasticpath.cmclient.core.nls.LocalizedMessageNLS;
import org.eclipse.osgi.util.NLS;

/**
 * Messages class for the report plugin.
 *
 */
public final class StockAllocationReportMessages {

	private static final String BUNDLE_NAME =
		"com.elasticpath.cmclient.reporting.stockallocation.StockAllocationReportPluginResources"; //$NON-NLS-1$

	private StockAllocationReportMessages() {
	}

	public static String reportTitle;

	public static String report;

	// ----------------------------------------------------
	// Stock Allocation Report params UI
	// ----------------------------------------------------
	public static String store;

	public static String skuCode;

	public static String skuAvailabilityRule;

	public static String backAndPreOrders;

	public static String preOrderOnly;

	public static String backOrderOnly;

	public static String allStores;

	public static String skuToolTip;

	// ----------------------------------------------------
	// Stock Allocation Report service
	// ----------------------------------------------------
	static {
		load();
	}

	/**
	 * loads localized messages for this plugin.
	 */
	public static void load() {
	    LocalizedMessageNLS.getUTF8Encoded(BUNDLE_NAME, StockAllocationReportMessages.class);
	}

}
