/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.searches.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.searches.KeywordSearchFormIdentifier;
import com.elasticpath.rest.definition.searches.KeywordSearchFormResource;
import com.elasticpath.rest.definition.searches.KeywordSearchResultIdentifier;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Submit prototype for keyword search form resource.
 */
public class SubmitKeywordSearchFormPrototype implements KeywordSearchFormResource.SubmitWithResult {

	private final SearchKeywordsEntity searchKeywordsEntity;

	private final Repository<SearchKeywordsEntity, KeywordSearchResultIdentifier> repository;

	private final KeywordSearchFormIdentifier keywordSearchFormIdentifier;

	/**
	 * Constructor.
	 *
	 * @param searchKeywordsEntity        SearchKeywordsEntity
	 * @param repository                  Repository
	 * @param keywordSearchFormIdentifier KeywordSearchFormIdentifier
	 */
	@Inject
	public SubmitKeywordSearchFormPrototype(@RequestForm final SearchKeywordsEntity searchKeywordsEntity,
											@ResourceRepository final Repository<SearchKeywordsEntity, KeywordSearchResultIdentifier> repository,
											@RequestIdentifier final KeywordSearchFormIdentifier keywordSearchFormIdentifier) {
		this.searchKeywordsEntity = searchKeywordsEntity;
		this.repository = repository;
		this.keywordSearchFormIdentifier = keywordSearchFormIdentifier;
	}

	@Override
	public Single<SubmitResult<KeywordSearchResultIdentifier>> onSubmitWithResult() {
		IdentifierPart<String> scope = keywordSearchFormIdentifier.getSearches().getScope();
		return repository.submit(searchKeywordsEntity, scope);
	}
}
