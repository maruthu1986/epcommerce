/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shoppingcart;

/**
 * Mutable extension of a {@link PromotionRecordContainer}.
 */
public interface MutablePromotionRecordContainer extends PromotionRecordContainer {

	/**
	 * Add a discount record.
	 *
	 * @param discountRecord the discount record to add
	 */
	void addDiscountRecord(DiscountRecord discountRecord);

	/**
	 * Clear the discount records.
	 */
	void clear();

	/**
	 * Add a limited usage promotion rule record.
	 *
	 * @param ruleCode the {@link com.elasticpath.domain.rules.Rule#getRuleCode() rule code} of the limited usage promotion
	 * @param ruleId the {@link com.elasticpath.domain.rules.Rule#getUidPk() ID} of the limited usage promotion
	 */
	void addLimitedUsagePromotionRuleCode(String ruleCode, long ruleId);

	/**
	 * Removes a limited usage promotion rule code from the record container.
	 *
	 * @param ruleCode the rule code of the record to be removed
	 */
	void removeLimitedUsagePromotionRuleCode(String ruleCode);

}
