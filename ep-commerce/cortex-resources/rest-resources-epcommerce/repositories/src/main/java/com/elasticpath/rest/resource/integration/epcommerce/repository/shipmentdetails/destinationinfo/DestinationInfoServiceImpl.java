/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.destinationinfo;

import io.reactivex.Maybe;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsServiceImpl;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsUtil;

/**
 * Destination info service that retrieves and validates the selected address.
 */
@Component
public class DestinationInfoServiceImpl implements DestinationInfoService {

	/**
	 * Error message when shipment can't be found.
	 */
	private CartOrderRepository cartOrderRepository;
	private ShoppingCartRepository shoppingCartRepository;


	@Override
	public Maybe<String> getSelectedAddressGuidIfShippable(final String scope, final String orderId) {
		return validateOrderIsShippable(scope, orderId)
				.flatMapMaybe(shippable -> shippable ? findSelectedAddressIdForShipment(scope, orderId)
						: Maybe.error(ResourceOperationFailure.notFound(ShipmentDetailsServiceImpl.COULD_NOT_FIND_SHIPMENT)));
	}

	@Override
	public Single<Boolean> validateOrderIsShippable(final String scope, final String orderId) {
		ExecutionResult<CartOrder> cartOrder = cartOrderRepository.getCartOrder(scope, orderId, CartOrderRepository.FindCartOrder.BY_ORDER_GUID);
		return
				shoppingCartRepository.getShoppingCart(cartOrder.getData().getShoppingCartGuid())
				.map(ShipmentDetailsUtil::containsPhysicalShipment);
	}

	private Maybe<String> findSelectedAddressIdForShipment(final String scope, final String cartId) {
		return cartOrderRepository.findByGuidAsSingle(scope, cartId)
				.flatMapMaybe(cartOrder -> cartOrderRepository.getShippingAddress(cartOrder))
				.map(Address::getGuid);
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
