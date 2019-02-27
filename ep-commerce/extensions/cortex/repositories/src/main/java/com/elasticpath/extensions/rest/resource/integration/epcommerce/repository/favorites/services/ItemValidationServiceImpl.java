/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.services;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.ItemValidationService;
import com.elasticpath.extensions.service.favorites.FavoriteListService;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.AddToCartAdvisorService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Item validation service.
 */
@Component
public class ItemValidationServiceImpl implements ItemValidationService {
    private static final Logger LOG = LoggerFactory.getLogger(ItemValidationServiceImpl.class);
    private static final String FAVORITES_WAS_NOT_FOUND = "Requested favorites was not found";
    private static final String ITEM_NOT_FOUND = "Item not found in favorites";

    @Reference
    private ItemRepository itemRepository;

    @Reference
    private AddToCartAdvisorService addToCartAdvisorService;

    @Reference
    private ShoppingCartRepository shoppingCartRepository;

    @Reference
    private ReactiveAdapter reactiveAdapter;

    @Reference
    private FavoriteListService favoritesService;

    @Override
    public Observable<Message> isItemPurchasable(final FavoriteLineItemIdentifier favoriteLineItemIdentifier) {
        LOG.info("ItemValidationServiceImpl.isItemPurchasable()");
        FavoriteIdentifier favoriteIdentifier = favoriteLineItemIdentifier.getFavoriteLineItems().getFavorite();
        String lineItemGuid = favoriteLineItemIdentifier.getLineItemId().getValue();
        String favorite = favoriteIdentifier.getFavoriteId().getValue();
        return shoppingCartRepository.getDefaultShoppingCartGuid()
                .flatMapObservable(cartGuid -> getFavorite(favorite)
                        .flatMapObservable(toMessages(cartGuid, lineItemGuid)));

    }

    public Single<FavoriteList> getFavorite(final String guid) {
        LOG.info("ItemValidationServiceImpl.getFavorite()");
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.findByGuid(guid), FAVORITES_WAS_NOT_FOUND);
    }

    /**
     * Get structured advise messages, if any.
     *
     * @param cartId       the cart id.
     * @param lineItemGuid the line item guid.
     * @return the function
     */
    public Function<FavoriteList, Observable<Message>> toMessages(final String cartId, final String lineItemGuid) {
        LOG.info("ItemValidationServiceImpl.toMessages()");
        return favoriteList -> getProductSku(favoriteList, lineItemGuid)
                .flatMapObservable(productSku -> addToCartAdvisorService.validateItemPurchasable(favoriteList.getStoreCode(), cartId, productSku, null));
    }

    /**
     * getProductSku.
     * @param favoriteList
     * @param lineItemGuid
     * @return
     */
    public Single<ProductSku> getProductSku(final FavoriteList favoriteList, final String lineItemGuid) {
        LOG.info("ItemValidationServiceImpl.getProductSku()"+favoriteList.getAllItems());
        return getShoppingItem(favoriteList, lineItemGuid)
                .flatMap(shoppingItem -> reactiveAdapter.fromRepositoryAsSingle(() -> itemRepository.getSkuForSkuGuid(shoppingItem.getSkuGuid())))
                .toObservable()
                .filter(productsku -> isProductDiscoverable(productsku))
                .switchIfEmpty(Observable.error(ResourceOperationFailure.notFound(ITEM_NOT_FOUND)))
                .singleElement().toSingle();
    }

    /**
     * getShoppingItem.
     * @param favoriteList
     * @param lineItemGuid
     * @return
     */
    public Single<ShoppingItem> getShoppingItem(final FavoriteList favoriteList, final String lineItemGuid) {
        LOG.info("ItemValidationServiceImpl.getShoppingItem()"+favoriteList);
        return Observable.fromIterable(favoriteList.getAllItems())
                .filter(shoppingItem -> shoppingItem.getGuid().equals(lineItemGuid))
                .switchIfEmpty(Observable.error(ResourceOperationFailure.notFound(ITEM_NOT_FOUND)))
                .firstElement().toSingle();
    }

    /**
     * Checks whether a product is discoverable.
     *
     * @param productSku the product sku
     * @return true if the product is discoverable
     */
    static boolean isProductDiscoverable(final ProductSku productSku) {
        Date now = new Date();
        return productSku != null
                && productSku.isWithinDateRange(now)
                && !productSku.getProduct().isHidden()
                && productSku.getProduct().isWithinDateRange(now);
    }
}
