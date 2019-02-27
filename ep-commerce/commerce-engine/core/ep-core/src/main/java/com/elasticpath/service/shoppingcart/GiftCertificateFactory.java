/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.service.shoppingcart;

import java.util.Currency;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.store.Store;

/**
 * Creates a GiftCertificate.
 */
public interface GiftCertificateFactory {

	/**
	 * Creates a new {@code GiftCertificate} bean, sets its purchaser to the given {@code Customer} and generates a code for it
	 * that is guaranteed to be unique within the store with the given UID.
	 *
	 * @param shoppingItem the LineItem containing GC data
	 * @param pricingSnapshot the pricing snapshot corresponding to the shopping item
	 * @param customer the customer
	 * @param store the store
	 * @param currency the currency
	 * @return the new GiftCertificate
	 */
	GiftCertificate createGiftCertificate(ShoppingItem shoppingItem, ShoppingItemPricingSnapshot pricingSnapshot, Customer customer, Store store,
										Currency currency);

}
