/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.relationships.purchases;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentsForPurchaseRelationship;
import com.elasticpath.rest.definition.shipments.ShipmentsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a purchase link in shipments.
 */
public class ShipmentsToPurchaseRelationshipImpl implements ShipmentsForPurchaseRelationship.LinkFrom {

	private final PurchaseIdentifier purchaseIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentsIdentifier	identifier
	 */
	@Inject
	public ShipmentsToPurchaseRelationshipImpl(@RequestIdentifier final ShipmentsIdentifier shipmentsIdentifier) {
		this.purchaseIdentifier = shipmentsIdentifier.getPurchase();
	}

	@Override
	public Observable<PurchaseIdentifier> onLinkFrom() {
		return Observable.just(purchaseIdentifier);
	}
}
