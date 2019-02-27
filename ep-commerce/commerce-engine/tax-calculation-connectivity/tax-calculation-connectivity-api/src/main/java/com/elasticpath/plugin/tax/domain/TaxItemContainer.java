/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain;

import java.util.Currency;
import java.util.List;

/**
 * Interface for a container of {@link TaxableItem}s.
 */
public interface TaxItemContainer {
	
	/**
	 * Gets a list of taxable items.
	 *
	 * @return a list of taxable items
	 */
	List<? extends TaxableItem> getItems();
	
	/**
	 * Gets the store code.
	 *
	 * @return the store code
	 */
	String getStoreCode();
	
	/**
	 * Gets the currency.
	 *
	 * @return the currency
	 */
	Currency getCurrency();
	
	/**
	 * Gets the destination tax address.
	 *
	 * @return the destination address to base tax calculations on
	 */
	TaxAddress getDestinationAddress();

	/**
	 * Gets the origin address (warehouse).
	 *
	 * @return the origin address to base tax calculations on
	 */
	TaxAddress getOriginAddress();
	
	/**
	 * Returns whether tax calculations are inclusive.
	 *
	 * @return true if this container is based on a tax inclusive jurisdiction
	 */
	boolean isTaxInclusive();
}
