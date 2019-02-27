/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.options.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.LineItemOptionsForPurchaseLineItemOptionRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Purchase line item option to line item options link.
 */
public class PurchaseLineItemOptionToLineItemOptionsRelationshipImpl implements LineItemOptionsForPurchaseLineItemOptionRelationship.LinkTo {

	private final PurchaseLineItemOptionIdentifier purchaseLineItemOptionIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemOptionIdentifier purchaseLineItemOptionIdentifier
	 */
	@Inject
	public PurchaseLineItemOptionToLineItemOptionsRelationshipImpl(
			@RequestIdentifier final PurchaseLineItemOptionIdentifier purchaseLineItemOptionIdentifier) {
		this.purchaseLineItemOptionIdentifier = purchaseLineItemOptionIdentifier;
	}

	@Override
	public Observable<PurchaseLineItemOptionsIdentifier> onLinkTo() {
		return Observable.just(purchaseLineItemOptionIdentifier.getPurchaseLineItemOptions());
	}
}
