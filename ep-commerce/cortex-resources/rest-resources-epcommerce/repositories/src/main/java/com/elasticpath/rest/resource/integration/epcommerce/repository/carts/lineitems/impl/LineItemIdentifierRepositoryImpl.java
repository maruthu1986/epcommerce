/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.impl;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.LineItemIdentifierRepository;

/**
 * Default implementation of {@link LineItemIdentifierRepository}.
 */
@Singleton
@Named("lineItemIdentifierRepository")
public class LineItemIdentifierRepositoryImpl implements LineItemIdentifierRepository {

	@Override
	public LineItemIdentifier buildLineItemIdentifier(final ShoppingCart cart, final ShoppingItem shoppingItem) {
		final CartIdentifier cartIdentifier = CartIdentifier.builder()
				.withCartId(StringIdentifier.of(cart.getGuid()))
				.withScope(StringIdentifier.of(cart.getStore().getCode()))
				.build();

		return buildLineItemIdentifier(cartIdentifier, shoppingItem);
	}

	@Override
	public LineItemIdentifier buildLineItemIdentifier(final CartIdentifier cartIdentifier, final ShoppingItem shoppingItem) {
		return LineItemIdentifier.builder()
				.withLineItems(LineItemsIdentifier.builder()
									   .withCart(cartIdentifier)
									   .build())
				.withLineItemId(StringIdentifier.of(shoppingItem.getGuid()))
				.build();
	}

}
