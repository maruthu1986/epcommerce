/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Test;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.rest.definition.promotions.PromotionEntity;

/**
 * Tests {@link PromotionTransformer}.
 */
public class PromotionTransformerTest {

	private static final String RULE_NAME = "RuleName";
	private static final String RULE_DISPLAY_NAME = "Rule Display Name";
	private static final String RULE_CODE = "RULECODE";
	private static final String RULE_DESCRIPTION = "This is the rule description";


	private final PromotionTransformer promotionTransformer = new PromotionTransformer();

	@Test
	public void testInternalTransformToEntity() {
		Rule mockRule = createMockRule();
		PromotionEntity expectedPromotionEntity = createExpectedPromotionEntity();

		PromotionEntity promotionEntity = promotionTransformer.transformToEntity(mockRule, new Locale("EN"));

		assertEquals(promotionEntity, expectedPromotionEntity);

	}

	private Rule createMockRule() {
		Rule mockRule = mock(Rule.class);
		when(mockRule.getName()).thenReturn(RULE_NAME);
		when(mockRule.getDisplayName(any(Locale.class))).thenReturn(RULE_DISPLAY_NAME);
		when(mockRule.getDescription()).thenReturn(RULE_DESCRIPTION);
		when(mockRule.getCode()).thenReturn(RULE_CODE);
		return mockRule;
	}

	private PromotionEntity createExpectedPromotionEntity() {
		return PromotionEntity.builder()
				.withPromotionId(RULE_CODE)
				.withDisplayDescription(RULE_DESCRIPTION)
				.withDisplayName(RULE_DISPLAY_NAME)
				.withName(RULE_NAME)
				.build();
	}

}
