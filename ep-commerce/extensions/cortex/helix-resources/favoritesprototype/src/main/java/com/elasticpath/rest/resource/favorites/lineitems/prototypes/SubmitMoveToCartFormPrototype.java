/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.lineitems.prototypes;

import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.MoveToCartService;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.MoveToCartFormIdentifier;
import com.elasticpath.rest.definition.favorites.MoveToCartFormResource;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import io.reactivex.Single;

import javax.inject.Inject;

/**
 * Submit move to cart form.
 */
public class SubmitMoveToCartFormPrototype implements MoveToCartFormResource.SubmitWithResult {

    private final FavoriteLineItemIdentifier favoriteLineItemIdentifier;

    private final LineItemEntity lineItemEntity;
    private final MoveToCartService moveToCartService;

    /**
     * Constructor.
     *
     * @param moveToCartFormIdentifier favoriteLineItemIdentifier
     * @param lineItemEntity           lineItemEntity
     * @param moveToCartService        moveToCartService
     */
    @Inject
    public SubmitMoveToCartFormPrototype(@RequestIdentifier final MoveToCartFormIdentifier moveToCartFormIdentifier,
                                         @RequestForm final LineItemEntity lineItemEntity,
                                         @ResourceService final MoveToCartService moveToCartService) {
        this.favoriteLineItemIdentifier = moveToCartFormIdentifier.getFavoriteLineItem();
        this.lineItemEntity = lineItemEntity;
        this.moveToCartService = moveToCartService;
    }

    @Override
    public Single<SubmitResult<LineItemIdentifier>> onSubmitWithResult() {
        return moveToCartService.move(favoriteLineItemIdentifier, lineItemEntity);
    }
}
