/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;

import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.searches.KeywordSearchResultIdentifier;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;

public class SearchKeywordsEntitySearchResultRepositoryImplTest {

	private static final int MAX_KEYWORDS_TO_GENERATE = 600;
	private static final IdentifierPart<String> SCOPE = StringIdentifier.of("scope");
	private Repository<SearchKeywordsEntity, KeywordSearchResultIdentifier> repository;

	@Before
	public void setUp() {
		repository = new SearchKeywordsEntitySearchResultRepositoryImpl<>();
	}

	@Test
	public void shouldNotCreateIdentifierForEmptyKeyword() {
		repository.submit(SearchKeywordsEntity.builder().withKeywords("").build(), SCOPE)
				.test()
				.assertError(ResourceOperationFailure.class)
				.assertError(throwable -> ResourceStatus.BAD_REQUEST_BODY.equals(
						((ResourceOperationFailure) throwable).getResourceStatus()));
	}

	@Test
	public void shouldNotCreateIdentifierForTooLongKeyword() {
		StringBuilder kewyordBuilder = new StringBuilder();
		IntStream.range(0, MAX_KEYWORDS_TO_GENERATE).forEach(kewyordBuilder::append);
		repository.submit(SearchKeywordsEntity.builder().withKeywords(kewyordBuilder.toString()).build(), SCOPE)
				.test()
				.assertError(ResourceOperationFailure.class)
				.assertError(throwable -> ResourceStatus.BAD_REQUEST_BODY.equals(
						((ResourceOperationFailure) throwable).getResourceStatus()));
	}

	@Test
	public void shouldCreateIdentifierForUndefinedPageSize() {
		repository.submit(SearchKeywordsEntity.builder()
						.withKeywords("a")
						.withPageSize(null)
						.build(),
				SCOPE)
				.test()
				.assertComplete();
	}

	@Test
	public void shouldCreateIdentifier() {
		repository.submit(SearchKeywordsEntity.builder()
						.withKeywords("a")
						.withPageSize(1)
						.build(),
				SCOPE)
				.test()
				.assertComplete();
	}

}