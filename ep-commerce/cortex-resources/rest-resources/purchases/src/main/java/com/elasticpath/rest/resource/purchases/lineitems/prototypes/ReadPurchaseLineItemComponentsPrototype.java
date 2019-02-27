/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemComponentsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemComponentsResource;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read operation for the purchase line item components.
 */
public class ReadPurchaseLineItemComponentsPrototype implements PurchaseLineItemComponentsResource.Read {
	private final PurchaseLineItemComponentsIdentifier componentsIdentifier;
	private final LinksRepository<PurchaseLineItemComponentsIdentifier, PurchaseLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param componentsIdentifier identifier
	 * @param repository repository for the line item components
	 */
	@Inject
	public ReadPurchaseLineItemComponentsPrototype(
			@RequestIdentifier final PurchaseLineItemComponentsIdentifier componentsIdentifier,
			@ResourceRepository final LinksRepository<PurchaseLineItemComponentsIdentifier, PurchaseLineItemIdentifier> repository) {

		this.componentsIdentifier = componentsIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PurchaseLineItemIdentifier> onRead() {
		return repository.getElements(componentsIdentifier);
	}
}
