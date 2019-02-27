/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.comparator;

import java.util.Comparator;
import java.util.Map;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;

/**
 * Sorts {@link AttributeValue}s by their ordering. If their ordering are same, use the key of {@link Attribute} for comparison.  
 * If no ordering is available, it sorts by attribute names in ascending order.
 */
public class AttributeValueComparatorByOrderingAndNameIgnoreCase extends AttributeValueComparatorByNameIgnoreCase implements
		Comparator<AttributeValue> {

	/** The attribute ordering. */
	private final Map<Attribute, Integer> attributeOrdering;

	/**
	 * Instantiates a new attribute value comparator by ordering and name ignore case.
	 *
	 * @param attributeOrderingMap the attribute ordering map
	 */
	public AttributeValueComparatorByOrderingAndNameIgnoreCase(final Map<Attribute, Integer> attributeOrderingMap) {
		this.attributeOrdering = attributeOrderingMap;
	}
	
	@Override
	public int compare(final AttributeValue attrVal1, final AttributeValue attrVal2) {
		
		if (attributesComparableByOrder(attrVal1, attrVal2)) {
			return compareByAttributeOrder(attrVal1, attrVal2);
		}
		
		return super.compare(attrVal1, attrVal2);
	}

	private int compareByAttributeOrder(final AttributeValue attrVal1, final AttributeValue attrVal2) {
		int compareResult = attributeOrdering.get(attrVal1.getAttribute()).compareTo(attributeOrdering.get(attrVal2.getAttribute()));
		if (compareResult == 0) {
			return attrVal1.getAttribute().getKey().compareTo(attrVal2.getAttribute().getKey());
		}
		return compareResult;
	}
	
	private boolean attributesComparableByOrder(final AttributeValue attrVal1, final AttributeValue attrVal2) {
		
		if (orderingMapContainsBothAttributes(attrVal1, attrVal2) && orderingDataIsUsableForSorting(attrVal1, attrVal2)) {
			return true;
		}
		
		return false;
	}

	private boolean orderingMapContainsBothAttributes(final AttributeValue attrVal1, final AttributeValue attrVal2) {
		return attributeOrdering.containsKey(attrVal1.getAttribute()) && attributeOrdering.containsKey(attrVal2.getAttribute());
	}
	
	private boolean orderingDataIsUsableForSorting(final AttributeValue attrVal1, final AttributeValue attrVal2) {
		return attributeOrdering.get(attrVal1.getAttribute()).intValue() > 0 || attributeOrdering.get(attrVal2.getAttribute()).intValue() > 0;
	}

}
