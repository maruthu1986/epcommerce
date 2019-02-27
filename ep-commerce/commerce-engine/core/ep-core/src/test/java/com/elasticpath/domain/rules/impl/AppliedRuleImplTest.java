/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Rule;

/** Test cases for <code>AppliedRuleImpl</code>. */
public class AppliedRuleImplTest {

	private static final Long UID_PK = 0L;
	private static final String RULE_CODE = "Rule Code";
	private static final String RULE_NAME = "RuleName";
	private static final String DESCRIPTION = "description of rule";
	private static final String ENGLISH_RULE_DISPLAY_NAME = "display name of rule";
	private static final Locale ENGLISH_LOCALE = Locale.ENGLISH;
	private AppliedRule appliedRule;

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final Rule rule = context.mock(Rule.class);

	@Before
	public void setUp() throws Exception {
		appliedRule = new AppliedRuleImpl();

		context.checking(new Expectations() {
			{
				allowing(rule).getName();
				will(returnValue(RULE_NAME));
				allowing(rule).getDescription();
				will(returnValue(DESCRIPTION));
				allowing(rule).getDisplayName(ENGLISH_LOCALE);
				will(returnValue(ENGLISH_RULE_DISPLAY_NAME));
				allowing(rule).getRuleCode();
				will(returnValue(RULE_CODE));
				allowing(rule).getUidPk();
				will(returnValue(UID_PK));
			}
		});
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AppliedRuleImpl.initializeFromRule(Rule)'.
	 */
	@Test
	public void testInitializeFromRule() {
		appliedRule.initialize(rule, ENGLISH_LOCALE);

		assertEquals(RULE_NAME, appliedRule.getRuleName());
		assertEquals(DESCRIPTION, appliedRule.getRuleDescription());
		assertEquals(ENGLISH_RULE_DISPLAY_NAME, appliedRule.getRuleDisplayName());
		assertTrue(appliedRule.getRuleCode().length() > 0);
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AppliedRuleImpl.getRuleName()'.
	 */
	@Test
	public void testGetRuleName() {
		appliedRule.setRuleName(RULE_NAME);
		assertEquals(RULE_NAME, appliedRule.getRuleName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AppliedRuleImpl.getRuleCode()'.
	 */
	@Test
	public void testGetRuleCode() {
		appliedRule.setRuleCode(RULE_CODE);
		assertEquals(RULE_CODE, appliedRule.getRuleCode());
		
	}

	/**
	 * Test that get on coupons returns empty collection.
	 */
	@Test
	public void testGetEmptyCoupons() {
		appliedRule.initialize(rule, ENGLISH_LOCALE);

		assertNotNull("Applied coupon collection should always exist.", appliedRule.getAppliedCoupons());
		assertEquals("Applied coupon collection should be empty.", 0, appliedRule.getAppliedCoupons().size());
	}
	
	/**
	 * Test that coupons can be set.
	 */
	@Test
	public void testSetCoupons() {
		final int usageCount = 12;
		appliedRule.initialize(rule, ENGLISH_LOCALE);
		AppliedCoupon coupon = new AppliedCouponImpl();
		coupon.setCouponCode("CouponCode");
		coupon.setUsageCount(usageCount);
		appliedRule.getAppliedCoupons().add(coupon);

		Iterator<AppliedCoupon> iter = appliedRule.getAppliedCoupons().iterator();

		coupon = iter.next();
		assertEquals("Usage Count should be that set.", usageCount, coupon.getUsageCount());
		assertEquals("CouponCode should be as set.", "CouponCode", coupon.getCouponCode());
	}

}
