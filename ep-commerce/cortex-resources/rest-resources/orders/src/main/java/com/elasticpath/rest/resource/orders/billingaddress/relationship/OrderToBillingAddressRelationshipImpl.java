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
 * Order to billing address info link.
 */
public class OrderToBillingAddressRelationshipImpl implements OrderToBillingaddressRelationship.LinkTo {

	private final OrderIdentifier orderIdentifier;

	/**
	 * Constructor.
	 *
	 * @param orderIdentifier orderIdentifier
	 */
	@Inject
	public OrderToBillingAddressRelationshipImpl(@RequestIdentifier final OrderIdentifier orderIdentifier) {
		this.orderIdentifier = orderIdentifier;
	}

	@Override
	public Observable<BillingaddressInfoIdentifier> onLinkTo() {
		return Observable.just(BillingaddressInfoIdentifier.builder()
				.withOrder(orderIdentifier)
				.build());
	}
}
