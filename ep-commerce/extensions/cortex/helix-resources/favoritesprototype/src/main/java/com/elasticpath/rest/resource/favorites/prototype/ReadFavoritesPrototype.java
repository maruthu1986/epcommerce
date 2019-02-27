/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.favorites.FavoriteEntity;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoritesIdentifier;
import com.elasticpath.rest.definition.favorites.FavoritesResource;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;
import io.reactivex.Observable;

import javax.inject.Inject;

/**
 * Read favorites.
 */
public class ReadFavoritesPrototype implements FavoritesResource.Read {
    private final IdentifierPart<String> scope;

    private final Repository<FavoriteEntity, FavoriteIdentifier> repository;

    /**
     * Constructor.
     *
     * @param scope      scope
     * @param repository repository
     */
    @Inject
    public ReadFavoritesPrototype(@UriPart(FavoritesIdentifier.SCOPE) final IdentifierPart<String> scope,
                                  @ResourceRepository final Repository<FavoriteEntity, FavoriteIdentifier> repository) {
        this.scope = scope;
        this.repository = repository;
    }

    @Override
    public Observable<FavoriteIdentifier> onRead() {
        return repository.findAll(scope);
    }
}
