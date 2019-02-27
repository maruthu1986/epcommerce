/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.comparator;

import java.util.Comparator;

import com.elasticpath.domain.attribute.Attribute;

/**
 * Sorts attributes by name (ignore case).
 */
public class AttributeComparatorByNameIgnoreCase implements Comparator<Attribute> {

	@Override
	public int compare(final Attribute attribute1, final Attribute attribute2) {
		if (attribute1 == null || attribute2 == null 
				|| attribute1.getName() == null || attribute2.getName() == null) {
			return 1;
		}
		return attribute1.getName().compareToIgnoreCase(attribute2.getName());
	}

}
