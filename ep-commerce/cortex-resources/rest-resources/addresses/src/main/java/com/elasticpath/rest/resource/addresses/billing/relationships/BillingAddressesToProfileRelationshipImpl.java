/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.billing.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.addresses.BillingAddressesIdentifier;
import com.elasticpath.rest.definition.addresses.ProfileFromBillingAddressesRelationship;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.UserId;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Billing addresses to profile link.
 */
public class BillingAddressesToProfileRelationshipImpl implements ProfileFromBillingAddressesRelationship.LinkTo {

	private final String userId;

	private final BillingAddressesIdentifier billingAddressesIdentifier;

	/**
	 * Constructor.
	 *
	 * @param userId                     userId
	 * @param billingAddressesIdentifier billingAddressesIdentifier
	 */
	@Inject
	public BillingAddressesToProfileRelationshipImpl(@UserId final String userId,
			@RequestIdentifier final BillingAddressesIdentifier billingAddressesIdentifier) {
		this.userId = userId;
		this.billingAddressesIdentifier = billingAddressesIdentifier;
	}

	@Override
	public Observable<ProfileIdentifier> onLinkTo() {
		return Observable.just(ProfileIdentifier.builder()
				.withProfileId(StringIdentifier.of(userId))
				.withScope(billingAddressesIdentifier.getAddresses().getScope())
				.build());
	}
}
