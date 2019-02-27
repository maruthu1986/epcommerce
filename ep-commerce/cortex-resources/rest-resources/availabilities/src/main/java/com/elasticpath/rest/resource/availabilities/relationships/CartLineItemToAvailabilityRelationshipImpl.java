/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.availabilities.AvailabilityForCartLineItemIdentifier;
import com.elasticpath.rest.definition.availabilities.CartLineItemToAvailabilityRelationship;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Cart line item to availability link.
 */
public class CartLineItemToAvailabilityRelationshipImpl implements CartLineItemToAvailabilityRelationship.LinkTo {

	private final LineItemIdentifier lineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param lineItemIdentifier lineItemIdentifier
	 */
	@Inject
	public CartLineItemToAvailabilityRelationshipImpl(@RequestIdentifier final LineItemIdentifier lineItemIdentifier) {
		this.lineItemIdentifier = lineItemIdentifier;
	}

	@Override
	public Observable<AvailabilityForCartLineItemIdentifier> onLinkTo() {
		return Observable.just(AvailabilityForCartLineItemIdentifier.builder()
				.withLineItem(lineItemIdentifier)
				.build());
	}
}
