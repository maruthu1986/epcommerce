/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForCartIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForCartRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Adds a cart link in promotions.
 */
public class PossiblePromotionsToCartRelationshipImpl implements PossiblePromotionsForCartRelationship.LinkFrom {

	private final PossiblePromotionsForCartIdentifier possiblePromotionsForCartIdentifier;

	/**
	 * Constructor.
	 *
	 * @param possiblePromotionsForCartIdentifier	identifier
	 */
	@Inject
	public PossiblePromotionsToCartRelationshipImpl(@RequestIdentifier final PossiblePromotionsForCartIdentifier
																possiblePromotionsForCartIdentifier) {
		this.possiblePromotionsForCartIdentifier = possiblePromotionsForCartIdentifier;
	}

	@Override
	public Observable<CartIdentifier> onLinkFrom() {
		CartIdentifier cartIdentifier = possiblePromotionsForCartIdentifier.getCart();
		IdentifierPart<String> scope = cartIdentifier.getScope();
		IdentifierPart<String> cartId = cartIdentifier.getCartId();
		return Observable.just(CartIdentifier.builder()
				.withScope(scope)
				.withCartId(cartId)
				.build());
	}
}
