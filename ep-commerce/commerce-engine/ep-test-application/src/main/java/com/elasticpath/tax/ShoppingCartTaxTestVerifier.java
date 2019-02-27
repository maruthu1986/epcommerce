/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.tax;

import org.junit.Assert;

import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * Test class for Shopping Cart Tax related tests. Verifies that the tax calculations on inflight Shopping Carts are correct.
 */
public class ShoppingCartTaxTestVerifier {

	/**
	 * Verifies that the calculated tax values in the {@link ShoppingCartPricingSnapshot} matches the expected number.
	 * @param shoppingCartTaxSnapshot the {@link ShoppingCartTaxSnapshot} to check
	 * @param numberOfTaxValuesExpected the number of tax values expected.
	 */
	public void verifyTaxCalculationValues(final ShoppingCartTaxSnapshot shoppingCartTaxSnapshot, final int numberOfTaxValuesExpected) {
		final TaxCalculationResult taxCalculation = shoppingCartTaxSnapshot.getTaxCalculationResult();

		Assert.assertEquals("The number of tax values did not match the number expected;",
				numberOfTaxValuesExpected, taxCalculation.getTaxMap().size());
	}

	/**
	 * Verifies that the calculated tax categories in the {@link ShoppingCartPricingSnapshot} matches the expected number.
	 * @param shoppingCartTaxSnapshot the {@link ShoppingCartTaxSnapshot} to check
	 * @param numberOfTaxCategoriesExpected the number of tax categories expected.
	 */
	public void verifyTaxCalculationCategories(final ShoppingCartTaxSnapshot shoppingCartTaxSnapshot, final int numberOfTaxCategoriesExpected) {
		final TaxCalculationResult taxCalculation = shoppingCartTaxSnapshot.getTaxCalculationResult();

		Assert.assertEquals("The number of tax categories did not match the number expected;",
				numberOfTaxCategoriesExpected, taxCalculation.getTaxCategoriesSet().size());
	}

}