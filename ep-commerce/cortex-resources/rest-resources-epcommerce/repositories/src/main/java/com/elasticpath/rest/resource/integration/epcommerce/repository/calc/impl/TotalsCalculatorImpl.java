/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc.impl;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository.FindCartOrder.BY_CART_GUID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository.FindCartOrder.BY_ORDER_GUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Single;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TotalsCalculator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;

/**
 * <p>
 * The TotalsCalculator is responsible calculating ShoppingCart related totals.
 * </p>
 * <p>
 * Since totals include discounts, please use the enriched ShoppingCart for all
 * totals calculations.  See {@link CartOrderRepository#getEnrichedShoppingCart(String, String,
 *                                    com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository.FindCartOrder)}.
 * NOTE: Please do not call ShoppingCartImpl.fireRules() directly.  Enriched cart will handle this for you.
 * </p>
 * <p>
 * Also see {@link TaxesCalculatorImpl} which performs a similar role for tax calculations.
 * </p>
 */
@Singleton
@Named("totalsCalculator")
public class TotalsCalculatorImpl implements TotalsCalculator {

	private final CartOrderRepository cartOrderRepository;

	private final PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Constructor.
	 *
	 * @param cartOrderRepository       the CartOrderRepository
	 * @param pricingSnapshotRepository the pricing snapshot repository
	 */
	@Inject
	TotalsCalculatorImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("pricingSnapshotRepository") final PricingSnapshotRepository pricingSnapshotRepository) {
		this.cartOrderRepository = cartOrderRepository;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Override
	public Single<Money> calculateTotalForShoppingCart(final String storeCode, final String shoppingCartGuid) {
		return cartOrderRepository.getEnrichedShoppingCartSingle(storeCode, shoppingCartGuid, BY_CART_GUID)
				.flatMap(pricingSnapshotRepository::getShoppingCartPricingSnapshotSingle)
				.map(ShoppingCartPricingSnapshot::getSubtotalMoney);
	}

	@Override
	public Single<Money> calculateTotalForCartOrder(final String storeCode, final String cartOrderGuid) {
		return cartOrderRepository.getEnrichedShoppingCartSingle(storeCode, cartOrderGuid, BY_ORDER_GUID)
				.flatMap(pricingSnapshotRepository::getShoppingCartTaxSnapshot)
				.map(ShoppingCartTaxSnapshot::getTotalMoney);
	}

	@Override
	public ExecutionResult<Money> calculateSubTotalForCartOrder(final String storeCode, final String cartOrderGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				final ShoppingCart shoppingCart = Assign.ifSuccessful(
						cartOrderRepository.getEnrichedShoppingCart(storeCode, cartOrderGuid, BY_ORDER_GUID));
				final ShoppingCartPricingSnapshot pricingSnapshot = Assign.ifSuccessful(
						pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart));

				return ExecutionResultFactory.createReadOK(pricingSnapshot.getSubtotalMoney());
			}
		}.execute();
	}

	@Override
	public Single<Money> calculateSubTotalForCartOrderSingle(final String storeCode, final String cartOrderGuid) {
		return cartOrderRepository.getEnrichedShoppingCartSingle(storeCode, cartOrderGuid, BY_ORDER_GUID)
				.flatMap(pricingSnapshotRepository::getShoppingCartPricingSnapshotSingle)
				.map(ShoppingCartPricingSnapshot::getSubtotalMoney);
	}

	@Override
	public Single<Money> calculateTotalForLineItem(final String storeCode, final String shoppingCartGuid, final String cartItemGuid) {
		return cartOrderRepository.getEnrichedShoppingCartSingle(storeCode, shoppingCartGuid, BY_CART_GUID)
				.flatMap(shoppingCart -> getLineItemTotalFromShoppingCart(cartItemGuid, shoppingCart));
	}

	/**
	 * Get the line item total from the ShoppingCart.
	 *
	 * @param cartItemGuid cartItemGuid
	 * @param shoppingCart shoppingCart
	 * @return the line item total
	 */
	protected Single<Money> getLineItemTotalFromShoppingCart(final String cartItemGuid, final ShoppingCart shoppingCart) {
		return pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart)
				.map(shoppingCartPricingSnapshot ->
						getLineItemTotalFromCartPricingSnapshot(cartItemGuid, shoppingCart, shoppingCartPricingSnapshot));
	}

	/**
	 * Get the line item total from the ShoppingCartPricingSnapshot.
	 *
	 * @param cartItemGuid                cartItemGuid
	 * @param shoppingCart                shoppingCart
	 * @param shoppingCartPricingSnapshot shoppingCartPricingSnapshot
	 * @return the line item total
	 */
	protected Money getLineItemTotalFromCartPricingSnapshot(final String cartItemGuid,
															final ShoppingCart shoppingCart,
															final ShoppingCartPricingSnapshot shoppingCartPricingSnapshot) {
		return shoppingCartPricingSnapshot
				.getShoppingItemPricingSnapshot(shoppingCart.getCartItemByGuid(cartItemGuid))
				.getPriceCalc()
				.withCartDiscounts()
				.getMoney();
	}
}