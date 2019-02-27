/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.services;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.ItemValidationService;
import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.MoveToCartService;
import com.elasticpath.extensions.service.favorites.FavoriteListService;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.AddToCartAdvisorService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Move to cart service.
 */
@Component
public class MoveToCartServiceImpl implements MoveToCartService {

    private static final Logger LOG = LoggerFactory.getLogger(MoveToCartServiceImpl.class);
    private static final String FAVORITES_WAS_NOT_FOUND = "Requested favorites was not found";
    private static final String ITEM_NOT_FOUND = "Item not found in favorites";

    @Reference
    private ShoppingCartRepository shoppingCartRepository;

    @Reference
    private ItemValidationService itemValidationService;

    @Reference
    private AddToCartAdvisorService addToCartAdvisorService;

    @Reference
    private ItemRepository itemRepository;

    @Reference
    private ReactiveAdapter reactiveAdapter;

    @Reference
    private FavoriteListService favoritesService;

    @Override
    public Single<SubmitResult<LineItemIdentifier>> move(
            final FavoriteLineItemIdentifier favoriteLineItemIdentifier, final LineItemEntity lineItemEntity) {

        FavoriteIdentifier favoriteIdentifier = favoriteLineItemIdentifier.getFavoriteLineItems().getFavorite();
        LOG.info("MoveToCartServiceImpl.move(), Moving item {} from favorites {} to cart", lineItemEntity, favoriteIdentifier);

        return addToCartAdvisorService.validateLineItemEntity(lineItemEntity)
                .andThen(isItemPurchasable(favoriteLineItemIdentifier))
                .andThen(moveItemToCart(favoriteLineItemIdentifier, lineItemEntity, favoriteIdentifier));
    }


    /**
     * isItemPurchasable.
     * @param favoriteLineItemIdentifier FavoriteLineItemIdentifier
     */
    public Completable isItemPurchasable(final FavoriteLineItemIdentifier favoriteLineItemIdentifier) {
        return itemValidationService.isItemPurchasable(favoriteLineItemIdentifier)
                .flatMapCompletable(this::getStateFailure);
    }

    /**
     * Get the resource operation failure.
     *
     * @param message the message
     * @return the resource operation failure
     */
    public Completable getStateFailure(final Message message) {
        return Completable.error(ResourceOperationFailure.stateFailure("Item is not purchasable",
                Collections.singletonList(message)));
    }

    /**
     * Move item to cart.
     *
     * @param favoriteLineItemIdentifier favorites line item identifier
     * @param lineItemEntity             line item entity
     * @param favoriteIdentifier         favorites identifier
     * @return submit result of line item identifier
     */
    public Single<SubmitResult<LineItemIdentifier>> moveItemToCart(
            final FavoriteLineItemIdentifier favoriteLineItemIdentifier,
            final LineItemEntity lineItemEntity,
            final FavoriteIdentifier favoriteIdentifier) {

        String lineItemGuid = favoriteLineItemIdentifier.getLineItemId().getValue();
        String favoriteId = favoriteIdentifier.getFavoriteId().getValue();

        return getFavorite(favoriteId)
                .flatMap(favoriteList -> getProductSku(favoriteList, lineItemGuid)
                        .map(ProductSku::getSkuCode)
                        .flatMap(skuCode -> shoppingCartRepository.getDefaultShoppingCart()
                                .flatMap(cart -> moveItemToCart(cart, lineItemGuid, skuCode, lineItemEntity))));
    }

    public Single<FavoriteList> getFavorite(final String guid) {
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.findByGuid(guid), FAVORITES_WAS_NOT_FOUND);
    }

    @CacheResult(uniqueIdentifier = "cachingGetProductSku")
    public Single<ProductSku> getProductSku(final FavoriteList favoriteList, final String lineItemGuid) {
        return getShoppingItem(favoriteList, lineItemGuid)
                .flatMap(shoppingItem -> reactiveAdapter.fromRepositoryAsSingle(() -> itemRepository.getSkuForSkuGuid(shoppingItem.getSkuGuid())))
                .toObservable()
                .filter(productsku -> isProductDiscoverable(productsku))
                .switchIfEmpty(Observable.error(ResourceOperationFailure.notFound(ITEM_NOT_FOUND)))
                .singleElement().toSingle();
    }

    @CacheResult(uniqueIdentifier = "cachingGetShoppingItem")
    public Single<ShoppingItem> getShoppingItem(final FavoriteList favoriteList, final String lineItemGuid) {
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

    /**
     * Move item to cart.
     *
     * @param shoppingCart   shopping cart
     * @param lineItemGuid   line item guid
     * @param skuCode        sku code
     * @param lineItemEntity line item entity
     * @return submit result of line item identifier
     */
    public Single<SubmitResult<LineItemIdentifier>> moveItemToCart(
            final ShoppingCart shoppingCart, final String lineItemGuid, final String skuCode, final LineItemEntity lineItemEntity) {

        return shoppingCartRepository.moveItemToCart(shoppingCart, lineItemGuid, skuCode, lineItemEntity.getQuantity(),
                getConfigurableFields(lineItemEntity))
                .flatMap(shoppingItem -> buildLineItemIdentifier(shoppingCart, shoppingItem, lineItemEntity));
    }

    /**
     * Get configurable fields for a line item.
     *
     * @param lineItemEntity line item entity
     * @return configurable fields
     */
    public Map<String, String> getConfigurableFields(final LineItemEntity lineItemEntity) {
        return Optional.ofNullable(lineItemEntity.getConfiguration())
                .map(LineItemConfigurationEntity::getDynamicProperties)
                .orElse(Collections.emptyMap());
    }

    /**
     * Build line item identifier.
     *
     * @param cart           the cart
     * @param shoppingItem   the shopping item
     * @param lineItemEntity line item entity
     * @return submit result of line item identifier
     */
    public Single<SubmitResult<LineItemIdentifier>> buildLineItemIdentifier(
            final ShoppingCart cart, final ShoppingItem shoppingItem, final LineItemEntity lineItemEntity) {

        LineItemIdentifier identifier = LineItemIdentifier.builder()
                .withLineItemId(StringIdentifier.of(shoppingItem.getGuid()))
                .withLineItems(buildLineItemsIdentifier(cart))
                .build();

        boolean cartItemCreated = shoppingItem.getQuantity() == lineItemEntity.getQuantity();

        return Single.just(SubmitResult.<LineItemIdentifier>builder()
                .withIdentifier(identifier)
                .withStatus(cartItemCreated
                        ? SubmitStatus.CREATED
                        : SubmitStatus.UPDATED)
                .build());
    }

    private LineItemsIdentifier buildLineItemsIdentifier(final ShoppingCart cart) {
        return LineItemsIdentifier.builder()
                .withCart(buildCartIdentifier(cart))
                .build();
    }

    private CartIdentifier buildCartIdentifier(final ShoppingCart cart) {
        return CartIdentifier.builder()
                .withCartId(StringIdentifier.of(cart.getGuid()))
                .withScope(StringIdentifier.of(cart.getStore().getCode()))
                .build();
    }
}
