/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.util.Objects.requireNonNull;

import java.util.Currency;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.shipping.ShippableItemsPricing;
import com.elasticpath.service.shipping.impl.ShippableItemsPricingImpl;
import com.elasticpath.service.shipping.transformers.BaseShippableItemContainerTransformer;
import com.elasticpath.service.shipping.transformers.PricedShippableItemContainerTransformer;
import com.elasticpath.service.shipping.transformers.PricedShippableItemsTransformer;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;

/**
 * Implementation of {@link PricedShippableItemContainerTransformer}.
 */
public class PricedShippableItemContainerTransformerImpl implements PricedShippableItemContainerTransformer {

	private Predicate<ShoppingItem> shippableItemPredicate;
	private PricedShippableItemsTransformer shippableItemsTransformer;
	private BaseShippableItemContainerTransformer<PricedShippableItemContainer<PricedShippableItem>, PricedShippableItem> baseTransformer;

	@Override
	public PricedShippableItemContainer<PricedShippableItem> apply(final ShoppingCart shoppingCart,
																   final ShoppingCartPricingSnapshot cartPricingSnapshot) {
		final ShippableItemsPricing shippableItemsPricing = createShippableItemsPricing(shoppingCart, cartPricingSnapshot);

		final Stream<PricedShippableItem> pricedShippableItems = getShippableItemsTransformer()
				.apply(shoppingCart.getApportionedLeafItems(), shippableItemsPricing);

		return getBaseTransformer().apply(shoppingCart, pricedShippableItems);
	}

	/**
	 * Factory method to create a corresponding {@link ShippableItemsPricing} object for the given shopping cart and pricing snapshot.
	 *
	 * @param shoppingCart the shopping cart to generate from.
	 * @param cartPricingSnapshot the pricing snapshot to generate from.
	 * @return a corresponding {@link ShippableItemsPricing} object for the given shopping cart and pricing snapshot.
	 */
	protected ShippableItemsPricing createShippableItemsPricing(final ShoppingCart shoppingCart,
																final ShoppingCartPricingSnapshot cartPricingSnapshot) {
		final Currency currency = shoppingCart.getShopper().getCurrency();
		final Money subtotalDiscount = cartPricingSnapshot.getSubtotalDiscountMoney();

		final Function<ShoppingItem, ShoppingItemPricingSnapshot> pricingFunction
				= shoppingItem -> getShoppingItemPricingSnapshot(shoppingItem, cartPricingSnapshot);

		final Predicate<ShoppingItem> shippableItemPredicate = getShippableItemPredicate();
		requireNonNull(shippableItemPredicate, "Shippable Item Predicate must not be null");

		return new ShippableItemsPricingImpl(currency, subtotalDiscount, pricingFunction, shippableItemPredicate);
	}

	/**
	 * Returns the corresponding {@link ShoppingItemPricingSnapshot} for the given {@link ShoppingItem} from the given
	 * {@link ShoppingCartPricingSnapshot}.
	 *
	 * @param shoppingItem the shopping item to retrieve the {@link ShoppingItemPricingSnapshot} for.
	 * @param pricingSnapshot the overall shopping cart pricing snapshot to retrieve the {@link ShoppingItemPricingSnapshot} from.
	 * @return the corresponding {@link ShoppingItemPricingSnapshot} for the given {@link ShoppingItem}.
	 */
	protected ShoppingItemPricingSnapshot getShoppingItemPricingSnapshot(final ShoppingItem shoppingItem,
																		 final ShoppingCartPricingSnapshot pricingSnapshot) {
		return pricingSnapshot.getShoppingItemPricingSnapshot(shoppingItem);
	}

	protected Predicate<ShoppingItem> getShippableItemPredicate() {
		return this.shippableItemPredicate;
	}

	public void setShippableItemPredicate(final Predicate<ShoppingItem> shippableItemPredicate) {
		this.shippableItemPredicate = shippableItemPredicate;
	}

	protected PricedShippableItemsTransformer getShippableItemsTransformer() {
		return this.shippableItemsTransformer;
	}

	public void setShippableItemsTransformer(final PricedShippableItemsTransformer shippableItemsTransformer) {
		this.shippableItemsTransformer = shippableItemsTransformer;
	}

	protected BaseShippableItemContainerTransformer<PricedShippableItemContainer<PricedShippableItem>, PricedShippableItem> getBaseTransformer() {
		return this.baseTransformer;
	}

	public void setBaseTransformer(final BaseShippableItemContainerTransformer<PricedShippableItemContainer<PricedShippableItem>,
			PricedShippableItem> baseTransformer) {
		this.baseTransformer = baseTransformer;
	}
}
