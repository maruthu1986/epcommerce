/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.recommendations.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.rest.definition.recommendations.ItemRecommendationGroupIdentifier;
import com.elasticpath.rest.definition.recommendations.ItemRecommendationGroupResource;
import com.elasticpath.rest.definition.recommendations.PaginatedRecommendationsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.type.IntegerIdentifier;

/**
 * Read operation for the item recommendation group.
 */
public class ReadItemRecommendationGroupImpl implements ItemRecommendationGroupResource.Read {

	private static final Integer FIRST_PAGE = 1;
	private final ItemRecommendationGroupIdentifier groupIdentifier;

	/**
	 * Constructor.
	 *
	 * @param groupIdentifier group identifier
	 */
	@Inject
	public ReadItemRecommendationGroupImpl(@RequestIdentifier final ItemRecommendationGroupIdentifier groupIdentifier) {
		this.groupIdentifier = groupIdentifier;
	}

	@Override
	public Single<PaginatedRecommendationsIdentifier> onRead() {
		return Single.just(PaginatedRecommendationsIdentifier.builder()
				.withItemRecommendationGroup(groupIdentifier)
				.withPageId(IntegerIdentifier.of(FIRST_PAGE))
				.build());
	}
}
