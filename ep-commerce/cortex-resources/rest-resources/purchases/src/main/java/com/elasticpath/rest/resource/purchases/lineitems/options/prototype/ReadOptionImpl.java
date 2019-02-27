/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.options.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read operation for the specific option of the purchase line item.
 */
public class ReadOptionImpl implements PurchaseLineItemOptionResource.Read {

	private final PurchaseLineItemOptionIdentifier optionIdentifier;
	private final Repository<PurchaseLineItemOptionEntity, PurchaseLineItemOptionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param optionIdentifier option identifier
	 * @param repository repository for the option entity
	 */
	@Inject
	public ReadOptionImpl(
			@RequestIdentifier final PurchaseLineItemOptionIdentifier optionIdentifier,
			@ResourceRepository final Repository<PurchaseLineItemOptionEntity, PurchaseLineItemOptionIdentifier> repository) {

		this.optionIdentifier = optionIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<PurchaseLineItemOptionEntity> onRead() {
		return repository.findOne(optionIdentifier);
	}
}
