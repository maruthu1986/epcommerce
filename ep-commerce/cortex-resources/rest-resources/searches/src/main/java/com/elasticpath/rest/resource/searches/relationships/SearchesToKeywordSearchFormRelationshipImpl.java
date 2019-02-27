/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.searches.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.searches.KeywordSearchFormIdentifier;
import com.elasticpath.rest.definition.searches.SearchesIdentifier;
import com.elasticpath.rest.definition.searches.SearchesToKeywordSearchFormRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Searches resource to KeywordSearchForm resource relationship implementation.
 */
public class SearchesToKeywordSearchFormRelationshipImpl implements SearchesToKeywordSearchFormRelationship.LinkTo {


	private final SearchesIdentifier searchesIdentifier;

	/**
	 * Constructor.
	 *
	 * @param searchesIdentifier SearchesIdentifier
	 */
	@Inject
	public SearchesToKeywordSearchFormRelationshipImpl(@RequestIdentifier final SearchesIdentifier searchesIdentifier) {
		this.searchesIdentifier = searchesIdentifier;
	}

	@Override
	public Observable<KeywordSearchFormIdentifier> onLinkTo() {
		return Observable.just(KeywordSearchFormIdentifier.builder()
				.withSearches(searchesIdentifier)
				.build());
	}
}
