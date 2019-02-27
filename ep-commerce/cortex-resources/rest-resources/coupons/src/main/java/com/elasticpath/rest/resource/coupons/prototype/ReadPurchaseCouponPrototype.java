/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.coupons.PurchaseCouponIdentifier;
import com.elasticpath.rest.definition.coupons.PurchaseCouponResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read operation of purchase coupon.
 */
public class ReadPurchaseCouponPrototype implements PurchaseCouponResource.Read {

	private final PurchaseCouponIdentifier purchaseCouponIdentifier;
	private final Repository<CouponEntity, PurchaseCouponIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchaseCouponIdentifier purchase coupon identifier
	 * @param repository repo
	 */
	@Inject
	public ReadPurchaseCouponPrototype(@RequestIdentifier final PurchaseCouponIdentifier purchaseCouponIdentifier,
									   @ResourceRepository final Repository<CouponEntity, PurchaseCouponIdentifier> repository) {
		this.purchaseCouponIdentifier = purchaseCouponIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<CouponEntity> onRead() {
		return repository.findOne(purchaseCouponIdentifier);
	}
}
