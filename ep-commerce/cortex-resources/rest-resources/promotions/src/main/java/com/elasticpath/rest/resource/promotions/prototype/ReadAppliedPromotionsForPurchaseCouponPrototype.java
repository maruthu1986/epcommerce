/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseCouponIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseCouponResource;
import com.elasticpath.rest.definition.promotions.PurchaseCouponPromotionIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading promotion for coupon.
 */
public class ReadAppliedPromotionsForPurchaseCouponPrototype implements AppliedPromotionsForPurchaseCouponResource.Read {

	private final AppliedPromotionsForPurchaseCouponIdentifier appliedPromotionsForPurchaseCouponIdentifier;
	private final LinksRepository<AppliedPromotionsForPurchaseCouponIdentifier, PurchaseCouponPromotionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForPurchaseCouponIdentifier	identifier
	 * @param repository									repository
	 */
	@Inject
	public ReadAppliedPromotionsForPurchaseCouponPrototype(
			@RequestIdentifier final AppliedPromotionsForPurchaseCouponIdentifier appliedPromotionsForPurchaseCouponIdentifier,
			@ResourceRepository final LinksRepository<AppliedPromotionsForPurchaseCouponIdentifier, PurchaseCouponPromotionIdentifier> repository) {
		this.appliedPromotionsForPurchaseCouponIdentifier = appliedPromotionsForPurchaseCouponIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PurchaseCouponPromotionIdentifier> onRead() {
		return repository.getElements(appliedPromotionsForPurchaseCouponIdentifier);
	}
}
