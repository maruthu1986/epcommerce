/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.availabilities.AvailabilityForCartLineItemIdentifier;
import com.elasticpath.rest.definition.availabilities.AvailabilityForCartLineItemResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Cart Line Item Availability prototype for Read operation.
 */
public class ReadAvailabilityForCartLineItemPrototype implements AvailabilityForCartLineItemResource.Read {

	private final AvailabilityForCartLineItemIdentifier availabilityForCartLineItemIdentifier;

	private final Repository<AvailabilityEntity, AvailabilityForCartLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param availabilityForCartLineItemIdentifier availabilityForCartLineItemIdentifier
	 * @param repository                            repository
	 */
	@Inject
	public ReadAvailabilityForCartLineItemPrototype(
			@RequestIdentifier final AvailabilityForCartLineItemIdentifier availabilityForCartLineItemIdentifier,
			@ResourceRepository final Repository<AvailabilityEntity, AvailabilityForCartLineItemIdentifier> repository) {
		this.availabilityForCartLineItemIdentifier = availabilityForCartLineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<AvailabilityEntity> onRead() {
		return repository.findOne(availabilityForCartLineItemIdentifier);
	}
}
