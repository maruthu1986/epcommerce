/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.repositories;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.service.favorites.FavoriteListService;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * Item for Favorite repository.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class ItemIdentifierRepositoryImpl<I extends FavoriteLineItemIdentifier, LI extends ItemIdentifier>
        implements LinksRepository<FavoriteLineItemIdentifier, ItemIdentifier> {

    private static final Logger LOG = LoggerFactory.getLogger(ItemIdentifierRepositoryImpl.class);

    private static final String FAVORITES_WAS_NOT_FOUND = "Requested favorites was not found";

    private static final String ITEM_NOT_FOUND = "Item not found in favorites";

    @Reference
    private ItemRepository itemRepository;

    @Reference
    private ReactiveAdapter reactiveAdapter;

    @Reference
    private FavoriteListService favoritesService;

    @Override
    public Observable<ItemIdentifier> getElements(final FavoriteLineItemIdentifier identifier) {
        LOG.info("ItemIdentifierRepositoryImpl.getElements()");
        FavoriteIdentifier favoriteIdentifier = identifier.getFavoriteLineItems().getFavorite();
        String favoriteId = favoriteIdentifier.getFavoriteId().getValue();
        String lineItemGuid = identifier.getLineItemId().getValue();
        String scope = favoriteIdentifier.getFavorites().getScope().getValue();

        return getProductSku(favoriteId, lineItemGuid)
                .map(productSku -> itemRepository.getItemIdForProductSku(productSku))
                .map(itemId -> buildItemIdentifier(scope, itemId))
                .toObservable();
    }

    /**
     * Build the item identifier.
     *
     * @param scope  scope
     * @param itemId itemId
     * @return item identifier
     */
    public ItemIdentifier buildItemIdentifier(final String scope, final IdentifierPart<Map<String, String>> itemId) {
        LOG.info("ItemIdentifierRepositoryImpl.buildItemIdentifier()");
        return ItemIdentifier.builder()
                .withItems(ItemsIdentifier.builder().withScope(StringIdentifier.of(scope)).build())
                .withItemId(itemId)
                .build();
    }

    /**
     * Product sku.
     *
     * @param favoriteId
     * @param lineItemGuid
     * @return
     */
    public Single<ProductSku> getProductSku(final String favoriteId, final String lineItemGuid) {
        return getFavorite(favoriteId)
                .flatMap(favoriteList -> getProductSku(favoriteList, lineItemGuid));
    }

    public Single<FavoriteList> getFavorite(final String guid) {
        LOG.info("ItemIdentifierRepositoryImpl.getFavorite()");
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.findByGuid(guid), FAVORITES_WAS_NOT_FOUND);
    }

    public Single<ProductSku> getProductSku(final FavoriteList favoriteList, final String lineItemGuid) {
        return getShoppingItem(favoriteList, lineItemGuid)
                .flatMap(shoppingItem -> reactiveAdapter.fromRepositoryAsSingle(() -> itemRepository.getSkuForSkuGuid(shoppingItem.getSkuGuid())))
                .toObservable()
                .filter(productsku -> isProductDiscoverable(productsku))
                .switchIfEmpty(Observable.error(ResourceOperationFailure.notFound(ITEM_NOT_FOUND)))
                .singleElement().toSingle();
    }

    public Single<ShoppingItem> getShoppingItem(final FavoriteList favoriteList, final String lineItemGuid) {
        LOG.info("ItemIdentifierRepositoryImpl.getShoppingItem() favoriteList :: " + favoriteList.getAllItems() + " :: " + lineItemGuid);
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
