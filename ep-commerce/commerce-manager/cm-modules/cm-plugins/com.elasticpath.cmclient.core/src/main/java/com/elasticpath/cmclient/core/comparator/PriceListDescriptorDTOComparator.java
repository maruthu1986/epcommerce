/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.comparator;

import java.util.Comparator;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
/**
 * 
 * Comparator for {@link PriceListDescriptorDTO} class.
 *
 */
public class PriceListDescriptorDTOComparator implements Comparator<PriceListDescriptorDTO> {

	/**
	 * Compare two {@link PriceListDescriptorDTO} objects.
	 * @param descriptor1 - left object to compare
	 * @param descriptor2 - right object to compare
	 * @return int - result of compare
	 */
	public int compare(final PriceListDescriptorDTO descriptor1, final PriceListDescriptorDTO descriptor2) {
		return descriptor1.getName().compareTo(descriptor2.getName());
	}
	

}
