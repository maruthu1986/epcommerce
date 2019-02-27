/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.lowstock;

import com.elasticpath.cmclient.core.nls.LocalizedMessageNLS;
import org.eclipse.osgi.util.NLS;

/**
 * Messages class for the report plugin.
 *
 */
public final class InventoryLowStockReportMessages {

	private static final String BUNDLE_NAME = 
		"com.elasticpath.cmclient.reporting.lowstock.InventoryLowStockReportPluginResources"; //$NON-NLS-1$
	
	private InventoryLowStockReportMessages() {
	}

	public static String reportTitle;
	
	public static String report;
	
	// ----------------------------------------------------
	// low stock Report params UI
	// ----------------------------------------------------
	public static String warehouse;
	
	public static String skuCode;
	
	public static String brand;
	
	public static String locale;
	
	public static String warehouseComboFirstItem;
	
	public static String allBrands;
	
	public static String skuToolTip;
	
	// ----------------------------------------------------
	// low stock Report service
	// ----------------------------------------------------

	static {
		load();
	}

	/**
	 * loads localized messages for this plugin.
	 */
	public static void load() {
		LocalizedMessageNLS.getUTF8Encoded(BUNDLE_NAME, InventoryLowStockReportMessages.class);
	}
	
}
