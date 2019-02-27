/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.coupon.specifications.PotentialCouponUse;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.specifications.Specification;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;

public class CouponAutoApplierServiceImplTest {

	private static final String EMAIL = "EMAIL";

	private static final String COUPON_CODE = "COUPON_CODE";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final CouponService mockCouponService = context.mock(CouponService.class);

	private final CouponUsageService mockCouponUsageService = context.mock(CouponUsageService.class);

	@SuppressWarnings("unchecked")
	private final Specification<PotentialCouponUse> mockValidCouponUseSpecification = (Specification<PotentialCouponUse>) context
			.mock(Specification.class);

	private final Coupon mockCoupon = context.mock(Coupon.class);

	private final CouponUsage mockCouponUsage = context.mock(CouponUsage.class);

	private final CouponAutoApplierServiceImpl applierService = new CouponAutoApplierServiceImpl();

	private final Store mockStore = context.mock(Store.class);

	@Before
	public void setUp() {
		context.checking(new Expectations() {
			{
				allowing(mockCouponService).findByCouponCode(COUPON_CODE);
				will(returnValue(mockCoupon));
				
				allowing(mockCoupon).getCouponCode();
				will(returnValue(COUPON_CODE));

				ignoring(mockStore).getCode();
				ignoring(mockStore).getUidPk();
			}
		});

		applierService.setCouponService(mockCouponService);
		applierService.setCouponUsageService(mockCouponUsageService);
		applierService.setValidCouponUseSpecification(mockValidCouponUseSpecification);
	}

	@Test
	public void testFilterKeepsUserSpecificCoupons() {
		allowingCouponToBeUserSpecific(true);
		allowingCouponToBeValid(RuleValidationResultEnum.SUCCESS);

		Set<String> result = applierService.filterValidCouponsForCustomer(new HashSet<>(Arrays.asList(COUPON_CODE)),
				mockStore, EMAIL);

		assertEquals("User specific one should be kept.", 1, result.size());
		assertTrue("User specific one should be kept.", result.contains(COUPON_CODE));
	}

	@Test
	public void testFilterKeepsInvalidCoupons() {
		allowingCouponToBeUserSpecific(false);
		allowingCouponToBeValid(RuleValidationResultEnum.ERROR_UNSPECIFIED);

		Set<String> result = applierService.filterValidCouponsForCustomer(new HashSet<>(Arrays.asList(COUPON_CODE)),
				mockStore, EMAIL);

		assertEquals("Invalid one should be kept.", 1, result.size());
		assertTrue("Invalid one should be kept.", result.contains(COUPON_CODE));
	}

	@Test
	public void testRetrieveCouponsForAutoApplyDoesNotReturnInactiveCoupons() {
		setUpCouponUsageServiceToReturnCouponUsages();
		allowingCouponUsageToBeActiveInCart(false);

		Set<String> result = applierService.retrieveCouponsApplicableToAutoApply(mockStore, EMAIL);

		assertTrue("No inactive coupons should be retrieved.", result.isEmpty());
	}

	@Test
	public void testRetrieveCouponsForAutoApplyDoesNotReturnInvalidCoupons() {
		setUpCouponUsageServiceToReturnCouponUsages();
		allowingCouponUsageToBeActiveInCart(true);
		allowingCouponToBeValid(RuleValidationResultEnum.ERROR_UNSPECIFIED);

		Set<String> result = applierService.retrieveCouponsApplicableToAutoApply(mockStore, EMAIL);

		assertTrue("No invalid coupons should be retrieved.", result.isEmpty());
	}

	@Test
	public void testRetrieveCouponsForAutoApplyDoesReturnsActiveAndValidCouponsEligibleForCustomer() {
		setUpCouponUsageServiceToReturnCouponUsages();
		allowingCouponUsageToBeActiveInCart(true);
		allowingCouponToBeValid(RuleValidationResultEnum.SUCCESS);

		Set<String> result = applierService.retrieveCouponsApplicableToAutoApply(mockStore, EMAIL);

		assertEquals("Active and Valid coupons should be retrieved.", 1, result.size());
		assertTrue("Active and Valid coupons should be retrieved.", result.contains(COUPON_CODE));
	}

	private void allowingCouponToBeUserSpecific(final boolean isUserSpecific) {
		context.checking(new Expectations() {
			{
				CouponConfig mockCouponConfig = context.mock(CouponConfig.class);
				allowing(mockCoupon).getCouponConfig();
				will(returnValue(mockCouponConfig));

				allowing(mockCouponConfig).getUsageType();
				if (isUserSpecific) {
					will(returnValue(CouponUsageType.LIMIT_PER_SPECIFIED_USER));
				}
			}
		});
	}

	private void allowingCouponUsageToBeActiveInCart(final boolean activeInCart) {
		context.checking(new Expectations() {
			{
				allowing(mockCouponUsage).isActiveInCart();
				will(returnValue(activeInCart));
			}
		});
	}

	private void allowingCouponToBeValid(final RuleValidationResultEnum isValid) {
		context.checking(new Expectations() {
			{
				allowing(mockValidCouponUseSpecification).isSatisfiedBy(with(any(PotentialCouponUse.class)));
				will(returnValue(isValid));
			}
		});
	}

	private void setUpCouponUsageServiceToReturnCouponUsages() {
		context.checking(new Expectations() {
			{
				Collection<CouponUsage> couponUsages = new ArrayList<>();
				couponUsages.add(mockCouponUsage);

				allowing(mockCouponUsageService).findEligibleUsagesByEmailAddress(with(EMAIL), with(any(Long.class)));
				will(returnValue(couponUsages));

				allowing(mockCouponUsage).getCoupon();
				will(returnValue(mockCoupon));
			}
		});
	}
}
