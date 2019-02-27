/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemToPurchaseRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Link from item to the purchase to which it belongs.
 */
public class PurchaseLineItemToPurchaseImpl implements PurchaseLineItemToPurchaseRelationship.LinkTo {

	private final PurchaseLineItemIdentifier purchaseLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemIdentifier identifier
	 */
	@Inject
	public PurchaseLineItemToPurchaseImpl(@RequestIdentifier final PurchaseLineItemIdentifier purchaseLineItemIdentifier) {
		this.purchaseLineItemIdentifier = purchaseLineItemIdentifier;
	}

	@Override
	public Observable<PurchaseIdentifier> onLinkTo() {
		return Observable.just(purchaseLineItemIdentifier.getPurchaseLineItems().getPurchase());
	}
}
