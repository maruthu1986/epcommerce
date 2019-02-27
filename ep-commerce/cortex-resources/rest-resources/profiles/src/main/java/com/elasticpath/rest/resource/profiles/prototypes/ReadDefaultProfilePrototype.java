/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.profiles.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.rest.definition.profiles.DefaultProfileIdentifier;
import com.elasticpath.rest.definition.profiles.DefaultProfileResource;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.helix.data.annotation.UserId;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Default profile prototype for Read operation.
 */
public class ReadDefaultProfilePrototype implements DefaultProfileResource.Read {

	private final String profileId;

	private final IdentifierPart<String> scope;

	/**
	 * Constructor.
	 *
	 * @param profileId profile id
	 * @param scope     scope
	 */
	@Inject
	public ReadDefaultProfilePrototype(@UserId final String profileId, @UriPart(DefaultProfileIdentifier.SCOPE) final IdentifierPart<String> scope) {
		this.profileId = profileId;
		this.scope = scope;
	}

	@Override
	public Single<ProfileIdentifier> onRead() {
		return Single.just(ProfileIdentifier.builder()
				.withProfileId(StringIdentifier.of(profileId))
				.withScope(scope)
				.build());
	}
}
