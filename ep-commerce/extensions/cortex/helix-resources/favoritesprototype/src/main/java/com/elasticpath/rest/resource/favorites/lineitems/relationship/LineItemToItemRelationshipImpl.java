/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.lineitems.relationship;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.ItemForFavoriteLineItemRelationship;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import io.reactivex.Observable;

import javax.inject.Inject;

/**
 * Line item to item link.
 */
public class LineItemToItemRelationshipImpl implements ItemForFavoriteLineItemRelationship.LinkTo {

    private final FavoriteLineItemIdentifier favoriteLineItemIdentifier;

    private final LinksRepository<FavoriteLineItemIdentifier, ItemIdentifier> repository;

    /**
     * Constructor.
     *
     * @param favoriteLineItemIdentifier favoriteLineItemIdentifier
     * @param repository                 repository
     */
    @Inject
    public LineItemToItemRelationshipImpl(@RequestIdentifier final FavoriteLineItemIdentifier favoriteLineItemIdentifier,
                                          @ResourceRepository final LinksRepository<FavoriteLineItemIdentifier, ItemIdentifier> repository) {
        this.favoriteLineItemIdentifier = favoriteLineItemIdentifier;
        this.repository = repository;
    }

    @Override
    public Observable<ItemIdentifier> onLinkTo() {
        return repository.getElements(favoriteLineItemIdentifier);
    }
}
