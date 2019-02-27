/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;

/**
 * Repository that searches for order identifier given cart identifier.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class CartToOrderLinkRepositoryImpl<I extends CartIdentifier, LI extends OrderIdentifier>
		implements LinksRepository<CartIdentifier, OrderIdentifier> {

	private static final String CART_WAS_NOT_FOUND = "No cart was found with GUID = ";

	private ShoppingCartRepository shoppingCartRepository;

	private CartOrderRepository cartOrderRepository;

	@Override
	public Observable<OrderIdentifier> getElements(final CartIdentifier identifier) {
		IdentifierPart<String> scope = identifier.getScope();
		String cartId = identifier.getCartId().getValue();

		return getOrderByCartId(scope.getValue(), cartId)
				.map(cartOrder -> buildOrderIdentifier(scope, cartOrder))
				.toObservable();
	}

	/**
	 * Get cart order given cart and store ids.
	 *
	 * @param scope store code
	 * @param cartId cart guid
	 * @return cart order
	 */
	protected Single<CartOrder> getOrderByCartId(final String scope, final String cartId) {
		return shoppingCartRepository.verifyShoppingCartExistsForStore(cartId, scope)
				.flatMap(cartExists -> cartExists ? cartOrderRepository.findByCartGuidSingle(cartId)
						: Single.error(ResourceOperationFailure.notFound(CART_WAS_NOT_FOUND + cartId)));
	}

	/**
	 * Builds Order identifier.
	 *
	 * @param store scope/store
	 * @param cartOrder cart order
	 * @return order identifier
	 */
	protected OrderIdentifier buildOrderIdentifier(final IdentifierPart<String> store, final CartOrder cartOrder) {
		return OrderIdentifier.builder()
				.withScope(store)
				.withOrderId(cartOrder::getGuid)
				.build();
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}
}
