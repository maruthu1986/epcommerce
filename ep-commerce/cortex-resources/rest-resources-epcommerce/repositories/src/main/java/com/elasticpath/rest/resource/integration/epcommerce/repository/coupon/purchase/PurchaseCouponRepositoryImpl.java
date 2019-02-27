/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.purchase;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.coupons.PurchaseCouponIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponEntityBuilder;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;

/**
 * Repository that returns coupon entity for each applied coupon in purchase list.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class PurchaseCouponRepositoryImpl<E extends CouponEntity, I extends PurchaseCouponIdentifier>
		implements Repository<CouponEntity, PurchaseCouponIdentifier> {

	/**
	 * Error message for coupon not found.
	 */
	static final String COUPON_NOT_FOUND = "Coupon is not found for order.";

	private CouponRepository couponRepository;
	private CouponEntityBuilder builder;

	@Override
	public Single<CouponEntity> findOne(final PurchaseCouponIdentifier identifier) {
		String couponId = identifier.getCouponId().getValue();
		String purchaseId = identifier.getPurchaseCouponList().getPurchase().getPurchaseId().getValue();
		String scope = identifier.getPurchaseCouponList().getPurchase().getPurchases().getScope().getValue();

		return couponRepository.getAppliedCoupons(scope, purchaseId)
				.filter(appliedCoupon -> appliedCoupon.getCouponCode().equals(couponId))
				.flatMapSingle(appliedCoupon -> builder.build(appliedCoupon, PurchasesMediaTypes.PURCHASE.id(), purchaseId))
				.switchIfEmpty(Observable.error(ResourceOperationFailure.notFound(COUPON_NOT_FOUND)))
				.singleElement().toSingle();
	}

	@Reference
	public void setCouponEntityBuilder(final CouponEntityBuilder builder) {
		this.builder = builder;
	}

	@Reference
	public void setCouponRepository(final CouponRepository couponRepository) {
		this.couponRepository = couponRepository;
	}
}
