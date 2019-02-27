/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.comparators;

import java.util.Comparator;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
/**
 * 
 * Comparator for {@link PriceListDescriptorDTO} class.
 *
 */
public class PriceListDescriptorDTOComparator implements Comparator<PriceListDescriptorDTO> {

	@Override
	public int compare(final PriceListDescriptorDTO descriptor1, final PriceListDescriptorDTO descriptor2) {
		return descriptor1.getName().compareTo(descriptor2.getName());
	}
	

}
