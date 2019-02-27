/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.services;

import com.elasticpath.base.GloballyIdentifiable;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.ShopperReference;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.MoveToFavoriteService;
import com.elasticpath.extensions.service.favorites.FavoriteListService;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.shoppingcart.impl.AddToWishlistResult;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.FavoriteIdentifierUtil.buildFavoriteLineItemIdentifier;

/**
 * Move to favorites service.
 */
@Component
public class MoveToFavoriteServiceImpl implements MoveToFavoriteService {

    private static final Logger LOG = LoggerFactory.getLogger(MoveToFavoriteServiceImpl.class);
    private static final String FAVORITES_WAS_NOT_FOUND = "Requested favorites was not found";

    @Reference
    private ShoppingCartRepository shoppingCartRepository;

    @Reference
    private CustomerSessionRepository customerSessionRepository;

    @Reference
    private ReactiveAdapter reactiveAdapter;

    @Reference
    private FavoriteListService favoritesService;

    @Reference
    private ResourceOperationContext resourceOperationContext;


    @Override
    public Single<SubmitResult<FavoriteLineItemIdentifier>> move(final LineItemIdentifier lineItemIdentifier) {

        String cartLineItemId = lineItemIdentifier.getLineItemId().getValue();

        CartIdentifier cartIdentifier = lineItemIdentifier.getLineItems().getCart();
        String cartId = cartIdentifier.getCartId().getValue();
        String scope = cartIdentifier.getScope().getValue();

        LOG.trace("Moving line id {} from cart {} to favorites", cartLineItemId, cartId);
        return getDefaultFavoriteId(scope)
                .flatMap(toSubmitResult(cartLineItemId, scope));
    }

    /**
     * Move item to favorites.
     *
     * @param cartLineItemId line item id
     * @param scope          scope
     * @return the function
     */
    protected Function<String, Single<SubmitResult<FavoriteLineItemIdentifier>>> toSubmitResult(final String cartLineItemId, final String scope) {
        return favoritesId -> shoppingCartRepository.getDefaultShoppingCart()
                .flatMap(cart -> shoppingCartRepository.moveItemToWishlist(cart, cartLineItemId))
                .flatMap(addToFavoritesResult -> buildSubmitResult(scope, favoritesId, addToFavoritesResult));
    }

    public Single<SubmitResult<FavoriteLineItemIdentifier>> buildSubmitResult(final String scope, final String favoritesId,
                                                                              final AddToWishlistResult addToFavoriteResult) {
        ShoppingItem shoppingItem = addToFavoriteResult.getShoppingItem();
        return Single.just(buildFavoriteLineItemIdentifier(scope, favoritesId, shoppingItem.getGuid()))
                .map(favoriteLineItemIdentifier -> SubmitResult.<FavoriteLineItemIdentifier>builder()
                        .withIdentifier(favoriteLineItemIdentifier)
                        .withStatus(addToFavoriteResult.isNewlyCreated() ? SubmitStatus.CREATED : SubmitStatus.EXISTING)
                        .build());
    }

    @CacheResult
    public Observable<String> getFavoriteIds(final String customerGuid, final String storeCode) {
        LOG.trace("finding by customer");
        return customerSessionRepository.findCustomerSessionByGuidAsSingle(customerGuid)
                .map(ShopperReference::getShopper)
                .flatMapObservable(shopper -> getFavoriteInternal(shopper)
                        .map(GloballyIdentifiable::getGuid)
                        .toObservable());
    }

    @CacheResult(uniqueIdentifier = "cachingGetDefaultWishlist")
    public Single<String> getDefaultFavoriteId(final String scope) {
        //Only one favorites supported!!
        return getFavoriteIds(resourceOperationContext.getUserIdentifier(), scope)
                .firstElement()
                .toSingle();
    }

    /**
     * Get the favorites for a shopper.
     *
     * @param shopper the shopper
     * @return the favorites
     */
    protected Single<FavoriteList> getFavoriteInternal(final Shopper shopper) {
        LOG.trace("Wishlist by shopper: {}", shopper.getGuid());
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.findOrCreateFavoriteListByShopper(shopper), FAVORITES_WAS_NOT_FOUND)
                .flatMap(this::saveFavoriteIfNeeded);
    }

    /**
     * Save the favorites if needed.
     *
     * @param favoriteList the favoriteList
     * @return the favoriteList
     */
    protected Single<FavoriteList> saveFavoriteIfNeeded(final FavoriteList favoriteList) {
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
    protected Single<FavoriteList> saveChanges(final FavoriteList favoriteList) {
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.save(favoriteList));
    }
}
