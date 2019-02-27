/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.relationships;

import java.util.List;
import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemsForLineItemRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.PathIdentifier;

/**
 * Purchase line item to line items list link. This is rendered as 'list' rel.
 */
public class PurchaseLineItemToLineItemsRelationshipImpl implements PurchaseLineItemsForLineItemRelationship.LinkTo {

	private final PurchaseLineItemIdentifier purchaseLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemIdentifier purchaseLineItemIdentifier
	 */
	@Inject
	public PurchaseLineItemToLineItemsRelationshipImpl(@RequestIdentifier final PurchaseLineItemIdentifier purchaseLineItemIdentifier) {
		this.purchaseLineItemIdentifier = purchaseLineItemIdentifier;
	}

	@Override
	public Observable<PurchaseLineItemsIdentifier> onLinkTo() {
		IdentifierPart<List<String>> lineItemParentId = ((PathIdentifier) purchaseLineItemIdentifier.getLineItemId()).extractParentId();
		if (lineItemParentId.getValue().isEmpty()) {
			return Observable.just(purchaseLineItemIdentifier.getPurchaseLineItems());
		} else {
			return Observable.empty();
		}
	}
}
