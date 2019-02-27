/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartLineItemIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartLineItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Adds a lineitem link in promotions.
 */
public class AppliedPromotionsToCartLineItemRelationshipImpl implements AppliedPromotionsForCartLineItemRelationship.LinkFrom {

	private final AppliedPromotionsForCartLineItemIdentifier appliedPromotionsForCartLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForCartLineItemIdentifier identifier
	 */
	@Inject
	public AppliedPromotionsToCartLineItemRelationshipImpl(@RequestIdentifier final AppliedPromotionsForCartLineItemIdentifier
																appliedPromotionsForCartLineItemIdentifier) {
		this.appliedPromotionsForCartLineItemIdentifier = appliedPromotionsForCartLineItemIdentifier;
	}

	@Override
	public Observable<LineItemIdentifier> onLinkFrom() {
		LineItemIdentifier lineItemIdentifier = appliedPromotionsForCartLineItemIdentifier.getLineItem();
		IdentifierPart<String> lineItemId =  lineItemIdentifier.getLineItemId();
		LineItemsIdentifier lineItemsIdentifier = lineItemIdentifier.getLineItems();
		return Observable.just(LineItemIdentifier.builder()
				.withLineItemId(lineItemId)
				.withLineItems(lineItemsIdentifier)
				.build());
	}
}
