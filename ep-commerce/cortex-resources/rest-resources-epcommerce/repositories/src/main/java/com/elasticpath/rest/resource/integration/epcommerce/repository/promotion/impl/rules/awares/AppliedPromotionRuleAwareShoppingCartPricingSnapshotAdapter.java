/**
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.awares;

import java.util.Collection;

import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.AppliedPromotionRuleAware;

/**
 * Adapts a ShoppingCart to the AppliedPromotionRuleAware interface.
 */
public class AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter implements AppliedPromotionRuleAware<Long> {

	private final ShoppingCartPricingSnapshot pricingSnapshot;

	/**
	 * Constructor.
	 *
	 * @param pricingSnapshot The shopping cart pricing snapshot.
	 */
	public AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter(final ShoppingCartPricingSnapshot pricingSnapshot) {
		this.pricingSnapshot = pricingSnapshot;
	}

	@Override
	public Collection<Long> getAppliedRules() {
		return pricingSnapshot.getPromotionRecordContainer().getAppliedRules();
	}

}