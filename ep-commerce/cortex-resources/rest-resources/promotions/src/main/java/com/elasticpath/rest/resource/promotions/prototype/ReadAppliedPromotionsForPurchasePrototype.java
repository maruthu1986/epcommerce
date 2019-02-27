/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseResource;
import com.elasticpath.rest.definition.promotions.PurchasePromotionIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading promotions for purchases.
 */
public class ReadAppliedPromotionsForPurchasePrototype implements AppliedPromotionsForPurchaseResource.Read {

	private final AppliedPromotionsForPurchaseIdentifier appliedPromotionsForPurchaseIdentifier;
	private final LinksRepository<AppliedPromotionsForPurchaseIdentifier, PurchasePromotionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForPurchaseIdentifier	identifier
	 * @param repository								repository
	 */
	@Inject
	public ReadAppliedPromotionsForPurchasePrototype(
			@RequestIdentifier final AppliedPromotionsForPurchaseIdentifier appliedPromotionsForPurchaseIdentifier,
			@ResourceRepository final LinksRepository<AppliedPromotionsForPurchaseIdentifier, PurchasePromotionIdentifier> repository) {
		this.appliedPromotionsForPurchaseIdentifier = appliedPromotionsForPurchaseIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PurchasePromotionIdentifier> onRead() {
		return repository.getElements(appliedPromotionsForPurchaseIdentifier);
	}
}
