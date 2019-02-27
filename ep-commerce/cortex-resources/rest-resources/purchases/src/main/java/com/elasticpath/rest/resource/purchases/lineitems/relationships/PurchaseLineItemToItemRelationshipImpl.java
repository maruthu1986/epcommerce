/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.relationships;

import com.google.inject.Inject;
import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemToItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Item to Purchase Line Item Link.
 */
public class PurchaseLineItemToItemRelationshipImpl implements PurchaseLineItemToItemRelationship.LinkTo {

	private final PurchaseLineItemIdentifier purchaseLineItemIdentifier;
	private final LinksRepository<PurchaseLineItemIdentifier, ItemIdentifier> linksRepository;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemIdentifier purchase line item identifier.
	 * @param linksRepository            links repository which converts {@link PurchaseLineItemIdentifier} to {@link ItemIdentifier}.
	 */
	@Inject
	public PurchaseLineItemToItemRelationshipImpl(
			@RequestIdentifier final PurchaseLineItemIdentifier purchaseLineItemIdentifier,
			@ResourceRepository final LinksRepository<PurchaseLineItemIdentifier, ItemIdentifier> linksRepository) {

		this.purchaseLineItemIdentifier = purchaseLineItemIdentifier;
		this.linksRepository = linksRepository;

	}

	@Override
	public Observable<ItemIdentifier> onLinkTo() {
		return linksRepository.getElements(purchaseLineItemIdentifier);
	}
}
