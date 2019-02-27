/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.MoveToFavoriteService;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.MoveToFavoriteFormIdentifier;
import com.elasticpath.rest.definition.favorites.MoveToFavoriteFormResource;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import io.reactivex.Single;

import javax.inject.Inject;

/**
 * Submit move to favorites form.
 */
public class SubmitMoveToFavoriteFormPrototype implements MoveToFavoriteFormResource.SubmitWithResult {

    private final LineItemIdentifier lineItemIdentifier;

    private final MoveToFavoriteService moveToFavoriteService;

    /**
     * Constructor.
     *
     * @param moveToFavoriteFormIdentifier lineItemIdentifier
     * @param moveToFavoriteService        moveToFavoriteService
     */
    @Inject
    public SubmitMoveToFavoriteFormPrototype(@RequestIdentifier final MoveToFavoriteFormIdentifier moveToFavoriteFormIdentifier,
                                             @ResourceService final MoveToFavoriteService moveToFavoriteService) {
        this.lineItemIdentifier = moveToFavoriteFormIdentifier.getLineItem();
        this.moveToFavoriteService = moveToFavoriteService;
    }

    @Override
    public Single<SubmitResult<FavoriteLineItemIdentifier>> onSubmitWithResult() {
        return moveToFavoriteService.move(lineItemIdentifier);
    }
}
