/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.relationships;

import java.util.List;
import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.ParentComponentsForPurchaseLineItemRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemComponentsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.PathIdentifier;

/**
 * Purchase line item to parent line item components link. This is rendered as a 'list' rel.
 */
public class ParentComponentsForPurchaseLineItemRelationshipImpl implements ParentComponentsForPurchaseLineItemRelationship.LinkTo {

	private final PurchaseLineItemIdentifier purchaseLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemIdentifier identifier
	 */
	@Inject
	public ParentComponentsForPurchaseLineItemRelationshipImpl(
			@RequestIdentifier final PurchaseLineItemIdentifier purchaseLineItemIdentifier) {
		this.purchaseLineItemIdentifier = purchaseLineItemIdentifier;
	}

	@Override
	public Observable<PurchaseLineItemComponentsIdentifier> onLinkTo() {
		IdentifierPart<List<String>> parentLineItemId = ((PathIdentifier) purchaseLineItemIdentifier.getLineItemId()).extractParentId();
		if (parentLineItemId.getValue().isEmpty()) {
			return Observable.empty();
		} else {
			PurchaseLineItemComponentsIdentifier purchaseLineItemComponentsIdentifier = PurchaseLineItemComponentsIdentifier.builder()
					.withPurchaseLineItem(PurchaseLineItemIdentifier.builder()
							.withLineItemId(parentLineItemId)
							.withPurchaseLineItems(purchaseLineItemIdentifier.getPurchaseLineItems())
							.build())
					.build();
			return Observable.just(purchaseLineItemComponentsIdentifier);
		}
	}
}
