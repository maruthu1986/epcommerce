/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.billing.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.AliasRepository;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.DefaultBillingAddressIdentifier;
import com.elasticpath.rest.definition.addresses.DefaultBillingAddressResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Default Billing Address prototype for Read operation.
 */
public class ReadDefaultBillingAddressPrototype implements DefaultBillingAddressResource.Read {

	private final DefaultBillingAddressIdentifier defaultBillingAddressIdentifier;

	private final AliasRepository<DefaultBillingAddressIdentifier, AddressIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param defaultBillingAddressIdentifier defaultBillingAddressIdentifier
	 * @param repository                      repository
	 */
	@Inject
	public ReadDefaultBillingAddressPrototype(@RequestIdentifier final DefaultBillingAddressIdentifier defaultBillingAddressIdentifier,
			@ResourceRepository final AliasRepository<DefaultBillingAddressIdentifier, AddressIdentifier> repository) {
		this.defaultBillingAddressIdentifier = defaultBillingAddressIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<AddressIdentifier> onRead() {
		return repository.resolve(defaultBillingAddressIdentifier);
	}
}
