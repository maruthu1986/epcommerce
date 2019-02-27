/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules;

import java.util.Locale;
import java.util.Set;

import com.elasticpath.base.GloballyIdentifiable;
import com.elasticpath.persistence.api.Persistable;

/**
 * Represents a rule that has been applied to an order.
 */
public interface AppliedRule extends Persistable, GloballyIdentifiable {

	/**
	 * Initialize the <code>AppliedRule</code> from the given <code>Rule</code>.
	 * @param rule the <code>Rule</code>
	 * @param locale the locale
	 */
	void initialize(Rule rule, Locale locale);

	/**
	 * Set the name of the applied rule.
	 * @param ruleName the rule name
	 */
	void setRuleName(String ruleName);

	/**
	 * Get the name of the applied rule.
	 * @return the name of the applied rule
	 * @domainmodel.property
	 */
	String getRuleName();

	/**
	 * Set the rule engine code for the applied rule.
	 * @param ruleCode the rule code.
	 */
	void setRuleCode(String ruleCode);

	/**
	 * Get the rule engine code for the applied rule.	 *
	 * @return the rule engine code.
	 * @domainmodel.property
	 */
	String getRuleCode();

	/**
	 * Set the uid of the applied rule.
	 * @param ruleUid the UID
	 */
	void setRuleUid(long ruleUid);

	/**
	 * Get the UID of the applied rule.
	 * @return the UID
	 * @domainmodel.property
	 */
	long getRuleUid();

	/**
	 * Get the applied coupon associated with this applied Rule.
	 *
	 * @return a list of associated applied coupon.
	 */
	Set<AppliedCoupon> getAppliedCoupons();

	/**
	 * Create a blank AppliedCoupon and add it to the Rule.
	 * An AppliedCoupon is wholly owned by the Rule so we don't export the interface.
	 *
	 * @param couponCode the couponCode
	 * @param usageCount the usageCount
	 */
	void addAppliedCoupon(String couponCode, int usageCount);

	/**
	 * @param visitor the visitor to accept for this applied rule.
	 */
	void accept(Visitor visitor);

	/**
	 * @return rule display name.
	 */
	String getRuleDisplayName();

	/**
	 * @return rule description
	 */
	String getRuleDescription();

	/**
	 * Represents a visitor for applied rule.
	 */
	interface Visitor {
		/**
		 * @param appliedRule applied rule to visit
		 */
		void visit(AppliedRule appliedRule);
	}
}
