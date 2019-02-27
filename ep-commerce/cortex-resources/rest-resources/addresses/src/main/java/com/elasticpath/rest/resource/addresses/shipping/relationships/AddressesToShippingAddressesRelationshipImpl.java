/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.shipping.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.addresses.ShippingAddressesFromAddressesRelationship;
import com.elasticpath.rest.definition.addresses.ShippingAddressesIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Addresses to shipping addresses link.
 */
public class AddressesToShippingAddressesRelationshipImpl implements ShippingAddressesFromAddressesRelationship.LinkTo {

	private final AddressesIdentifier addressesIdentifier;

	/**
	 * Constructor.
	 *
	 * @param addressesIdentifier addressesIdentifier
	 */
	@Inject
	public AddressesToShippingAddressesRelationshipImpl(@RequestIdentifier final AddressesIdentifier addressesIdentifier) {
		this.addressesIdentifier = addressesIdentifier;
	}

	@Override
	public Observable<ShippingAddressesIdentifier> onLinkTo() {
		return Observable.just(ShippingAddressesIdentifier.builder()
				.withAddresses(addressesIdentifier)
				.build());
	}
}
