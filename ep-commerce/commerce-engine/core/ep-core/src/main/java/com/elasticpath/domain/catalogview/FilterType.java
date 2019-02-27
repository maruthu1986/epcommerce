/*
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.catalogview;

/**
 * Represents the filter type.
 *
 */
public enum FilterType {
	
	/**
	 * The <code>FilterType</code> instance for {@link AttributeValueFilter}.
	 */
	ATTRIBUTE_FILTER("attribute"),

	/**
	 * The <code>FilterType</code> instance for {@link AttributeRangeFilter}.
	 */
	ATTRIBUTE_RANGE_FILTER("attributeRange"),
	
	/**
	 * The <code>FilterType</code> instance for {@link AttributeKeywordFilter}.
	 */
	ATTRIBUTE_KEYWORD_FILTER("attributeKeyword"),
	
	/**
	 * The <code>FilterType</code> instance for {@link PriceFilter}.
	 */
	PRICE_FILTER("price"),
	
	/**
	 * The <code>FilterType</code> instance for {@link BrandFilter}.
	 */
	BRAND_FILTER("brands");

	
	private String propertyKey = "";

	/**
	 * Constructor.
	 * 
	 * @param propertyKey the property key.
	 */
	FilterType(final String propertyKey) {
		this.propertyKey = propertyKey;
	}

	/**
	 * Get the property key.
	 * 
	 * @return the property key
	 */
	public String getPropertyKey() {
		return this.propertyKey;
	}
	
}
