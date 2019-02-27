/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.lineitems.prototypes;


import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read line item.
 */
public class ReadLineItemPrototype implements LineItemResource.Read {

	private final LineItemIdentifier lineItemIdentifier;

	private final Repository<LineItemEntity, LineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param lineItemIdentifier lineItemIdentifier
	 * @param repository         repository
	 */
	@Inject
	public ReadLineItemPrototype(@RequestIdentifier final LineItemIdentifier lineItemIdentifier,
								 @ResourceRepository final Repository<LineItemEntity, LineItemIdentifier> repository) {
		this.lineItemIdentifier = lineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<LineItemEntity> onRead() {
		return repository.findOne(lineItemIdentifier);
	}
}
