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
 * Line items to favorites link.
 */
public class LineItemsToFavoriteRelationshipImpl implements FavoriteLineItemsForFavoriteRelationship.LinkFrom {

    private final FavoriteLineItemsIdentifier favoriteLineItemsIdentifier;

    /**
     * Constructor.
     *
     * @param favoriteLineItemsIdentifier line items identifier
     */
    @Inject
    public LineItemsToFavoriteRelationshipImpl(@RequestIdentifier final FavoriteLineItemsIdentifier favoriteLineItemsIdentifier) {
        this.favoriteLineItemsIdentifier = favoriteLineItemsIdentifier;
    }

    @Override
    public Observable<FavoriteIdentifier> onLinkFrom() {
        return Observable.just(favoriteLineItemsIdentifier.getFavorite());
    }
}
