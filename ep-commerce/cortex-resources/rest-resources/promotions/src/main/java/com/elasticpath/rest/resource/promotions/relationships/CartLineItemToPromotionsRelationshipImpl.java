/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartLineItemIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartLineItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a promotions link in cart lineitem.
 */
public class CartLineItemToPromotionsRelationshipImpl implements AppliedPromotionsForCartLineItemRelationship.LinkTo {

	private final LineItemIdentifier lineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param lineItemIdentifier	identifier
	 */
	@Inject
	public CartLineItemToPromotionsRelationshipImpl(@RequestIdentifier final LineItemIdentifier lineItemIdentifier) {
		this.lineItemIdentifier = lineItemIdentifier;
	}

	@Override
	public Observable<AppliedPromotionsForCartLineItemIdentifier> onLinkTo() {
		return Observable.just(AppliedPromotionsForCartLineItemIdentifier.builder()
				.withLineItem(lineItemIdentifier)
				.build());
	}
}
