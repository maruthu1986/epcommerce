/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shoppingcart;

import java.math.BigDecimal;
import java.util.Map;

import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.money.Money;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * The ShoppingCartPricingSnapshot holds transient calculated taxation information for
 * a {@link com.elasticpath.domain.shoppingcart.ShoppingCart ShoppingCart}.
 */
public interface ShoppingCartTaxSnapshot {

	/**
	 * Retrieves the current tax calculation result if there is any, or returns a new initialized instance of {@link TaxCalculationResult}.
	 * @return the current tax values
	 */
	TaxCalculationResult getTaxCalculationResult();

	/**
	 * Retrieves the mapping of TaxCategories to values.
	 * @return the map of TaxCategories to values. Never <code>null</code>.
	 */
	Map<TaxCategory, Money> getTaxMap();

	/**
	 * Return the localized tax category name -> tax value (<code>Money</code>) map for this <code>ShoppingCart</code>.
	 *
	 * @return the localized tax category name -> tax value (<code>Money</code>) map.
	 */
	Map<String, Money> getLocalizedTaxMap();


	/**
	 * Get the sub total of all items in the cart after shipping, promotions, etc.
	 *
	 * @return a <code>Money</code> object representing the total
	 */
	Money getTotalMoney();

	/**
	 * Get the sub total of all items in the cart after shipping, promotions, etc.
	 *
	 * @return a <code>BigDecimal</code> object representing the total
	 */
	BigDecimal getTotal();

	/**
	 * Get the {@link ShoppingCartPricingSnapshot} that was used to calculate this tax snapshot.
	 *
	 * @return the Shopping Cart Pricing Snapshot
	 */
	ShoppingCartPricingSnapshot getShoppingCartPricingSnapshot();

	/**
	 * Given a ShoppingItem, returns the corresponding {@link ShoppingItemTaxSnapshot}.
	 * If no such snapshot exists, throws an EpServiceException.
	 *
	 * @param item a shopping item
	 * @return the corresponding ShoppingItemTaxSnapshot
	 * @throws com.elasticpath.base.exception.EpServiceException if the corresponding snapshot does not exist
	 */
	ShoppingItemTaxSnapshot getShoppingItemTaxSnapshot(ShoppingItem item);

}
