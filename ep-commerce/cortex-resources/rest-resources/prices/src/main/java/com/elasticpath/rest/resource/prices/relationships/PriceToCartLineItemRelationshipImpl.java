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
 * Adds a link from price to cart lineitem.
 */
public class PriceToCartLineItemRelationshipImpl implements PriceForCartLineItemRelationship.LinkFrom {

	private final PriceForCartLineItemIdentifier priceForCartLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param priceForCartLineItemIdentifier	priceForCartLineItemIdentifier
	 */
	@Inject
	public PriceToCartLineItemRelationshipImpl(@RequestIdentifier final PriceForCartLineItemIdentifier priceForCartLineItemIdentifier) {
		this.priceForCartLineItemIdentifier = priceForCartLineItemIdentifier;
	}

	@Override
	public Observable<LineItemIdentifier> onLinkFrom() {
		LineItemIdentifier lineItemIdentifier = priceForCartLineItemIdentifier.getLineItem();
		return Observable.just(LineItemIdentifier.builder()
				.withLineItemId(lineItemIdentifier.getLineItemId())
				.withLineItems(lineItemIdentifier.getLineItems())
				.build());
	}
}
