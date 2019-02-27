/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.RulePredicate;

/**
 * This predicates matches Rules that contain the same rule code
 * as the coupon configs rule code.
 */
public class CouponRulePredicate implements RulePredicate<Rule> {
	private final Coupon coupon;

	/**
	 * Constructor.
	 * @param coupon coupon to check rule against.
	 */
	public CouponRulePredicate(final Coupon coupon) {
		this.coupon = coupon;
	}

	@Override
	public boolean isSatisfied(final Rule rule) {
		CouponConfig couponConfig = coupon.getCouponConfig();
		return rule.getCode().equals(couponConfig.getRuleCode());
	}
}
