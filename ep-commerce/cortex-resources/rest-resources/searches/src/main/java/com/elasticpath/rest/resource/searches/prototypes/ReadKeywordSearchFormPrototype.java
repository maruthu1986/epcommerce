/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.searches.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.searches.KeywordSearchFormIdentifier;
import com.elasticpath.rest.definition.searches.KeywordSearchFormResource;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read prototype for keyword search form resource.
 */
public class ReadKeywordSearchFormPrototype implements KeywordSearchFormResource.Read {

	private final KeywordSearchFormIdentifier keywordSearchFormIdentifier;
	private final Repository<SearchKeywordsEntity, KeywordSearchFormIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param keywordSearchFormIdentifier KeywordSearchFormIdentifier
	 * @param repository                  Repository
	 */
	@Inject
	public ReadKeywordSearchFormPrototype(@RequestIdentifier final KeywordSearchFormIdentifier keywordSearchFormIdentifier,
										  @ResourceRepository final Repository<SearchKeywordsEntity, KeywordSearchFormIdentifier> repository) {
		this.keywordSearchFormIdentifier = keywordSearchFormIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<SearchKeywordsEntity> onRead() {
		return repository.findOne(keywordSearchFormIdentifier);
	}
}