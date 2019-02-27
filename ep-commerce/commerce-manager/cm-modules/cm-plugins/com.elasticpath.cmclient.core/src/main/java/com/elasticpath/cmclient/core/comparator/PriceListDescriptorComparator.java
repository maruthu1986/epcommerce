/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.comparator;

import java.util.Comparator;

import com.elasticpath.domain.pricing.PriceListDescriptor;

/**
 * Comparator for {@link PriceListDescriptor}.
 *
 */
public class PriceListDescriptorComparator implements Comparator<PriceListDescriptor> {

	/**
	 * Compares two {@link PriceListDescriptor} objects.
	 * @param object1 - left object to compare
	 * @param object2 - right object to compare
	 * @return int - result of compare 
	 */
	public int compare(final PriceListDescriptor object1, final PriceListDescriptor object2) {
		return object1.getName().compareToIgnoreCase(object2.getName());
	}

}
