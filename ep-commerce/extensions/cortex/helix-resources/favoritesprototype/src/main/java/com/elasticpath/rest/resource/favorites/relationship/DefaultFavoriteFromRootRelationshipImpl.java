/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.relationship;

import com.elasticpath.rest.definition.favorites.DefaultFavoriteFromRootRelationship;
import com.elasticpath.rest.definition.favorites.DefaultFavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoritesIdentifier;
import com.elasticpath.rest.helix.data.annotation.UserScopes;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.google.inject.Inject;
import io.reactivex.Observable;
import org.apache.log4j.Logger;

public class DefaultFavoriteFromRootRelationshipImpl implements DefaultFavoriteFromRootRelationship.LinkTo {
    private static final Logger LOG = Logger.getLogger(DefaultFavoriteFromRootRelationshipImpl.class);

    private final Iterable<String> scopes;

    /**
     * Constructor.
     *
     * @param scopes The scopes
     */
    @Inject
    public DefaultFavoriteFromRootRelationshipImpl(@UserScopes final Iterable<String> scopes) {
        this.scopes = scopes;
    }

    @Override
    public Observable<DefaultFavoriteIdentifier> onLinkTo() {
        LOG.info("DefaultFavoriteFromRootRelationshipImpl");
        return Observable.fromIterable(scopes)
                .take(1)
                .map(StringIdentifier::of)
                .map(scopeId -> DefaultFavoriteIdentifier.builder()
                        .withFavorites(FavoritesIdentifier.builder()
                                .withScope(StringIdentifier.of(scopeId.getValue()))
                                .build())
                        .build());
    }
}
