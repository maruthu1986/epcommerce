/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.navigations.impl;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
 * The tests for {@link RootNavigationsRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RootNavigationsRepositoryImplTest {

	private static final String STORE_CODE = "Test store";
	private static final List<String> CODES = ImmutableList.of("first category", "second category", "third category");

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private RootNavigationsRepositoryImpl<NavigationsIdentifier, NavigationIdentifier> repository;

	@Test
	public void getRootLevelNavigationsTest() {
		NavigationsIdentifier navigations = NavigationsIdentifier.builder()
				.withScope(StringIdentifier.of(STORE_CODE)).build();

		List<Category> categories = new ArrayList<>();
		List<NavigationIdentifier> result = new ArrayList<>();
		for (String code : CODES) {
			categories.add(createCategoryWithCode(code));
			result.add(createNavigationIdentifier(navigations, code));
		}

		when(categoryRepository.findRootCategories(STORE_CODE))
				.thenReturn(Observable.fromIterable(categories));

		repository.getElements(navigations)
				.test()
				.assertValueSequence(result);
	}

	private NavigationIdentifier createNavigationIdentifier(final NavigationsIdentifier navigationsIdentifier, final String code) {
		return NavigationIdentifier.builder()
				.withNavigations(navigationsIdentifier)
				.withNodeId(StringIdentifier.of(code))
				.build();
	}

	private CategoryImpl createCategoryWithCode(final String code) {
		CategoryImpl category = new CategoryImpl();
		category.setCode(code);
		return category;
	}

}
