/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.purchase;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.coupons.PurchaseCouponIdentifier;
import com.elasticpath.rest.definition.coupons.PurchaseCouponListIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;

/**
 * This repository maps coupon list to single coupon for the purchase.
 *
 * @param <LI> coupon List Identifier
 * @param <CI> purchase Coupon Identifier
 */
@Component
public class PurchaseCouponListToPurchaseCouponsRepository<LI extends PurchaseCouponListIdentifier, CI extends PurchaseCouponIdentifier>
		implements LinksRepository<PurchaseCouponListIdentifier, PurchaseCouponIdentifier> {

	private CouponRepository couponRepository;

	@Override
	public Observable<PurchaseCouponIdentifier> getElements(final PurchaseCouponListIdentifier listIdentifier) {
		String scope = listIdentifier.getPurchase().getPurchases().getScope().getValue();
		String purchaseId = listIdentifier.getPurchase().getPurchaseId().getValue();

		//codes for the purchase
		return couponRepository.getAppliedCoupons(scope, purchaseId)
				.map(AppliedCoupon::getCouponCode)
				.map(couponCode -> build(listIdentifier, couponCode));
	}

	/**
	 * Builds purchase coupon id given list id.
	 *
	 * @param listIdentifier list identifier
	 * @param couponId       coupon id
	 * @return purchase coupon identifier
	 */
	protected PurchaseCouponIdentifier build(final PurchaseCouponListIdentifier listIdentifier, final String couponId) {
		return PurchaseCouponIdentifier.builder()
				.withPurchaseCouponList(listIdentifier)
				.withCouponId(StringIdentifier.of(couponId))
				.build();
	}

	@Reference
	public void setCouponRepository(final CouponRepository couponRepository) {
		this.couponRepository = couponRepository;
	}
}
