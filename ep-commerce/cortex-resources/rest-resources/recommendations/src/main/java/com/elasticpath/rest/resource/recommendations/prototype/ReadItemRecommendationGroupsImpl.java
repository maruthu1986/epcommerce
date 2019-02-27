/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.recommendations.prototype;

import io.reactivex.Completable;

import com.elasticpath.rest.definition.recommendations.ItemRecommendationGroupsResource;

/**
 * Read operation for the item recommendation groups.
 */
public class ReadItemRecommendationGroupsImpl implements ItemRecommendationGroupsResource.Read {

	@Override
	public Completable onRead() {
		return Completable.complete();
	}
}