/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.ProfileForAddressRelationship;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.UserId;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Address to profile link.
 */
public class AddressToProfileRelationshipImpl implements ProfileForAddressRelationship.LinkTo {

	private final String userId;

	private final AddressIdentifier addressIdentifier;

	/**
	 * Constructor.
	 *
	 * @param userId            userId
	 * @param addressIdentifier addressIdentifier
	 */
	@Inject
	public AddressToProfileRelationshipImpl(@UserId final String userId, @RequestIdentifier final AddressIdentifier addressIdentifier) {
		this.userId = userId;
		this.addressIdentifier = addressIdentifier;
	}

	@Override
	public Observable<ProfileIdentifier> onLinkTo() {
		return Observable.just(ProfileIdentifier.builder()
				.withProfileId(StringIdentifier.of(userId))
				.withScope(addressIdentifier.getAddresses().getScope())
				.build());
	}
}
