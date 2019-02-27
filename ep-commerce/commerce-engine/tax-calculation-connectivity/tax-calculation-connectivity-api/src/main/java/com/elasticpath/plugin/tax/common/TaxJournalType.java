/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.common;

/**
 * Tax journal type enumeration. This identifies whether tax journal entries represent a purchase or the reversal of a
 * previous purchase. Changes to an item are recorded by a reversal of the previous purchase plus a new purchase.
 */
public enum TaxJournalType {

	/**
	 * Purchase.
	 */
	PURCHASE("purchase"),
	
	/**
	 * Reversal.
	 */
	REVERSAL("reversal");

	private final String name;
	
	/**
	 * Constructor.
	 * 
	 * @param name the String representation of the enumeration element
	 */
	TaxJournalType(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
}