/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Coupon;

/**
 * Unit Tests for {@link CouponAppliedRulePredicate}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CouponAppliedRulePredicateTest {

	private static final String UNMATCHED_COUPON_CODE = "NOT_MATCHING_COUPON_CODE";
	private static final String COUPON_CODE = "COUPON_CODE";

	private CouponAppliedRulePredicate predicate;

	@Mock
	private Coupon mockCoupon;

	@Mock
	private AppliedRule mockAppliedRule;

	@Mock
	private AppliedCoupon mockAppliedCoupon;

	private final Set<AppliedCoupon> appliedCoupons = new HashSet<>();

	@Before
	public void setUp() {
		when(mockAppliedRule.getAppliedCoupons()).thenReturn(appliedCoupons);
		when(mockCoupon.getCouponCode()).thenReturn(COUPON_CODE);

		appliedCoupons.add(mockAppliedCoupon);

		predicate = new CouponAppliedRulePredicate(mockCoupon);
	}

	@Test
	public void testIsSatisfied() {
		when(mockAppliedCoupon.getCouponCode()).thenReturn(COUPON_CODE);

		boolean result = predicate.isSatisfied(mockAppliedRule);

		assertTrue("Predicate should be satisfied when applied coupon code matches coupon code", result);
	}

	@Test
	public void testIsNotSatisfied() {
		when(mockAppliedCoupon.getCouponCode()).thenReturn(UNMATCHED_COUPON_CODE);

		boolean result = predicate.isSatisfied(mockAppliedRule);

		assertFalse("Predicate should not be satisfied when applied coupon code does not match coupon code", result);
	}

	@Test
	public void testRuleHasNoAppliedCouponsIsNotSatisfied() {
		appliedCoupons.clear();

		boolean result = predicate.isSatisfied(mockAppliedRule);

		assertFalse("Predicate should not be satisfied when applied coupon code does not match coupon code", result);
	}

}
