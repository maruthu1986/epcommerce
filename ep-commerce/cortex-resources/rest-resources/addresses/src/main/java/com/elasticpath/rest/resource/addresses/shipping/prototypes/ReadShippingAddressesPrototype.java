/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.shipping.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.addresses.ShippingAddressesResource;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Shipping Addresses prototype for Read operation.
 */
public class ReadShippingAddressesPrototype implements ShippingAddressesResource.Read {

	private final IdentifierPart<String> scope;

	private final Repository<AddressEntity, AddressIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param scope      scope
	 * @param repository repository
	 */
	@Inject
	public ReadShippingAddressesPrototype(@UriPart(AddressesIdentifier.SCOPE) final IdentifierPart<String> scope,
			@ResourceRepository final Repository<AddressEntity, AddressIdentifier> repository) {
		this.scope = scope;
		this.repository = repository;
	}

	@Override
	public Observable<AddressIdentifier> onRead() {
		return repository.findAll(scope);
	}
}
