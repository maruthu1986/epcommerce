/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.promotiondetails;

import com.elasticpath.cmclient.core.nls.LocalizedMessageNLS;
import org.eclipse.osgi.util.NLS;

/**
 * Messages class for the report plugin.
 *
 */
@SuppressWarnings("PMD.VariableNamingConventions")
public final class PromotionDetailsMessages {

	private static final String BUNDLE_NAME = 
		"com.elasticpath.cmclient.reporting.promotiondetails.PromotionDetailsMessages"; //$NON-NLS-1$
	
	private PromotionDetailsMessages() {
	}

	public static String reportTitle;
	public static String report;
	
	public static String selectAll;
	public static String selectCurrency;
	public static String store;
	public static String currency;
	public static String fromdate;
	public static String todate;
	public static String Promotion;
	public static String CouponCode;
	public static String selectAStore;

	static {
		load();
	}

	/**
	 * loads localized messages for this plugin.
	 */
	public static void load() {
		LocalizedMessageNLS.getUTF8Encoded(BUNDLE_NAME, PromotionDetailsMessages.class);
	}
	
}
