/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion;

import java.util.Collection;

/**
 * Applied promotion rule aware.
 * @param <T> The type of rules.
 */
public interface AppliedPromotionRuleAware<T> {

	/**
	 * Gets the applied rules.
	 * @return The applied rules.
	 */
	Collection<T> getAppliedRules();
}