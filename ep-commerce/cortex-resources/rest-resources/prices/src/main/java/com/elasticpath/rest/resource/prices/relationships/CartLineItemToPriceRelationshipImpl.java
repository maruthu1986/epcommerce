/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForCartLineItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForCartLineItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a link from cart lineitem to price.
 */
public class CartLineItemToPriceRelationshipImpl implements PriceForCartLineItemRelationship.LinkTo {

	private final LineItemIdentifier lineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param lineItemIdentifier	lineItemIdentifier
	 */
	@Inject
	public CartLineItemToPriceRelationshipImpl(@RequestIdentifier final LineItemIdentifier lineItemIdentifier) {
		this.lineItemIdentifier = lineItemIdentifier;
	}

	@Override
	public Observable<PriceForCartLineItemIdentifier> onLinkTo() {
		return Observable.just(PriceForCartLineItemIdentifier.builder()
				.withLineItem(lineItemIdentifier)
				.build());
	}
}
