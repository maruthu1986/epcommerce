/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billingaddress.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.BillingaddressInfoIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorIdentifier;
import com.elasticpath.rest.definition.orders.SelectorForBillingaddressInfoRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Billing address info to billing address selector.
 */
public class BillingAddressInfoToSelectorRelationshipImpl implements SelectorForBillingaddressInfoRelationship.LinkTo {

	private final BillingaddressInfoIdentifier billingaddressInfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param billingaddressInfoIdentifier billingaddressInfoIdentifier
	 */
	@Inject
	public BillingAddressInfoToSelectorRelationshipImpl(@RequestIdentifier final BillingaddressInfoIdentifier billingaddressInfoIdentifier) {
		this.billingaddressInfoIdentifier = billingaddressInfoIdentifier;
	}

	@Override
	public Observable<BillingaddressInfoSelectorIdentifier> onLinkTo() {
		return Observable.just(BillingaddressInfoSelectorIdentifier.builder()
				.withBillingaddressInfo(billingaddressInfoIdentifier)
				.build());
	}
}
