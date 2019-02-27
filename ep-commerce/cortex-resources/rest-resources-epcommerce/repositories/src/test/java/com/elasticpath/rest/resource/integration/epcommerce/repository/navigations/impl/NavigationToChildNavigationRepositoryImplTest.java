/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.navigations.impl;

import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;

/**
 * The tests for {@link NavigationToChildNavigationRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class NavigationToChildNavigationRepositoryImplTest {

	private static final String PARENT_CODE = "parent code";
	private static final String SCOPE = "store";
	private static final String CHILD_1_LEVEL_1 = "child code";
	private static final String CHILD_2_LEVEL_1 = "sibling code";
	private static final String CHILD_LEVEL_2 = "grandson code";

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private	NavigationToChildNavigationRepositoryImpl<NavigationIdentifier, NavigationIdentifier> repository;

	@Test
	public void checkChildrenNavigationsTest() {
		NavigationIdentifier parentIdentifier = createNavigationWithId(PARENT_CODE);

		List<NavigationIdentifier> result = setUpFirstTreeLevel();

		repository.getElements(parentIdentifier)
				.test()
				.assertValueSequence(result);
	}

	private List<NavigationIdentifier> setUpFirstTreeLevel() {
		List<Category> categories = ImmutableList.of(
				createCategoryWithCode(CHILD_1_LEVEL_1),
				createCategoryWithCode(CHILD_2_LEVEL_1)
		);
		List<NavigationIdentifier> result = ImmutableList.of(
				createNavigationWithId(CHILD_1_LEVEL_1),
				createNavigationWithId(CHILD_2_LEVEL_1)
		);

		when(categoryRepository.findChildren(SCOPE, PARENT_CODE))
				.thenReturn(Observable.fromIterable(categories));

		return result;
	}

	private List<NavigationIdentifier> setUpSecondTreeLevel() {
		List<Category> categories = Collections.singletonList(
				createCategoryWithCode(CHILD_LEVEL_2)
		);

		List<NavigationIdentifier> result = Collections.singletonList(
				createNavigationWithId(CHILD_LEVEL_2)
		);

		when(categoryRepository.findChildren(SCOPE, CHILD_1_LEVEL_1))
				.thenReturn(Observable.fromIterable(categories));

		return result;
	}

	@Test
	public void checkTreeForNavigationTest() {
		setUpFirstTreeLevel();
		List<NavigationIdentifier> result = setUpSecondTreeLevel();

		repository.getElements(createSubParentNavigation())
				.test()
				.assertValueSequence(result);
	}

	private NavigationIdentifier createSubParentNavigation() {
		return createNavigationWithId(CHILD_1_LEVEL_1);
	}

	private CategoryImpl createCategoryWithCode(final String code) {
		CategoryImpl category = new CategoryImpl();
		category.setCode(code);
		return category;
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
