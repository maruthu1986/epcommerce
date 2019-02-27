/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForCartIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForCartResource;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading promotions for cart.
 */
public class ReadPossiblePromotionsForCartPrototype implements PossiblePromotionsForCartResource.Read {

	private final PossiblePromotionsForCartIdentifier possiblePromotionsForCartIdentifier;
	private final LinksRepository<PossiblePromotionsForCartIdentifier, PromotionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param possiblePromotionsForCartIdentifier	identifier
	 * @param repository							repository
	 */
	@Inject
	public ReadPossiblePromotionsForCartPrototype(
			@RequestIdentifier final PossiblePromotionsForCartIdentifier possiblePromotionsForCartIdentifier,
			@ResourceRepository final LinksRepository<PossiblePromotionsForCartIdentifier, PromotionIdentifier> repository) {
		this.possiblePromotionsForCartIdentifier = possiblePromotionsForCartIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PromotionIdentifier> onRead() {
		return repository.getElements(possiblePromotionsForCartIdentifier);
	}
}
