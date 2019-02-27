/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.lineitems.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsForCartRelationship;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Line items to car link.
 */
public class LineItemsToCartRelationshipImpl implements LineItemsForCartRelationship.LinkFrom {

	private final LineItemsIdentifier lineItemsIdentifier;

	/**
	 * Constructor.
	 *
	 * @param lineItemsIdentifier line item identifier
	 */
	@Inject
	public LineItemsToCartRelationshipImpl(@RequestIdentifier final LineItemsIdentifier lineItemsIdentifier) {
		this.lineItemsIdentifier = lineItemsIdentifier;
	}

	@Override
	public Observable<CartIdentifier> onLinkFrom() {
		return Observable.just(lineItemsIdentifier.getCart());
	}
}
