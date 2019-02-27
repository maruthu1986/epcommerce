/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.coupons.PurchaseCouponIdentifier;
import com.elasticpath.rest.definition.coupons.PurchaseCouponListIdentifier;
import com.elasticpath.rest.definition.coupons.PurchaseCouponListResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read operation for the PurchaseCoupon.
 */
public class ReadPurchaseCouponListPrototype implements PurchaseCouponListResource.Read {

	private final PurchaseCouponListIdentifier purchaseCouponListIdentifier;
	private final LinksRepository<PurchaseCouponListIdentifier, PurchaseCouponIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchaseCouponListIdentifier purchase coupon list identifier
	 * @param repository repo
	 */
	@Inject
	public ReadPurchaseCouponListPrototype(@RequestIdentifier final PurchaseCouponListIdentifier purchaseCouponListIdentifier,
										   @ResourceRepository
										   final LinksRepository<PurchaseCouponListIdentifier, PurchaseCouponIdentifier> repository) {

		this.purchaseCouponListIdentifier = purchaseCouponListIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PurchaseCouponIdentifier> onRead() {
		return repository.getElements(purchaseCouponListIdentifier);
	}
}
