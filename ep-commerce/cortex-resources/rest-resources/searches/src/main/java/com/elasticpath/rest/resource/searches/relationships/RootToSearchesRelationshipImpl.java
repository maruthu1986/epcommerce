/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.searches.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.searches.RootToSearchesRelationship;
import com.elasticpath.rest.definition.searches.SearchesIdentifier;
import com.elasticpath.rest.helix.data.annotation.UserScopes;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Root resource to Searches resource relationship implementation.
 */
public class RootToSearchesRelationshipImpl implements RootToSearchesRelationship.LinkTo {


	private final Iterable<String> scopes;

	/**
	 * Constructor.
	 *
	 * @param scopes scopes
	 */
	@Inject
	public RootToSearchesRelationshipImpl(@UserScopes final Iterable<String> scopes) {
		this.scopes = scopes;
	}

	@Override
	public Observable<SearchesIdentifier> onLinkTo() {
		return Observable.fromIterable(scopes)
				.map(StringIdentifier::of)
				.map(scopeId -> SearchesIdentifier.builder().withScope(scopeId).build())
				.firstElement().toObservable();
	}
}
