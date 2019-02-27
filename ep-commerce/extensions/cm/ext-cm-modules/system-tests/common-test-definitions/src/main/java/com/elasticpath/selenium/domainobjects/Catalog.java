package com.elasticpath.selenium.domainobjects;

/**
 * Catalog class.
 */
public class Catalog {
	private String catalogName;
	private String language;
	private String brand;
	private String attributeKey;
	private String attributeName;
	private String attributeUsage;
	private String attributeType;
	private boolean attributeRequired;


	public String getAttributeKey() {
		return attributeKey;
	}

	public void setAttributeKey(final String attributeKey) {
		this.attributeKey = attributeKey;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(final String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeUsage() {
		return attributeUsage;
	}

	public void setAttributeUsage(final String attributeUsage) {
		this.attributeUsage = attributeUsage;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(final String attributeType) {
		this.attributeType = attributeType;
	}

	public boolean isAttributeRequired() {
		return attributeRequired;
	}

	public void setAttributeRequired(final boolean attributeRequired) {
		this.attributeRequired = attributeRequired;
	}


	public String getLanguage() {
		return language;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(final String brand) {
		this.brand = brand;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(final String catalogName) {
		this.catalogName = catalogName;
	}
}
