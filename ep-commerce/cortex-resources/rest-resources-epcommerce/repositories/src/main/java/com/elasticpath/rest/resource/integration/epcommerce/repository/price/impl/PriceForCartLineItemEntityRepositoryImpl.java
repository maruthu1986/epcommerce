/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl.ShoppingCartRepositoryImpl.LINEITEM_WAS_NOT_FOUND;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceForCartLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Repository that implements reading price for a cart lineitem.
 *
 * @param <E> extends CartLineItemPriceEntity
 * @param <I> extends PriceForCartLineItemIdentifier
 */
@Component
public class PriceForCartLineItemEntityRepositoryImpl<E extends CartLineItemPriceEntity, I extends PriceForCartLineItemIdentifier>
		implements Repository<CartLineItemPriceEntity, PriceForCartLineItemIdentifier> {

	private MoneyWrapperTransformer moneyWrapperTransformer;
	private CartOrderRepository cartOrderRepository;
	private PricingSnapshotRepository pricingSnapshotRepository;
	private ReactiveAdapter reactiveAdapter;

	@Override
	public Single<CartLineItemPriceEntity> findOne(final PriceForCartLineItemIdentifier priceForCartLineItemIdentifier) {
		CartIdentifier cartIdentifier = priceForCartLineItemIdentifier.getLineItem().getLineItems().getCart();
		String cartGuid = cartIdentifier.getCartId().getValue();
		String cartScope = cartIdentifier.getScope().getValue();
		String lineItemId = priceForCartLineItemIdentifier.getLineItem().getLineItemId().getValue();
		return getShoppingItemPricingSnapshotSingle(cartGuid, cartScope, lineItemId)
				.flatMap(this::buildCartLineItemPriceEntitySingle);
	}

	private Single<CartLineItemPriceEntity> buildCartLineItemPriceEntitySingle(final ShoppingItemPricingSnapshot shoppingItemPricingSnapshot) {
		PriceCalculator priceCalculator = shoppingItemPricingSnapshot.getPriceCalc().forUnitPrice();
		Money listPrice = shoppingItemPricingSnapshot.getListUnitPrice();
		Money purchasePrice = priceCalculator.withCartDiscounts().getMoney();
		return getCartLineItemPriceEntitySingle(listPrice, purchasePrice);
	}

	private Single<CartLineItemPriceEntity> getCartLineItemPriceEntitySingle(final Money listPrice, final Money purchasePrice) {
		MoneyWrapper lineItemPrice = new MoneyWrapper();
		lineItemPrice.setListPrice(listPrice);
		lineItemPrice.setPurchasePrice(purchasePrice);
		return Single.just(moneyWrapperTransformer.transformToEntity(lineItemPrice));
	}

	private Single<ShoppingItemPricingSnapshot> getShoppingItemPricingSnapshotSingle(final String cartGuid, final String cartScope,
																					 final String lineItemId) {
		return cartOrderRepository.getEnrichedShoppingCartSingle(cartScope, cartGuid, CartOrderRepository.FindCartOrder.BY_CART_GUID)
				.flatMap(shoppingCart -> pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart)
						.flatMap(cartPricingSnapshot -> getShoppingItemPricingSnapshotSingle(lineItemId, shoppingCart, cartPricingSnapshot)));
	}

	private Single<ShoppingItemPricingSnapshot> getShoppingItemPricingSnapshotSingle(final String lineItemId, final ShoppingCart shoppingCart,
																					 final ShoppingCartPricingSnapshot cartPricingSnapshot) {
		return reactiveAdapter.fromNullableAsSingle(() -> cartPricingSnapshot
				.getShoppingItemPricingSnapshot(shoppingCart.getShoppingItemByGuid(lineItemId)), String.format(LINEITEM_WAS_NOT_FOUND, lineItemId));
	}

	@Reference
	public void setMoneyWrapperTransformer(final MoneyWrapperTransformer moneyWrapperTransformer) {
		this.moneyWrapperTransformer = moneyWrapperTransformer;
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Reference
	public void setPricingSnapshotRepository(final PricingSnapshotRepository pricingSnapshotRepository) {
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}
}
