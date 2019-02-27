/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.searches.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.repository.PaginationRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.searches.NavigationSearchResultIdentifier;
import com.elasticpath.rest.definition.searches.NavigationSearchResultResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.pagination.PaginationEntity;
import com.elasticpath.rest.pagination.PagingLink;

/**
 * Pageable prototype for navigation search result resource.
 */
public class PageableNavigationSearchResultPrototype implements NavigationSearchResultResource.Pageable {

	private final NavigationSearchResultIdentifier navigationSearchResultIdentifier;

	private final PaginationRepository<NavigationSearchResultIdentifier, ItemIdentifier> paginationRepository;


	/**
	 * Constructor.
	 *
	 * @param navigationSearchResultIdentifier NavigationSearchResultIdentifier
	 * @param paginationRepository             PaginationRepository
	 */
	@Inject
	public PageableNavigationSearchResultPrototype(@RequestIdentifier final NavigationSearchResultIdentifier navigationSearchResultIdentifier,
												   @ResourceRepository final PaginationRepository<NavigationSearchResultIdentifier, ItemIdentifier>
														   paginationRepository) {
		this.navigationSearchResultIdentifier = navigationSearchResultIdentifier;
		this.paginationRepository = paginationRepository;
	}

	@Override
	public Single<PaginationEntity> onRead() {
		return paginationRepository.getPaginationInfo(navigationSearchResultIdentifier);
	}

	@Override
	public Observable<ItemIdentifier> elements() {
		return paginationRepository.getElements(navigationSearchResultIdentifier);
	}

	@Override
	public Observable<PagingLink<NavigationSearchResultIdentifier>> pagingLinks() {
		return paginationRepository.getPagingLinks(navigationSearchResultIdentifier);
	}
}
