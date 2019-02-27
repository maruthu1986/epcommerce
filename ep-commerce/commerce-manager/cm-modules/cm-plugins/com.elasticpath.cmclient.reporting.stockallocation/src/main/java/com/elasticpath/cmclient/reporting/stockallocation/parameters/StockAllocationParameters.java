/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.stockallocation.parameters;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Model for low stock report's parameters.
 *  
 */
public class StockAllocationParameters {
	
	private Collection<String> storeNames = new ArrayList<String>();
	
	private String skuCode;
	
	private int skuAvailRule;
	
	/** SkuAvailabilityRule code meaning "Available for pre-order only. */
	public static final int AVAIL_PRE_ORDER_ONLY = 1;
	/** SkuAvailabilityRule code meaning "Available for back-order only. */
	public static final int AVAIL_BACK_ORDER_ONLY = 2;
	/** SkuAvailabilityRule code meaning "Available for both pre-order and back-order. */
	public static final int AVAIL_PRE_BACK_ORDER = 3;
	
	/**
	 * Gets the names of the stores for which to run the report.
	 * @return the names of the stores
	 */
	public Collection<String> getStoreNames() {
		return this.storeNames;
	}

	/**
	 * Sets the names of the stores for which to run the report.
	 * @param storeNames the names of the stores
	 */
	public void setStoreNames(final Collection<String> storeNames) {
		this.storeNames = storeNames;
	}

	/**
	 * Gets the sku code.
	 * @return the sku code
	 */
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * Sets the sku code.
	 * @param skuCode the sku code
	 */
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * Gets Sku Availability rule code, corresponding to public static final in this class.
	 * @return the availability rule code
	 */
	public int getSkuAvailRule() {
		return skuAvailRule;
	}

	/**
	 * Sets the Availability rule, corresponding to public static final in this class.
	 * @param skuAvailRule the availability rule code
	 */
	public void setSkuAvailRule(final int skuAvailRule) {
		this.skuAvailRule = skuAvailRule;
	}

}
