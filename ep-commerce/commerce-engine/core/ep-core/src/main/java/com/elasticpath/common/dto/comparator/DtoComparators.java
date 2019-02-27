/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.common.dto.comparator;

import java.util.Comparator;

import com.elasticpath.common.dto.PropertyDTO;
import com.elasticpath.common.dto.tax.TaxCategoryDTO;
import com.elasticpath.common.dto.tax.TaxRegionDTO;

/**
 * Comparator classes for DTOs.
 */
public final class DtoComparators {

	/**
	 * Prevent this class from being instantiated.
	 */
	private DtoComparators() {
		// Do nothing
	}

	/**
	 * Comparator for sorting PropertyDTO objects before exporting.
	 */
	public static final Comparator<PropertyDTO> PROPERTY_DTO_COMPARATOR = Comparator
			.comparing(PropertyDTO::getPropertyKey);

	/**
	 * Comparator for sorting TaxCategoryDTO objects before exporting.
	 */
	public static final Comparator<TaxCategoryDTO> TAX_CATEGORY_DTO_COMPARATOR = Comparator
			.comparing(TaxCategoryDTO::getGuid);

	/**
	 * Comparator for sorting TaxRegionDTO objects before exporting.
	 */
	public static final Comparator<TaxRegionDTO> TAX_REGION_DTO_COMPARATOR = Comparator
			.comparing(TaxRegionDTO::getRegionName);
}
