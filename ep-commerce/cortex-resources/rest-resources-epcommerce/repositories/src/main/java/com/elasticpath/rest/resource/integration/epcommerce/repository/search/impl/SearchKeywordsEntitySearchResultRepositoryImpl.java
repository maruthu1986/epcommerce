/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;

import com.google.common.collect.ImmutableMap;
import io.reactivex.Completable;
import io.reactivex.Single;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.searches.KeywordSearchResultIdentifier;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.definition.searches.SearchesIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.IntegerIdentifier;

/**
 * Repository for search keywords entity.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class SearchKeywordsEntitySearchResultRepositoryImpl<E extends SearchKeywordsEntity, I extends KeywordSearchResultIdentifier>
		implements Repository<SearchKeywordsEntity, KeywordSearchResultIdentifier> {

	private static final int KEYWORDS_MAX_LENGTH = 500;
	private static final Range<Integer> RANGE = Range.between(1, Integer.MAX_VALUE);
	private static final int FIRST_PAGE = 1;

	/**
	 * validate search kewords entity.
	 *
	 * @param searchKeywordsEntity SearchKeywordsEntity
	 * @return validation result
	 */
	protected Completable validate(final SearchKeywordsEntity searchKeywordsEntity) {
		String keywords = searchKeywordsEntity.getKeywords();
		if (StringUtils.isEmpty(keywords)) {
			return Completable.error(ResourceOperationFailure
					.badRequestBody("Keywords field is missing a value."));
		}
		if (StringUtils.length(keywords) > KEYWORDS_MAX_LENGTH) {
			return Completable.error(ResourceOperationFailure
					.badRequestBody(String.format("Keywords field is too long, the maximum length is %s.",
							KEYWORDS_MAX_LENGTH)));
		}

		Integer pageSize = searchKeywordsEntity.getPageSize();
		if (pageSize != null && !RANGE.contains(pageSize)) {
			return Completable.error(ResourceOperationFailure
					.badRequestBody(String.format("Page Size is outside this range: %s", RANGE)));
		}

		return Completable.complete();
	}

	@Override
	public Single<SubmitResult<KeywordSearchResultIdentifier>> submit(final SearchKeywordsEntity searchKeywordsEntity,
																	  final IdentifierPart<String> scope) {
		return validate(searchKeywordsEntity)
				.andThen(Single.just(SubmitResult.<KeywordSearchResultIdentifier>builder()
						.withIdentifier(buildKeywordSearchResultIdentifier(searchKeywordsEntity, scope))
						.withStatus(SubmitStatus.CREATED)
						.build()));
	}

	private KeywordSearchResultIdentifier buildKeywordSearchResultIdentifier(final SearchKeywordsEntity searchKeywordsEntity,
																			 final IdentifierPart<String> scope) {
		return KeywordSearchResultIdentifier.builder()
				.withSearchId(CompositeIdentifier.of(ImmutableMap.of(
						SearchKeywordsEntity.KEYWORDS_PROPERTY, searchKeywordsEntity.getKeywords(),
						SearchKeywordsEntity.PAGE_SIZE_PROPERTY, String.valueOf(searchKeywordsEntity.getPageSize())
				)))
				.withPageId(IntegerIdentifier.of(FIRST_PAGE))
				.withSearches(SearchesIdentifier.builder().withScope(scope).build())
				.build();
	}

}
