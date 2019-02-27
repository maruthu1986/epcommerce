/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.dto.catalog.impl;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

import java.util.Collection;
import java.util.List;

/**
 * Represents product sku price list model.
 */
public class ProductSkuPriceListModelImpl extends PriceListEditorModelImpl {

	private static final long serialVersionUID = 12348912357L;

	/**
	 * Constructs product sku list model.
	 * 
	 * @param priceListDescriptor price list descriptor
	 * @param baseAmounts base amounts
	 */
	public ProductSkuPriceListModelImpl(final PriceListDescriptorDTO priceListDescriptor, final Collection<BaseAmountDTO> baseAmounts) {
		super(priceListDescriptor, baseAmounts);
	}

	@Override
	public List<BaseAmountDTO> getBaseAmounts() {
		final List<BaseAmountDTO> filterBaseAmounts = filterBaseAmounts("SKU", super.getBaseAmounts());
		if (filterBaseAmounts.isEmpty()) {
			return super.getBaseAmounts();
		}
		return filterBaseAmounts;
	}

	/**
	 * @return number of product sku base amounts
	 */
	public boolean hasProductSkuBaseAmounts() {
		return !filterBaseAmounts("SKU").isEmpty();
	}

	@Override
	public int getNumberOfBaseAmounts() {
		return getBaseAmounts().size();
	}

	@Override
	public BaseAmountDTO getBaseAmountDTO(final int qty) {
		return getBaseAmountDTO(getBaseAmounts(), qty);
	}
}
