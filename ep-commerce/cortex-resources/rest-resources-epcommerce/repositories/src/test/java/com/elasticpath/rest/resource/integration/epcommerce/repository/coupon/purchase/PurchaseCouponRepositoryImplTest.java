/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.purchase;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.COUPON_CODE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PURCHASE_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponTestFactory.buildCouponEntity;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.impl.AppliedCouponImpl;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.coupons.PurchaseCouponIdentifier;
import com.elasticpath.rest.definition.coupons.PurchaseCouponListIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponEntityBuilder;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.impl.CouponRepositoryImpl;

/**
 * Test for {@link PurchaseCouponRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseCouponRepositoryImplTest {

	private static final String COUPON_ID_NOT_FOUND = "random id";

	@Mock
	private CouponEntityBuilder builder;

	@Mock
	private CouponRepositoryImpl couponRepository;

	@InjectMocks
	private PurchaseCouponRepositoryImpl<CouponEntity, PurchaseCouponIdentifier> purchaseCouponRepository;

	@Test
	public void findMatchingAppliedCouponsSuccess() {
		AppliedCoupon appliedCoupon = createAppliedCoupon();

		when(couponRepository.getAppliedCoupons(SCOPE, PURCHASE_ID)).thenReturn(Observable.just(appliedCoupon));
		when(builder.build(appliedCoupon, PurchasesMediaTypes.PURCHASE.id(), PURCHASE_ID))
				.thenReturn(Single.just(buildCouponEntity(COUPON_CODE, PURCHASE_ID, "type")));

		purchaseCouponRepository.findOne(createCouponIdentifier(COUPON_CODE))
				.test()
				.assertNoErrors()
				.assertValue(couponEntity -> couponEntity.getCode().equals(COUPON_CODE));
	}

	@Test
	public void findMatchingAppliedCouponsWithFailure() {
		AppliedCoupon appliedCoupon = createAppliedCoupon();

		when(couponRepository.getAppliedCoupons(SCOPE, PURCHASE_ID)).thenReturn(Observable.just(appliedCoupon));

		//find Applied coupon with id that is not present in coupon repository
		purchaseCouponRepository.findOne(createCouponIdentifier(COUPON_ID_NOT_FOUND))
				.test()
				.assertError(ResourceOperationFailure.class)
				.assertError(throwable -> PurchaseCouponRepositoryImpl.COUPON_NOT_FOUND.equals(throwable.getMessage()));
	}

	private AppliedCoupon createAppliedCoupon() {
		AppliedCoupon appliedCoupon = new AppliedCouponImpl();
		appliedCoupon.setCouponCode(COUPON_CODE);
		return appliedCoupon;
	}

	private PurchaseCouponIdentifier createCouponIdentifier(final String couponId) {
		PurchasesIdentifier purchases = PurchasesIdentifier.builder()
				.withScope(StringIdentifier.of(SCOPE))
				.build();

		PurchaseIdentifier purchase = PurchaseIdentifier.builder()
				.withPurchaseId(StringIdentifier.of(PURCHASE_ID))
				.withPurchases(purchases)
				.build();

		PurchaseCouponListIdentifier purchaseCouponList = PurchaseCouponListIdentifier.builder()
				.withPurchase(purchase)
				.build();

		return PurchaseCouponIdentifier.builder()
				.withPurchaseCouponList(purchaseCouponList)
				.withCouponId(StringIdentifier.of(couponId))
				.build();
	}
}
