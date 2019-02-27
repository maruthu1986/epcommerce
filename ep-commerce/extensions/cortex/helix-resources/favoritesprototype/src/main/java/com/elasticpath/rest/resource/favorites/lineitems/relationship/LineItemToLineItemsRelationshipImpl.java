/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.lineitems.relationship;

import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemsForFavoriteLineItemRelationship;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import io.reactivex.Observable;

import javax.inject.Inject;

/**
 * Line item to line items link.
 */
public class LineItemToLineItemsRelationshipImpl implements FavoriteLineItemsForFavoriteLineItemRelationship.LinkTo {

    private final FavoriteLineItemIdentifier favoriteLineItemIdentifier;

    /**
     * Constructor.
     *
     * @param favoriteLineItemIdentifier line item identifier
     */
    @Inject
    public LineItemToLineItemsRelationshipImpl(@RequestIdentifier final FavoriteLineItemIdentifier favoriteLineItemIdentifier) {
        this.favoriteLineItemIdentifier = favoriteLineItemIdentifier;
    }

    @Override
    public Observable<FavoriteLineItemsIdentifier> onLinkTo() {
        return Observable.just(favoriteLineItemIdentifier.getFavoriteLineItems());
    }

}
