/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.DefaultCartIdentifier;
import com.elasticpath.rest.definition.carts.RootToDefaultCartRelationship;
import com.elasticpath.rest.helix.data.annotation.UserScopes;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Root to default cart link.
 */
public class RootToDefaultCartRelationshipImpl implements RootToDefaultCartRelationship.LinkTo {

	@Inject
	@UserScopes
	private Iterable<String> scopes;

	@Override
	public Observable<DefaultCartIdentifier> onLinkTo() {
		return Observable.fromIterable(scopes)
				.map(StringIdentifier::of)
				.map(scopeId -> DefaultCartIdentifier.builder().withScope(scopeId).build())
				.firstElement()
				.toObservable();
	}
}
