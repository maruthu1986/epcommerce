/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Test;

import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.rest.definition.promotions.PromotionEntity;

/**
 * Tests {@link AppliedPromotionTransformer}.
 */
public class AppliedPromotionTransformerTest {

	private static final String RULE_NAME = "RuleName";
	private static final String RULE_DISPLAY_NAME = "Rule Display Name";
	private static final String GUID = "GUID";
	private static final String RULE_DESCRIPTION = "This is the rule description";


	private final AppliedPromotionTransformer promotionTransformer = new AppliedPromotionTransformer();

	@Test
	public void testInternalTransformToEntity() {
		AppliedRule mockRule = createMockAppliedRule();
		PromotionEntity expectedPromotionEntity = createExpectedPromotionEntity();

		PromotionEntity promotionEntity = promotionTransformer.transformToEntity(mockRule, new Locale("EN"));

		assertEquals(promotionEntity, expectedPromotionEntity);

	}

	private AppliedRule createMockAppliedRule() {
		AppliedRule mockRule = mock(AppliedRule.class);
		when(mockRule.getRuleName()).thenReturn(RULE_NAME);
		when(mockRule.getRuleDisplayName()).thenReturn(RULE_DISPLAY_NAME);
		when(mockRule.getRuleDescription()).thenReturn(RULE_DESCRIPTION);
		when(mockRule.getGuid()).thenReturn(GUID);
		return mockRule;
	}

	private PromotionEntity createExpectedPromotionEntity() {
		return PromotionEntity.builder()
				.withPromotionId(GUID)
				.withDisplayDescription(RULE_DESCRIPTION)
				.withDisplayName(RULE_DISPLAY_NAME)
				.withName(RULE_NAME)
				.build();
	}

}
