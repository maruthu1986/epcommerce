/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shoppingcart;

import java.math.BigDecimal;

/**
 * The ShoppingItemTaxSnapshot holds transient calculated tax information for a
 * {@link com.elasticpath.domain.shoppingcart.ShoppingItem ShoppingItem}.
 */
public interface ShoppingItemTaxSnapshot {

	/**
	 * Returns the amount of tax for this shopping item.
	 * @return the amount of tax, as a BigDecimal
	 */
	BigDecimal getTaxAmount();

	/**
	 * Retrieves a tax-aware price calculator. For tax-exclusive stores, this calculator will
	 * include the taxes; for tax-inclusive stores, this calculator will exclude the taxes.
	 *
	 * Use the {@link ShoppingItemPricingSnapshot} for other cases.
	 *
	 * @return a {@link TaxPriceCalculator}
	 */
	TaxPriceCalculator getTaxPriceCalculator();

	/**
	 * Get the regular {@link ShoppingItemPricingSnapshot} that ignores taxes.
	 *
	 * @return a {@link ShoppingItemPricingSnapshot}
	 */
	ShoppingItemPricingSnapshot getPricingSnapshot();

}
