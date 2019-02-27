/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.shoppingcart;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Keep track of applied discount records by rules and actions.
 */
public interface PromotionRecordContainer extends Serializable {

	/**
	 * Returns the discount record for the items that were discounted based on {@code rule}.
	 * @param rule The rule to use
	 * @param action the RuleAction
	 * @return the matching discount record or null
	 */
	DiscountRecord getDiscountRecord(Rule rule, RuleAction action);

	/**
	 * Retrieve all of the discount records that have been added.
	 *
	 * @return collection of discount records or an empty collection
	 */
	Collection<DiscountRecord> getAllDiscountRecords();

	/**
	 * Examines the shopping items for each discount record to get rules that applied to line items.
	 * Returns only unique rule ids.
	 *
	 * @param lineItemId the line item id of the shopping item
	 * @return applied rule ids or an empty collection
	 */
	Collection<Long> getAppliedRulesByLineItem(String lineItemId);

	/**
	 * Get the set of rules that have been applied to the given shipping option.
	 *
	 * @param shippingOption the shipping option to examine for applied rules
	 * @return a set of {@code Long} Rule UidPks
	 */
	Set<Long> getAppliedRulesByShippingOption(ShippingOption shippingOption);

	/**
	 * Get the set of rules that have been applied to the cart.
	 *
	 * @return a set of <code>Long</code> Rule UidPks
	 */
	Set<Long> getAppliedRules();

	/**
	 * Gets the map of limited usage promotion rule codes to rule ids.
	 *
	 * @return the map of limited usage promotion rule codes to rule ids
	 */
	Map<String, Long> getLimitedUsagePromotionRuleCodes();

}
