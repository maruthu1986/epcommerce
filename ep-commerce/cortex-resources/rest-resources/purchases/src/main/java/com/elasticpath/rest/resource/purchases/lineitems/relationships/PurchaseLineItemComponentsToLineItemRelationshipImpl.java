/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.ComponentsForPurchaseLineItemRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemComponentsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Purchase line item components to the parent purchase line item. This is rendered as 'lineitem' rel.
 */
public class PurchaseLineItemComponentsToLineItemRelationshipImpl implements ComponentsForPurchaseLineItemRelationship.LinkFrom {

	private final PurchaseLineItemComponentsIdentifier purchaseLineItemComponentsIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemComponentsIdentifier purchaseLineItemComponentsIdentifier
	 */
	@Inject
	public PurchaseLineItemComponentsToLineItemRelationshipImpl(
			@RequestIdentifier final PurchaseLineItemComponentsIdentifier purchaseLineItemComponentsIdentifier) {
		this.purchaseLineItemComponentsIdentifier = purchaseLineItemComponentsIdentifier;
	}

	@Override
	public Observable<PurchaseLineItemIdentifier> onLinkFrom() {
		return Observable.just(purchaseLineItemComponentsIdentifier.getPurchaseLineItem());
	}

}
