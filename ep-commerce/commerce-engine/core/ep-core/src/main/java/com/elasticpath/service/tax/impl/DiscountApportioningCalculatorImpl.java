/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.tax.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.tax.DiscountApportioningCalculator;

/**
 * This class wraps up the discount apportioning functionality.  Normal usage is to 
 * construct a new instance and call <code>apportionDiscountToShoppingItems</code> and then
 * use <code>getShoppingItemDiscount</code> to retrieve the discount for a particular 
 * <code>ShoppingItem</code>. 
 */
public class DiscountApportioningCalculatorImpl extends ApportioningCalculatorImpl implements DiscountApportioningCalculator {

	@Override
	public Map<String, BigDecimal> apportionDiscountToShoppingItems(
			final Money discount, final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap) {
		final Collection<ShoppingItem> discountableShoppingItems = getDiscountableShoppingItems(shoppingItemPricingSnapshotMap.keySet());
		verifySkus(discountableShoppingItems);

		final Map<String, BigDecimal> sortedPriceMap = createSortedPriceMap(shoppingItemPricingSnapshotMap);

		return calculateApportionedAmounts(discount.getAmount(), sortedPriceMap);
	}

	@Override
	protected BigDecimal calculateProportion(final BigDecimal amountToApportion,
			final BigDecimal portion, final BigDecimal total) {
		BigDecimal proportion = super.calculateProportion(amountToApportion, portion, total);
		
		return proportion.min(proportion);
	}
	
	@Override
	public Map<String, BigDecimal> calculateApportionedAmounts(
			final BigDecimal amountToApportion, final Map<String, BigDecimal> amounts) {
		
		verifyInput(amountToApportion, sumAmounts(amounts.values()));
		return super.calculateApportionedAmounts(amountToApportion, amounts);
	}

	private void verifySkus(
			final Collection<ShoppingItem> discountableShoppingItems) {
		for (ShoppingItem item : discountableShoppingItems) {
			ProductSku productSku = getProductSkuLookup().findByGuid(item.getSkuGuid());
			if (productSku == null) {
				throw new EpDomainException(
				"Attempting to calculate apportioned value for ShoppingItem with no product sku.");
			}
		}
	}
	
	@Override
	protected BigDecimal positiveAdjustment(final BigDecimal amountBeforeApportioning, final BigDecimal amountAfterApportioning,
			final BigDecimal remainingRoundingError) {
		BigDecimal amountAfterAdjustment = amountAfterApportioning.add(remainingRoundingError);
		if (amountAfterAdjustment.compareTo(amountBeforeApportioning) > 0) {
			BigDecimal excess = amountAfterAdjustment.subtract(amountBeforeApportioning);
			return remainingRoundingError.subtract(excess);
		}

		return remainingRoundingError;
	}

	/**
	 * From the supplied collections of ShoppingItems finds the subset which may have apportioning applied to
	 * them, and returns them in a new collection.
	 * @param shoppingItems items
	 * @return discountable shopping items
	 */
	Collection<ShoppingItem> getDiscountableShoppingItems(final Collection<? extends ShoppingItem> shoppingItems) {
		final Collection<ShoppingItem> discountableShoppingItems = new ArrayList<>();
		for (ShoppingItem shoppingItem : shoppingItems) {
			//On Order exchange wizard tax calculation may be invoked 
			//on item without unit price
			if (!shoppingItem.hasPrice() || !shoppingItem.isDiscountable(getProductSkuLookup())) {
				continue;
			}
			discountableShoppingItems.add(shoppingItem);
		}
		return selectApportionableItems(discountableShoppingItems);
	}

	private void verifyInput(final BigDecimal amountToApportion,
			final BigDecimal total) {
		if (amountToApportion.compareTo(total) > 0) {
			throw new IllegalArgumentException("The amount value ("
					+ amountToApportion.toPlainString()
					+ ") is greater than total amount value ("
					+ total.toPlainString() + ")");
		}
	}
}

