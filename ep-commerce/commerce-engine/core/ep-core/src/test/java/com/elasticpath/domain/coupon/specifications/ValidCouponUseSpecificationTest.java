/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.coupon.specifications;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.StringUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.rules.impl.RuleValidationResultEnum;

/**
 * Coupon validity in cart specification.
 */
public class ValidCouponUseSpecificationTest {

	private static final String COUPON_CODE = "COUPON_CODE";

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final RuleService ruleService = context.mock(RuleService.class);

	private final CouponUsageService couponUsageService = context.mock(CouponUsageService.class);

	private static final String STORE_CODE = "STORE_CODE";

	private static final String EMAIL = "EMAIL";

	private final Coupon coupon = context.mock(Coupon.class);

	private final ValidCouponUseSpecification specification = new ValidCouponUseSpecification();

	private PotentialCouponUse potentialCouponUse;

	@Before
	public void setUp() {
		specification.setCouponUsageService(couponUsageService);
		specification.setRuleService(ruleService);

		potentialCouponUse = createPotentialCouponUsageDTO(coupon);
	}

	@Test
	public void testWhenCouponIsNullSpecIsNotSatisfied() {
		boolean result = specification.isSatisfiedBy(createPotentialCouponUsageDTO(null)).isSuccess();

		assertFalse("Specification should not be satisfied by null coupon.", result);
	}

	@Test
	public void testWhenCouponCodeIsEmptySpecIsNotSatisfied() {
		setUpCouponCodeForCoupon(StringUtils.EMPTY);

		boolean result = specification.isSatisfiedBy(potentialCouponUse).isSuccess();

		assertFalse("Specification should not be satisfied by null coupon code.", result);
	}

	@Test
	public void testWhenRuleIsNotValidSpecIsNotSatisfied() {
		setUpCouponCodeForCoupon(COUPON_CODE);
		setUpRuleToBeValid(RuleValidationResultEnum.ERROR_UNSPECIFIED);

		boolean result = specification.isSatisfiedBy(potentialCouponUse).isSuccess();

		assertFalse("Specification should not be satisfied by invalid rule.", result);
	}

	@Test
	public void testWhenCouponUsageIsInvalidThenSpecIfNotSatisfied() {
		setUpCouponCodeForCoupon(COUPON_CODE);
		setUpRuleToBeValid(RuleValidationResultEnum.SUCCESS);
		setUpCouponUsageToBeValidForRule(false);

		boolean result = specification.isSatisfiedBy(potentialCouponUse).isSuccess();

		assertFalse("Specification should not be satisfied by invalid coupon usage.", result);
	}

	@Test
	public void testWhenCouponExistsHasValidRuleAndValidCouponUsageSpecIsSatisfied() {
		setUpCouponCodeForCoupon(COUPON_CODE);
		setUpRuleToBeValid(RuleValidationResultEnum.SUCCESS);
		setUpCouponUsageToBeValidForRule(true);

		boolean result = specification.isSatisfiedBy(potentialCouponUse).isSuccess();

		assertTrue("Specification should be satisfied by valid potential coupon usage.", result);
	}

	private Rule setUpRuleToBeValid(final RuleValidationResultEnum isValid) {
		final Rule rule = context.mock(Rule.class);
		context.checking(new Expectations() {
			{
				allowing(ruleService).findByPromoCode(COUPON_CODE);
				will(returnValue(rule));

				allowing(ruleService).isRuleValid(rule, STORE_CODE);
				will(returnValue(isValid));

				allowing(rule).hasLimitedUseCondition();
				will(returnValue(true));
			}
		});
		return rule;
	}

	private void setUpCouponUsageToBeValidForRule(final boolean isValid) {
		context.checking(new Expectations() {
			{
				allowing(couponUsageService).isValidCouponUsage(EMAIL, coupon, null);
				will(returnValue(isValid));
			}
		});
	}

	private void setUpCouponCodeForCoupon(final String couponCode) {
		context.checking(new Expectations() {
			{
				allowing(coupon).getCouponCode();
				will(returnValue(couponCode));
			}
		});
	}

	private PotentialCouponUse createPotentialCouponUsageDTO(final Coupon coupon) {
		return new PotentialCouponUse(coupon, STORE_CODE, EMAIL);
	}
}
