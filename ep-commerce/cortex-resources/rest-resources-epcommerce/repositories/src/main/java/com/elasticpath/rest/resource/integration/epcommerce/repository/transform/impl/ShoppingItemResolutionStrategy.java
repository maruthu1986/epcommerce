/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import java.util.Optional;

import io.reactivex.Maybe;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.id.ResourceIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorResolutionStrategy;

/**
 * Translates a structured error message resolution for a ShoppingItem into the appropriate Cortex ResourceIdentifier.
 */
public class ShoppingItemResolutionStrategy implements StructuredErrorResolutionStrategy {

	private ShoppingCartRepository shoppingCartRepository;

	@Override
	public Maybe<ResourceIdentifier> getResourceIdentifier(final StructuredErrorMessage message, final String cortexResourceID) {

		Optional<StructuredErrorResolution> resolution = message.getResolution()
				.filter(res -> res.getDomain().isAssignableFrom(ShoppingItem.class));
		if (!resolution.isPresent()) {
			return Maybe.empty();
		}
		return shoppingCartRepository.getShoppingCart(cortexResourceID)
				.flatMapMaybe(shoppingCart -> {
			ShoppingItem item = shoppingCart.getCartItemByGuid(resolution.get().getGuid());
			LineItemIdentifier lineItemIdentifier = buildAvailabilityIdentifier(shoppingCart.getStore().getCode(), shoppingCart.getGuid(), item);
			return Maybe.just(lineItemIdentifier);
		});
	}

	/**
	 * Get the AvailabilityForCartLineItemIdentifier.
	 *
	 * @param scope        scope
	 * @param cartId       cartId
	 * @param shoppingItem shoppingItem
	 * @return the LineItemIdentifier
	 */
	protected LineItemIdentifier buildAvailabilityIdentifier(
			final String scope, final String cartId, final ShoppingItem shoppingItem) {
		LineItemsIdentifier lineItemsIdentifier = LineItemsIdentifier.builder()
				.withCart(CartIdentifier.builder()
						.withScope(StringIdentifier.of(scope))
						.withCartId(StringIdentifier.of(cartId))
						.build())
				.build();

		return LineItemIdentifier.builder()
				.withLineItemId(StringIdentifier.of(shoppingItem.getGuid()))
				.withLineItems(lineItemsIdentifier)
				.build();
	}

	protected ShoppingCartRepository getShoppingCartRepository() {
		return shoppingCartRepository;
	}

	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

}
