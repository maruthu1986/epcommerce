/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.ComponentsForPurchaseLineItemRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemComponentsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Purchase line item to children line item components link. This is rendered as a 'components' rel.
 */
public class ChildrenComponentsForPurchaseLineItemRelationshipImpl implements ComponentsForPurchaseLineItemRelationship.LinkTo {

	private final PurchaseLineItemIdentifier purchaseLineItemIdentifier;
	private final LinksRepository<PurchaseLineItemIdentifier, PurchaseLineItemComponentsIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemIdentifier identifier
	 * @param repository repo for the line item components
	 */
	@Inject
	public ChildrenComponentsForPurchaseLineItemRelationshipImpl(
			@RequestIdentifier final PurchaseLineItemIdentifier purchaseLineItemIdentifier,
			@ResourceRepository final LinksRepository<PurchaseLineItemIdentifier, PurchaseLineItemComponentsIdentifier> repository) {

		this.purchaseLineItemIdentifier = purchaseLineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PurchaseLineItemComponentsIdentifier> onLinkTo() {
		return repository.getElements(purchaseLineItemIdentifier);
	}
}
