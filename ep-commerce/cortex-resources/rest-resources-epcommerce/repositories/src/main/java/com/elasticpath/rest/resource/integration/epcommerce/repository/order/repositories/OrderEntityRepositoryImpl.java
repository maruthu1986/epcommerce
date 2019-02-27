/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.cartorder.CartOrderService;

/**
 * Finds order entity given it's identifier.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class OrderEntityRepositoryImpl<E extends OrderEntity, I extends OrderIdentifier>
		implements Repository<OrderEntity, OrderIdentifier> {

	private ResourceOperationContext resourceOperationContext;
	private ShoppingCartRepository shoppingCartRepository;
	private OrderRepository orderRepository;
	private ReactiveAdapter reactiveAdapter;
	private CartOrderService coreCartOrderService;

	@Override
	public Single<OrderEntity> findOne(final OrderIdentifier identifier) {
		String store = identifier.getScope().getValue();
		String orderId = identifier.getOrderId().getValue();

		return orderRepository.getOrderByOrderId(store, orderId)
				.map(this::transformToEntity);
	}

	@Override
	public Observable<OrderIdentifier> findAll(final IdentifierPart<String> scope) {
		String userId = resourceOperationContext.getUserIdentifier();

		return shoppingCartRepository.findAllCarts(userId, scope.getValue())
				.flatMap(cartId -> reactiveAdapter.fromService(() -> coreCartOrderService.findByShoppingCartGuid(cartId)))
				.map(cartOrder -> OrderIdentifier.builder()
						.withOrderId(cartOrder::getGuid)
						.withScope(scope)
						.build());
	}

	/**
	 * Transform order to the entity.
	 *
	 * @param cartOrder cart order
	 * @return order entity
	 */
	protected OrderEntity transformToEntity(final CartOrder cartOrder) {
		return OrderEntity.builder()
				.withOrderId(cartOrder.getGuid())
				.withCartId(cartOrder.getShoppingCartGuid())
				.build();
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}

	@Reference
	public void setCoreCartOrderService(final CartOrderService coreCartOrderService) {
		this.coreCartOrderService = coreCartOrderService;
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
}
