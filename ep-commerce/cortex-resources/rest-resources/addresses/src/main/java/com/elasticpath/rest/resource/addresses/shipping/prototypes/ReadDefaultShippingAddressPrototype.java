/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.shipping.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.AliasRepository;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.DefaultShippingAddressIdentifier;
import com.elasticpath.rest.definition.addresses.DefaultShippingAddressResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Default Shipping Address prototype for Read operation.
 */
public class ReadDefaultShippingAddressPrototype implements DefaultShippingAddressResource.Read {

	private final DefaultShippingAddressIdentifier defaultShippingAddressIdentifier;

	private final AliasRepository<DefaultShippingAddressIdentifier, AddressIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param defaultShippingAddressIdentifier defaultShippingAddressIdentifier
	 * @param repository                       repository
	 */
	@Inject
	public ReadDefaultShippingAddressPrototype(@RequestIdentifier final DefaultShippingAddressIdentifier defaultShippingAddressIdentifier,
			@ResourceRepository final AliasRepository<DefaultShippingAddressIdentifier, AddressIdentifier> repository) {
		this.defaultShippingAddressIdentifier = defaultShippingAddressIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<AddressIdentifier> onRead() {
		return repository.resolve(defaultShippingAddressIdentifier);
	}
}
