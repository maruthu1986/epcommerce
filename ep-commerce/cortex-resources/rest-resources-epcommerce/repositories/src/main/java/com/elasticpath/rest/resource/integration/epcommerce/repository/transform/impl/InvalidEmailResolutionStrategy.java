/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import io.reactivex.Maybe;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.definition.orders.EmailInfoIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.ResourceIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorResolutionStrategy;
import com.elasticpath.service.shoppingcart.validation.impl.EmailAddressShoppingCartValidatorImpl;

/**
 * Translates a structured error message into a identifier for specifying email.
 */
public class InvalidEmailResolutionStrategy implements StructuredErrorResolutionStrategy {

	private ShoppingCartRepository shoppingCartRepository;
	private CartOrderRepository cartOrderRepository;

	@Override
	public Maybe<ResourceIdentifier> getResourceIdentifier(final StructuredErrorMessage message, final String cortexResourceID) {

		if (StringUtils.equals(message.getMessageId(), EmailAddressShoppingCartValidatorImpl.MESSAGE_ID)) {
			return shoppingCartRepository.getShoppingCart(cortexResourceID)
					.flatMapMaybe(shoppingCart -> cartOrderRepository.findByCartGuidSingle(cortexResourceID)
							.flatMapMaybe(cartOrder -> {
								EmailInfoIdentifier emailInfoIdentifier = buildEmailInfoIdentifier(shoppingCart, cartOrder);
								return Maybe.just(emailInfoIdentifier);
							}));

		}
		return Maybe.empty();
	}

	private EmailInfoIdentifier buildEmailInfoIdentifier(final ShoppingCart shoppingCart, final CartOrder cartOrder) {
		return EmailInfoIdentifier.builder()
				.withOrder(OrderIdentifier.builder()
						.withOrderId(StringIdentifier.of(cartOrder.getGuid()))
						.withScope(StringIdentifier.of(shoppingCart.getStore().getCode()))
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
