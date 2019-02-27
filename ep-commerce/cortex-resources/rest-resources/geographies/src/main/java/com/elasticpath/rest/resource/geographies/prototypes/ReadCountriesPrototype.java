/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.definition.geographies.CountriesResource;
import com.elasticpath.rest.definition.geographies.CountryEntity;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Countries prototype for Read operation.
 */
public class ReadCountriesPrototype implements CountriesResource.Read {

	private final IdentifierPart<String> scope;

	private final Repository<CountryEntity, CountryIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param scope      scope
	 * @param repository repository
	 */
	@Inject
	public ReadCountriesPrototype(@UriPart(CountriesIdentifier.SCOPE) final IdentifierPart<String> scope,
								  @ResourceRepository final Repository<CountryEntity, CountryIdentifier> repository) {
		this.scope = scope;
		this.repository = repository;
	}

	@Override
	public Observable<CountryIdentifier> onRead() {
		return repository.findAll(scope);
	}
}
