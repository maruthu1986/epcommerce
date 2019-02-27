/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForItemIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Adds a possiblepromotion link in item.
 */
public class ItemToPossiblePromotionsRelationshipImpl implements PossiblePromotionsForItemRelationship.LinkTo {

	private final ItemIdentifier itemIdentifier;
	private final LinksRepository<ItemIdentifier, PossiblePromotionsForItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param itemIdentifier	identifier
	 * @param repository    	repository
	 */
	@Inject
	public ItemToPossiblePromotionsRelationshipImpl(
			@RequestIdentifier final ItemIdentifier itemIdentifier,
			@ResourceRepository final LinksRepository<ItemIdentifier, PossiblePromotionsForItemIdentifier> repository) {
		this.itemIdentifier = itemIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PossiblePromotionsForItemIdentifier> onLinkTo() {
		return repository.getElements(itemIdentifier);
	}
}
