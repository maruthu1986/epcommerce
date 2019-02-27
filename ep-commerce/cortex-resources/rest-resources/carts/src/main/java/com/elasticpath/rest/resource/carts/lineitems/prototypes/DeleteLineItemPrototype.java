/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.lineitems.prototypes;


import javax.inject.Inject;

import io.reactivex.Completable;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Delete line item.
 */
public class DeleteLineItemPrototype implements LineItemResource.Delete {

	private final LineItemIdentifier lineItemIdentifier;

	private final Repository<LineItemEntity, LineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param lineItemIdentifier lineItemIdentifier
	 * @param repository         repository
	 */
	@Inject
	public DeleteLineItemPrototype(@RequestIdentifier final LineItemIdentifier lineItemIdentifier,
								   @ResourceRepository final Repository<LineItemEntity, LineItemIdentifier> repository) {
		this.lineItemIdentifier = lineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Completable onDelete() {
		return repository.delete(lineItemIdentifier);
	}
}
