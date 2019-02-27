/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions;

import java.util.Collection;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Utility class to build PromotionIdentifier.
 */
public final class PromotionIdentifierUtil {

	/**
	 * Private constructor to avoid instantiation.
	 */
	private PromotionIdentifierUtil() {

	}

	/**
	 * Builds a promotion identifier.
	 *
	 * @param scope				scope
	 * @param promotionId		applied promotion
	 * @return a promotion identifier.
	 */
	public static PromotionIdentifier buildPromotionIdentifier(final String scope, final String promotionId) {
		return PromotionIdentifier.builder()
				.withScope(StringIdentifier.of(scope))
				.withPromotionId(StringIdentifier.of(promotionId))
				.build();
	}

	/**
	 * Builds promotion identifiers in an observable.
	 *
	 * @param scope				scope
	 * @param promotionIds		applied promotions
	 * @return promotions identifiers in an observable.
	 */
	public static Observable<PromotionIdentifier> buildPromotionIdentifiers(final String scope, final Collection<String> promotionIds) {
		return Observable.fromIterable(promotionIds)
				.map(appliedPromotion -> buildPromotionIdentifier(scope, appliedPromotion));
	}

	/**
	 * Builds promotion identifiers in an observable.
	 *
	 * @param scope				scope
	 * @param promotionIds		observable applied promotions
	 * @return promotions identifiers in an observable.
	 */
	public static Observable<PromotionIdentifier> buildPromotionIdentifiers(final String scope, final Observable<String> promotionIds) {
		return promotionIds.map(appliedPromotion -> buildPromotionIdentifier(scope, appliedPromotion));
	}
}
