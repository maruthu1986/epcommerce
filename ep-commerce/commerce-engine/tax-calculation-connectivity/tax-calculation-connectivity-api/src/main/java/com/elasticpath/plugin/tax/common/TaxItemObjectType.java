/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.common;

/**
 * Identifies the EP domain object to which a tax item is related.
 */
public enum TaxItemObjectType {

	/**
	 * ORDER_SKU.
	 */
	ORDER_SKU("Order SKU"),
	
	/**
	 * ORDER_SHIPMENT.
	 */
	ORDER_SHIPMENT("Order Shipment"),
	
	/**
	 * ORDER_RETURN_SKU.
	 */
	ORDER_RETURN_SKU("Order Return SKU"),
	
	/**
	 * ORDER_RETURN.
	 */
	ORDER_RETURN("Order Return");

	private final String name;
	
	/**
	 * Constructor.
	 * 
	 * @param name the String representation of the enumeration element
	 */
	TaxItemObjectType(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
}