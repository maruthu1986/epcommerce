/*
 * Copyright (c) Elastic Path Software Inc., 2006-2014
 */
package com.elasticpath.service.tax;

import java.util.Map;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;

/**
 * A service that will calculate the applicable taxes for a <code>ShoppingCart</code>.
 */
public interface TaxCalculationService {
	
	/**
	 * Calculates the applicable taxes on a list of items, depending on the address to which they are being billed or shipped.
	 * NOTICE: Only enabled is store tax jurisdictions should be used for calculating tax.
	 * 
	 *
	 * @param storeCode guid of the store that will be used to retrieve tax jurisdictions
	 * @param destinationAddress the address to use for tax calculations. If null, no calculations will be performed
	 * @param originAddress the origin address (warehouse) and is optional. Certain tax providers will request for this
	 * @param shippingCost the cost of shipping, so that shipping taxes can be factored in
	 * @param shoppingItemPricingSnapshotMap map of items that must be taxed and their corresponding pricing snapshots, must be non-null
	 * @param preTaxDiscount the total pre-tax discount to be applied on items, before taxes are calculated
	 * @param taxOperationContext the tax operation context
	 * @return the result of the tax calculations
	 */
	TaxCalculationResult calculateTaxes(
			String storeCode,
			TaxAddress destinationAddress,
			TaxAddress originAddress,
			Money shippingCost,
			Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap,
			Money preTaxDiscount,
			TaxOperationContext taxOperationContext);

	/**
	 * Calculates the applicable taxes on a list of items, depending on the address to which they are being billed or shipped.
	 * NOTICE: Only those tax jurisdictions enabled in the store should be used for calculating tax.
	 *
	 * @param taxCalculationResult the tax calculation result to be used to add up the taxes to
	 * @param storeCode guid of the store that will be used to retrieve tax jurisdictions.
	 * @param destinationAddress the address to use for tax calculations. If null, no calculations will be performed.
	 * @param originAddress the origin address (warehouse) and is optional. Certain tax providers will request for this
	 * @param shippingCost the cost of shipping, so that shipping taxes can be factored in, must be non-null
	 * @param shoppingItemPricingSnapshotMap map of items that must be taxed and their corresponding pricing snapshots, must be non-null
	 * @param preTaxDiscount the total pre-tax discount to be applied on items, before taxes are calculated, must be non-null
	 * @param taxOperationContext the tax operation context
	 * @return the result of the tax calculations
	 */
	// CHECKSTYLE:OFF
	TaxCalculationResult calculateTaxesAndAddToResult(
			TaxCalculationResult taxCalculationResult, 
			String storeCode,
			TaxAddress destinationAddress,
			TaxAddress originAddress,
			Money shippingCost,
			Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap,
			Money preTaxDiscount,
			TaxOperationContext taxOperationContext);
	// CHECKSTYLE:ON
}
