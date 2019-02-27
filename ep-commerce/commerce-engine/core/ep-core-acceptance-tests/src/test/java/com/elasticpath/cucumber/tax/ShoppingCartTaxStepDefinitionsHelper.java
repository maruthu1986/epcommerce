/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.tax;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.tax.ShoppingCartTaxTestVerifier;

/**
 * Helper class for {@link com.elasticpath.cucumber.tax.ShoppingCartTaxStepDefinitions}.
 */
public class ShoppingCartTaxStepDefinitionsHelper {

	@Inject
	@Named("shoppingCartHolder")
	private ScenarioContextValueHolder<ShoppingCart> shoppingCartHolder;

	@Autowired
	private PricingSnapshotService pricingSnapshotService;

	@Autowired
	private TaxSnapshotService taxSnapshotService;

	@Autowired
	private ShoppingCartTaxTestVerifier shoppingCartTaxTestVerifier;

	private ShoppingCartTaxSnapshot taxSnapshot;

	/**
	 * Requests that taxes are calculated on the current shopping cart (see {@link #getShoppingCart()}).
	 */
	public void requestTaxCalculationOnShoppingCart() {
		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(getShoppingCart());
		taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(getShoppingCart(), pricingSnapshot);
	}

	/**
	 * Delegates to the configured {@link ShoppingCartTaxTestVerifier} to verify the number of calculated tax values matches the number expected.
	 *
	 * @param numberOfTaxValuesExpected the number of calculated tax values expected.
	 */
	public void verifyTaxCalculationValues(final int numberOfTaxValuesExpected) {
		getShoppingCartTaxTestVerifier().verifyTaxCalculationValues(taxSnapshot, numberOfTaxValuesExpected);
	}

	/**
	 * Delegates to the configured {@link ShoppingCartTaxTestVerifier} to verify the number of calculated tax categories matches the number expected.
	 *
	 * @param numberOfTaxCategoriesExpected the number of calculated tax categories expected.
	 */
	public void verifyTaxCalculationCategories(final int numberOfTaxCategoriesExpected) {
		getShoppingCartTaxTestVerifier().verifyTaxCalculationCategories(taxSnapshot, numberOfTaxCategoriesExpected);
	}
	
	// Helper methods

	/**
	 * Convenience method to return the {@link ShoppingCart} from {@link #getShoppingCartHolder()}.
	 *
	 * @return the {@link ShoppingCart} from {@link #getShoppingCartHolder()}.
	 */
	protected ShoppingCart getShoppingCart() {
		return getShoppingCartHolder().get();
	}
	
	// Getters and Setters

	protected ScenarioContextValueHolder<ShoppingCart> getShoppingCartHolder() {
		return this.shoppingCartHolder;
	}

	public void setShoppingCartHolder(final ScenarioContextValueHolder<ShoppingCart> shoppingCartHolder) {
		this.shoppingCartHolder = shoppingCartHolder;
	}

	protected ShoppingCartTaxTestVerifier getShoppingCartTaxTestVerifier() {
		return this.shoppingCartTaxTestVerifier;
	}

	public void setShoppingCartTaxTestVerifier(final ShoppingCartTaxTestVerifier shoppingCartTaxTestVerifier) {
		this.shoppingCartTaxTestVerifier = shoppingCartTaxTestVerifier;
	}
}
