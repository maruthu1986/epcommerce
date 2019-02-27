/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.repositories;

import com.elasticpath.base.GloballyIdentifiable;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.ShopperReference;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.service.favorites.FavoriteListService;
import com.elasticpath.repository.AliasRepository;
import com.elasticpath.rest.definition.favorites.DefaultFavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoritesIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Repository for Default Favoritelist Identifiers.
 *
 * @param <AI> the Alias identifier type
 * @param <I>  the identifier type
 */
@Component
public class DefaultFavoriteIdentifierRepositoryImpl<AI extends DefaultFavoriteIdentifier, I extends FavoriteIdentifier>
        implements AliasRepository<DefaultFavoriteIdentifier, FavoriteIdentifier> {

    private static final Logger LOG = Logger.getLogger(DefaultFavoriteIdentifierRepositoryImpl.class);

    private static final String FAVORITES_WAS_NOT_FOUND = "Requested favorites was not found";

    @Reference
    private CustomerSessionRepository customerSessionRepository;

    @Reference
    private ResourceOperationContext resourceOperationContext;

    @Reference
    private ReactiveAdapter reactiveAdapter;

    @Reference
    private FavoriteListService favoritesService;

    @Override
    public Single<FavoriteIdentifier> resolve(final DefaultFavoriteIdentifier defaultFavoriteIdentifier) {
        LOG.info("DefaultFavoriteIdentifierRepositoryImpl.resolve()");
        IdentifierPart<String> scope = defaultFavoriteIdentifier.getFavorites().getScope();
        return getDefaultFavoriteId(scope.getValue())
                .map(defaultId -> FavoriteIdentifier.builder()
                        .withFavoriteId(StringIdentifier.of(defaultId))
                        .withFavorites(FavoritesIdentifier.builder()
                                .withScope(scope)
                                .build())
                        .build());
    }

    public Single<String> getDefaultFavoriteId(final String scope) {
        LOG.info("DefaultFavoriteIdentifierRepositoryImpl.getDefaultFavoriteId()");
        //Only one favorites supported!!
        return getFavoriteIds(resourceOperationContext.getUserIdentifier(), scope)
                .firstElement()
                .toSingle();
    }

    public Observable<String> getFavoriteIds(final String customerGuid, final String storeCode) {
        LOG.info("DefaultFavoriteIdentifierRepositoryImpl.getFavoriteIds()");
        return customerSessionRepository.findCustomerSessionByGuidAsSingle(customerGuid)
                .map(ShopperReference::getShopper)
                .flatMapObservable(shopper -> getFavoriteInternal(shopper)
                        .map(GloballyIdentifiable::getGuid)
                        .toObservable());
    }

    /**
     * Get the favorites for a shopper.
     *
     * @param shopper the shopper
     * @return the favorites
     */
    public Single<FavoriteList> getFavoriteInternal(final Shopper shopper) {
        LOG.info("DefaultFavoriteIdentifierRepositoryImpl.getFavoriteInternal()");
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.findOrCreateFavoriteListByShopper(shopper), FAVORITES_WAS_NOT_FOUND)
                .flatMap(this::saveFavoriteIfNeeded);
    }

    /**
     * Save the favorites if needed.
     *
     * @param favoriteList the favoriteList
     * @return the favoriteList
     */
    public Single<FavoriteList> saveFavoriteIfNeeded(final FavoriteList favoriteList) {
        LOG.info("DefaultFavoriteIdentifierRepositoryImpl.saveFavoriteIfNeeded()");
        if (!favoriteList.isPersisted()) {
            return saveChanges(favoriteList);
        }
        return Single.just(favoriteList);
    }

    /**
     * Save the changes to the favorites.
     *
     * @param favoriteList the favorites
     * @return the updated favorites
     */
    public Single<FavoriteList> saveChanges(final FavoriteList favoriteList) {
        LOG.info("DefaultFavoriteIdentifierRepositoryImpl.saveChanges()");
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.save(favoriteList));
    }
}


