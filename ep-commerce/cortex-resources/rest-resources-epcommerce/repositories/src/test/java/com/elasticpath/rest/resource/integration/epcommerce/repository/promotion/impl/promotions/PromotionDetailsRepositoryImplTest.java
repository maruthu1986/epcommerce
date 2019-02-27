/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionTestFactory.buildPromotionIdentifier;

import java.util.Locale;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Test for {@link PromotionDetailsRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PromotionDetailsRepositoryImplTest {

	private final PromotionIdentifier promotionIdentifier = buildPromotionIdentifier();

	@Mock
	private Rule rule;

	@InjectMocks
	private PromotionDetailsRepositoryImpl<PromotionEntity, PromotionIdentifier> repository;

	@Mock
	private PromotionRepository promotionRepository;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@InjectMocks
	private PromotionTransformer promotionTransformer;

	@Before
	public void setUp() {
		repository.setPromotionTransformer(promotionTransformer);
		when(resourceOperationContext.getSubject()).thenReturn(TestSubjectFactory.createWithScopeAndUserIdAndLocale(ResourceTestConstants.SCOPE,
				ResourceTestConstants.USER_ID, Locale.ENGLISH));
	}

	@Test
	public void verifyFindOneReturnsNotFoundWhenPromotionCanNotBeFound() {
		when(promotionRepository.findByPromotionId(ResourceTestConstants.PROMOTION_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.findOne(promotionIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsPromotionEntity() {
		String description = "description";
		String displayName = "displayName";
		String name = "name";

		when(promotionRepository.findByPromotionId(ResourceTestConstants.PROMOTION_ID)).thenReturn(Single.just(rule));
		when(rule.getDescription()).thenReturn(description);
		when(rule.getDisplayName(Locale.ENGLISH)).thenReturn(displayName);
		when(rule.getCode()).thenReturn(ResourceTestConstants.PROMOTION_ID);
		when(rule.getName()).thenReturn(name);

		repository.findOne(promotionIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(promotionEntity -> promotionEntity.getPromotionId().equals(ResourceTestConstants.PROMOTION_ID)
						&& promotionEntity.getDisplayName().equals(displayName)
						&& promotionEntity.getName().equals(name)
						&& promotionEntity.getDisplayDescription().equals(description));
	}
}
