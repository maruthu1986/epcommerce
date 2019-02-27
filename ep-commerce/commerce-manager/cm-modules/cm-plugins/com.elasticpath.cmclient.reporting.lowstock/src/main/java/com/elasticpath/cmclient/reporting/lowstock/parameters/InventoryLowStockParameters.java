/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.lowstock.parameters;

import java.util.Locale;

/**
 * Model for low stock report's parameters.
 *  
 */
public class InventoryLowStockParameters {
	
	private String warehouse;
	
	private String skuCode;
	
	private String brand;
	
	private Locale locale;

	/**
	 * Gets the warehouse name.
	 * @return warehouse's name
	 */
	public String getWarehouse() {
		return warehouse;
	}

	/**
	 * Sets the warehouse name.
	 * @param warehouse the name of the warehouse
	 */
	public void setWarehouse(final String warehouse) {
		this.warehouse = warehouse;
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
	 * Gets brand.
	 * @return brand name
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * Sets the brand name.
	 * @param brand the name of the brand
	 */
	public void setBrand(final String brand) {
		this.brand = brand;
	}

	/**
	 * Get yhe locale.
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Sets the locale.
	 * @param locale the locale to set
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

}
