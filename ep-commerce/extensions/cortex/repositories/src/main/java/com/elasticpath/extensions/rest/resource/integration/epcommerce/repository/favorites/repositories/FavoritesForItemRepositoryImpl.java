/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.repositories;

import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.service.favorites.FavoriteListService;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.FavoriteIdentifierUtil.buildFavoriteIdentifier;

/**
 * favorites for item membership repository.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component(service = LinksRepository.class)
public class FavoritesForItemRepositoryImpl<I extends ItemIdentifier, LI extends FavoriteIdentifier>
        implements LinksRepository<ItemIdentifier, FavoriteIdentifier> {

    private static final Logger LOG = LoggerFactory.getLogger(FavoritesForItemRepositoryImpl.class);

    private static final String FAVORITES_WAS_NOT_FOUND = "Requested favorites was not found";

    @Reference
    private IdentifierTransformerProvider identifierTransformerProvider;

    @Reference
    private CustomerSessionRepository customerSessionRepository;

    @Reference
    private ReactiveAdapter reactiveAdapter;

    @Reference
    private ItemRepository itemRepository;

    @Reference
    private FavoriteListService favoritesService;

    @Override
    public Observable<FavoriteIdentifier> getElements(final ItemIdentifier itemIdentifier) {
        LOG.info("FavoritesForItemRepositoryImpl.getElements()");
        String encodedItemId = identifierTransformerProvider.forUriPart(ItemIdentifier.ITEM_ID).identifierToUri(itemIdentifier.getItemId());
        return findFavoritesContainingItem(encodedItemId)
                .map(favoriteList -> buildFavoriteIdentifier(favoriteList.getStoreCode(), favoriteList.getGuid()))
                .toObservable();
    }

    @CacheResult
    public Maybe<FavoriteList> findFavoritesContainingItem(final String itemId) {
        LOG.info("FavoritesForItemRepositoryImpl.findFavoritesContainingItem() ItemID :: "+itemId);
        return customerSessionRepository.findOrCreateCustomerSessionAsSingle()
                .flatMap(customerSession -> getFavoriteInternal(customerSession.getShopper()))
                .flatMapMaybe(filterFavorite(itemId))
                .filter(Objects::nonNull);
    }

    /**
     * Get the favorites for a shopper.
     *
     * @param shopper the shopper
     * @return the favorites
     */
    public Single<FavoriteList> getFavoriteInternal(final Shopper shopper) {
        LOG.info("FavoritesForItemRepositoryImpl.getFavoriteInternal()");
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
        LOG.info("FavoritesForItemRepositoryImpl.saveFavoriteIfNeeded()");
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
        LOG.info("FavoritesForItemRepositoryImpl.saveChanges()");
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.save(favoriteList));
    }

    /**
     * Filter favorites containing given item.
     *
     * @param itemId the itemId
     * @return function to perform the operation
     */
    public Function<FavoriteList, Maybe<? extends FavoriteList>> filterFavorite(final String itemId) {
        return favoriteList -> itemRepository.getSkuForItemIdAsSingle(itemId)
                .flatMapMaybe(productSku -> getFavoriteContainingItem(favoriteList, productSku.getGuid()));
    }

    /**
     * Utility method to check to see if the given sku is contained in the given favorites.
     *
     * @param favoriteList the favorites
     * @param skuGuid      the guid of the SKU
     * @return the favoriteList if the item is in the favorites, nothing otherwise
     */
    public Maybe<FavoriteList> getFavoriteContainingItem(final FavoriteList favoriteList, final String skuGuid) {
        LOG.info("FavoritesForItemRepositoryImpl.getFavoriteContainingItem() favoriteList :: "+favoriteList);
        return Observable.fromIterable(favoriteList.getAllItems())
                .filter(shoppingItem -> skuGuid.equals(shoppingItem.getSkuGuid()))
                .flatMapMaybe(shoppingItem -> Maybe.just(favoriteList))
                .switchIfEmpty(Observable.empty())
                .singleElement();
    }
}
