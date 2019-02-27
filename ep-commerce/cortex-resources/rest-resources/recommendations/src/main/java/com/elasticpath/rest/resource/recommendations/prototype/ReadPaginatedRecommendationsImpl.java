/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.recommendations.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.repository.PaginationRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.recommendations.PaginatedRecommendationsIdentifier;
import com.elasticpath.rest.definition.recommendations.PaginatedRecommendationsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.pagination.PaginationEntity;
import com.elasticpath.rest.pagination.PagingLink;

/**
 * Read items that are recommended in a form of pages.
 */
public class ReadPaginatedRecommendationsImpl implements PaginatedRecommendationsResource.Pageable {

	private final PaginatedRecommendationsIdentifier paginatedRecommendationsIdentifier;

	private final PaginationRepository<PaginatedRecommendationsIdentifier, ItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param paginatedRecommendationsIdentifier paginated recommendation identifier
	 * @param repository repository to get items
	 */
	@Inject
	public ReadPaginatedRecommendationsImpl(
			@RequestIdentifier final PaginatedRecommendationsIdentifier paginatedRecommendationsIdentifier,
			@ResourceRepository final PaginationRepository<PaginatedRecommendationsIdentifier, ItemIdentifier> repository) {

		this.paginatedRecommendationsIdentifier = paginatedRecommendationsIdentifier;
		this.repository = repository;
	}


	@Override
	public Single<PaginationEntity> onRead() {
		return repository.getPaginationInfo(paginatedRecommendationsIdentifier);
	}

	@Override
	public Observable<ItemIdentifier> elements() {
		return repository.getElements(paginatedRecommendationsIdentifier);
	}

	@Override
	public Observable<PagingLink<PaginatedRecommendationsIdentifier>> pagingLinks() {
		return repository.getPagingLinks(paginatedRecommendationsIdentifier);
	}
}
