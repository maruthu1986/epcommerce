/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.lookups.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.lookups.LookupsIdentifier;
import com.elasticpath.rest.definition.lookups.RootToLookupsRelationship;
import com.elasticpath.rest.helix.data.annotation.UserScopes;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Root resource to lookups resource relationship implementation.
 */
public class RootToLookupsRelationshipImpl implements RootToLookupsRelationship.LinkTo {


	private final Iterable<String> scopes;

	/**
	 * Constructor.
	 *
	 * @param scopes scopes
	 */
	@Inject
	public RootToLookupsRelationshipImpl(@UserScopes final Iterable<String> scopes) {
		this.scopes = scopes;
	}

	@Override
	public Observable<LookupsIdentifier> onLinkTo() {
		return Observable.fromIterable(scopes)
				.map(StringIdentifier::of)
				.map(scopeId -> LookupsIdentifier.builder().withScope(scopeId).build())
				.firstElement().toObservable();
	}
}
