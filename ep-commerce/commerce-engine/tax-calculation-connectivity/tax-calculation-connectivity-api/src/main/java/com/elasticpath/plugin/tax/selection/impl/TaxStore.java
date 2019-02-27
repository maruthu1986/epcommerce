/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.selection.impl;

import org.apache.commons.lang.StringUtils;

/**
 * The tax calculation store.
 */
public class TaxStore {

	private String storeCode;
	
	public String getStoreCode() {
		return storeCode;
	}
	
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}
	
	/**
	 * Determines whether the tax store matches a given store.
	 * 
	 * @param storeCode the store code to be matched
	 * @return true if storeCodes match
	 */
	public boolean matches(final String storeCode) {
		return StringUtils.equalsIgnoreCase(storeCode, getStoreCode());
	}
	
}
