/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions;

import java.util.Locale;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transform CE rule to promotion entity.
 */
@Named("promotionTransformer")
@Singleton
public class PromotionTransformer extends AbstractDomainTransformer<Rule, PromotionEntity> {

	@Override
	public Rule transformToDomain(final PromotionEntity resourceEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public PromotionEntity transformToEntity(final Rule rule, final Locale locale) {
		return PromotionEntity.builder()
				/*.withDisplayConditions(extractRuleConditions(rule))*/
				.withDisplayDescription(rule.getDescription())
				.withDisplayName(rule.getDisplayName(locale))
				.withPromotionId(rule.getCode())
				.withName(rule.getName())
				.build();
	}
}
