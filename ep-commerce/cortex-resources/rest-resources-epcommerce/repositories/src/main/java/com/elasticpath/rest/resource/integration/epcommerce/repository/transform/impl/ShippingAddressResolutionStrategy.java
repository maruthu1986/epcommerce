/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsUtil.createShipmentDetailsId;

import io.reactivex.Maybe;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoIdentifier;
import com.elasticpath.rest.id.ResourceIdentifier;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorResolutionStrategy;
import com.elasticpath.service.shoppingcart.validation.impl.ShippingAddressShoppingCartValidatorImpl;

/**
 * Resolves resource identifier to add shipping address.
 */
public class ShippingAddressResolutionStrategy implements StructuredErrorResolutionStrategy {

	private ShoppingCartRepository shoppingCartRepository;
	private CartOrderRepository cartOrderRepository;

	/**
	 * Transforms structured error resolution into a resource identifier.
	 *
	 * @param message a structured error message
	 * @param cortexResourceID the cart Id.
	 * @return a resource identifier, or Optional.empty() if none exists.
	 */
	@Override
	public Maybe<ResourceIdentifier> getResourceIdentifier(final StructuredErrorMessage message, final String cortexResourceID) {
		if (StringUtils.equals(message.getMessageId(), ShippingAddressShoppingCartValidatorImpl.MESSAGE_ID)) {
			 return shoppingCartRepository.findStoreForCartGuid(cortexResourceID)
					.flatMapMaybe(storeCode-> cartOrderRepository.findByCartGuidSingle(cortexResourceID)
							.flatMapMaybe(cartOrder -> Maybe.just(


									buildDestinationInfoIdentifier(storeCode, cartOrder, ShipmentDetailsConstants.SHIPMENT_TYPE)
									)));
		}
		return Maybe.empty();
	}

	private DestinationInfoIdentifier buildDestinationInfoIdentifier(final String storeCode,
			final CartOrder cartOrder,
			final String deliveryId) {
		return DestinationInfoIdentifier.builder()
				.withScope(StringIdentifier.of(storeCode))
				.withShipmentDetailsId(CompositeIdentifier.of(createShipmentDetailsId(cartOrder.getGuid(), deliveryId)))
				.build();
	}

	protected ShoppingCartRepository getShoppingCartRepository() {
		return shoppingCartRepository;
	}

	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	protected  CartOrderRepository getCartOrderRepository() {
		return cartOrderRepository;
	}

	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}
}
