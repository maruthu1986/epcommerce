/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;

/**
 * Repository that searches for cart identifier given order identifier.
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class OrderToCartLinkRepository<I extends OrderIdentifier, LI extends CartIdentifier>
		implements LinksRepository<OrderIdentifier, CartIdentifier> {

	private OrderRepository orderRepository;

	@Override
	public Observable<CartIdentifier> getElements(final OrderIdentifier identifier) {
		IdentifierPart<String> scope = identifier.getScope();
		String orderId = identifier.getOrderId().getValue();

		return orderRepository.getOrderByOrderId(scope.getValue(), orderId)
				.map(cartOrder -> buildCartIdentifier(scope, cartOrder))
				.toObservable();
	}

	/**
	 * Builds Cart identifier.
	 *
	 * @param scope scope
	 * @param cartOrder cartOrder
	 * @return cart identifier
	 */
	protected CartIdentifier buildCartIdentifier(final IdentifierPart<String> scope, final CartOrder cartOrder) {
		return CartIdentifier.builder()
				.withScope(scope)
				.withCartId(cartOrder::getShoppingCartGuid)
				.build();
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
}
