/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.AddressResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Address prototype for Read operation.
 */
public class ReadAddressPrototype implements AddressResource.Read {

	private final AddressIdentifier addressIdentifier;

	private final Repository<AddressEntity, AddressIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param addressIdentifier addressIdentifier
	 * @param repository        repository
	 */
	@Inject
	public ReadAddressPrototype(@RequestIdentifier final AddressIdentifier addressIdentifier,
			@ResourceRepository final Repository<AddressEntity, AddressIdentifier> repository) {
		this.addressIdentifier = addressIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<AddressEntity> onRead() {
		return repository.findOne(addressIdentifier);
	}
}
