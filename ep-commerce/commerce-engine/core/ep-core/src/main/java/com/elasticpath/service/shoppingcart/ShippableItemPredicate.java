/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.shoppingcart;

import java.util.function.Predicate;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * {@link Predicate} used for identifying shippable items.
 * @param <T> ShoppingItem or subclass thereof
 */
public class ShippableItemPredicate<T extends ShoppingItem> implements Predicate<T> {

	private final ProductSkuLookup productSkuLookup;

	/**
	 * Constructor.
	 * @param productSkuLookup the {@link ProductSkuLookup} used for determining isShippable.
	 */
	public ShippableItemPredicate(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	@Override
	public boolean test(final T shoppingItem) {
		return shoppingItem.isShippable(productSkuLookup);
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return this.productSkuLookup;
	}
}
