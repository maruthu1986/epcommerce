package com.elasticpath.extensions.service.favorites.impl;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.core.messaging.customer.CustomerEventType;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.store.Store;
import com.elasticpath.extensions.dao.favorites.FavoritesDao;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.service.favorites.FavoriteListService;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.impl.AddToWishlistResult;
import com.elasticpath.service.store.StoreService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class FavoriteListServiceImpl implements FavoriteListService {
    private static final Logger LOG = Logger.getLogger(FavoriteListServiceImpl.class);
    /**
     * The key used to specify the message locale within the event message details when sharing a favorite List.
     */
    static final String LOCALE_KEY = "locale";

    /**
     * The key used to specify the store code within the event message details when sharing a favorite List.
     */
    static final String STORE_CODE_KEY = "storeCode";

    /**
     * The key used to specify the message favorite List UID within the event message details when sharing a favorite List.
     */
    static final String FAVORITE_UID_KEY = "favoriteUid";

    /**
     * The key used to specify the message within the event message details when sharing a favorite List.
     */
    static final String FAVORITE_MESSAGE_KEY = "favoriteMessage";

    /**
     * The key used to specify the recipients within the event message details when sharing a favorite List.
     */
    static final String FAVORITE_RECIPIENTS_KEY = "favoriteRecipients";

    /**
     * The key used to specify the sender name within the event message details when sharing a favorite List.
     */
    static final String FAVORITE_SENDER_NAME_KEY = "favoriteSender";

    private static final int QUANTITY_ONE = 1;

    private BeanFactory beanFactory;

    private FavoritesDao favoritesDao;

    private CartDirector cartDirector;

    private PriceLookupFacade priceLookupFacade;

    private StoreService storeService;

    private ProductSkuLookup productSkuLookup;

    private EventMessageFactory eventMessageFactory;

    private EventMessagePublisher eventMessagePublisher;

    private CartItemModifierService cartItemModifierService;

    @Override
    public FavoriteList createFavoriteList(final Shopper shopper) {
        LOG.info("FavoriteListServiceImpl.createFavoriteList()");
        final FavoriteList favoriteList = beanFactory.getBean("favoritesFlag");
        favoriteList.initialize();
        favoriteList.setShopper(shopper);
        favoriteList.setStoreCode(shopper.getStoreCode());
        LOG.info("createFavoriteList favoriteList :: " + favoriteList.getAllItems());
        return favoriteList;
    }

    @Override
    public FavoriteList get(final long uid) {
        return favoritesDao.get(uid);
    }

    @Override
    public FavoriteList findByGuid(final String guid) {
        LOG.info("FavoriteListServiceImpl.findByGuid() guid :: " + guid);
        return favoritesDao.findByGuid(guid);
    }

    @Override
    public AddToWishlistResult addProductSku(final FavoriteList favoriteList, final Store store, final String productSku) {
        LOG.info("ENTERED FavoriteListServiceImpl.addProductSku()");
        ShoppingItem item = cartDirector.createShoppingItem(productSku, store, QUANTITY_ONE);

        ProductType productType = productSkuLookup.findBySkuCode(productSku).getProduct().getProductType();
        cartItemModifierService
                .findCartItemModifierFieldsByProductType(productType)
                .forEach(cartItemModifierField -> item.setFieldValue(cartItemModifierField.getCode(), ""));

        AddToWishlistResult addTofavoritelistResult = addItem(favoriteList, item);
        save(favoriteList);
        LOG.info("EXIT FavoriteListServiceImpl.addProductSku()");
        return addTofavoritelistResult;
    }

    @Override
    public void addAllItems(final FavoriteList favoriteList, final List<ShoppingItem> items) {
        if (CollectionUtils.isNotEmpty(items)) {
            for (ShoppingItem shoppingItem : items) {
                addItem(favoriteList, shoppingItem);
            }
        }
    }

    @Override
    public AddToWishlistResult addItem(final FavoriteList favoriteList, final ShoppingItem item) {
        LOG.info("ENTERED FavoriteListServiceImpl.addItem()");
        ShoppingItem existingItem = getExistingItem(favoriteList, item);
        if (existingItem != null) {
            return new AddToWishlistResult(existingItem, false);
        }
        LOG.info("EXIT FavoriteListServiceImpl.addItem()");
        return new AddToWishlistResult(favoriteList.addItem(item), true);
    }

    /**
     * Returns an identical item on the favorites if one exists, null otherwise.
     *
     * @param favoriteList the favorites
     * @param shoppingItem the item
     * @return an identical item on the favorites if one exists, null otherwise
     */
    public ShoppingItem getExistingItem(final FavoriteList favoriteList, final ShoppingItem shoppingItem) {
        return favoriteList.getAllItems().stream()
                .filter(item -> cartDirector.itemsAreEqual(shoppingItem, item))
                .findFirst()
                .orElse(null);
    }

    @Override
    public FavoriteList save(final FavoriteList favoriteList) {
        if (favoriteList != null) {
            return favoritesDao.saveOrUpdate(favoriteList);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This remove is null safe and only removes persisted favorites.
     */
    @Override
    public void remove(final FavoriteList favoriteList) {
        if (favoriteList != null && favoriteList.isPersisted()) {
            favoritesDao.remove(favoriteList);
        }
    }

    @Override
    public FavoriteList findOrCreateFavoriteListByShopper(final Shopper shopper) {
        if (shopper == null) {
            return null;
        }
        FavoriteList favoriteList = getFavoritesDao().findByShopper(shopper);
        LOG.info("FavoriteList :: " + favoriteList);
        if (favoriteList == null) {
            favoriteList = createFavoriteList(shopper);
        }

        return favoriteList;
    }

    @Override
    public FavoriteList findOrCreateFavoriteListWithPrice(final CustomerSession customerSession) {
        final Shopper shopper = customerSession.getShopper();
        final FavoriteList favoriteList = findOrCreateFavoriteListByShopper(shopper);
        final Store store = storeService.findStoreWithCode(shopper.getStoreCode());
        for (ShoppingItem shoppingItem : favoriteList.getAllItems()) {
            final ProductSku sku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());
            final Price price = priceLookupFacade.getPromotedPriceForSku(sku, store, shopper);
            if (price != null) {
                PricingScheme pricingScheme = price.getPricingScheme();
                Set<Integer> minQuantities = pricingScheme.getPriceTiersMinQuantities();
                int quantity = minQuantities.iterator().next();
                shoppingItem.setPrice(quantity, price);
            }
        }

        return favoriteList;
    }

    @Override
    public int deleteEmptyFavoriteListsByShopperUids(final List<Long> shopperUids) {
        return favoritesDao.deleteEmptyFavoriteListsByShopperUids(shopperUids);
    }

    @Override
    public int deleteAllFavoriteListsByShopperUids(final List<Long> shopperUids) {
        return favoritesDao.deleteAllFavoriteListsByShopperUids(shopperUids);
    }

    @Override
    public void shareFavoriteList(final WishListMessage favoritesMessage, final FavoriteList favoriteList, final String storeCode, final Locale locale) {
        final Map<String, Object> favoriteListMessageData = Maps.newHashMap();
        favoriteListMessageData.put(LOCALE_KEY, locale.toString());
        favoriteListMessageData.put(STORE_CODE_KEY, storeCode);
        favoriteListMessageData.put(FAVORITE_MESSAGE_KEY, favoritesMessage.getMessage());
        favoriteListMessageData.put(FAVORITE_RECIPIENTS_KEY, favoritesMessage.getRecipientEmails());
        favoriteListMessageData.put(FAVORITE_SENDER_NAME_KEY, favoritesMessage.getSenderName());
        favoriteListMessageData.put(FAVORITE_UID_KEY, favoriteList.getUidPk());

        final EventMessage eventMessage = eventMessageFactory.createEventMessage(CustomerEventType.WISH_LIST_SHARED,
                favoriteList.getGuid(),
                favoriteListMessageData);

        eventMessagePublisher.publish(eventMessage);
    }

    // Setters/Getters for spring.
    // ---------------------------

    /**
     * Get the bean factory.
     *
     * @return the bean factory
     */
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * set the bean factory.
     *
     * @param beanFactory the bean factory
     */
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * @return favoritesDao.
     */
    public FavoritesDao getFavoritesDao() {
        return favoritesDao;
    }

    /**
     * @param favoritesDao FavoritesDao
     */
    public void setFavoritesDao(final FavoritesDao favoritesDao) {
        this.favoritesDao = favoritesDao;
    }

    /**
     * Set the price look up facade.
     *
     * @param priceLookupFacade the price look up facade instance
     */
    public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
        this.priceLookupFacade = priceLookupFacade;
    }

    /**
     * Gets the store service.
     *
     * @return the store service
     */
    protected StoreService getStoreService() {
        return storeService;
    }

    /**
     * Sets the store service.
     *
     * @param storeService the new store service
     */
    public void setStoreService(final StoreService storeService) {
        this.storeService = storeService;
    }

    protected EventMessageFactory getEventMessageFactory() {
        return eventMessageFactory;
    }

    public void setEventMessageFactory(final EventMessageFactory eventMessageFactory) {
        this.eventMessageFactory = eventMessageFactory;
    }

    protected EventMessagePublisher getEventMessagePublisher() {
        return eventMessagePublisher;
    }

    public void setEventMessagePublisher(final EventMessagePublisher eventMessagePublisher) {
        this.eventMessagePublisher = eventMessagePublisher;
    }

    protected ProductSkuLookup getProductSkuLookup() {
        return productSkuLookup;
    }

    public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
        this.productSkuLookup = productSkuLookup;
    }

    public void setCartDirector(final CartDirector cartDirector) {
        this.cartDirector = cartDirector;
    }

    public void setCartItemModifierService(final CartItemModifierService cartItemModifierService) {
        this.cartItemModifierService = cartItemModifierService;
    }
}