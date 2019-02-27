/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.catalog;


/**
 * Verifies key names for newly created sku options and sku option values.
 */
public interface SkuOptionNameVerifier {

	
	/**
	 * Checks for existing sku option keys like the one passed as parameter.
	 * 
	 * @param newValue the new value
	 * @return true if value is ok
	 */
	boolean verifySkuOptionKey(String newValue);
	
	/**
	 * Checks for existing sku option value keys like the one passed as parameter.
	 * 
	 * @param newValue the new value
	 * @return true if value is ok
	 */
	boolean verifySkuOptionValueKey(String newValue);
}
