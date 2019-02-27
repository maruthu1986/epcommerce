/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.cmclient.core.comparator.AttributeValueComparatorByOrderingAndNameIgnoreCase;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Product;

/**
 * A helper class that returns a product's attribute values sorted by ordering (if available). 
 */
public class ProductAttributeValueSorter {
	
	private AttributeValue[] attributeValues;
	private final Map<Attribute, Integer> attributeOrderingMap = new HashMap<>();

	/**
	 * Instantiates a new product attribute value sorter.
	 *
	 * @param product the product
	 * @param locale the locale
	 */
	public ProductAttributeValueSorter(final Product product, final Locale locale) {
		createAttributeToOrderingMap(product);
		retrieveAttributeDataFromProduct(product, locale);
	}

	private void retrieveAttributeDataFromProduct(final Product product, final Locale locale) {
		List<AttributeValue> fullAttributeValues = product.getFullAttributeValues(locale);
		Collections.sort(fullAttributeValues, new AttributeValueComparatorByOrderingAndNameIgnoreCase(attributeOrderingMap));
		attributeValues = fullAttributeValues.toArray(new AttributeValue[fullAttributeValues.size()]);
	}

	private void createAttributeToOrderingMap(final Product product) {
		final Set<AttributeGroupAttribute> attributeGroupAttributes =
				product.getProductType().getProductAttributeGroup().getAttributeGroupAttributes();
		for (AttributeGroupAttribute attrGrpAttr : attributeGroupAttributes) {
			attributeOrderingMap.put(attrGrpAttr.getAttribute(), attrGrpAttr.getOrdering());
		}
	}

	/**
	 * Gets the product's ordered attribute values.
	 *
	 * @return the ordered attribute values
	 */
	public AttributeValue[] getOrderedAttributeValues() {
		return attributeValues; // NOPMD
	}
	
}
