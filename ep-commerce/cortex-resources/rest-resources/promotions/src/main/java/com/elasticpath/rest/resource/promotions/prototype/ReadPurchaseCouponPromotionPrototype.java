/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.definition.promotions.PurchaseCouponPromotionIdentifier;
import com.elasticpath.rest.definition.promotions.PurchaseCouponPromotionResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading a purchase coupon promotion.
 */
public class ReadPurchaseCouponPromotionPrototype implements PurchaseCouponPromotionResource.Read {

	private final PurchaseCouponPromotionIdentifier purchaseCouponPromotionIdentifier;
	private final Repository<PromotionEntity, PurchaseCouponPromotionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchaseCouponPromotionIdentifier	identifier
	 * @param repository						repository
	 */
	@Inject
	public ReadPurchaseCouponPromotionPrototype(@RequestIdentifier final PurchaseCouponPromotionIdentifier purchaseCouponPromotionIdentifier,
											@ResourceRepository final Repository<PromotionEntity, PurchaseCouponPromotionIdentifier> repository) {
		this.purchaseCouponPromotionIdentifier = purchaseCouponPromotionIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<PromotionEntity> onRead() {
		return repository.findOne(purchaseCouponPromotionIdentifier);
	}
}
