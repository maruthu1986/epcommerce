/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.LineItemsForPurchaseRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Purchase line items list to purchase link.
 */
public class PurchaseLineItemsToPurchaseRelationshipImpl implements LineItemsForPurchaseRelationship.LinkFrom {

	private final PurchaseLineItemsIdentifier purchaseLineItemsIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemsIdentifier purchaseLineItemsIdentifier
	 */
	@Inject
	public PurchaseLineItemsToPurchaseRelationshipImpl(@RequestIdentifier final PurchaseLineItemsIdentifier purchaseLineItemsIdentifier) {
		this.purchaseLineItemsIdentifier = purchaseLineItemsIdentifier;
	}

	@Override
	public Observable<PurchaseIdentifier> onLinkFrom() {
		return Observable.just(purchaseLineItemsIdentifier.getPurchase());
	}
}
