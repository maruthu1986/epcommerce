/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.relationship;

import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteMoveToFavoriteFormRelationship;
import com.elasticpath.rest.definition.favorites.FavoritesIdentifier;
import com.elasticpath.rest.definition.favorites.MoveToFavoriteFormIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;
import io.reactivex.Observable;
import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * Move to Favorite form link.
 */
public class FavoriteMoveToFavoriteFormRelationshipImpl implements FavoriteMoveToFavoriteFormRelationship.LinkTo {
    private static final Logger LOG = Logger.getLogger(FavoriteMoveToFavoriteFormRelationshipImpl.class);
    private final LineItemIdentifier lineItemIdentifier;
    private final IdentifierPart<String> scope;

    /**
     * Constructor.
     *
     * @param lineItemIdentifier lineItemIdentifier
     * @param scope              scope
     */
    @Inject
    public FavoriteMoveToFavoriteFormRelationshipImpl(@RequestIdentifier final LineItemIdentifier lineItemIdentifier,
                                                      @UriPart(FavoritesIdentifier.SCOPE) final IdentifierPart<String> scope) {
        this.lineItemIdentifier = lineItemIdentifier;
        this.scope = scope;
    }

    @Override
    public Observable<MoveToFavoriteFormIdentifier> onLinkTo() {
        LOG.info("FavoriteMoveToFavoriteFormRelationshipImpl");
        return Observable.just(MoveToFavoriteFormIdentifier.builder()
                .withFavorites(FavoritesIdentifier.builder()
                        .withScope(scope)
                        .build())
                .withLineItem(lineItemIdentifier)
                .build());
    }
}
