/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.lineitems.relationship;

import com.elasticpath.rest.definition.favorites.FavoriteForFavoriteLineItemRelationship;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import io.reactivex.Observable;

import javax.inject.Inject;

/**
 * Line item to favorites link.
 */
public class LineItemToFavoriteRelationshipImpl implements FavoriteForFavoriteLineItemRelationship.LinkTo {

    private final FavoriteLineItemIdentifier favoriteLineItemIdentifier;

    /**
     * Constructor.
     *
     * @param favoriteLineItemIdentifier line item identifier
     */
    @Inject
    public LineItemToFavoriteRelationshipImpl(@RequestIdentifier final FavoriteLineItemIdentifier favoriteLineItemIdentifier) {
        this.favoriteLineItemIdentifier = favoriteLineItemIdentifier;
    }

    @Override
    public Observable<FavoriteIdentifier> onLinkTo() {
        return Observable.just(favoriteLineItemIdentifier.getFavoriteLineItems().getFavorite());
    }
}
