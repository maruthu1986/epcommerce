/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.shipping.ShippableItemPricing;
import com.elasticpath.service.shipping.ShippableItemsPricing;
import com.elasticpath.service.shipping.impl.ShippableItemPricingImpl;
import com.elasticpath.service.shipping.transformers.PricedShippableItemTransformer;
import com.elasticpath.service.shipping.transformers.PricedShippableItemsTransformer;
import com.elasticpath.service.tax.DiscountApportioningCalculator;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;

/**
 * Default implementation of {@link PricedShippableItemsTransformer}.
 */
public class PricedShippableItemsTransformerImpl implements PricedShippableItemsTransformer {
	private PricedShippableItemTransformer pricedShippableItemTransformer;
	private DiscountApportioningCalculator discountApportioningCalculator;

	@Override
	public Stream<PricedShippableItem> apply(final Collection<? extends ShoppingItem> allShoppingItems,
											 final ShippableItemsPricing shippableItemsPricing) {
		requireNonNull(allShoppingItems, "ShoppingItem collection is required, even if empty.");
		requireNonNull(shippableItemsPricing, "ShippableItemsPricing is required.");

		final Currency currency = shippableItemsPricing.getCurrency();
		requireNonNull(currency, "The Currency must be provided on the ShippableItemsPricing object.");

		// Calculate the input map required by DiscountApportioningCalculator - apportion across all items not just shippable items
		final Map<String, BigDecimal> apportionedSubTotalDiscounts = apportionSubtotalDiscount(allShoppingItems, shippableItemsPricing);

		// But not all the items may be shippable so we if we're provided with a filter we filter them return a Stream of PricedShippableItems
		// If not we use all the ShoppingItems given - used when handling PhysicalOrderShipment's OrderSkus which are all by definition shippable
		Stream<? extends ShoppingItem> shippableShoppingItems = allShoppingItems.stream();

		final Optional<Predicate<ShoppingItem>> shippableItemPredicate = shippableItemsPricing.getShippableItemPredicate();
		if (shippableItemPredicate.isPresent()) {
			shippableShoppingItems = allShoppingItems.stream()
					.filter(shippableItemPredicate.get());
		}

		return shippableShoppingItems
				.map(shoppingItem -> pricedShippableItemTransformer.apply(shoppingItem,
																		  createShippableItemPricing(shoppingItem, shippableItemsPricing,
																									 apportionedSubTotalDiscounts, currency)));
	}

	/**
	 * Apportions any subtotal discount found by {@link ShippableItemsPricing#getSubtotalDiscount()} across all the {@link ShoppingItem} objects
	 * provided. Delegates to a {@link DiscountApportioningCalculator} to do the apportioning.
	 *
	 * If there is no subtotal discount then an empty map is returned instead.
	 *
	 * @param allShoppingItems all {@link ShoppingItem} objects to apportion the discount over. It's important that all applicable
	 * {@link ShoppingItem} objects are provided here and not just the shippable ones as otherwise the calculated apportions will be incorrect.
	 * @param shippableItemsPricing information on the subtotal discount to apportion as well as the currency in use.
	 * @return a map of apportioned subtotal discounts by shopping item guid, or an empty map if there is no subtotal discount to apportion.
	 */
	protected Map<String, BigDecimal> apportionSubtotalDiscount(final Collection<? extends ShoppingItem> allShoppingItems,
																final ShippableItemsPricing shippableItemsPricing) {
		final Money zero = Money.zero(shippableItemsPricing.getCurrency());
		final Money subtotalDiscount = Optional.ofNullable(shippableItemsPricing.getSubtotalDiscount())
				.orElse(zero);

		// If there's no subtotal discount to apportion, then just return an empty map, as that's sufficient for our purposes here
		if (subtotalDiscount.equals(zero)) {
			return new HashMap<>();
		}

		final Map<ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap = allShoppingItems.stream()
				.collect(Collectors.toMap(identity(), shippableItemsPricing.getShoppingItemPricingFunction()));

		// Now delegate to DiscountApportioningCalculator to apportion the subtotal discount
		return getDiscountApportioningCalculator()
				.apportionDiscountToShoppingItems(subtotalDiscount, shoppingItemPricingSnapshotMap);
	}

	/**
	 * Factory method to create a {@link ShippableItemPricing} object for the given {@link ShoppingItem} to be passed to the
	 * {@link PricedShippableItemTransformer} in the {@link #apply(Collection, ShippableItemsPricing)} method above.
	 *
	 * @param shoppingItem the corresponding {@link ShoppingItem}.
	 * @param shippableItemsPricing the overall pricing information to use.
	 * @param apportionedSubTotalDiscounts the apportioned subtotal discounts by shopping item guid.
	 * @param currency the currency being apportioned.
	 * @return a {@link ShippableItemPricing} object containing the corresponding information for the given {@link ShoppingItem}.
	 */
	protected ShippableItemPricing createShippableItemPricing(final ShoppingItem shoppingItem, final ShippableItemsPricing shippableItemsPricing,
															  final Map<String, BigDecimal> apportionedSubTotalDiscounts, final Currency currency) {
		final ShoppingItemPricingSnapshot itemPricingSnapshot = shippableItemsPricing.getShoppingItemPricingFunction().apply(shoppingItem);

		final Money apportionedItemSubTotalDiscount = getApportionedItemSubtotalDiscount(shoppingItem.getGuid(), apportionedSubTotalDiscounts,
																						 currency);

		final Money apportionedItemSubTotalUnitDiscount = apportionedItemSubTotalDiscount.divide(shoppingItem.getQuantity());

		return new ShippableItemPricingImpl(itemPricingSnapshot, apportionedItemSubTotalUnitDiscount, apportionedItemSubTotalDiscount);
	}

	/**
	 * Returned the apportioned subtotal discount for an individual {@link ShoppingItem} based on its guid.
	 *
	 * @param shoppingItemGuid the shopping item guid to retrieve the apportioned subtotal discount.
	 * @param apportionedSubTotalDiscounts the apportioned subtotal discount map.
	 * @param currency the currency being apportioned.
	 * @return the apportioned subtotal discount, or a zero value if there is no discount to apportion. Never {@code null}.
	 */
	protected Money getApportionedItemSubtotalDiscount(final String shoppingItemGuid, final Map<String, BigDecimal> apportionedSubTotalDiscounts,
													   final Currency currency) {
		return Optional.ofNullable(apportionedSubTotalDiscounts.get(shoppingItemGuid))
				.map(discountAmount -> Money.valueOf(discountAmount, currency))
				.orElse(Money.zero(currency));
	}

	protected PricedShippableItemTransformer getPricedShippableItemTransformer() {
		return this.pricedShippableItemTransformer;
	}

	public void setPricedShippableItemTransformer(final PricedShippableItemTransformer pricedShippableItemTransformer) {
		this.pricedShippableItemTransformer = pricedShippableItemTransformer;
	}

	protected DiscountApportioningCalculator getDiscountApportioningCalculator() {
		return this.discountApportioningCalculator;
	}

	public void setDiscountApportioningCalculator(final DiscountApportioningCalculator discountApportioningCalculator) {
		this.discountApportioningCalculator = discountApportioningCalculator;
	}
}
