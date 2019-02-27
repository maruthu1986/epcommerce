/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.orders.DeliveriesIdentifier;
import com.elasticpath.rest.definition.orders.DeliveryIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;

/**
 * Repository for the deliveries given the order.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class DeliveryListRepositoryImpl<I extends DeliveriesIdentifier, LI extends DeliveryIdentifier>
		implements LinksRepository<DeliveriesIdentifier, DeliveryIdentifier> {

	private static final String SHIPMENT = "SHIPMENT";

	private CartOrderRepository cartOrderRepository;
	private ShoppingCartRepository shoppingCartRepository;

	@Override
	public Observable<DeliveryIdentifier> getElements(final DeliveriesIdentifier identifier) {
		OrderIdentifier order = identifier.getOrder();
		String orderId = order.getOrderId().getValue();
		String storeCode = order.getScope().getValue();

		return cartOrderRepository.findByGuidAsSingle(storeCode, orderId)
				.map(CartOrder::getShoppingCartGuid)
				.flatMapObservable(this::getShoppingCartDeliveryTypes)
				.map(deliveryId -> getDeliveryIdentifier(order, deliveryId));
	}

	/**
	 * Get delivery types for the cart.
	 * Currently only one type is supported.
	 *
	 * @param shoppingCartId the shopping cart ID.
	 * @return delivery types
	 */
	protected Observable<String> getShoppingCartDeliveryTypes(final String shoppingCartId) {
		return shoppingCartRepository.getShoppingCart(shoppingCartId)
				.flatMapObservable(this::getShipmentTypes);
	}

	/**
	 * Get the shipment types for the cart.
	 *
	 * @param shoppingCart shoppingCart
	 * @return shipment types for the cart
	 */
	protected Observable<String> getShipmentTypes(final ShoppingCart shoppingCart) {
		//The only delivery type that exists is SHIPMENT
		return Observable.fromIterable(shoppingCart.getShipmentTypes())
				.any(shipmentType -> shipmentType.equals(ShipmentType.PHYSICAL))
				.flatMapObservable(isPhysical -> isPhysical ? Observable.just(SHIPMENT) : Observable.empty());
	}

	/**
	 * Get the delivery identifier.
	 *
	 * @param orderIdentifier orderIdentifier
	 * @param deliveryId      deliveryId
	 * @return delivery identifier
	 */
	protected DeliveryIdentifier getDeliveryIdentifier(final OrderIdentifier orderIdentifier, final String deliveryId) {
		DeliveriesIdentifier deliveries = DeliveriesIdentifier.builder()
				.withOrder(orderIdentifier)
				.build();

		return DeliveryIdentifier.builder()
				.withDeliveries(deliveries)
				.withDeliveryId(StringIdentifier.of(deliveryId))
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
