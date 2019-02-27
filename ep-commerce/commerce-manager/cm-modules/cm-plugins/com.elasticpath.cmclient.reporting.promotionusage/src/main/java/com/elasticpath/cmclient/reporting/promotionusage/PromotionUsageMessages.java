/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.promotionusage;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the report plugin.
 *
 */
@SuppressWarnings("PMD.VariableNamingConventions")
public final class PromotionUsageMessages {

	private static final String BUNDLE_NAME = 
		"com.elasticpath.cmclient.reporting.promotionusage.PromotionUsageMessages"; //$NON-NLS-1$
	
	private PromotionUsageMessages() {
	}

	public String reportTitle;
	public String report;
	
	public String selectStore;
	public String store;
	public String fromdate;
	public String todate;
	public String noCurrency;
	public String promotype;
	public String promotypeCart;
	public String promotypeCatalog;
	public String onlyWithCoupons;

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static PromotionUsageMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, PromotionUsageMessages.class);
	}

}
