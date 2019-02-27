/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.shipping.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.AliasRepository;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.DefaultShippingAddressFromShippingAddressesRelationship;
import com.elasticpath.rest.definition.addresses.DefaultShippingAddressIdentifier;
import com.elasticpath.rest.definition.addresses.ShippingAddressesIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Shipping addresses to default shipping address link.
 */
public class ShippingAddressToDefaultShippingAddressRelationshipImpl implements DefaultShippingAddressFromShippingAddressesRelationship.LinkTo {

	private final ShippingAddressesIdentifier shippingAddressesIdentifier;

	private final AliasRepository<DefaultShippingAddressIdentifier, AddressIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shippingAddressesIdentifier shippingAddressesIdentifier
	 * @param repository                  repository
	 */
	@Inject
	public ShippingAddressToDefaultShippingAddressRelationshipImpl(@RequestIdentifier final ShippingAddressesIdentifier shippingAddressesIdentifier,
			@ResourceRepository final AliasRepository<DefaultShippingAddressIdentifier, AddressIdentifier> repository) {
		this.shippingAddressesIdentifier = shippingAddressesIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<DefaultShippingAddressIdentifier> onLinkTo() {
		DefaultShippingAddressIdentifier defaultShippingAddressIdentifier = DefaultShippingAddressIdentifier.builder()
				.withShippingAddresses(shippingAddressesIdentifier)
				.build();
		
		return repository.resolve(defaultShippingAddressIdentifier)
				.toObservable()
				.map(addressIdentifier -> defaultShippingAddressIdentifier)
				.onErrorResumeNext(Observable.empty());
	}
}
