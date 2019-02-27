/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.common;


/**
 * Tax transaction type enumeration. This identifies the business operation that triggered the tax calculation.
 */
public enum TaxTransactionType {

	/**
	 * ORDER.
	 */
	ORDER("Order"),
	
	/**
	 * ORDER_CANCEL.
	 */
	ORDER_CANCEL("Order Cancel"),
	
	/**
	 * ORDER_CHANGE.
	 */
	ORDER_CHANGE("Order Change"),
	
	/**
	 * REVERSAL.
	 */
	RETURN("Return"),
	
	/**
	 * RETURN_CANCEL.
	 */
	RETURN_CANCEL("Return Cancel"),
	
	/**
	 * RETURN_CHANGE.
	 */
	RETURN_CHANGE("Return Change");

	private final String name;
	
	/**
	 * Constructor.
	 * 
	 * @param name the String representation of the enumeration element
	 */
	TaxTransactionType(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
}