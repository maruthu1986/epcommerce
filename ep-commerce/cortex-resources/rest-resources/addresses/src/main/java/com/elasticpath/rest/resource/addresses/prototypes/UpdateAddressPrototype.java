/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.prototypes;

import javax.inject.Inject;

import io.reactivex.Completable;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.AddressResource;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Address prototype for Update operation.
 */
public class UpdateAddressPrototype implements AddressResource.Update {

	private final AddressEntity addressEntityForm;

	private final AddressIdentifier addressIdentifier;

	private final Repository<AddressEntity, AddressIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param addressEntityForm addressEntityForm
	 * @param addressIdentifier addressIdentifier
	 * @param repository        repository
	 */
	@Inject
	public UpdateAddressPrototype(@RequestForm final AddressEntity addressEntityForm,
			@RequestIdentifier final AddressIdentifier addressIdentifier,
			@ResourceRepository final Repository<AddressEntity, AddressIdentifier> repository) {
		this.addressEntityForm = addressEntityForm;
		this.addressIdentifier = addressIdentifier;
		this.repository = repository;
	}

	@Override
	public Completable onUpdate() {
		return repository.update(addressEntityForm, addressIdentifier);
	}
}
