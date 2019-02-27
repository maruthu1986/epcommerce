/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Purchase Line Items prototype for Read operation.
 */
public class ReadPurchaseLineItemsPrototype implements PurchaseLineItemsResource.Read {

	private final PurchaseLineItemsIdentifier purchaseLineItemsIdentifier;

	private final LinksRepository<PurchaseLineItemsIdentifier, PurchaseLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemsIdentifier purchaseLineItemsIdentifier
	 * @param repository                  repository
	 */
	@Inject
	public ReadPurchaseLineItemsPrototype(
			@RequestIdentifier final PurchaseLineItemsIdentifier purchaseLineItemsIdentifier,
			@ResourceRepository final LinksRepository<PurchaseLineItemsIdentifier, PurchaseLineItemIdentifier> repository) {
		this.purchaseLineItemsIdentifier = purchaseLineItemsIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PurchaseLineItemIdentifier> onRead() {
		return repository.getElements(purchaseLineItemsIdentifier);
	}
}
