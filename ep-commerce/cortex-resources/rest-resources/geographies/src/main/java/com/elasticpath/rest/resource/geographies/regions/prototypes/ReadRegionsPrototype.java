/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.regions.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;
import com.elasticpath.rest.definition.geographies.RegionsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Regions prototype for Read operation.
 */
public class ReadRegionsPrototype implements RegionsResource.Read {

	private final RegionsIdentifier regionsIdentifier;

	private final LinksRepository<RegionsIdentifier, RegionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param regionsIdentifier regionsIdentifier
	 * @param repository        repository
	 */
	@Inject
	public ReadRegionsPrototype(@RequestIdentifier final RegionsIdentifier regionsIdentifier,
								@ResourceRepository final LinksRepository<RegionsIdentifier, RegionIdentifier> repository) {
		this.regionsIdentifier = regionsIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<RegionIdentifier> onRead() {
		return repository.getElements(regionsIdentifier);
	}
}
