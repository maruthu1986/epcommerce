/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.attribute.impl;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;

/**
 * AttributeValueFactory that creates ProductAttributeValueImpl objects.
 */
public class SkuAttributeValueFactoryImpl extends AbstractAttributeValueFactoryImpl {

	private static final long serialVersionUID = 670L;

	/**
	 * Creates a SkuAttributeValueImpl for the given attribute and localizedAttributeKey.
	 *
	 * @param attribute the metadata object for this attributeValue
	 * @param localizedAttributeKey the locale-specific attribute key for this value
	 * @return the attribute value
	 */
	@Override
	public AttributeValue createAttributeValue(final Attribute attribute, final String localizedAttributeKey) {
		SkuAttributeValueImpl value = new SkuAttributeValueImpl();
		populateAttributeValue(value, attribute, localizedAttributeKey);

		return value;
	}

}
