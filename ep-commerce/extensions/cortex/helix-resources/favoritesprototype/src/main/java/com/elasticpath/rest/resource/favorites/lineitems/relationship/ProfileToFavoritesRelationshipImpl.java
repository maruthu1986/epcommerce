/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.lineitems.relationship;

import com.elasticpath.rest.definition.favorites.FavoritesIdentifier;
import com.elasticpath.rest.definition.favorites.ProfileToFavoritesRelationship;
import com.elasticpath.rest.helix.data.annotation.UserScopes;
import com.elasticpath.rest.id.type.StringIdentifier;
import io.reactivex.Observable;

import javax.inject.Inject;

/**
 * Profile to Favorites link.
 */
public class ProfileToFavoritesRelationshipImpl implements ProfileToFavoritesRelationship.LinkTo {

    @Inject
    @UserScopes
    private Iterable<String> scopes;

    @Override
    public Observable<FavoritesIdentifier> onLinkTo() {
        return Observable.fromIterable(scopes)
                .map(StringIdentifier::of)
                .map(scopeId -> FavoritesIdentifier.builder().withScope(scopeId).build())
                .firstElement().toObservable();

    }
}
