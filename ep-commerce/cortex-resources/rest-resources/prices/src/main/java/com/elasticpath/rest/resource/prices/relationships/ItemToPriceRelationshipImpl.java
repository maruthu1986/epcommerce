/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Adds a link from item to price.
 */
public class ItemToPriceRelationshipImpl implements PriceForItemRelationship.LinkTo {

	private final ItemIdentifier itemIdentifier;
	private final LinksRepository<ItemIdentifier, PriceForItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param itemIdentifier	itemIdentifier
	 * @param repository		repository
	 */
	@Inject
	public ItemToPriceRelationshipImpl(@RequestIdentifier final ItemIdentifier itemIdentifier,
									   @ResourceRepository final LinksRepository<ItemIdentifier, PriceForItemIdentifier> repository) {
		this.itemIdentifier = itemIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PriceForItemIdentifier> onLinkTo() {
		return repository.getElements(itemIdentifier);
	}
}