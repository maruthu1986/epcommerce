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
 * Email info to order link.
 */
public class EmailInfoToOrderRelationshipImpl implements EmailInfoForOrderRelationship.LinkFrom {

	private final EmailInfoIdentifier emailInfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param emailInfoIdentifier emailInfoIdentifier
	 */
	@Inject
	public EmailInfoToOrderRelationshipImpl(@RequestIdentifier final EmailInfoIdentifier emailInfoIdentifier) {
		this.emailInfoIdentifier = emailInfoIdentifier;
	}

	@Override
	public Observable<OrderIdentifier> onLinkFrom() {
		return Observable.just(emailInfoIdentifier.getOrder());
	}
}
