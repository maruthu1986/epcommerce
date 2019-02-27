/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;

import io.reactivex.Single;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.searches.KeywordSearchFormIdentifier;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.search.SearchRepository;

/**
 * Repository for search keywords entity.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class SearchKeywordsEntitySearchFormRepositoryImpl<E extends SearchKeywordsEntity, I extends KeywordSearchFormIdentifier>
		implements Repository<SearchKeywordsEntity, KeywordSearchFormIdentifier> {


	private SearchRepository searchRepository;

	@Reference
	public void setSearchRepository(final SearchRepository searchRepository) {
		this.searchRepository = searchRepository;
	}

	@Override
	public Single<SearchKeywordsEntity> findOne(final KeywordSearchFormIdentifier identifier) {
		String scope = identifier.getSearches().getScope().getValue();
		Single<Integer> defaultPageSize = searchRepository.getDefaultPageSize(scope);
		return defaultPageSize
				.map(
						pageSize -> SearchKeywordsEntity.builder()
								.withKeywords(StringUtils.EMPTY)
								.withPageSize(pageSize)
								.build()
				);
	}
}
