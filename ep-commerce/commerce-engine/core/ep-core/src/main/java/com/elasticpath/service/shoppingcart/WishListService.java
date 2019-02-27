/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart;

import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shoppingcart.impl.AddToWishlistResult;

/**
 * Provide wish list related business service.
 */
public interface WishListService {

	/**
	 * Get the wish list by uidPk.
	 *
	 * @param uid the uidPk
	 * @return the wish list found
	 */
	WishList get(long uid);


	/**
	 * Add all items in the wish list.
	 *
	 * @param wishList the favorites
	 * @param items    the collection of items to add
	 */
	void addAllItems(WishList wishList, List<ShoppingItem> items);

	/**
	 * <p>Add item into wish list. If an identical item exists on the favorites then the existing item is returned.</p>
	 * <p>
	 * <p>The boolean value is <code>true</code> if a new item is created, <code>false</code> otherwise.</p>
	 *
	 * @param wishList the favorites
	 * @param item     the item to be added
	 * @return the shopping item added, or the existing identical item
	 */
	AddToWishlistResult addItem(WishList wishList, ShoppingItem item);

	/**
	 * Save the wish list.
	 *
	 * @param wishList the wish list to be saved
	 * @return the saved wish list
	 */
	WishList save(WishList wishList);

	/**
	 * Create the wish list by the {@link Shopper}.
	 *
	 * @param shopper the Shopper
	 * @return the wish list created
	 */
	WishList createWishList(Shopper shopper);

	/**
	 * Find the wish list by shopper.
	 *
	 * @param shopper the shopper
	 * @return the wish list found
	 */
	WishList findOrCreateWishListByShopper(Shopper shopper);

	/**
	 * Find the wish list with prices.
	 *
	 * @param customerSession the customer session
	 * @return the wish list found
	 */
	WishList findOrCreateWishListWithPrice(CustomerSession customerSession);

	/**
	 * Find the favorites from its guid.
	 *
	 * @param guid the guid
	 * @return the favorites
	 */
	WishList findByGuid(String guid);

	/**
	 * Add a product to the favorites.
	 *
	 * @param wishList   the favorites
	 * @param store      the store
	 * @param productSku sku
	 * @return the result of add operation
	 */
	AddToWishlistResult addProductSku(WishList wishList, Store store, String productSku);

	/**
	 * Remove one wish list.
	 *
	 * @param wishList the wish list to be removed
	 */
	void remove(WishList wishList);

	/**
	 * Delete all Empty WishLists that are associated with the list of Shoppers.
	 *
	 * @param shopperUids the uids of the associated Shoppers.
	 * @return the number of deleted WishLists
	 */
	int deleteEmptyWishListsByShopperUids(List<Long> shopperUids);

	/**
	 * Delete all WishLists that are associated with the list of Shoppers.
	 * Even the ones that are not empty.
	 *
	 * @param shopperUids the uids of the associated Shoppers.
	 * @return the number of deleted WishLists
	 */
	int deleteAllWishListsByShopperUids(List<Long> shopperUids);

	/**
	 * Shares a Wish List with one or many recipients.
	 *
	 * @param wishListMessage the wishListMessage to send, includes the list of recipients
	 * @param wishList        the wish list
	 * @param storeCode       the code of the Store with which the Wish List is associated
	 * @param locale          the locale
	 */
	void shareWishList(WishListMessage wishListMessage, WishList wishList, String storeCode, Locale locale);

}
