/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.lineitems.advise;

import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.ItemValidationService;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.MoveToCartFormAdvisor;
import com.elasticpath.rest.definition.favorites.MoveToCartFormIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import io.reactivex.Observable;

import javax.inject.Inject;

/**
 * Advisor for move to cart.
 */
public class MoveToCartFormAdvisorImpl implements MoveToCartFormAdvisor.FormAdvisor {

    private final FavoriteLineItemIdentifier favoriteLineItemIdentifier;

    private final ItemValidationService itemValidationService;

    /**
     * Constructor.
     *
     * @param moveToCartFormIdentifier moveToCartFormIdentifier
     * @param itemValidationService    itemValidationService
     */
    @Inject
    public MoveToCartFormAdvisorImpl(@RequestIdentifier final MoveToCartFormIdentifier moveToCartFormIdentifier,
                                     @ResourceService final ItemValidationService itemValidationService) {
        this.favoriteLineItemIdentifier = moveToCartFormIdentifier.getFavoriteLineItem();
        this.itemValidationService = itemValidationService;
    }

    @Override
    public Observable<Message> onAdvise() {
        return itemValidationService.isItemPurchasable(favoriteLineItemIdentifier);
    }
}
