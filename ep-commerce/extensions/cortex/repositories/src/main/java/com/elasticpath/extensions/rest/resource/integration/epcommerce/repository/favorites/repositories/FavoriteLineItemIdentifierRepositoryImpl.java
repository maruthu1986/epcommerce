/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.repositories;

import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.service.favorites.FavoriteListService;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.cache.CacheRemove;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.FavoriteIdentifierUtil.buildFavoriteLineItemIdentifier;

/**
 * Repository for line items in a favorites.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class FavoriteLineItemIdentifierRepositoryImpl<I extends FavoriteIdentifier, LI extends FavoriteLineItemIdentifier>
        implements LinksRepository<FavoriteIdentifier, FavoriteLineItemIdentifier> {

    private static final Logger LOG = LoggerFactory.getLogger(FavoriteLineItemIdentifierRepositoryImpl.class);

    private static final String FAVORITES_WAS_NOT_FOUND = "Requested favorites was not found";

    @Reference
    private ReactiveAdapter reactiveAdapter;

    @Reference
    private FavoriteListService favoritesService;

    @Override
    public Observable<FavoriteLineItemIdentifier> getElements(final FavoriteIdentifier favoriteIdentifier) {
        LOG.info("FavoriteLineItemIdentifierRepositoryImpl.getElements()");
        String favoriteId = favoriteIdentifier.getFavoriteId().getValue();
        String scope = favoriteIdentifier.getFavorites().getScope().getValue();
        return getFavorite(favoriteId)
                .flatMapObservable(favoriteList -> Observable.fromIterable(favoriteList.getAllItems()))
                .map(shoppingItem -> buildFavoriteLineItemIdentifier(scope, favoriteId, shoppingItem.getGuid()));
    }

    /**
     * getFavorite.
     * @param guid
     * @return
     */
    public Single<FavoriteList> getFavorite(final String guid) {
        LOG.info("FavoriteLineItemIdentifierRepositoryImpl.getFavorite() "+guid);
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.findByGuid(guid), FAVORITES_WAS_NOT_FOUND);
    }

    @Override
    public Completable deleteAll(final FavoriteIdentifier favoriteIdentifier) {
        String favoriteID = favoriteIdentifier.getFavoriteId().getValue();
        return removeAllItemsFromFavorites(favoriteID);
    }

    @CacheRemove(typesToInvalidate = {FavoriteList.class})
    public Completable removeAllItemsFromFavorites(final String favoriteGuid) {
        return getFavorite(favoriteGuid)
                .flatMapCompletable(this::removeAllItemsFromFavorites)
                .onErrorResumeNext(throwable -> Completable.error(ResourceOperationFailure
                        .serverError("Error saving the changes to favorites with id {}" + favoriteGuid + ", Could not remove all items.")));
    }

    /**
     * Remove all items from favorites.
     *
     * @param favoriteList favorite list
     * @return the Completable
     */
    public Completable removeAllItemsFromFavorites(final FavoriteList favoriteList) {
        favoriteList.setAllItems(new ArrayList<>());
        return saveChanges(favoriteList).toCompletable();
    }

    /**
     * Save the changes to the favorites.
     *
     * @param favoriteList the favorites
     * @return the updated favorites
     */
    public Single<FavoriteList> saveChanges(final FavoriteList favoriteList) {
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.save(favoriteList));
    }

}
