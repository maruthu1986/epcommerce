/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressFormResource;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Address Form prototype for Submit operation.
 */
public class SubmitAddressFormPrototype implements AddressFormResource.SubmitWithResult {

	private final AddressEntity addressEntity;

	private final Repository<AddressEntity, AddressIdentifier> repository;

	private final IdentifierPart<String> scope;

	/**
	 * Constructor.
	 *
	 * @param addressEntity addressEntity
	 * @param scope         scope
	 * @param repository    repository
	 */
	@Inject
	public SubmitAddressFormPrototype(@RequestForm final AddressEntity addressEntity,
			@UriPart(AddressesIdentifier.SCOPE) final IdentifierPart<String> scope,
			@ResourceRepository final Repository<AddressEntity, AddressIdentifier> repository) {
		this.addressEntity = addressEntity;
		this.scope = scope;
		this.repository = repository;
	}

	@Override
	public Single<SubmitResult<AddressIdentifier>> onSubmitWithResult() {
		return repository.submit(addressEntity, scope);
	}
}
