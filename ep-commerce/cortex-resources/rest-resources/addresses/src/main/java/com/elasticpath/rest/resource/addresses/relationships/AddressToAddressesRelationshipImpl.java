/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesForAddressRelationship;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Address to addresses link.
 */
public class AddressToAddressesRelationshipImpl implements AddressesForAddressRelationship.LinkTo {

	private final AddressIdentifier addressIdentifier;

	/**
	 * Constructor.
	 *
	 * @param addressIdentifier addressIdentifier
	 */
	@Inject
	public AddressToAddressesRelationshipImpl(@RequestIdentifier final AddressIdentifier addressIdentifier) {
		this.addressIdentifier = addressIdentifier;
	}

	@Override
	public Observable<AddressesIdentifier> onLinkTo() {
		return Observable.just(addressIdentifier.getAddresses());
	}
}
