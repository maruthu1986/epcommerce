/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Single;

import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TaxesCalculator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * This class hides all ShoppingCartImpl.fireRules(). Other classes in the epcommerce integration should not directly nor
 * transitively call fireRules under the punishment of poor performance. {@link TotalsCalculatorImpl} has the same
 * responsibility, but for the totals calculations.
 */
@Singleton
@Named("taxesCalculator")
public class TaxesCalculatorImpl implements TaxesCalculator {

	private final CartOrderRepository cartOrderRepository;
	private final PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Constructor.
	 *
	 * @param cartOrderRepository the CartOrderRepository
	 * @param pricingSnapshotRepository the pricing snapshot service
	 */
	@Inject
	public TaxesCalculatorImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("pricingSnapshotRepository") final PricingSnapshotRepository pricingSnapshotRepository) {
		this.cartOrderRepository = cartOrderRepository;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Override
	public Single<TaxCalculationResult> calculateTax(final String storeCode, final String cartOrderGuid) {
		return cartOrderRepository.getEnrichedShoppingCartSingle(storeCode, cartOrderGuid, CartOrderRepository.FindCartOrder.BY_ORDER_GUID)
				.flatMap(pricingSnapshotRepository::getShoppingCartTaxSnapshot)
				.map(ShoppingCartTaxSnapshot::getTaxCalculationResult);
	}

}
