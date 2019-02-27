package com.elasticpath.selenium.domainobjects;

/**
 * DST class.
 */
public class DST {

	private String priceListName;
	private String changeSetGuid;
	private String productName;
	private String productCode;

	public String getPriceListName() {
		return priceListName;
	}

	public void setPriceListName(final String priceListName) {
		this.priceListName = priceListName;
	}

	public String getChangeSetGuid() {
		return changeSetGuid;
	}

	public void setChangeSetGuid(final String changeSetGuid) {
		this.changeSetGuid = changeSetGuid;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(final String productName) {
		this.productName = productName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(final String productCode) {
		this.productCode = productCode;
	}
}
