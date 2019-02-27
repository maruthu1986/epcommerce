/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.registrations.prototypes;

import io.reactivex.Single;

import com.elasticpath.rest.definition.registrations.NewAccountRegistrationFormResource;
import com.elasticpath.rest.definition.registrations.RegistrationEntity;

/**
 * Registration prototype for read operation.
 */
public class NewAccountRegistrationFormPrototype implements NewAccountRegistrationFormResource.Read {

	@Override
	public Single<RegistrationEntity> onRead() {
		return Single.just(RegistrationEntity.builder()
				.withFamilyName("")
				.withGivenName("")
				.withPassword("")
				.withUsername("")
				.build());
	}

}
