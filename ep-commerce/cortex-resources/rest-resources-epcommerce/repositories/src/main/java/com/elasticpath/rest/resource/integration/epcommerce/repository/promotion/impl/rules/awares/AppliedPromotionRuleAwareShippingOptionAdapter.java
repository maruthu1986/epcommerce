/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.awares;

import java.util.Collection;

import com.elasticpath.domain.shoppingcart.PromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.AppliedPromotionRuleAware;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
* Adapts a ShippingOption to the AppliedPromotionRuleAware interface.
*/
public class AppliedPromotionRuleAwareShippingOptionAdapter implements AppliedPromotionRuleAware<Long> {
	private final ShoppingCartPricingSnapshot cartPricingSnapshot;
	private final ShippingOption shippingOption;

	/**
	 * Constructor.
	 *
	 * @param cartPricingSnapshot the cart pricing snapshot
	 * @param shippingOption The shipping option.
	 */
	public AppliedPromotionRuleAwareShippingOptionAdapter(final ShoppingCartPricingSnapshot cartPricingSnapshot,
															final ShippingOption shippingOption) {
		this.cartPricingSnapshot = cartPricingSnapshot;
		this.shippingOption = shippingOption;
	}

	@Override
	public Collection<Long> getAppliedRules() {
		final PromotionRecordContainer promotionRecordContainer = cartPricingSnapshot.getPromotionRecordContainer();
		return promotionRecordContainer.getAppliedRulesByShippingOption(shippingOption);
	}
}
