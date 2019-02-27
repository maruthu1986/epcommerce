/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.lineitems.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read line items.
 */
public class ReadLineItemsPrototype implements LineItemsResource.Read {

	private final LinksRepository<CartIdentifier, LineItemIdentifier> repository;
	private final LineItemsIdentifier lineItemsIdentifier;

	/**
	 * Constructor.
	 *
	 * @param lineItemsIdentifier line items identifier
	 * @param repository     repository
	 */
	@Inject
	public ReadLineItemsPrototype(@RequestIdentifier final LineItemsIdentifier lineItemsIdentifier,
								  @ResourceRepository final LinksRepository<CartIdentifier, LineItemIdentifier> repository) {
		this.lineItemsIdentifier = lineItemsIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<LineItemIdentifier> onRead() {
		return repository.getElements(lineItemsIdentifier.getCart());
	}
}
