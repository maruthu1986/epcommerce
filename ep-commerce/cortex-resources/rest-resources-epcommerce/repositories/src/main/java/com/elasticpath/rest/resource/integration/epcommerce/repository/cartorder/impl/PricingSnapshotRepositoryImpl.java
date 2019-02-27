/**
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Single;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;

/**
 * The facade for pricing snapshot related operations.
 */
@Singleton
@Named("pricingSnapshotRepository")
public class PricingSnapshotRepositoryImpl implements PricingSnapshotRepository {

	private static final String SNAPSHOT_NOT_FOUND = "Pricing snapshot not found for shopping cart.";
	private final PricingSnapshotService pricingSnapshotService;
	private final TaxSnapshotService taxSnapshotService;
	private final ReactiveAdapter reactiveAdapter;

	/**
	 * Constructor.
	 *
	 * @param pricingSnapshotService the pricing snapshot service
	 * @param taxSnapshotService     the tax snapshot service
	 * @param reactiveAdapter        the reactive adapter
	 */
	@Inject
	public PricingSnapshotRepositoryImpl(
			@Named("pricingSnapshotService") final PricingSnapshotService pricingSnapshotService,
			@Named("taxSnapshotService") final TaxSnapshotService taxSnapshotService,
			@Named("reactiveAdapter") final ReactiveAdapter reactiveAdapter) {

		this.pricingSnapshotService = pricingSnapshotService;
		this.taxSnapshotService = taxSnapshotService;
		this.reactiveAdapter = reactiveAdapter;
	}

	@Override
	@CacheResult
	public ExecutionResult<ShoppingCartPricingSnapshot> getShoppingCartPricingSnapshot(final ShoppingCart shoppingCart) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				return ExecutionResultFactory.createReadOK(pricingSnapshotService.getPricingSnapshotForCart(shoppingCart));
			}
		}.execute();
	}

	@Override
	@CacheResult
	public Single<ShoppingCartPricingSnapshot> getShoppingCartPricingSnapshotSingle(final ShoppingCart shoppingCart) {
		return reactiveAdapter.fromServiceAsSingle(() -> pricingSnapshotService.getPricingSnapshotForCart(shoppingCart), SNAPSHOT_NOT_FOUND);
	}

	@Override
	@CacheResult(uniqueIdentifier = "getPricingSnapshotForOrderSku")
	public Single<ShoppingItemPricingSnapshot> getPricingSnapshotForOrderSku(final OrderSku orderSku) {
		return reactiveAdapter.fromServiceAsSingle(() -> pricingSnapshotService.getPricingSnapshotForOrderSku(orderSku), SNAPSHOT_NOT_FOUND);
	}

	@Override
	@CacheResult(uniqueIdentifier = "getShoppingCartTaxSnapshot")
	public Single<ShoppingCartTaxSnapshot> getShoppingCartTaxSnapshot(final ShoppingCart shoppingCart) {
		return getShoppingCartPricingSnapshotSingle(shoppingCart)
				.flatMap(pricingSnapshot ->
						reactiveAdapter.fromServiceAsSingle(() -> taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot)));
	}

	@Override
	@CacheResult(uniqueIdentifier = "getTaxSnapshotForOrderSku")
	public Single<ShoppingItemTaxSnapshot> getTaxSnapshotForOrderSku(final OrderSku orderSku) {
		return getPricingSnapshotForOrderSku(orderSku)
				.map(pricingSnapshot -> taxSnapshotService.getTaxSnapshotForOrderSku(orderSku, pricingSnapshot));
	}
}
