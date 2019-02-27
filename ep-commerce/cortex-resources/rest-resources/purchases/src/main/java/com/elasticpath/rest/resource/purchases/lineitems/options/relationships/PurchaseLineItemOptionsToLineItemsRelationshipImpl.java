/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.options.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.OptionsForPurchaseLineItemRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Purchase line item options to purchase line item.
 */
public class PurchaseLineItemOptionsToLineItemsRelationshipImpl implements OptionsForPurchaseLineItemRelationship.LinkFrom {

	private final PurchaseLineItemOptionsIdentifier purchaseLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemIdentifier purchaseLineItemIdentifier
	 */
	@Inject
	public PurchaseLineItemOptionsToLineItemsRelationshipImpl(@RequestIdentifier final PurchaseLineItemOptionsIdentifier purchaseLineItemIdentifier) {
		this.purchaseLineItemIdentifier = purchaseLineItemIdentifier;
	}

	@Override
	public Observable<PurchaseLineItemIdentifier> onLinkFrom() {
		return Observable.just(purchaseLineItemIdentifier.getPurchaseLineItem());
	}
}
