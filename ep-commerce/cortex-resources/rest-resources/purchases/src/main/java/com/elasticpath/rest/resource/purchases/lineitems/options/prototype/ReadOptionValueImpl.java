/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.options.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read operation for the value resource.
 */
public class ReadOptionValueImpl implements PurchaseLineItemOptionValueResource.Read {

	private final PurchaseLineItemOptionValueIdentifier valueIdentifier;
	private final Repository<PurchaseLineItemOptionValueEntity, PurchaseLineItemOptionValueIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param valueIdentifier value identifier
	 * @param repository repo for the option value entity
	 */
	@Inject
	public ReadOptionValueImpl(
			@RequestIdentifier final PurchaseLineItemOptionValueIdentifier valueIdentifier,
			@ResourceRepository final Repository<PurchaseLineItemOptionValueEntity, PurchaseLineItemOptionValueIdentifier> repository) {

		this.valueIdentifier = valueIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<PurchaseLineItemOptionValueEntity> onRead() {
		return repository.findOne(valueIdentifier);
	}
}
