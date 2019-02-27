/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.options.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.OptionsForPurchaseLineItemRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Purchase line item to options link.
 */
public class PurchaseLineItemToLineItemOptionsRelationshipImpl implements OptionsForPurchaseLineItemRelationship.LinkTo {

	private final PurchaseLineItemIdentifier purchaseLineItemIdentifier;
	private final LinksRepository<PurchaseLineItemIdentifier, PurchaseLineItemOptionsIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemIdentifier identifier
	 * @param repository repo for the line item options
	 */
	@Inject
	public PurchaseLineItemToLineItemOptionsRelationshipImpl(
			@RequestIdentifier final PurchaseLineItemIdentifier purchaseLineItemIdentifier,
			@ResourceRepository final LinksRepository<PurchaseLineItemIdentifier, PurchaseLineItemOptionsIdentifier> repository) {

		this.purchaseLineItemIdentifier = purchaseLineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PurchaseLineItemOptionsIdentifier> onLinkTo() {
		return repository.getElements(purchaseLineItemIdentifier);
	}
}
