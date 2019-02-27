/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.options.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueIdentifier;
import com.elasticpath.rest.definition.purchases.ValueForPurchaseLineItemOptionRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Purchase line item option to option value link.
 */
public class PurchaseLineItemOptionToOptionValueRelationshipImpl implements ValueForPurchaseLineItemOptionRelationship.LinkTo {

	private final PurchaseLineItemOptionIdentifier purchaseLineItemOptionIdentifier;
	private final LinksRepository<PurchaseLineItemOptionIdentifier, PurchaseLineItemOptionValueIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemOptionIdentifier option identifier
	 * @param repository repo for the option value
	 */
	@Inject
	public PurchaseLineItemOptionToOptionValueRelationshipImpl(
			@RequestIdentifier final PurchaseLineItemOptionIdentifier purchaseLineItemOptionIdentifier,
			@ResourceRepository final LinksRepository<PurchaseLineItemOptionIdentifier, PurchaseLineItemOptionValueIdentifier> repository) {

		this.purchaseLineItemOptionIdentifier = purchaseLineItemOptionIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PurchaseLineItemOptionValueIdentifier> onLinkTo() {
		return repository.getElements(purchaseLineItemOptionIdentifier);
	}
}
