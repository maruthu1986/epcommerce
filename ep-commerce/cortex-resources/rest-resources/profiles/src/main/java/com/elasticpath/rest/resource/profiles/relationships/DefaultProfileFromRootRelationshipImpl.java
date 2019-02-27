/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.profiles.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.profiles.DefaultProfileFromRootRelationship;
import com.elasticpath.rest.definition.profiles.DefaultProfileIdentifier;
import com.elasticpath.rest.helix.data.annotation.UserScopes;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Implementation for Default Profile-From-Root com.elasticpath.rest.resource.wishlists.relationship.
 */
public class DefaultProfileFromRootRelationshipImpl implements DefaultProfileFromRootRelationship.LinkTo {
	@Inject
	@UserScopes
	private Iterable<String> scopes;

	@Override
	public Observable<DefaultProfileIdentifier> onLinkTo() {
		return Observable.fromIterable(scopes)
				.map(StringIdentifier::of)
				.map(scopeId -> DefaultProfileIdentifier.builder().withScope(scopeId).build())
				.firstElement().toObservable();
	}
}
