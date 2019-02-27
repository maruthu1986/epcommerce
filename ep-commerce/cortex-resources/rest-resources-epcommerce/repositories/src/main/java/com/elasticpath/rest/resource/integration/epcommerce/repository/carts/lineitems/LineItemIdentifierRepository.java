/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;

/**
 * Repository for creating cart Line Item identifiers.
 */
public interface LineItemIdentifierRepository {

	/**
	 * Builds a line item identifier.
	 *
	 * @param cart cart
	 * @param shoppingItem shoppingItem
	 * @return line item identifier
	 */
	LineItemIdentifier buildLineItemIdentifier(ShoppingCart cart, ShoppingItem shoppingItem);

	/**
	 * Builds a line item identifier.
	 *
	 * @param cartIdentifier cartIdentifier
	 * @param shoppingItem shoppingItem
	 * @return line item identifier
	 */
	LineItemIdentifier buildLineItemIdentifier(CartIdentifier cartIdentifier, ShoppingItem shoppingItem);

}
