/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.editors.price.model;

import java.math.BigDecimal;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.domain.catalog.ProductBundle;

/**
 * The root of the PriceAdjustmentModel. Only the root needs to know the currency and priceListGuid.   
 * 
 */
public class PriceAdjustmentModelRoot extends PriceAdjustmentModel {
	private PriceListDescriptorDTO priceListDescriptorDto;

	/**
	 * Constructs just the root node of the tree model with the bundle. Constituents can be added
	 * with <code>addChild</code>.
	 * 
	 * @param bundle bundle
	 * @param selectionParameter selectionParameter
	 * @param price price
	 * @param priceListDescriptorDto priceListDescriptor
	 */
	public PriceAdjustmentModelRoot(final ProductBundle bundle, final int selectionParameter, final BigDecimal price, 
			final PriceListDescriptorDTO priceListDescriptorDto) {
		setProduct(bundle);
		setSelectionParameter(selectionParameter);
		setPrice(price);
		this.priceListDescriptorDto = priceListDescriptorDto;
	}

	@Override
	public ProductBundle getProduct() {
		return (ProductBundle) super.getProduct();
	}

	/**
	 * @param priceListDescriptorDto the priceListDescriptorDto to set
	 */
	public void setPriceListDescriptorDto(final PriceListDescriptorDTO priceListDescriptorDto) {
		this.priceListDescriptorDto = priceListDescriptorDto;
	}

	/**
	 * @return the priceListDescriptorDto
	 */
	public PriceListDescriptorDTO getPriceListDescriptorDto() {
		return priceListDescriptorDto;
	}
}