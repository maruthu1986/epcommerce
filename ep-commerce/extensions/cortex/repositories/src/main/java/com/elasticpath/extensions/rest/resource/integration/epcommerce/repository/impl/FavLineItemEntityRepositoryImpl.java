package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.impl;

import com.elasticpath.base.GloballyIdentifiable;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.ShopperReference;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.service.favorites.FavoriteListService;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.cache.CacheRemove;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemEntity;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartItemModifiersRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.shoppingcart.impl.AddToWishlistResult;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.FavoriteIdentifierUtil.buildFavoriteLineItemIdentifier;

@Component
public class FavLineItemEntityRepositoryImpl<E extends FavoriteLineItemEntity, I extends FavoriteLineItemIdentifier> implements Repository<FavoriteLineItemEntity, FavoriteLineItemIdentifier> {

    private static final Logger LOG = LoggerFactory.getLogger(FavLineItemEntityRepositoryImpl.class);
    private static final String FAVORITES_WAS_NOT_FOUND = "Requested favorites was not found";
    private static final String ITEM_NOT_FOUND = "Item not found in favorites";

    @Reference
    private ItemRepository itemRepository;

    @Reference
    private CartItemModifiersRepository cartItemModifiersRepository;

    @Reference
    private CustomerSessionRepository customerSessionRepository;

    @Reference
    private StoreRepository storeRepository;

    @Reference
    private ResourceOperationContext resourceOperationContext;

    @Reference
    private ReactiveAdapter reactiveAdapter;

    @Reference
    private FavoriteListService favoritesService;

    @Reference
    private BeanFactory coreBeanFactory;

    @Override
    public Single<SubmitResult<FavoriteLineItemIdentifier>> submit(final FavoriteLineItemEntity entity, final IdentifierPart<String> scope) {
        LOG.info("FavLineItemEntityRepositoryImpl, Adding item id {} to favorites {}", entity.getItemId(), entity.getFavoriteId());
        String sku = entity.getItemId();
        return getDefaultFavoriteId(scope.getValue())
                .flatMap(favoriteId -> addItemToFavorite(favoriteId, scope.getValue(), sku)
                        .flatMap(addToFavoriteResult -> buildSubmitResult(scope.getValue(), favoriteId,
                                addToFavoriteResult)));
    }

    @Override
    public Single<FavoriteLineItemEntity> findOne(final FavoriteLineItemIdentifier identifier) {
        LOG.info("FavLineItemEntityRepositoryImpl.findOne()");
        FavoriteIdentifier favoriteIdentifier = identifier.getFavoriteLineItems().getFavorite();
        String favoriteId = favoriteIdentifier.getFavoriteId().getValue();
        String lineItemGuid = identifier.getLineItemId().getValue();

        return getFavorite(favoriteId)
                .flatMap(toFavoriteLineItemEntity(lineItemGuid));
    }

    /**
     * Get the favorites line item entity.
     *
     * @param lineItemGuid the line item guid
     * @return function
     */
    public Function<FavoriteList, Single<FavoriteLineItemEntity>> toFavoriteLineItemEntity(final String lineItemGuid) {
        LOG.info("FavLineItemEntityRepositoryImpl.toFavoriteLineItemEntity()");
        return favoriteList -> getShoppingItem(favoriteList, lineItemGuid)
                .flatMap(shoppingItem -> getProductSku(favoriteList, lineItemGuid)
                        .flatMap(productSku -> itemRepository.getItemIdForSkuAsSingle(productSku)
                                .flatMap(itemId -> buildFavoriteLineItemEntity(shoppingItem, productSku, itemId, lineItemGuid, favoriteList))));
    }

    /**
     * Get product.
     *
     * @param favoriteId
     * @param lineItemGuid
     * @return
     */
    public Single<ProductSku> getProductSku(final String favoriteId, final String lineItemGuid) {
        LOG.info("FavLineItemEntityRepositoryImpl.getProductSku()");
        return getFavorite(favoriteId)
                .flatMap(favoriteList -> getProductSku(favoriteList, lineItemGuid));
    }

    /**
     * getProductSku.
     *
     * @param favoriteList
     * @param lineItemGuid
     * @return
     */
    public Single<ProductSku> getProductSku(final FavoriteList favoriteList, final String lineItemGuid) {
        LOG.info("FavLineItemEntityRepositoryImpl.getProductSku()");
        return getShoppingItem(favoriteList, lineItemGuid)
                .flatMap(shoppingItem -> reactiveAdapter.fromRepositoryAsSingle(() -> itemRepository.getSkuForSkuGuid(shoppingItem.getSkuGuid())))
                .toObservable()
                .filter(productSku -> isProductDiscoverable(productSku))
                .switchIfEmpty(Observable.error(ResourceOperationFailure.notFound(ITEM_NOT_FOUND)))
                .singleElement().toSingle();
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
     * Build the favorites line item entity.
     *
     * @param shoppingItem the shopping item
     * @param productSku   the product sku
     * @param itemId       item id
     * @param lineItemGuid line item guid
     * @param favoriteList favorites
     * @return favorites line item entity
     */
    public Single<FavoriteLineItemEntity> buildFavoriteLineItemEntity(final ShoppingItem shoppingItem, final ProductSku
            productSku, final String itemId, final String lineItemGuid, final FavoriteList favoriteList) {
        LOG.info("FavLineItemEntityRepositoryImpl.buildFavoriteLineItemEntity()");
        Map<String, String> fields = shoppingItem.getFields();
        Single<LineItemConfigurationEntity> lineItemConfigurationEntity;
        if (fields == null) {
            lineItemConfigurationEntity = Single.just(LineItemConfigurationEntity.builder().build());
        } else {
            lineItemConfigurationEntity = reactiveAdapter
                    .fromServiceAsSingle(() -> cartItemModifiersRepository.findCartItemModifiersByProduct(productSku.getProduct()))
                    .map(cartItemModifierFields -> buildLineItemConfigurationEntity(fields, cartItemModifierFields));
        }
        LOG.info("FavLineItemEntityRepositoryImpl.buildFavoriteLineItemEntity() "+lineItemConfigurationEntity);
        return lineItemConfigurationEntity.map(configuration -> FavoriteLineItemEntity.builder()
                .withItemId(itemId)
                .withLineItemId(lineItemGuid)
                .withFavoriteId(favoriteList.getGuid())
                .withConfiguration(configuration)
                .build());
    }

    /**
     * Build the line item configuration entity.
     *
     * @param fields                 shopping item configuration fields
     * @param cartItemModifierFields cart item modifier fields
     * @return line item configuration entity
     */
    public LineItemConfigurationEntity buildLineItemConfigurationEntity(final Map<String, String> fields,
                                                                        final List<CartItemModifierField> cartItemModifierFields) {
        LOG.info("FavLineItemEntityRepositoryImpl.buildLineItemConfigurationEntity()");
        LineItemConfigurationEntity.Builder builder = LineItemConfigurationEntity.builder();
        cartItemModifierFields.forEach(field -> builder.addingProperty(field.getCode(), fields.get(field.getCode())));
        return builder.build();
    }

    @Override
    public Completable delete(final FavoriteLineItemIdentifier lineItemIdentifier) {
        FavoriteIdentifier favoriteIdentifier = lineItemIdentifier.getFavoriteLineItems().getFavorite();
        String favoriteId = favoriteIdentifier.getFavoriteId().getValue();
        return removeItemFromFavorite(favoriteId, lineItemIdentifier.getLineItemId().getValue());
    }

    @CacheRemove(typesToInvalidate = {FavoriteList.class})
    public Completable removeItemFromFavorite(final String favoriteId, final String lineItemGuid) {
        return getFavorite(favoriteId)
                .flatMapCompletable(favoriteList -> removeItemFromFavorite(favoriteId, lineItemGuid))
                .onErrorResumeNext(throwable -> Completable.error(ResourceOperationFailure.
                        serverError("Error saving the changes to favorites with id {}"
                                + favoriteId + ", Could not remove item with id" + lineItemGuid)));
    }

    /**
     * addItemToFavorite.
     *
     * @param favoriteId
     * @param storecode
     * @param sku
     * @return
     */
    public Single<AddToWishlistResult> addItemToFavorite(final String favoriteId, final String storecode, final String sku) {
        LOG.info("FavLineItemEntityRepositoryImpl.addItemToFavorite(), favoriteId :: "+favoriteId);
        return getFavorite(favoriteId)
                .flatMap(favoriteList -> storeRepository.findStoreAsSingle(storecode)
                        .flatMap(store -> addItemToFavorite(sku, favoriteList, store)));
    }

    /**
     * getFavorite.
     *
     * @param guid
     * @return
     */
    public Single<FavoriteList> getFavorite(final String guid) {
        LOG.info("FavLineItemEntityRepositoryImpl.getFavorite, guid :: "+guid);
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.findByGuid(guid), FAVORITES_WAS_NOT_FOUND);
    }

    /**
     * addItemToFavorite.
     *
     * @param sku
     * @param favoriteList
     * @param store
     * @return
     */
    public Single<AddToWishlistResult> addItemToFavorite(final String sku, final FavoriteList favoriteList, final Store store) {
        LOG.info("FavLineItemEntityRepositoryImpl.addItemToFavorite()");
        return reactiveAdapter.fromServiceAsSingle(() -> favoritesService.addProductSku(favoriteList, store, sku));
    }

    /**
     * buildSubmitResult.
     *
     * @param scope
     * @param favoritesId
     * @param addToFavoriteResult
     * @return
     */
    public Single<SubmitResult<FavoriteLineItemIdentifier>> buildSubmitResult(final String scope, final String favoritesId,
                                                                              final AddToWishlistResult addToFavoriteResult) {
        LOG.info("FavLineItemEntityRepositoryImpl.buildSubmitResult()");
        ShoppingItem shoppingItem = addToFavoriteResult.getShoppingItem();
        return Single.just(buildFavoriteLineItemIdentifier(scope, favoritesId, shoppingItem.getGuid()))
                .map(favoriteLineItemIdentifier -> SubmitResult.<FavoriteLineItemIdentifier>builder()
                        .withIdentifier(favoriteLineItemIdentifier)
                        .withStatus(addToFavoriteResult.isNewlyCreated() ? SubmitStatus.CREATED : SubmitStatus.EXISTING)
                        .build());
    }

    /**
     * getDefaultFavoriteId.
     *
     * @param scope
     * @return
     */
    public Single<String> getDefaultFavoriteId(final String scope) {
        LOG.info("FavLineItemEntityRepositoryImpl.getDefaultFavoriteId() :: "+getFavoriteIds(resourceOperationContext.getUserIdentifier(), scope)
                .firstElement()
                .toSingle());
        //Only one favorites supported!!
        return getFavoriteIds(resourceOperationContext.getUserIdentifier(), scope)
                .firstElement()
                .toSingle();
    }

    /**
     * getFavoriteIds.
     *
     * @param customerGuid
     * @param storeCode
     * @return
     */
    public Observable<String> getFavoriteIds(final String customerGuid, final String storeCode) {
        LOG.info("FavLineItemEntityRepositoryImpl.getFavoriteIds() :: "+customerGuid);
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
        LOG.info("Favorite List by shopper: {}", shopper.getGuid());
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

    @CacheResult(uniqueIdentifier = "cachingGetShoppingItem")
    public Single<ShoppingItem> getShoppingItem(final FavoriteList favoriteList, final String lineItemGuid) {
        LOG.info("getShoppingItem", favoriteList);
        return Observable.fromIterable(favoriteList.getAllItems())
                .filter(shoppingItem -> shoppingItem.getGuid().equals(lineItemGuid))
                .switchIfEmpty(Observable.error(ResourceOperationFailure.notFound(ITEM_NOT_FOUND)))
                .firstElement().toSingle();
    }
}
