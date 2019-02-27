/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview;

import com.elasticpath.domain.attribute.AttributeValueWithType;


/**
 * The {@link AttributeFilter} represents the filter on a specified attribute for using keyword searching.
 */
public interface AttributeKeywordFilter extends AttributeFilter<AttributeKeywordFilter> {

	/** property key for attribute value. */
	String ATTRIBUTE_KEYWORD_PROPERTY = "attributeKeyword";

	/**
	 * Gets the {@link AttributeValueWithType} of this filter.
	 *
	 * @return the {@link AttributeValueWithType} of this filter
	 */
	AttributeValueWithType getAttributeValue();

	/**
	 *
	 * @param attributeValue the attributeValue to set
	 */
	void setAttributeValue(AttributeValueWithType attributeValue);
	
	/**
	 * Set the attribute value.
	 * The given value should be a String, and it should be convert to the AttributeValue.
	 * @param attributeValue the attributeValue to set
	 */
	void setAttributeValueFromString(String attributeValue);

	/**
	 * Sets the display name that will be shown when describing the AttributeKeywordFilter. Typically
	 * this will be used as the label for the guided navigation link. A blank value will
	 * raise an IllegalArgumentException.
	 *
	 * @param displayName the display name for this AttributeValueFilter.
	 */
	void setDisplayName(String displayName);
}
