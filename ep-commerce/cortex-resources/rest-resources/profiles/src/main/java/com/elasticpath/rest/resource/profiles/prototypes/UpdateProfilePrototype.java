/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.profiles.prototypes;

import javax.inject.Inject;

import io.reactivex.Completable;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.definition.profiles.ProfileResource;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Customer profile prototype for Update operation.
 */
public class UpdateProfilePrototype implements ProfileResource.Update {

	private final ProfileEntity profileEntityForm;

	private final ProfileIdentifier profileIdentifier;

	private final Repository<ProfileEntity, ProfileIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param profileEntityForm profile entity
	 * @param profileIdentifier profile identifier
	 * @param repository        repository
	 */
	@Inject
	public UpdateProfilePrototype(
			@RequestForm final ProfileEntity profileEntityForm,
			@RequestIdentifier final ProfileIdentifier profileIdentifier,
			@ResourceRepository final Repository<ProfileEntity, ProfileIdentifier> repository) {
		this.profileEntityForm = profileEntityForm;
		this.profileIdentifier = profileIdentifier;
		this.repository = repository;
	}

	@Override
	public Completable onUpdate() {
		return repository.update(profileEntityForm, profileIdentifier);
	}
}
