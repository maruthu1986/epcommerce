/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import io.reactivex.Maybe;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.rest.definition.orders.BillingaddressInfoIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.ResourceIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorResolutionStrategy;
import com.elasticpath.service.shoppingcart.validation.impl.BillingAddressShoppingCartValidatorImpl;

/**
 * Resolves resource identifier to add shipping address.
 */
public class BillingAddressResolutionStrategy implements StructuredErrorResolutionStrategy {

	private ShoppingCartRepository shoppingCartRepository;
	private CartOrderRepository cartOrderRepository;

	/**
	 * Transforms structured error resolution into a resource identifier.
	 *
	 * @param message a structured error message
	 * @param cortexResourceID the id of the cart.
	 * @return a resource identifier, or Optional.empty() if none exists.
	 */
	@Override
	public Maybe<ResourceIdentifier> getResourceIdentifier(final StructuredErrorMessage message, final String cortexResourceID) {
		if (StringUtils.equals(message.getMessageId(), BillingAddressShoppingCartValidatorImpl.MESSAGE_ID)) {
			return shoppingCartRepository.findStoreForCartGuid(cortexResourceID)
					.flatMapMaybe(storeCode -> cartOrderRepository.findByCartGuidSingle(cortexResourceID)
							.flatMapMaybe(cartOrder -> {
								BillingaddressInfoIdentifier billingAddressesIdentifier = buildBillingAddressIdentifier(
										StringIdentifier.of(storeCode), cartOrder.getGuid());
								return Maybe.just(billingAddressesIdentifier);
							}));
		}
		return Maybe.empty();
	}

	private BillingaddressInfoIdentifier buildBillingAddressIdentifier(final IdentifierPart<String> scope,
																	   final String orderId) {
		return BillingaddressInfoIdentifier.builder()
				.withOrder(OrderIdentifier.builder()
						.withOrderId(StringIdentifier.of(orderId))
						.withScope(scope)
						.build())
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
