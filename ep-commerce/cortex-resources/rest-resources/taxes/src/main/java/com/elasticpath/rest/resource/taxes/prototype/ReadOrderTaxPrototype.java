/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.taxes.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.taxes.OrderTaxIdentifier;
import com.elasticpath.rest.definition.taxes.OrderTaxResource;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read Order Tax Prototype.
 */
public class ReadOrderTaxPrototype implements OrderTaxResource.Read {

	private final OrderTaxIdentifier orderTaxIdentifier;

	private final Repository<TaxesEntity, OrderTaxIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param orderTaxIdentifier order tax identifier
	 * @param repository         repository
	 */
	@Inject
	public ReadOrderTaxPrototype(@RequestIdentifier final OrderTaxIdentifier orderTaxIdentifier,
								 @ResourceRepository final Repository<TaxesEntity, OrderTaxIdentifier> repository) {
		this.orderTaxIdentifier = orderTaxIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<TaxesEntity> onRead() {
		return repository.findOne(orderTaxIdentifier);
	}
}
