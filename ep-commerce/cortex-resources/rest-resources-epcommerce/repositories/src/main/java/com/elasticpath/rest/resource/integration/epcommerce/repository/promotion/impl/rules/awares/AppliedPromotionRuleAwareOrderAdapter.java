/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.awares;

import java.util.Collection;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.AppliedPromotionRuleAware;

/**
* Adapts an Order to the AppliedPromotionRuleAware interface.
*/
public class AppliedPromotionRuleAwareOrderAdapter implements AppliedPromotionRuleAware<AppliedRule> {

	private final Order order;

	/**
	 * Constructor.
	 * @param order The order.
	 */
	public AppliedPromotionRuleAwareOrderAdapter(final Order order) {
		this.order = order;
	}

	@Override
	public Collection<AppliedRule> getAppliedRules() {
		return order.getAppliedRules();
	}
}