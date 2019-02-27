/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.navigations.impl;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.navigations.CategoryCodeEntity;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationsIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.schema.StructuredMessageTypes;

/**
 * Implementing repository Navigation lookup.
 *
 * @param <E> extends CategoryCodeEntity
 * @param <I> extends NavigationIdentifier
 */
@Component
public class NavigationLookupByCategoryCodeRepositoryImpl<E extends CategoryCodeEntity, I extends NavigationIdentifier>
		implements Repository<CategoryCodeEntity, NavigationIdentifier> {

	/**
	 * Missing code value.
	 */
	protected static final String MISSING_REQUIRED_REQUEST_BODY = "Code field is missing a value.";
	/**
	 * Message ID.
	 */
	protected static final String MESSAGE_ID = "category-code.not.found";
	private static final Map<String, String> ERROR_DATA = ImmutableMap.of("field-name", "category-code");

	private CategoryRepository categoryRepository;

	@Override
	public Single<SubmitResult<NavigationIdentifier>> submit(final CategoryCodeEntity entity, final IdentifierPart<String> scope) {
		Single<Category> categorySingle = findCategoryByCodeInStore(entity, scope);

		return categorySingle
				.map(category -> SubmitResult.<NavigationIdentifier>builder()
						.withIdentifier(buildNavigationIdentifier(category.getCode(), scope))
						.withStatus(SubmitStatus.CREATED)
						.build());
	}

	/**
	 * Build navigation with parent links.
	 * @param categoryCode category code
	 * @param scope scope
	 * @return Navigation identifier
	 */
	private NavigationIdentifier buildNavigationIdentifier(final String categoryCode, final IdentifierPart<String>
			scope) {

		return NavigationIdentifier.builder()
				.withNavigations(NavigationsIdentifier
						.builder()
						.withScope(scope)
						.build())
				.withNodeId(StringIdentifier.of(categoryCode))
				.build();
	}

	/**
	 * Validate Category Code.
	 *
	 * @param entity CategoryCodeEntity
	 * @param scope  scope
	 * @return Single of Category
	 */
	private Single<Category> findCategoryByCodeInStore(final CategoryCodeEntity entity, final IdentifierPart<String> scope) {
		if (entity.getCode().isEmpty()) {
			Message structuredError = Message.builder()
					.withType(StructuredMessageTypes.ERROR)
					.withId(MESSAGE_ID)
					.withDebugMessage(MISSING_REQUIRED_REQUEST_BODY)
					.withData(ERROR_DATA)
					.build();

			return Single.error(ResourceOperationFailure.badRequestBody(MISSING_REQUIRED_REQUEST_BODY, Collections.singletonList(structuredError)));
		}

		return categoryRepository.findByStoreAndCategoryCode(scope.getValue(), entity.getCode())
				.flatMap(this::mapToSingle);
	}

	private Single<Category> mapToSingle(final Category category) {
		return Single.just(category);
	}

	@Reference
	public void setCategoryRepository(final CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
}
