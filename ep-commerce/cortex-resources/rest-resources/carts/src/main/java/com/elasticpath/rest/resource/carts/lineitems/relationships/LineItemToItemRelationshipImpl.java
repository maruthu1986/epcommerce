/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.lineitems.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.ItemForLineItemRelationship;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Line item to item link.
 */
public class LineItemToItemRelationshipImpl implements ItemForLineItemRelationship.LinkTo {

	private final LineItemIdentifier lineItemIdentifier;

	private final LinksRepository<LineItemIdentifier, ItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param lineItemIdentifier lineItemIdentifier
	 * @param repository         repository
	 */
	@Inject
	public LineItemToItemRelationshipImpl(@RequestIdentifier final LineItemIdentifier lineItemIdentifier,
										  @ResourceRepository final LinksRepository<LineItemIdentifier, ItemIdentifier> repository) {
		this.lineItemIdentifier = lineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<ItemIdentifier> onLinkTo() {
		return repository.getElements(lineItemIdentifier);
	}
}
