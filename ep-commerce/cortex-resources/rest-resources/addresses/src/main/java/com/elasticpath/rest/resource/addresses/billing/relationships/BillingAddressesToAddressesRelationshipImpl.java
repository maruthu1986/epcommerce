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
 * Billing addresses to addresses link.
 */
public class BillingAddressesToAddressesRelationshipImpl implements BillingAddressesFromAddressesRelationship.LinkFrom {

	private final BillingAddressesIdentifier billingAddressesIdentifier;

	/**
	 * Constructor.
	 *
	 * @param billingAddressesIdentifier billingAddressesIdentifier
	 */
	@Inject
	public BillingAddressesToAddressesRelationshipImpl(@RequestIdentifier final BillingAddressesIdentifier billingAddressesIdentifier) {
		this.billingAddressesIdentifier = billingAddressesIdentifier;
	}

	@Override
	public Observable<AddressesIdentifier> onLinkFrom() {
		return Observable.just(billingAddressesIdentifier.getAddresses());
	}
}
