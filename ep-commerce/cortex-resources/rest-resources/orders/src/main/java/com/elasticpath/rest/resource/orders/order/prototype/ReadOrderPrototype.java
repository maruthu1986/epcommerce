/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.order.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.orders.OrderResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read operation for the Order.
 */
public class ReadOrderPrototype implements OrderResource.Read {

	private final OrderIdentifier orderIdentifier;

	private final Repository<OrderEntity, OrderIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param orderIdentifier orderIdentifier
	 * @param repository      repository
	 */
	@Inject
	public ReadOrderPrototype(@RequestIdentifier final OrderIdentifier orderIdentifier,
							  @ResourceRepository final Repository<OrderEntity, OrderIdentifier> repository) {
		this.orderIdentifier = orderIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<OrderEntity> onRead() {
		return repository.findOne(orderIdentifier);
	}
}
