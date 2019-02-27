/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.addresses.AddressesForProfileRelationship;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Profile to addresses link.
 */
public class ProfileToAddressesRelationshipImpl implements AddressesForProfileRelationship.LinkTo {

	private final ProfileIdentifier profileIdentifier;

	/**
	 * Constructor.
	 *
	 * @param profileIdentifier profileIdentifier
	 */
	@Inject
	public ProfileToAddressesRelationshipImpl(@RequestIdentifier final ProfileIdentifier profileIdentifier) {
		this.profileIdentifier = profileIdentifier;
	}

	@Override
	public Observable<AddressesIdentifier> onLinkTo() {
		return Observable.just(AddressesIdentifier.builder()
				.withScope(profileIdentifier.getScope())
				.build());
	}
}
