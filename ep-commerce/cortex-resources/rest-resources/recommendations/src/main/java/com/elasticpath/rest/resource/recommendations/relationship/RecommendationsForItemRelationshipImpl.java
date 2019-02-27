/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.recommendations.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.recommendations.ItemRecommendationGroupsIdentifier;
import com.elasticpath.rest.definition.recommendations.RecommendationsForItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Link from Item to the its recommendation.
 */
public class RecommendationsForItemRelationshipImpl implements RecommendationsForItemRelationship.LinkTo {

	private final ItemIdentifier itemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param itemIdentifier item identifier
	 */
	@Inject
	public RecommendationsForItemRelationshipImpl(@RequestIdentifier final ItemIdentifier itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}

	@Override
	public Observable<ItemRecommendationGroupsIdentifier> onLinkTo() {
		return Observable.just(ItemRecommendationGroupsIdentifier.builder()
				.withItem(itemIdentifier)
				.build());
	}
}
