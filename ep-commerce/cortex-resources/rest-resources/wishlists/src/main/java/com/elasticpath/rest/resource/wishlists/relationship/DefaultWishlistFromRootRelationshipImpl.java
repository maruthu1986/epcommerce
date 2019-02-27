/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.relationship;

import com.elasticpath.rest.definition.wishlists.DefaultWishlistFromRootRelationship;
import com.elasticpath.rest.definition.wishlists.DefaultWishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.helix.data.annotation.UserScopes;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.google.inject.Inject;
import io.reactivex.Observable;

/**
 * Implementation for Default Profile-From-Root com.elasticpath.rest.resource.wishlists.relationship.
 */
public class DefaultWishlistFromRootRelationshipImpl implements DefaultWishlistFromRootRelationship.LinkTo {

    private final Iterable<String> scopes;

    /**
     * Constructor.
     *
     * @param scopes The scopes
     */
    @Inject
    public DefaultWishlistFromRootRelationshipImpl(@UserScopes final Iterable<String> scopes) {
        this.scopes = scopes;
    }

    @Override
    public Observable<DefaultWishlistIdentifier> onLinkTo() {
        return Observable.fromIterable(scopes)
                .take(1)
                .map(StringIdentifier::of)
                .map(scopeId -> DefaultWishlistIdentifier.builder()
                     .withWishlists(WishlistsIdentifier.builder()
                                    .withScope(StringIdentifier.of(scopeId.getValue()))
                                    .build())
                     .build());
    }
}
