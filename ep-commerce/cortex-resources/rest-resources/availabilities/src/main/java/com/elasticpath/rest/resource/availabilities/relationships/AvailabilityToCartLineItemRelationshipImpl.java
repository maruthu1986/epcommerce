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
 * Availability to cart line item link.
 */
public class AvailabilityToCartLineItemRelationshipImpl implements CartLineItemToAvailabilityRelationship.LinkFrom {

	private final AvailabilityForCartLineItemIdentifier availabilityForCartLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param availabilityForCartLineItemIdentifier availabilityForCartLineItemIdentifier
	 */
	@Inject
	public AvailabilityToCartLineItemRelationshipImpl(
			@RequestIdentifier final AvailabilityForCartLineItemIdentifier availabilityForCartLineItemIdentifier) {
		this.availabilityForCartLineItemIdentifier = availabilityForCartLineItemIdentifier;
	}

	@Override
	public Observable<LineItemIdentifier> onLinkFrom() {
		return Observable.just(availabilityForCartLineItemIdentifier.getLineItem());
	}
}
