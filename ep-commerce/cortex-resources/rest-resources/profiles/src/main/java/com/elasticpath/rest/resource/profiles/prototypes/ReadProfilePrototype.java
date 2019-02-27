/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.profiles.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.definition.profiles.ProfileResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;


/**
 * Customer profile prototype for Read operation.
 */
public class ReadProfilePrototype implements ProfileResource.Read {

	private final ProfileIdentifier profileIdentifier;

	private final Repository<ProfileEntity, ProfileIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param profileIdentifier profile identifier
	 * @param repository        repository
	 */
	@Inject
	public ReadProfilePrototype(@RequestIdentifier final ProfileIdentifier profileIdentifier,
								@ResourceRepository final Repository<ProfileEntity, ProfileIdentifier> repository) {
		this.profileIdentifier = profileIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<ProfileEntity> onRead() {
		return repository.findOne(profileIdentifier);
	}

}