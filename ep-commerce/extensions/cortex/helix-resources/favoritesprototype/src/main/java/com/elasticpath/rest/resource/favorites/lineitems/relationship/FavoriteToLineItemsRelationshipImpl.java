/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.lineitems.relationship;

import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemsForFavoriteRelationship;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import io.reactivex.Observable;

import javax.inject.Inject;

/**
 * Favorite to line items link.
 */
public class FavoriteToLineItemsRelationshipImpl implements FavoriteLineItemsForFavoriteRelationship.LinkTo {

    private final FavoriteIdentifier favoriteIdentifier;

    /**
     * Constructor.
     *
     * @param favoriteIdentifier FavoriteIdentifier
     */
    @Inject
    public FavoriteToLineItemsRelationshipImpl(@RequestIdentifier final FavoriteIdentifier favoriteIdentifier) {
        this.favoriteIdentifier = favoriteIdentifier;
    }

    @Override
    public Observable<FavoriteLineItemsIdentifier> onLinkTo() {
        return Observable.just(FavoriteLineItemsIdentifier.builder()
                .withFavorite(favoriteIdentifier)
                .build());
    }
}
