/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.relationship;

import com.elasticpath.rest.definition.favorites.AddItemToFavoriteFormIdentifier;
import com.elasticpath.rest.definition.favorites.ItemToFavoriteFormRelationship;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import io.reactivex.Observable;
import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * Add item to Favorite form link.
 */
public class ItemToFavoriteFormRelationshipImpl implements ItemToFavoriteFormRelationship.LinkTo {
    private static final Logger LOG = Logger.getLogger(ItemToFavoriteFormRelationshipImpl.class);
    private final ItemIdentifier itemIdentifier;

    /**
     * Constructor.
     *
     * @param itemIdentifier itemIdentifier
     */
    @Inject
    public ItemToFavoriteFormRelationshipImpl(@RequestIdentifier final ItemIdentifier itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    @Override
    public Observable<AddItemToFavoriteFormIdentifier> onLinkTo() {
        LOG.info("ItemToFavoriteFormRelationshipImpl");
        return Observable.just(AddItemToFavoriteFormIdentifier.builder()
                .withItem(itemIdentifier)
                .build());
    }
}
