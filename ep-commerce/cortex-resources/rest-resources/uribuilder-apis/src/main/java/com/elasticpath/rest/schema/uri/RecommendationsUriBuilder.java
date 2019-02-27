/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builder of recommendations URIs.
 */
public interface RecommendationsUriBuilder extends ScopedUriBuilder<RecommendationsUriBuilder>, ReadFromOtherUriBuilder<RecommendationsUriBuilder> {

	/**
	 * Sets the recommendation group eg: cross-sells / up-sells / etc.
	 *
	 * @param recommendationGroup the link to the recommended items
	 * @return the builder
	 */
	RecommendationsUriBuilder setRecommendationGroup(String recommendationGroup);

	/**
	 * Sets the page number for the recommended items.
	 *
	 * @param pageNumber the page number to display
	 * @return the builder
	 */
	RecommendationsUriBuilder setPageNumber(int pageNumber);
}
