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
 * Cart total to cart link.
 */
public class TotalToCartRelationshipImpl implements TotalForCartRelationship.LinkFrom {

	private final CartTotalIdentifier cartTotalIdentifier;

	/**
	 * Constructor.
	 *
	 * @param cartTotalIdentifier cartTotalIdentifier
	 */
	@Inject
	public TotalToCartRelationshipImpl(@RequestIdentifier final CartTotalIdentifier cartTotalIdentifier) {
		this.cartTotalIdentifier = cartTotalIdentifier;
	}

	@Override
	public Observable<CartIdentifier> onLinkFrom() {
		return Observable.just(cartTotalIdentifier.getCart());
	}
}
