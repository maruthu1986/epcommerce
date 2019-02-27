/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.billing.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.addresses.BillingAddressesFromAddressesRelationship;
import com.elasticpath.rest.definition.addresses.BillingAddressesIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Addresses to billing addresses link.
 */
public class AddressesToBillingAddressesRelationshipImpl implements BillingAddressesFromAddressesRelationship.LinkTo {

	private final AddressesIdentifier addressesIdentifier;

	/**
	 * Constructor.
	 *
	 * @param addressesIdentifier addressesIdentifier
	 */
	@Inject
	public AddressesToBillingAddressesRelationshipImpl(@RequestIdentifier final AddressesIdentifier addressesIdentifier) {
		this.addressesIdentifier = addressesIdentifier;
	}

	@Override
	public Observable<BillingAddressesIdentifier> onLinkTo() {
		return Observable.just(BillingAddressesIdentifier.builder()
				.withAddresses(addressesIdentifier)
				.build());
	}
}
