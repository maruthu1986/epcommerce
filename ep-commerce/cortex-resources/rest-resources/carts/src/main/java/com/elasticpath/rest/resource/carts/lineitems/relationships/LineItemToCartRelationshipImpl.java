/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.lineitems.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.CartForLineItemRelationship;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Line item to cart link.
 */
public class LineItemToCartRelationshipImpl implements CartForLineItemRelationship.LinkTo {

	private final LineItemIdentifier lineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param lineItemIdentifier lineItemIdentifier
	 */
	@Inject
	public LineItemToCartRelationshipImpl(@RequestIdentifier final LineItemIdentifier lineItemIdentifier) {
		this.lineItemIdentifier = lineItemIdentifier;
	}

	@Override
	public Observable<CartIdentifier> onLinkTo() {
		return Observable.just(lineItemIdentifier.getLineItems().getCart());
	}
}
