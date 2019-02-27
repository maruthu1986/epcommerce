/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.impl;

import static java.lang.String.format;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shipping.transformers.visitors.ShippableItemPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemBuilderPopulator;

/**
 * Standard visitor implementation to populate a {@link com.elasticpath.shipping.connectivity.dto.ShippableItem}
 * from a {@link ShoppingItem}.
 *
 * Used by {@link com.elasticpath.service.shipping.transformers.impl.ShippableItemTransformerImpl}.
 */
public class ShippableItemPopulatorVisitorImpl implements ShippableItemPopulatorVisitor {
	private ProductSkuLookup productSkuLookup;

	@Override
	public void accept(final ShoppingItem shoppingItem, final ShippableItemBuilderPopulator populator) {
		final ProductSku productSku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());

		if (productSku == null) {
			throw new IllegalArgumentException(format("Cannot find corresponding ProductSku with GUID '%s' for ShoppingItem with GUID '%s'",
													  shoppingItem.getSkuGuid(), shoppingItem.getGuid()));
		}

		populator.withQuantity(shoppingItem.getQuantity())
				.withSkuGuid(shoppingItem.getSkuGuid())
				.withWeight(productSku.getWeight())
				.withHeight(productSku.getHeight())
				.withWidth(productSku.getWidth())
				.withLength(productSku.getLength());
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return this.productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}
}
