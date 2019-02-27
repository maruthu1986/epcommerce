package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.COUPON_CODE;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.rest.definition.coupons.CouponEntity;

/**
 * Test for {@link CouponEntityBuilderImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CouponEntityBuilderImplTest {

	private static final String TYPE = "type";
	private static final String PARENT_ID = "parent id";

	@InjectMocks
	private CouponEntityBuilderImpl couponEntityBuilder;

	@Test
	public void buildWithCouponReturnsACouponEntityWithTheCorrectFields() {
		Coupon coupon = mock(Coupon.class);
		when(coupon.getCouponCode()).thenReturn(COUPON_CODE);

		couponEntityBuilder.build(coupon, TYPE, PARENT_ID)
				.test()
				.assertValue(this::verifyCouponEntityFields);
	}

	@Test
	public void buildWithAppliedCouponReturnsACouponEntityWithTheCorrectFields() {
		AppliedCoupon appliedCoupon = mock(AppliedCoupon.class);
		when(appliedCoupon.getCouponCode()).thenReturn(COUPON_CODE);

		couponEntityBuilder.build(appliedCoupon, TYPE, PARENT_ID)
				.test()
				.assertValue(this::verifyCouponEntityFields);
	}

	private boolean verifyCouponEntityFields(final CouponEntity couponEntity) {
		return couponEntity.getCode().equals(COUPON_CODE)
				&& couponEntity.getCouponId().equals(COUPON_CODE)
				&& couponEntity.getParentId().equals(PARENT_ID)
				&& couponEntity.getParentType().equals(TYPE);
	}
}