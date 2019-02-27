/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billingaddress.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.BillingaddressInfoIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.orders.OrderToBillingaddressRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Billing address info to the order link.
 */
public class BillingAddressToOrderRelationshipImpl implements OrderToBillingaddressRelationship.LinkFrom {

	private final BillingaddressInfoIdentifier billingaddressInfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param billingaddressInfoIdentifier billingaddressInfoIdentifier
	 */
	@Inject
	public BillingAddressToOrderRelationshipImpl(@RequestIdentifier final BillingaddressInfoIdentifier billingaddressInfoIdentifier) {
		this.billingaddressInfoIdentifier = billingaddressInfoIdentifier;
	}

	@Override
	public Observable<OrderIdentifier> onLinkFrom() {
		return Observable.just(billingaddressInfoIdentifier.getOrder());
	}
}
