package com.elasticpath.rest.resource.integration.epcommerce.repository.navigations.impl;


import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.rest.definition.navigations.CategoryCodeEntity;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;

@RunWith(MockitoJUnitRunner.class)
public class NavigationLookupByCategoryCodeRepositoryImplTest {
	private static final String SCOPE = "SCOPE";
	private static final String CATEGORY_ID = "category-id";

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	Category category;

	@InjectMocks
	private NavigationLookupByCategoryCodeRepositoryImpl<CategoryCodeEntity, NavigationIdentifier> navigationLookupByCategoryCodeRepository;

	@Test
	public void returnNavigationItemSuccess() {
		navigationLookupByCategoryCodeRepository.setCategoryRepository(categoryRepository);
		when(categoryRepository.findByStoreAndCategoryCode(SCOPE, CATEGORY_ID)).thenReturn(Single.just(category));
		when(category.getCode()).thenReturn(CATEGORY_ID);
		navigationLookupByCategoryCodeRepository.submit(returnCategoryCodeEntity(), StringIdentifier.of(SCOPE))
				.test()
				.assertValue(navigationIdentifierSubmitResult -> navigationIdentifierSubmitResult.
						getIdentifier()
						.getNodeId()
						.getValue()
						.contains(CATEGORY_ID));
	}

	@Test
	public void returnMissingCategoryCodeMessage() {
		navigationLookupByCategoryCodeRepository.setCategoryRepository(categoryRepository);
		navigationLookupByCategoryCodeRepository.submit(returnEmptyCategoryCodeEntity(), StringIdentifier.of(SCOPE))
				.test()
				.assertErrorMessage(NavigationLookupByCategoryCodeRepositoryImpl.MISSING_REQUIRED_REQUEST_BODY);
	}

	@Test
	public void returnNavigationItemFailure() {
		navigationLookupByCategoryCodeRepository.setCategoryRepository(categoryRepository);
		when(categoryRepository.findByStoreAndCategoryCode(SCOPE, CATEGORY_ID)).thenReturn(Single.just(category));
		navigationLookupByCategoryCodeRepository.submit(returnCategoryCodeEntity(), StringIdentifier.of(SCOPE))
				.test()
				.assertNoValues();
	}

	public NavigationIdentifier getNavigationIdentifier() {
		return NavigationIdentifier.builder()
				.withNavigations(NavigationsIdentifier.builder()
						.withScope(StringIdentifier.of(SCOPE)).build())
				.withNodeId(StringIdentifier.of(CATEGORY_ID))
				.build();
	}

	public CategoryCodeEntity returnCategoryCodeEntity() {
		return CategoryCodeEntity.builder().withCode(CATEGORY_ID).build();
	}

	public CategoryCodeEntity returnEmptyCategoryCodeEntity() {
		return CategoryCodeEntity.builder().withCode("").build();
	}
}
