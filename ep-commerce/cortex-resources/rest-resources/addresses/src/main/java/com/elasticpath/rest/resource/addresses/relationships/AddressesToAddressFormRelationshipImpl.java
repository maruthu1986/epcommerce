/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.addresses.AddAddressFormFromAddressesRelationship;
import com.elasticpath.rest.definition.addresses.AddressFormIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Addresses to add address form link.
 */
public class AddressesToAddressFormRelationshipImpl implements AddAddressFormFromAddressesRelationship.LinkTo {

	private final AddressesIdentifier addressesIdentifier;

	/**
	 * Constructor.
	 *
	 * @param addressesIdentifier addressesIdentifier
	 */
	@Inject
	public AddressesToAddressFormRelationshipImpl(@RequestIdentifier final AddressesIdentifier addressesIdentifier) {
		this.addressesIdentifier = addressesIdentifier;
	}

	@Override
	public Observable<AddressFormIdentifier> onLinkTo() {
		return Observable.just(AddressFormIdentifier.builder()
				.withAddresses(addressesIdentifier)
				.build());
	}
}
