package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.impl;

import com.elasticpath.base.GloballyIdentifiable;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.ShopperReference;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.service.favorites.FavoriteListService;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.favorites.FavoriteEntity;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


import static com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.FavoriteIdentifierUtil.buildFavoriteIdentifier;


/**
 * Favorite Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class FavEntityRepositoryImpl<E extends FavoriteEntity, I extends FavoriteIdentifier> implements Repository<FavoriteEntity, FavoriteIdentifier> {

    private static final Logger LOG = Logger.getLogger(FavEntityRepositoryImpl.class);
    private static final String FAVORITES_WAS_NOT_FOUND = "Requested favorites was not found";

    @Reference
    private ResourceOperationContext resourceOperationContext;

    @Reference
    private CustomerSessionRepository customerSessionRepository;

    @Reference
    private ReactiveAdapter reactiveAdapter;

    @Reference
    private FavoriteListService favoritesService;

    @Reference
    private BeanFactory coreBeanFactory;

    @Override
    public Single<FavoriteEntity> findOne(final FavoriteIdentifier identifier) {
        String favoriteId = identifier.getFavoriteId().getValue();
        return getFavorite(favoriteId)
                .map(favoriteList -> FavoriteEntity.builder()
                        .withFavoriteId(favoriteList.getGuid())
                        .build());
    }

    @Override
    public Observable<FavoriteIdentifier> findAll(final IdentifierPart<String> scope) {
        LOG.info("FavEntityRepositoryImpl.findAll()");
        String customerGuid = resourceOperationContext.getUserIdentifier();
        return getFavoriteIds(customerGuid, scope.getValue()).map(favoriteId -> buildFavoriteIdentifier(scope.getValue(), favoriteId));
    }

    public Observable<String> getFavoriteIds(final String customerGuid, final String storeCode) {
        LOG.info("FavEntityRepositoryImpl.getFavoriteIds()");
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
        LOG.info("FavEntityRepositoryImpl getFavoriteInternal()");
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
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.save(favoriteList));
    }

    /**
     * Save the changes to the favorites.
     *
     * @param guid the favorites
     * @return the updated favorites
     */
    public Single<FavoriteList> getFavorite(final String guid) {
        LOG.info("FavEntityRepositoryImpl.getFavorite()");
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.findByGuid(guid), FAVORITES_WAS_NOT_FOUND);
    }
}
