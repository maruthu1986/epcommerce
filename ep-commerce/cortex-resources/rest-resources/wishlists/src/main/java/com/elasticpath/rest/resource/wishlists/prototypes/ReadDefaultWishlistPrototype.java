/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.AliasRepository;
import com.elasticpath.rest.definition.wishlists.DefaultWishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.DefaultWishlistResource;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read prototype for the default favorites.
 */
public class ReadDefaultWishlistPrototype implements DefaultWishlistResource.Read {

    private final DefaultWishlistIdentifier defaultWishlistIdentifier;

    private final AliasRepository<DefaultWishlistIdentifier, WishlistIdentifier> repository;

    /**
     * Constructor.
     *
     * @param defaultWishlistIdentifier defaultWishlistIdentifier
     * @param repository the repository
     */
    @Inject
    public ReadDefaultWishlistPrototype(@RequestIdentifier final DefaultWishlistIdentifier defaultWishlistIdentifier,
                                        @ResourceRepository final AliasRepository<DefaultWishlistIdentifier, WishlistIdentifier> repository) {
        this.defaultWishlistIdentifier = defaultWishlistIdentifier;
        this.repository = repository;
    }

    @Override
    public Single<WishlistIdentifier> onRead() {
        return repository.resolve(defaultWishlistIdentifier);
    }
}
