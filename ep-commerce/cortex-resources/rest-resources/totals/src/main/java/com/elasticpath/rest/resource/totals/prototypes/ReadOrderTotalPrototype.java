/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.totals.OrderTotalIdentifier;
import com.elasticpath.rest.definition.totals.OrderTotalResource;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Order Total prototype for Read operation.
 */
public class ReadOrderTotalPrototype implements OrderTotalResource.Read {

	private final OrderTotalIdentifier orderTotalIdentifier;

	private final Repository<TotalEntity, OrderTotalIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param orderTotalIdentifier orderTotalIdentifier
	 * @param repository           repository
	 */
	@Inject
	public ReadOrderTotalPrototype(@RequestIdentifier final OrderTotalIdentifier orderTotalIdentifier,
								   @ResourceRepository final Repository<TotalEntity, OrderTotalIdentifier> repository) {
		this.orderTotalIdentifier = orderTotalIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<TotalEntity> onRead() {
		return repository.findOne(orderTotalIdentifier);
	}
}
