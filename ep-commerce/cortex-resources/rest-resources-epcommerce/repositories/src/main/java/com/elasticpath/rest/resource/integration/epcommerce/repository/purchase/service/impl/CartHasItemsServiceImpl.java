/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.service.impl;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.StructuredErrorMessageIdConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.service.CartHasItemsService;
import com.elasticpath.rest.schema.StructuredMessageTypes;

/**
 * Implementation of the order purchasable service.
 */
@Component
public class CartHasItemsServiceImpl implements CartHasItemsService {

	private static final String CART_IS_EMPTY = "Shopping cart must not be empty during checkout.";

	private CartOrderRepository cartOrderRepository;

	@Override
	public Observable<Message> validateCartHasItems(final OrderIdentifier order) {
		return isCartEmpty(order).flatMapObservable(this::createMessage);
	}

	@Override
	public Single<Boolean> isCartEmpty(final OrderIdentifier order) {
		String storeCode = order.getScope().getValue();
		String cartOrderGuid = order.getOrderId().getValue();

		Single<ShoppingCart> shoppingCartSingle = cartOrderRepository.findByGuidAsSingle(storeCode, cartOrderGuid)
				.flatMap(cartOrder -> getShoppingCart(StringIdentifier.of(storeCode), cartOrder));

		return shoppingCartSingle.map(ShoppingCart::isEmpty);
	}

	/**
	 * Creates message if the cart for the order is empty.
	 * Otherwise form is not blocked.
	 *
	 * @param isCartEmpty flag for order purchase
	 * @return message when not purchasable
	 */
	protected Observable<Message> createMessage(final boolean isCartEmpty) {
		if (isCartEmpty) {
			return Observable.just(Message.builder()
					.withType(StructuredMessageTypes.ERROR)
					.withId(StructuredErrorMessageIdConstants.CART_NOT_PURCHASABLE)
					.withDebugMessage(CART_IS_EMPTY)
					.build());
		}
		return Observable.empty();
	}

	/**
	 * Get the shopping cart.
	 *
	 * @param scope     scope
	 * @param cartOrder cartOrder
	 * @return the shopping cart
	 */
	protected Single<ShoppingCart> getShoppingCart(final IdentifierPart<String> scope, final CartOrder cartOrder) {
		return cartOrderRepository.getEnrichedShoppingCartSingle(scope.getValue(), cartOrder);
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}
}
