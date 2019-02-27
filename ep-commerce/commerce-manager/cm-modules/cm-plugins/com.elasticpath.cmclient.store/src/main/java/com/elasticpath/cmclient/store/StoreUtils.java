/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store;

import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.domain.rules.Rule;

/**
 * Utility class for the store plugin.
 */
public final class StoreUtils {
	private StoreUtils() {
		// static class
	}

	/**
	 * Gets the state as a localized string for the given {@link Rule}.
	 *
	 * @param rule a {@link Rule}
	 * @return localized state
	 */
	public static String getPromotionState(final Rule rule) {
		if (rule.isEnabled()) {
			if (rule.isWithinDateRange()) {
				return PromotionsMessages.get().Promotion_State_Active;
			} else {
				return PromotionsMessages.get().Promotion_State_Expired;
			}
		}
		return PromotionsMessages.get().Promotion_State_Disabled;
	}
}
