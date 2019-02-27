/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForItemIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForItemResource;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading promotion for an item.
 */
public class ReadPossiblePromotionsForItemPrototype implements PossiblePromotionsForItemResource.Read {

	private final PossiblePromotionsForItemIdentifier possiblePromotionsForItemIdentifier;
	private final LinksRepository<PossiblePromotionsForItemIdentifier, PromotionIdentifier> repository;

	/**
	 * Cosntructor.
	 *
	 * @param possiblePromotionsForItemIdentifier	identifier
	 * @param repository							repository
	 */
	@Inject
	public ReadPossiblePromotionsForItemPrototype(
			@RequestIdentifier final PossiblePromotionsForItemIdentifier possiblePromotionsForItemIdentifier,
			@ResourceRepository final LinksRepository<PossiblePromotionsForItemIdentifier, PromotionIdentifier> repository) {
		this.possiblePromotionsForItemIdentifier = possiblePromotionsForItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PromotionIdentifier> onRead() {
		return repository.getElements(possiblePromotionsForItemIdentifier);
	}
}
