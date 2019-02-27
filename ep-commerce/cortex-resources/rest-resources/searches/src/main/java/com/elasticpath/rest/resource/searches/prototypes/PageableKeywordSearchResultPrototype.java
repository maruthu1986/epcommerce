/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.searches.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.repository.PaginationRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.searches.KeywordSearchResultIdentifier;
import com.elasticpath.rest.definition.searches.KeywordSearchResultResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.pagination.PaginationEntity;
import com.elasticpath.rest.pagination.PagingLink;

/**
 * Pageable prototype for keyword search result resource.
 */
public class PageableKeywordSearchResultPrototype implements KeywordSearchResultResource.Pageable {

	private final KeywordSearchResultIdentifier keywordSearchResultIdentifier;

	private final PaginationRepository<KeywordSearchResultIdentifier, ItemIdentifier> paginationRepository;


	/**
	 * Constructor.
	 *
	 * @param keywordSearchResultIdentifier KeywordSearchResultIdentifier
	 * @param paginationRepository          PaginationRepository
	 */
	@Inject
	public PageableKeywordSearchResultPrototype(@RequestIdentifier final KeywordSearchResultIdentifier keywordSearchResultIdentifier,
												@ResourceRepository final PaginationRepository<KeywordSearchResultIdentifier, ItemIdentifier>
														paginationRepository) {
		this.keywordSearchResultIdentifier = keywordSearchResultIdentifier;
		this.paginationRepository = paginationRepository;
	}

	@Override
	public Single<PaginationEntity> onRead() {
		return paginationRepository.getPaginationInfo(keywordSearchResultIdentifier);
	}

	@Override
	public Observable<ItemIdentifier> elements() {
		return paginationRepository.getElements(keywordSearchResultIdentifier);
	}

	@Override
	public Observable<PagingLink<KeywordSearchResultIdentifier>> pagingLinks() {
		return paginationRepository.getPagingLinks(keywordSearchResultIdentifier);
	}
}
