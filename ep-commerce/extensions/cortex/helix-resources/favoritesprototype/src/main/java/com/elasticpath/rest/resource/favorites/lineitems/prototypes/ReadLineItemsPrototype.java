/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.lineitems.prototypes;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemsIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import io.reactivex.Observable;

import javax.inject.Inject;

/**
 * Read line items.
 */
public class ReadLineItemsPrototype implements FavoriteLineItemsResource.Read {

    private final LinksRepository<FavoriteIdentifier, FavoriteLineItemIdentifier> repository;
    private final FavoriteLineItemsIdentifier favoriteLineItemsIdentifier;

    /**
     * Constructor.
     *
     * @param favoriteLineItemsIdentifier line items identifier
     * @param repository                  repository
     */
    @Inject
    public ReadLineItemsPrototype(@RequestIdentifier final FavoriteLineItemsIdentifier favoriteLineItemsIdentifier,
                                  @ResourceRepository final LinksRepository<FavoriteIdentifier, FavoriteLineItemIdentifier> repository) {
        this.favoriteLineItemsIdentifier = favoriteLineItemsIdentifier;
        this.repository = repository;
    }

    @Override
    public Observable<FavoriteLineItemIdentifier> onRead() {
        return repository.getElements(favoriteLineItemsIdentifier.getFavorite());
    }
}
