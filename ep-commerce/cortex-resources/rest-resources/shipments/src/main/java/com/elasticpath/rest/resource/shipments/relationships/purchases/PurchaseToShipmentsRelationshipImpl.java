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
 * Adds a shipments link in purchase.
 */
public class PurchaseToShipmentsRelationshipImpl implements ShipmentsForPurchaseRelationship.LinkTo {

	private final PurchaseIdentifier purchaseIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseIdentifier	identifier
	 */
	@Inject
	public PurchaseToShipmentsRelationshipImpl(@RequestIdentifier final PurchaseIdentifier purchaseIdentifier) {
		this.purchaseIdentifier = purchaseIdentifier;
	}

	@Override
	public Observable<ShipmentsIdentifier> onLinkTo() {
		return Observable.just(ShipmentsIdentifier.builder()
				.withPurchase(purchaseIdentifier)
				.build());
	}
}
