/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.navigations.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.AttributeValueTransformer;

/**
 * The tests for {@link NavigationEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class NavigationEntityRepositoryImplTest {

	private static final String PARENT_CODE = "parent code";
	private static final String SCOPE = "store";
	private static final Locale TEST_LOCALE = Locale.CANADA;
	private static final String USER_ID = "userid";
	private static final String DISPLAY_NAME = "display name";

	@Mock
	private AttributeValueTransformer attributeValueTransformer;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@InjectMocks
	private NavigationEntityRepositoryImpl<NavigationEntity, NavigationIdentifier> repository;

	@Test
	public void checkNavigationWithNoDescription() {
		NavigationIdentifier navigationIdentifier = createNavigationWithId(PARENT_CODE);

		NavigationEntity result = NavigationEntity.builder()
				.withNodeId(PARENT_CODE)
				.withName(PARENT_CODE)
				.withDisplayName(DISPLAY_NAME)
				.build();

		mockCategoryMethodsWithAttributes(null);

		repository.findOne(navigationIdentifier)
				.test()
				.assertValue(result);
	}

	@Test
	public void checkNavigationDescription() {
		NavigationIdentifier navigationIdentifier = createNavigationWithId(PARENT_CODE);

		DetailsEntity details = mock(DetailsEntity.class);
		NavigationEntity result = NavigationEntity.builder()
				.withNodeId(PARENT_CODE)
				.withName(PARENT_CODE)
				.withDetails(Collections.singletonList(details))
				.withDisplayName(DISPLAY_NAME)
				.build();

		AttributeValue attributeValue = mock(AttributeValue.class);
		mockCategoryMethodsWithAttributes(Collections.singletonList(attributeValue));
		when(attributeValueTransformer.transformToEntity(attributeValue, TEST_LOCALE)).thenReturn(details);

		repository.findOne(navigationIdentifier)
				.test()
				.assertValue(result);
	}

	private void mockCategoryMethodsWithAttributes(final List<AttributeValue> returnValue) {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(SCOPE, USER_ID, TEST_LOCALE);
		Category category = mock(Category.class);

		when(resourceOperationContext.getSubject()).thenReturn(subject);
		when(categoryRepository.findByStoreAndCategoryCode(SCOPE, PARENT_CODE)).thenReturn(Single.just(category));
		mockGetAttributeValuesWhichReturns(category, returnValue);
		when(category.getDisplayName(TEST_LOCALE)).thenReturn(DISPLAY_NAME);
		when(category.getCode()).thenReturn(PARENT_CODE);
	}

	private void mockGetAttributeValuesWhichReturns(final Category category, final List<AttributeValue> returnValue) {
		CategoryType categoryType = mock(CategoryType.class);
		AttributeGroup attributeGroup = mock(AttributeGroup.class);
		AttributeValueGroup attributeValueGroup = mock(AttributeValueGroup.class);

		when(category.getCategoryType()).thenReturn(categoryType);
		when(categoryType.getAttributeGroup()).thenReturn(attributeGroup);
		when(category.getAttributeValueGroup()).thenReturn(attributeValueGroup);
		when(attributeValueGroup.getAttributeValues(attributeGroup, TEST_LOCALE)).thenReturn(returnValue);
	}

	private NavigationIdentifier createNavigationWithId(final String navigationId) {
		return NavigationIdentifier.builder()
				.withNavigations(createNavigations())
				.withNodeId(StringIdentifier.of(navigationId))
				.build();
	}

	private NavigationsIdentifier createNavigations() {
		return NavigationsIdentifier.builder()
				.withScope(StringIdentifier.of(SCOPE))
				.build();
	}

}
