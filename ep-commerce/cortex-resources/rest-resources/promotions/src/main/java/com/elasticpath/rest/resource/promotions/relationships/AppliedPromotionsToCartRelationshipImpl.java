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
 * Adds a cart link to appliedpromotions.
 */
public class AppliedPromotionsToCartRelationshipImpl implements AppliedPromotionsForCartRelationship.LinkFrom {

	private final CartIdentifier cartIdentifier;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForCartIdentifier	identifier
	 */
	@Inject
	public AppliedPromotionsToCartRelationshipImpl(@RequestIdentifier final AppliedPromotionsForCartIdentifier appliedPromotionsForCartIdentifier) {
		this.cartIdentifier = appliedPromotionsForCartIdentifier.getCart();
	}

	@Override
	public Observable<CartIdentifier> onLinkFrom() {
		return Observable.just(CartIdentifier.builder()
				.withCartId(cartIdentifier.getCartId())
				.withScope(cartIdentifier.getScope())
				.build());
	}
}
