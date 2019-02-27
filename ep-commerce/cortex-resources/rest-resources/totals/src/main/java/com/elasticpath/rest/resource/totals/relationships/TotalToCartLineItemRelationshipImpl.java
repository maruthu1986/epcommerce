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
 * Cart line item total to cart line item.
 */
public class TotalToCartLineItemRelationshipImpl implements TotalForCartLineItemRelationship.LinkFrom {

	private final CartLineItemTotalIdentifier cartLineItemTotalIdentifier;

	/**
	 * Contructor.
	 *
	 * @param cartLineItemTotalIdentifier cartLineItemTotalIdentifier
	 */
	@Inject
	public TotalToCartLineItemRelationshipImpl(@RequestIdentifier final CartLineItemTotalIdentifier cartLineItemTotalIdentifier) {
		this.cartLineItemTotalIdentifier = cartLineItemTotalIdentifier;
	}

	@Override
	public Observable<LineItemIdentifier> onLinkFrom() {
		return Observable.just(cartLineItemTotalIdentifier.getLineItem());
	}
}
