/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.geographies.CountryEntity;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.definition.geographies.CountryResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Country prototype for Read operation.
 */
public class ReadCountryPrototype implements CountryResource.Read {

	private final CountryIdentifier countryIdentifier;

	private final Repository<CountryEntity, CountryIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param countryIdentifier country identifier
	 * @param repository        repository
	 */
	@Inject
	public ReadCountryPrototype(@RequestIdentifier final CountryIdentifier countryIdentifier,
								@ResourceRepository final Repository<CountryEntity, CountryIdentifier> repository) {
		this.countryIdentifier = countryIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<CountryEntity> onRead() {
		return repository.findOne(countryIdentifier);
	}
}
