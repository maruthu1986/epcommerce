/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.totals.CartLineItemTotalIdentifier;
import com.elasticpath.rest.definition.totals.TotalForCartLineItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Cart line item to cart line item total link.
 */
public class CartLineItemToTotalRelationshipImpl implements TotalForCartLineItemRelationship.LinkTo {

	private final LineItemIdentifier lineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param lineItemIdentifier lineItemIdentifier
	 */
	@Inject
	public CartLineItemToTotalRelationshipImpl(@RequestIdentifier final LineItemIdentifier lineItemIdentifier) {
		this.lineItemIdentifier = lineItemIdentifier;
	}


	@Override
	public Observable<CartLineItemTotalIdentifier> onLinkTo() {
		return Observable.just(CartLineItemTotalIdentifier.builder()
				.withLineItem(lineItemIdentifier)
				.build());
	}
}
