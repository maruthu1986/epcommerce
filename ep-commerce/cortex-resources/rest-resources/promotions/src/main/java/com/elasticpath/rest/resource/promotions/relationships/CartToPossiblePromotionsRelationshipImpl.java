/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForCartIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForCartRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Adds a possiblepromotions link in cart.
 */
public class CartToPossiblePromotionsRelationshipImpl implements PossiblePromotionsForCartRelationship.LinkTo {

	private final CartIdentifier cartIdentifier;
	private final LinksRepository<CartIdentifier, PossiblePromotionsForCartIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param cartIdentifier	identifier
	 * @param repository		repository
	 */
	@Inject
	public CartToPossiblePromotionsRelationshipImpl(
			@RequestIdentifier final CartIdentifier cartIdentifier,
			@ResourceRepository final LinksRepository<CartIdentifier, PossiblePromotionsForCartIdentifier> repository) {
		this.cartIdentifier = cartIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PossiblePromotionsForCartIdentifier> onLinkTo() {
		return repository.getElements(cartIdentifier);
	}
}
