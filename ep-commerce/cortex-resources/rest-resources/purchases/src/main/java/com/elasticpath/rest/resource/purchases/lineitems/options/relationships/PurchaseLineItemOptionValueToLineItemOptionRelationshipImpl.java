/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.options.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueIdentifier;
import com.elasticpath.rest.definition.purchases.ValueForPurchaseLineItemOptionRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Purchase line item option value to option link.
 */
public class PurchaseLineItemOptionValueToLineItemOptionRelationshipImpl implements ValueForPurchaseLineItemOptionRelationship.LinkFrom {

	private final PurchaseLineItemOptionValueIdentifier purchaseLineItemOptionValueIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemOptionValueIdentifier purchaseLineItemOptionValueIdentifier
	 */
	@Inject
	public PurchaseLineItemOptionValueToLineItemOptionRelationshipImpl(
			@RequestIdentifier final PurchaseLineItemOptionValueIdentifier purchaseLineItemOptionValueIdentifier) {
		this.purchaseLineItemOptionValueIdentifier = purchaseLineItemOptionValueIdentifier;
	}

	@Override
	public Observable<PurchaseLineItemOptionIdentifier> onLinkFrom() {
		return Observable.just(purchaseLineItemOptionValueIdentifier.getPurchaseLineItemOption());
	}
}
