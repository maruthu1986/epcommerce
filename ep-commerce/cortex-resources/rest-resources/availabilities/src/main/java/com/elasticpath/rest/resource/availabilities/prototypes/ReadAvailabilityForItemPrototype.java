/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.availabilities.AvailabilityForItemIdentifier;
import com.elasticpath.rest.definition.availabilities.AvailabilityForItemResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Item Availability prototype for Read operation.
 */
public class ReadAvailabilityForItemPrototype implements AvailabilityForItemResource.Read {

	private final AvailabilityForItemIdentifier availabilityForItemIdentifier;

	private final Repository<AvailabilityEntity, AvailabilityForItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param availabilityForItemIdentifier availabilityForItemIdentifier
	 * @param repository                    repository
	 */
	@Inject
	public ReadAvailabilityForItemPrototype(
			@RequestIdentifier final AvailabilityForItemIdentifier availabilityForItemIdentifier,
			@ResourceRepository final Repository<AvailabilityEntity, AvailabilityForItemIdentifier> repository) {
		this.availabilityForItemIdentifier = availabilityForItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<AvailabilityEntity> onRead() {
		return repository.findOne(availabilityForItemIdentifier);
	}
}
