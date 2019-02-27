/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.totals.CartTotalIdentifier;
import com.elasticpath.rest.definition.totals.TotalForCartRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Cart to cart total link.
 */
public class CartToTotalRelationshipImpl implements TotalForCartRelationship.LinkTo {


	private final CartIdentifier cartIdentifier;

	/**
	 * Constructor.
	 *
	 * @param cartIdentifier cartIdentifier
	 */
	@Inject
	public CartToTotalRelationshipImpl(@RequestIdentifier final CartIdentifier cartIdentifier) {
		this.cartIdentifier = cartIdentifier;
	}

	@Override
	public Observable<CartTotalIdentifier> onLinkTo() {
		return Observable.just(CartTotalIdentifier.builder()
				.withCart(cartIdentifier)
				.build());
	}
}
