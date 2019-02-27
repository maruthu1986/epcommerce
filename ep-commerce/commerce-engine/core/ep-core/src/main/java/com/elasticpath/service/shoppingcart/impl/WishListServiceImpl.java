/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.core.messaging.customer.CustomerEventType;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.store.Store;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.service.shoppingcart.dao.WishListDao;
import com.elasticpath.service.store.StoreService;

/**
 * Service for customer favorites persistence.
 */
public class WishListServiceImpl implements WishListService {

	/**
	 * The key used to specify the message locale within the event message details when sharing a Wish List.
	 */
	static final String LOCALE_KEY = "locale";

	/**
	 * The key used to specify the store code within the event message details when sharing a Wish List.
	 */
	static final String STORE_CODE_KEY = "storeCode";

	/**
	 * The key used to specify the message Wish List UID within the event message details when sharing a Wish List.
	 */
	static final String WISH_LIST_UID_KEY = "wishListUid";

	/**
	 * The key used to specify the message within the event message details when sharing a Wish List.
	 */
	static final String WISH_LIST_MESSAGE_KEY = "wishListMessage";

	/**
	 * The key used to specify the recipients within the event message details when sharing a Wish List.
	 */
	static final String WISH_LIST_RECIPIENTS_KEY = "wishListRecipients";

	/**
	 * The key used to specify the sender name within the event message details when sharing a Wish List.
	 */
	static final String WISH_LIST_SENDER_NAME_KEY = "wishListSender";

	private static final int QUANTITY_ONE = 1;

	private BeanFactory beanFactory;

	private WishListDao wishListDao;

	private CartDirector cartDirector;

	private PriceLookupFacade priceLookupFacade;

	private StoreService storeService;
	private ProductSkuLookup productSkuLookup;

	private EventMessageFactory eventMessageFactory;

	private EventMessagePublisher eventMessagePublisher;

	private CartItemModifierService cartItemModifierService;

	@Override
	public WishList createWishList(final Shopper shopper) {
		final WishList wishList = beanFactory.getBean(ContextIdNames.WISH_LIST);
		wishList.initialize();
		wishList.setShopper(shopper);
		wishList.setStoreCode(shopper.getStoreCode());
		return wishList;
	}

	@Override
	public WishList get(final long uid) {
		return wishListDao.get(uid);
	}

	@Override
	public WishList findByGuid(final String guid) {
		return wishListDao.findByGuid(guid);
	}

	@Override
	public AddToWishlistResult addProductSku(final WishList wishList, final Store store, final String productSku) {
		ShoppingItem item = cartDirector.createShoppingItem(productSku, store, QUANTITY_ONE);

		ProductType productType = productSkuLookup.findBySkuCode(productSku).getProduct().getProductType();
		cartItemModifierService
				.findCartItemModifierFieldsByProductType(productType)
				.forEach(cartItemModifierField -> item.setFieldValue(cartItemModifierField.getCode(), ""));

		AddToWishlistResult addToWishlistResult = addItem(wishList, item);
		save(wishList);
		return addToWishlistResult;
	}

	@Override
	public void addAllItems(final WishList wishList, final List<ShoppingItem> items) {
		if (CollectionUtils.isNotEmpty(items)) {
			for (ShoppingItem shoppingItem : items) {
				addItem(wishList, shoppingItem);
			}
		}
	}

	@Override
	public AddToWishlistResult addItem(final WishList wishList, final ShoppingItem item) {
		ShoppingItem existingItem = getExistingItem(wishList, item);
		if (existingItem != null) {
			return new AddToWishlistResult(existingItem, false);
		}
		return new AddToWishlistResult(wishList.addItem(item), true);
	}

	/**
	 * Returns an identical item on the favorites if one exists, null otherwise.
	 *
	 * @param wishList     the favorites
	 * @param shoppingItem the item
	 * @return an identical item on the favorites if one exists, null otherwise
	 */
	protected ShoppingItem getExistingItem(final WishList wishList, final ShoppingItem shoppingItem) {
		return wishList.getAllItems().stream()
				.filter(item -> cartDirector.itemsAreEqual(shoppingItem, item))
				.findFirst()
				.orElse(null);
	}

	@Override
	public WishList save(final WishList wishList) {
		if (wishList != null) {
			return wishListDao.saveOrUpdate(wishList);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This remove is null safe and only removes persisted wishlists.
	 */
	@Override
	public void remove(final WishList wishList) {
		if (wishList != null && wishList.isPersisted()) {
			wishListDao.remove(wishList);
		}
	}

	@Override
	public WishList findOrCreateWishListByShopper(final Shopper shopper) {
		if (shopper == null) {
			return null;
		}

		WishList wishList = getWishListDao().findByShopper(shopper);

		if (wishList == null) {
			wishList = createWishList(shopper);
		}

		return wishList;
	}

	@Override
	public WishList findOrCreateWishListWithPrice(final CustomerSession customerSession) {
		final Shopper shopper = customerSession.getShopper();
		final WishList wishList = findOrCreateWishListByShopper(shopper);
		final Store store = storeService.findStoreWithCode(shopper.getStoreCode());
		for (ShoppingItem shoppingItem : wishList.getAllItems()) {
			final ProductSku sku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());
			final Price price = priceLookupFacade.getPromotedPriceForSku(sku, store, shopper);
			if (price != null) {
				PricingScheme pricingScheme = price.getPricingScheme();
				Set<Integer> minQuantities = pricingScheme.getPriceTiersMinQuantities();
				int quantity = minQuantities.iterator().next();
				shoppingItem.setPrice(quantity, price);
			}
		}

		return wishList;
	}

	@Override
	public int deleteEmptyWishListsByShopperUids(final List<Long> shopperUids) {
		return wishListDao.deleteEmptyWishListsByShopperUids(shopperUids);
	}

	@Override
	public int deleteAllWishListsByShopperUids(final List<Long> shopperUids) {
		return wishListDao.deleteAllWishListsByShopperUids(shopperUids);
	}

	@Override
	public void shareWishList(final WishListMessage wishListMessage, final WishList wishList, final String storeCode, final Locale locale) {
		final Map<String, Object> wishListMessageData = Maps.newHashMap();
		wishListMessageData.put(LOCALE_KEY, locale.toString());
		wishListMessageData.put(STORE_CODE_KEY, storeCode);
		wishListMessageData.put(WISH_LIST_MESSAGE_KEY, wishListMessage.getMessage());
		wishListMessageData.put(WISH_LIST_RECIPIENTS_KEY, wishListMessage.getRecipientEmails());
		wishListMessageData.put(WISH_LIST_SENDER_NAME_KEY, wishListMessage.getSenderName());
		wishListMessageData.put(WISH_LIST_UID_KEY, wishList.getUidPk());

		final EventMessage eventMessage = eventMessageFactory.createEventMessage(CustomerEventType.WISH_LIST_SHARED,
				wishList.getGuid(),
				wishListMessageData);

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
	 * Get the wish list dao.
	 *
	 * @return the wish list dao
	 */
	public WishListDao getWishListDao() {
		return wishListDao;
	}

	/**
	 * Set the wish list dao.
	 *
	 * @param wishListDao the wish list dao
	 */
	public void setWishListDao(final WishListDao wishListDao) {
		this.wishListDao = wishListDao;
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
