/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.relationship;

import com.elasticpath.rest.definition.favorites.FavoriteMembershipFromItemRelationship;
import com.elasticpath.rest.definition.favorites.ReadFavoriteMembershipsIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import io.reactivex.Observable;
import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * Item to Favorite memberships link.
 */
public class FavoriteMembershipFromItemRelationshipImpl implements FavoriteMembershipFromItemRelationship.LinkTo {
    private static final Logger LOG = Logger.getLogger(FavoriteMembershipFromItemRelationshipImpl.class);
    private final ItemIdentifier itemIdentifier;

    /**
     * Constructor.
     *
     * @param itemIdentifier itemIdentifier
     */
    @Inject
    public FavoriteMembershipFromItemRelationshipImpl(@RequestIdentifier final ItemIdentifier itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    @Override
    public Observable<ReadFavoriteMembershipsIdentifier> onLinkTo() {
        LOG.info("FavoriteMembershipFromItemRelationshipImpl");
        return Observable.just(ReadFavoriteMembershipsIdentifier.builder()
                .withItem(itemIdentifier)
                .build());
    }
}
