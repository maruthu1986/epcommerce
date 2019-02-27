/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billingaddress.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.addresses.AddressFormIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressFormRelationship;
import com.elasticpath.rest.definition.orders.BillingaddressInfoIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Billing address info to billing address form link.
 */
public class BillingAddressInfoToFormRelationshipImpl implements BillingaddressFormRelationship.LinkTo {

	private final BillingaddressInfoIdentifier billingaddressInfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param billingaddressInfoIdentifier billingaddressInfoIdentifier
	 */
	@Inject
	public BillingAddressInfoToFormRelationshipImpl(@RequestIdentifier final BillingaddressInfoIdentifier billingaddressInfoIdentifier) {
		this.billingaddressInfoIdentifier = billingaddressInfoIdentifier;
	}

	@Override
	public Observable<AddressFormIdentifier> onLinkTo() {
		return Observable.just(AddressFormIdentifier.builder()
				.withAddresses(AddressesIdentifier.builder()
						.withScope(billingaddressInfoIdentifier.getOrder().getScope())
						.build())
				.build());
	}
}
