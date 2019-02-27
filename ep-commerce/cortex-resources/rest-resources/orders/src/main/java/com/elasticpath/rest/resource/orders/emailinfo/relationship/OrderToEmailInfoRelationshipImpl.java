/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.EmailInfoForOrderRelationship;
import com.elasticpath.rest.definition.orders.EmailInfoIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Order to Email info link.
 */
public class OrderToEmailInfoRelationshipImpl implements EmailInfoForOrderRelationship.LinkTo {

	private final OrderIdentifier orderIdentifier;

	/**
	 * Constructor.
	 *
	 * @param orderIdentifier orderIdentifier
	 */
	@Inject
	public OrderToEmailInfoRelationshipImpl(@RequestIdentifier final OrderIdentifier orderIdentifier) {
		this.orderIdentifier = orderIdentifier;
	}

	@Override
	public Observable<EmailInfoIdentifier> onLinkTo() {
		return Observable.just(EmailInfoIdentifier.builder()
				.withOrder(orderIdentifier)
				.build());
	}
}
