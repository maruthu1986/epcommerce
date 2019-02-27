/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.regions.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.geographies.RegionEntity;
import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.definition.geographies.RegionResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Region prototype for Read operation.
 */
public class ReadRegionPrototype implements RegionResource.Read {

	private final RegionIdentifier regionIdentifier;

	private final Repository<RegionEntity, RegionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param regionIdentifier region identifier
	 * @param repository       repository
	 */
	@Inject
	public ReadRegionPrototype(@RequestIdentifier final RegionIdentifier regionIdentifier,
							   @ResourceRepository final Repository<RegionEntity, RegionIdentifier> repository) {
		this.regionIdentifier = regionIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<RegionEntity> onRead() {
		return repository.findOne(regionIdentifier);
	}
}
