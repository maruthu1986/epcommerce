/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.registrations.prototypes;

import com.google.inject.Inject;
import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.profiles.DefaultProfileIdentifier;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.definition.registrations.NewAccountRegistrationFormResource;
import com.elasticpath.rest.definition.registrations.RegistrationEntity;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.helix.data.annotation.UserId;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Registration prototype for create operation.
 */
public class NewAccountRegistrationActionPrototype implements NewAccountRegistrationFormResource.SubmitWithResult {

	private final RegistrationEntity registrationEntity;
	private final IdentifierPart<String> scope;
	private final String userId;
	private final Repository<RegistrationEntity, ProfileIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param registrationEntity entity
	 * @param scope              scope
	 * @param userId             user identifier
	 * @param repository         repository
	 */
	@Inject
	public NewAccountRegistrationActionPrototype(
			@RequestForm final RegistrationEntity registrationEntity,
			@UriPart(DefaultProfileIdentifier.SCOPE) final IdentifierPart<String> scope,
			@UserId final String userId,
			@ResourceRepository final Repository<RegistrationEntity, ProfileIdentifier> repository) {
		this.registrationEntity = registrationEntity;
		this.scope = scope;
		this.userId = userId;
		this.repository = repository;
	}

	@Override
	public Single<SubmitResult<ProfileIdentifier>> onSubmitWithResult() {
		ProfileIdentifier profileIdentifier = ProfileIdentifier.builder()
				.withProfileId(StringIdentifier.of(userId))
				.withScope(scope)
				.build();
		return repository.update(registrationEntity, profileIdentifier)
				.andThen(Single.just(SubmitResult.<ProfileIdentifier>builder()
						.withIdentifier(profileIdentifier)
						.withStatus(SubmitStatus.CREATED)
						.build()));
	}
}
