/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Adds a purchase link in promotions.
 */
public class AppliedPromotionsToPurchaseRelationshipImpl implements AppliedPromotionsForPurchaseRelationship.LinkFrom {

	private final AppliedPromotionsForPurchaseIdentifier appliedPromotionsForPurchaseIdentifier;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForPurchaseIdentifier	identifier
	 */
	@Inject
	public AppliedPromotionsToPurchaseRelationshipImpl(@RequestIdentifier final AppliedPromotionsForPurchaseIdentifier
																   appliedPromotionsForPurchaseIdentifier) {
		this.appliedPromotionsForPurchaseIdentifier = appliedPromotionsForPurchaseIdentifier;
	}

	@Override
	public Observable<PurchaseIdentifier> onLinkFrom() {
		PurchaseIdentifier purchaseIdentifier = appliedPromotionsForPurchaseIdentifier.getPurchase();
		IdentifierPart<String> purchaseId = purchaseIdentifier.getPurchaseId();
		PurchasesIdentifier purchasesIdentifier = purchaseIdentifier.getPurchases();
		return Observable.just(PurchaseIdentifier.builder()
				.withPurchaseId(purchaseId)
				.withPurchases(purchasesIdentifier)
				.build());
	}
}
