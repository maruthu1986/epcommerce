/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.shipping.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.addresses.ProfileFromShippingAddressesRelationship;
import com.elasticpath.rest.definition.addresses.ShippingAddressesIdentifier;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.UserId;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Shipping addresses to profile link.
 */
public class ShippingAddressesToProfileRelationshipImpl implements ProfileFromShippingAddressesRelationship.LinkTo {

	private final String userId;

	private final ShippingAddressesIdentifier shippingAddressesIdentifier;

	/**
	 * Constructor.
	 *
	 * @param userId                      userId
	 * @param shippingAddressesIdentifier shippingAddressesIdentifier
	 */
	@Inject
	public ShippingAddressesToProfileRelationshipImpl(@UserId final String userId,
			@RequestIdentifier final ShippingAddressesIdentifier shippingAddressesIdentifier) {
		this.userId = userId;
		this.shippingAddressesIdentifier = shippingAddressesIdentifier;
	}

	@Override
	public Observable<ProfileIdentifier> onLinkTo() {
		return Observable.just(ProfileIdentifier.builder()
				.withProfileId(StringIdentifier.of(userId))
				.withScope(shippingAddressesIdentifier.getAddresses().getScope())
				.build());
	}
}
