/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied;

import java.util.Locale;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transform CE rule to promotion entity.
 */
@Named("appliedPromotionTransformer")
@Singleton
public class AppliedPromotionTransformer extends AbstractDomainTransformer<AppliedRule, PromotionEntity> {

	@Override
	public AppliedRule transformToDomain(final PromotionEntity resourceEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public PromotionEntity transformToEntity(final AppliedRule appliedRule, final Locale locale) {
		return PromotionEntity.builder()
				/*.withDisplayConditions(extractRuleConditions(appliedRule))*/
				.withDisplayDescription(appliedRule.getRuleDescription())
				.withDisplayName(appliedRule.getRuleDisplayName())
				.withPromotionId(appliedRule.getGuid())
				.withName(appliedRule.getRuleName())
				.build();
	}
}
