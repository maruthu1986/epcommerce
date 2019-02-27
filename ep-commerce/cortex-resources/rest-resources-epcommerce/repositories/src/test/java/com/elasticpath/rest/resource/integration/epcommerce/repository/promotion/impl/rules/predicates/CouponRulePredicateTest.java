/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.Rule;

/**
 * Unit Tests for {@link CouponRulePredicate}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CouponRulePredicateTest {

	private static final String RULE_CODE = "RULE_CODE";
	private static final String UNMATCHED_RULE_CODE = "NOT_MATCHING_RULE_CODE";

	private CouponRulePredicate predicate;

	@Mock
	private Coupon mockCoupon;

	@Mock
	private Rule mockRule;

	@Before
	public void setUp() {
		CouponConfig mockCouponConfig = mock(CouponConfig.class);
		when(mockCoupon.getCouponConfig()).thenReturn(mockCouponConfig);
		when(mockCouponConfig.getRuleCode()).thenReturn(RULE_CODE);

		predicate = new CouponRulePredicate(mockCoupon);
	}

	@Test
	public void testIsSatisfied() {
		when(mockRule.getCode()).thenReturn(RULE_CODE);

		boolean result = predicate.isSatisfied(mockRule);

		assertTrue("Predicate should be satisfied when rule code matches coupon rule code", result);
	}

	@Test
	public void testIsNotSatisfied() {
		when(mockRule.getCode()).thenReturn(UNMATCHED_RULE_CODE);

		boolean result = predicate.isSatisfied(mockRule);

		assertFalse("Predicate should not be satisfied when rule code doesn't match coupon rule code", result);
	}


}
