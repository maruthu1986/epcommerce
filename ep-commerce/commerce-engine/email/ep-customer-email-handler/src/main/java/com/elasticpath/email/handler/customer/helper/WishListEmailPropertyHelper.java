/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.email.handler.customer.helper;

import java.util.Locale;

import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.store.Store;
import com.elasticpath.email.domain.EmailProperties;

/**
 * Helper for constructing email properties.
 */
public interface WishListEmailPropertyHelper {

	/**
	 * Sends the customers favorites via e-mail to a list of recipients.
	 * 
	 * @param wishListMessage the wishListMessage to send, includes the list of recipients
	 * @param wishList the wish list
	 * @param store the store
	 * @param locale the locale
	 * @return the email properties
	 */
	EmailProperties getWishListEmailProperties(WishListMessage wishListMessage, WishList wishList,
			Store store, Locale locale);

}
