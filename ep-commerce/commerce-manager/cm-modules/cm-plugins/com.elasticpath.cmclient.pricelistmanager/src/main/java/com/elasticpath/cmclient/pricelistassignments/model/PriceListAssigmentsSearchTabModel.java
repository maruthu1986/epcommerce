/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistassignments.model;

/**
 * 
 * Search criteria for price list assignment. 
 *
 */
public class PriceListAssigmentsSearchTabModel {
	
	private String priceListName;
	
	private String catalogName;

	/**
	 * @return price list name
	 */
	public String getPriceListName() {
		return priceListName;
	}

	/**
	 * @param priceListName price list name
	 */
	public void setPriceListName(final String priceListName) {
		this.priceListName = priceListName;
	}

	/**
	 * @return catalog name
	 */
	public String getCatalogName() {
		return catalogName;
	}

	/**
	 * @param catalogName catalog name
	 */
	public void setCatalogName(final String catalogName) {
		this.catalogName = catalogName;
	}
	
	

}
