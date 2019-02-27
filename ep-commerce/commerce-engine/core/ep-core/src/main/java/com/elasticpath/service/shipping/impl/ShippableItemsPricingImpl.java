/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.impl;

import java.util.Currency;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.shipping.ShippableItemsPricing;

/**
 * Default implementation of {@link ShippableItemsPricing}.
 */
public class ShippableItemsPricingImpl implements ShippableItemsPricing {
	private final Currency currency;
	private final Money subtotalDiscount;
	private final Function<ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingFunction;
	private final Predicate<ShoppingItem> shippableItemPredicate;

	/**
	 * Constructor which doesn't use an optional {@link Predicate} to filter the {@link ShoppingItem}s with.
	 *
	 * @param currency the currency the items are priced in.
	 * @param subtotalDiscount the subtotal discount currently applied.
	 * @param shoppingItemPricingFunction a {@link Function} to retrieve the corresponding {@link ShoppingItemPricingSnapshot} from a given
	 * {@link ShoppingItem}.
	 */
	public ShippableItemsPricingImpl(final Currency currency, final Money subtotalDiscount,
									 final Function<ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingFunction) {
		this(currency, subtotalDiscount, shoppingItemPricingFunction, null);
	}

	/**
	 * Constructor which provides an optional {@link Predicate} to filter the {@link ShoppingItem}s with.
	 *
	 * @param currency the currency the items are priced in.
	 * @param subtotalDiscount the subtotal discount currently applied.
	 * @param shoppingItemPricingFunction a {@link Function} to retrieve the corresponding {@link ShoppingItemPricingSnapshot} from a given
	 * {@link ShoppingItem}.
	 * @param shippableItemPredicate a {@link Predicate} to filter all {@link ShoppingItem} objects before generating corresponding
	 * {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItem}.
	 * @see com.elasticpath.service.shipping.transformers.impl.PricedShippableItemsTransformerImpl for where the {@link Predicate} is used.
	 */
	public ShippableItemsPricingImpl(final Currency currency, final Money subtotalDiscount,
									 final Function<ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingFunction,
									 final Predicate<ShoppingItem> shippableItemPredicate) {
		this.currency = currency;
		this.subtotalDiscount = subtotalDiscount;
		this.shoppingItemPricingFunction = shoppingItemPricingFunction;
		this.shippableItemPredicate = shippableItemPredicate;
	}

	@Override
	public Currency getCurrency() {
		return this.currency;
	}

	@Override
	public Money getSubtotalDiscount() {
		return this.subtotalDiscount;
	}

	@Override
	public Function<ShoppingItem, ShoppingItemPricingSnapshot> getShoppingItemPricingFunction() {
		return this.shoppingItemPricingFunction;
	}

	@Override
	public Optional<Predicate<ShoppingItem>> getShippableItemPredicate() {
		return Optional.ofNullable(shippableItemPredicate);
	}
}
