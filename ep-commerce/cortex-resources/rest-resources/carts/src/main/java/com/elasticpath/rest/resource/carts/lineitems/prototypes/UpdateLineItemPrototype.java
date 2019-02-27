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
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Update line item.
 */
public class UpdateLineItemPrototype implements LineItemResource.Update {

	private final LineItemIdentifier lineItemIdentifier;

	private final LineItemEntity lineItemEntity;

	private final Repository<LineItemEntity, LineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param lineItemIdentifier lineItemIdentifier
	 * @param lineItemEntity     lineItemEntity
	 * @param repository         repository
	 */
	@Inject
	public UpdateLineItemPrototype(@RequestIdentifier final LineItemIdentifier lineItemIdentifier,
								   @RequestForm final LineItemEntity lineItemEntity,
								   @ResourceRepository final Repository<LineItemEntity, LineItemIdentifier> repository) {
		this.lineItemIdentifier = lineItemIdentifier;
		this.lineItemEntity = lineItemEntity;
		this.repository = repository;
	}

	@Override
	public Completable onUpdate() {
		return repository.update(lineItemEntity, lineItemIdentifier);
	}

}