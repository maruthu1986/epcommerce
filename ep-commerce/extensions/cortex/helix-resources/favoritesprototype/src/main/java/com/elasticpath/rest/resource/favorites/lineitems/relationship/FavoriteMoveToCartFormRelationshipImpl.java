/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.lineitems.relationship;

import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteMoveToCartFormRelationship;
import com.elasticpath.rest.definition.favorites.MoveToCartFormIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import io.reactivex.Observable;

import javax.inject.Inject;

/**
 * Move to cart form link.
 */
public class FavoriteMoveToCartFormRelationshipImpl implements FavoriteMoveToCartFormRelationship.LinkTo {

    private final FavoriteLineItemIdentifier favoriteLineItemIdentifier;

    /**
     * Constructor.
     *
     * @param favoriteLineItemIdentifier favoriteLineItemIdentifier
     */
    @Inject
    public FavoriteMoveToCartFormRelationshipImpl(@RequestIdentifier final FavoriteLineItemIdentifier favoriteLineItemIdentifier) {
        this.favoriteLineItemIdentifier = favoriteLineItemIdentifier;
    }

    @Override
    public Observable<MoveToCartFormIdentifier> onLinkTo() {
        return Observable.just(MoveToCartFormIdentifier.builder()
                .withFavoriteLineItem(favoriteLineItemIdentifier)
                .build());
    }
}
