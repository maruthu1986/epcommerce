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
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Address prototype for Delete operation.
 */
public class DeleteAddressPrototype implements AddressResource.Delete {

	private final AddressIdentifier addressIdentifier;

	private final Repository<AddressEntity, AddressIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param addressIdentifier addressIdentifier
	 * @param repository        repository
	 */
	@Inject
	public DeleteAddressPrototype(@RequestIdentifier final AddressIdentifier addressIdentifier,
			@ResourceRepository final Repository<AddressEntity, AddressIdentifier> repository) {
		this.addressIdentifier = addressIdentifier;
		this.repository = repository;
	}

	@Override
	public Completable onDelete() {
		return repository.delete(addressIdentifier);
	}
}
