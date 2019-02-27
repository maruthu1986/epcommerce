/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.navigations.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.navigations.NavigationsFromRootRelationship;
import com.elasticpath.rest.definition.navigations.NavigationsIdentifier;
import com.elasticpath.rest.helix.data.annotation.UserScopes;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Link from the root to the navigations.
 */
public class NavigationsFromRootRelationshipImpl implements NavigationsFromRootRelationship.LinkTo {

	private final Iterable<String> scopes;

	/**
	 * Constructor.
	 *
	 * @param scopes scopes
	 */
	@Inject
	public NavigationsFromRootRelationshipImpl(@UserScopes final Iterable<String> scopes) {
		this.scopes = scopes;
	}

	@Override
	public Observable<NavigationsIdentifier> onLinkTo() {
		return Observable.fromIterable(scopes)
				.map(StringIdentifier::of)
				.map(scopeId -> NavigationsIdentifier.builder().withScope(scopeId).build())
				.firstElement().toObservable();
	}
}
