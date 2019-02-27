/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.lineitems.prototypes;


import javax.inject.Inject;

import io.reactivex.Completable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Delete line items.
 */
public class DeleteLineItemsPrototype implements LineItemsResource.Delete {

	private final LineItemsIdentifier lineItemsIdentifier;
	private final LinksRepository<CartIdentifier, LineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param lineItemsIdentifier line items identifier
	 * @param repository     repository
	 */
	@Inject
	public DeleteLineItemsPrototype(@RequestIdentifier final LineItemsIdentifier lineItemsIdentifier,
									@ResourceRepository final LinksRepository<CartIdentifier, LineItemIdentifier> repository) {
		this.lineItemsIdentifier = lineItemsIdentifier;
		this.repository = repository;
	}

	@Override
	public Completable onDelete() {
		return repository.deleteAll(lineItemsIdentifier.getCart());
	}
}
