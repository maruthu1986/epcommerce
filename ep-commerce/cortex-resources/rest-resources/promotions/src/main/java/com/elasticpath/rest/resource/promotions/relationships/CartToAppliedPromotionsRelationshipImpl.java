/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a promotion link in cart.
 */
public class CartToAppliedPromotionsRelationshipImpl implements AppliedPromotionsForCartRelationship.LinkTo {

	private final CartIdentifier cartIdentifier;

	/**
	 * Constructor.
	 *
	 * @param cartIdentifier	identifier
	 */
	@Inject
	public CartToAppliedPromotionsRelationshipImpl(@RequestIdentifier final CartIdentifier cartIdentifier) {
		this.cartIdentifier = cartIdentifier;
	}

	@Override
	public Observable<AppliedPromotionsForCartIdentifier> onLinkTo() {
		return Observable.just(AppliedPromotionsForCartIdentifier.builder()
				.withCart(cartIdentifier)
				.build());
	}
}
