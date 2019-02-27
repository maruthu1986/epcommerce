/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.lineitems.prototypes;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemEntity;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.MoveToCartFormIdentifier;
import com.elasticpath.rest.definition.favorites.MoveToCartFormResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import io.reactivex.Single;

import javax.inject.Inject;

/**
 * Move to cart form.
 */
public class ReadMoveToCartFormPrototype implements MoveToCartFormResource.Read {

    private final FavoriteLineItemIdentifier favoriteLineItemIdentifier;

    private final Repository<FavoriteLineItemEntity, FavoriteLineItemIdentifier> repository;

    /**
     * Constructor.
     *
     * @param moveToCartFormIdentifier moveToCartFormIdentifier
     * @param repository               repository
     */
    @Inject
    public ReadMoveToCartFormPrototype(@RequestIdentifier final MoveToCartFormIdentifier moveToCartFormIdentifier,
                                       @ResourceRepository final Repository<FavoriteLineItemEntity, FavoriteLineItemIdentifier> repository) {
        this.favoriteLineItemIdentifier = moveToCartFormIdentifier.getFavoriteLineItem();
        this.repository = repository;
    }

    @Override
    public Single<LineItemEntity> onRead() {
        return repository.findOne(favoriteLineItemIdentifier)
                .map(entity -> LineItemEntity.builder()
                        .withQuantity(0)
                        .withConfiguration(entity.getConfiguration())
                        .build());
    }
}
