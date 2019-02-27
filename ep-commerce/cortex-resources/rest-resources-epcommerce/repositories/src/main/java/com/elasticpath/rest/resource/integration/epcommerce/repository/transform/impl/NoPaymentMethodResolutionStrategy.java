/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import io.reactivex.Maybe;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.paymentmethods.PaymentmethodInfoIdentifier;
import com.elasticpath.rest.id.ResourceIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorResolutionStrategy;
import com.elasticpath.service.shoppingcart.validation.impl.PaymentMethodShoppingCartValidatorImpl;

/**
 * Resolves resource identifier to add payment method.
 */
public class NoPaymentMethodResolutionStrategy implements StructuredErrorResolutionStrategy {

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
		if (StringUtils.equals(message.getMessageId(), PaymentMethodShoppingCartValidatorImpl.MESSAGE_ID)) {

			return shoppingCartRepository.findStoreForCartGuid(cortexResourceID)
					.flatMapMaybe(
					storeCode -> cartOrderRepository.findByCartGuidSingle(cortexResourceID)
							.flatMapMaybe(cartOrder -> Maybe.just(buildPaymentInfoIdentifier(storeCode, cartOrder))));

		}
		return Maybe.empty();
	}

	private PaymentmethodInfoIdentifier buildPaymentInfoIdentifier(final String storeCode, final CartOrder cartOrder) {
		return PaymentmethodInfoIdentifier.builder().withOrder(OrderIdentifier.builder()
				.withOrderId(StringIdentifier.of(cartOrder.getGuid()))
				.withScope(StringIdentifier.of(storeCode))
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
