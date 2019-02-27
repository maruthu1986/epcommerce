/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.orders.DeliveriesIdentifier;
import com.elasticpath.rest.definition.orders.DeliveriesResource;
import com.elasticpath.rest.definition.orders.DeliveryIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read operation for the delivery list.
 */
public class ReadDeliveriesPrototype implements DeliveriesResource.Read {

	private final DeliveriesIdentifier deliveriesIdentifier;

	private final LinksRepository<DeliveriesIdentifier, DeliveryIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param deliveriesIdentifier deliveriesIdentifier
	 * @param repository           repository
	 */
	@Inject
	public ReadDeliveriesPrototype(@RequestIdentifier final DeliveriesIdentifier deliveriesIdentifier,
								   @ResourceRepository final LinksRepository<DeliveriesIdentifier, DeliveryIdentifier> repository) {
		this.deliveriesIdentifier = deliveriesIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<DeliveryIdentifier> onRead() {
		return repository.getElements(deliveriesIdentifier);
	}
}
