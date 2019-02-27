/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForOrderCouponIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForOrderCouponResource;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading promotion for coupon.
 */
public class ReadAppliedPromotionsForOrderCouponPrototype implements AppliedPromotionsForOrderCouponResource.Read {

	private final AppliedPromotionsForOrderCouponIdentifier appliedPromotionsForOrderCouponIdentifier;
	private final LinksRepository<AppliedPromotionsForOrderCouponIdentifier, PromotionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForOrderCouponIdentifier	identifier
	 * @param repository								repository
	 */
	@Inject
	public ReadAppliedPromotionsForOrderCouponPrototype(
			@RequestIdentifier final AppliedPromotionsForOrderCouponIdentifier appliedPromotionsForOrderCouponIdentifier,
			@ResourceRepository final LinksRepository<AppliedPromotionsForOrderCouponIdentifier, PromotionIdentifier> repository) {
		this.appliedPromotionsForOrderCouponIdentifier = appliedPromotionsForOrderCouponIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PromotionIdentifier> onRead() {
		return repository.getElements(appliedPromotionsForOrderCouponIdentifier);
	}
}
