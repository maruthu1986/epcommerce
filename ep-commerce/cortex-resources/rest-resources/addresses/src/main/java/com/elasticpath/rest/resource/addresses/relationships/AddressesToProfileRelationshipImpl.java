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
import com.elasticpath.rest.helix.data.annotation.UserId;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Addresses to profile link.
 */
public class AddressesToProfileRelationshipImpl implements AddressesForProfileRelationship.LinkFrom {

	private final String userId;

	private final AddressesIdentifier addressesIdentifier;

	/**
	 * Constructor.
	 *
	 * @param userId              userId
	 * @param addressesIdentifier addressesIdentifier
	 */
	@Inject
	public AddressesToProfileRelationshipImpl(@UserId final String userId, @RequestIdentifier final AddressesIdentifier addressesIdentifier) {
		this.userId = userId;
		this.addressesIdentifier = addressesIdentifier;
	}

	@Override
	public Observable<ProfileIdentifier> onLinkFrom() {
		return Observable.just(ProfileIdentifier.builder()
				.withProfileId(StringIdentifier.of(userId))
				.withScope(addressesIdentifier.getScope())
				.build());
	}
}
