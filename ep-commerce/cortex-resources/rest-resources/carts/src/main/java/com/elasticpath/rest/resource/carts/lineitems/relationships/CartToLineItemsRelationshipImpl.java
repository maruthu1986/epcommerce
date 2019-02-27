/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.lineitems.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsForCartRelationship;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Cart to line items link.
 */
public class CartToLineItemsRelationshipImpl implements LineItemsForCartRelationship.LinkTo {

	private final CartIdentifier cartIdentifier;

	/**
	 * Constructor.
	 *
	 * @param cartIdentifier cart identifier
	 */
	@Inject
	public CartToLineItemsRelationshipImpl(@RequestIdentifier final CartIdentifier cartIdentifier) {
		this.cartIdentifier = cartIdentifier;
	}

	@Override
	public Observable<LineItemsIdentifier> onLinkTo() {
		return Observable.just(LineItemsIdentifier.builder().withCart(cartIdentifier).build());
	}
}
