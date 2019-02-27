/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.shipping.transformers.BaseShippableItemContainerTransformer;
import com.elasticpath.service.shipping.transformers.ShippableItemContainerTransformer;
import com.elasticpath.service.shipping.transformers.ShippableItemTransformer;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;

/**
 * Adapts {@link ShoppingCart} to {@link ShippableItemContainer}.
 */
public class ShippableItemContainerTransformerImpl implements ShippableItemContainerTransformer {

	private Predicate<ShoppingItem> shippableItemPredicate;
	private ShippableItemTransformer shippableItemTransformer;
	private BaseShippableItemContainerTransformer<ShippableItemContainer<ShippableItem>, ShippableItem> baseTransformer;

	@Override
	public ShippableItemContainer<ShippableItem> apply(final ShoppingCart shoppingCart) {
		final Stream<ShippableItem> shippableItems = filterShoppingItems(shoppingCart)
				.map(shippableItemTransformer);

		return getBaseTransformer().apply(shoppingCart, shippableItems);
	}

	/**
	 * Filters the given {@link ShoppingCart} object's shopping items using {@link #getShippableItemPredicate()}.
	 *
	 * @param shoppingCart the cart to get and filter its shopping items.
	 * @return a filtered Stream of {@link ShoppingItem} objects.
	 */
	protected Stream<ShoppingItem> filterShoppingItems(final ShoppingCart shoppingCart) {
		requireNonNull(shoppingCart, "Shopping Cart is required.");

		return shoppingCart.getApportionedLeafItems().stream()
				.filter(getShippableItemPredicate());
	}

	protected Predicate<ShoppingItem> getShippableItemPredicate() {
		return this.shippableItemPredicate;
	}

	public void setShippableItemPredicate(final Predicate<ShoppingItem> shippableItemPredicate) {
		this.shippableItemPredicate = shippableItemPredicate;
	}

	protected ShippableItemTransformer getShippableItemTransformer() {
		return this.shippableItemTransformer;
	}

	public void setShippableItemTransformer(final ShippableItemTransformer shippableItemTransformer) {
		this.shippableItemTransformer = shippableItemTransformer;
	}

	protected BaseShippableItemContainerTransformer<ShippableItemContainer<ShippableItem>, ShippableItem> getBaseTransformer() {
		return this.baseTransformer;
	}

	public void setBaseTransformer(final BaseShippableItemContainerTransformer<ShippableItemContainer<ShippableItem>, ShippableItem>
										   baseTransformer) {
		this.baseTransformer = baseTransformer;
	}
}
