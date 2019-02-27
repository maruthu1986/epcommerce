/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.comparator;

import java.util.Comparator;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;

/**
 * AttributeValue comparator, sorts objects by their attribute names in ascending order.
 */
public class AttributeValueComparatorByNameIgnoreCase implements Comparator<AttributeValue> {

	/**
	 * Compares two AttributeValue objects by there attributes' names.
	 * 
	 * @param attrVal1 first attribute value
	 * @param attrVal2 second attribute value
	 * 
	 * @return a negative integer, zero, or a positive integer as the first AttributeValue name 
	 * is less than, equal to, or greater than the second ignoring the case 
	 */
	public int compare(final AttributeValue attrVal1, final AttributeValue attrVal2) {
		
		final Attribute attribute1 = attrVal1.getAttribute();
		final Attribute attribute2 = attrVal2.getAttribute();
		
		if (attribute1 == null || attribute2 == null 
				|| attribute1.getName() == null || attribute2.getName() == null) {
			return 1;
		}
		
		String name1 = attribute1.getName();
		String name2 = attribute2.getName();
		
		if (name1 == null || name2 == null) {
			return 1;
		}
		
		return name1.compareToIgnoreCase(name2);
	}

}
