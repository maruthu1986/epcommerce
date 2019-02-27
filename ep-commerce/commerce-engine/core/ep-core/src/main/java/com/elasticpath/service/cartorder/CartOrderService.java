/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.service.cartorder;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * This Class can perform services for CartOrders, CartOrder should not be used in versions of EP prior to 6.4.
 */
public interface CartOrderService {

	/**
	 * If a CartOrder with the given GUID cannot be found then null is returned.
	 * If the referenced ShoppingCart cannot be found then null is returned.
	 *
	 * If the CartOrder contains illegal references (for instance, unsupported shipping information) then
	 * the cart order is illegal references are <em>sanitized and persisted</em> before being returned
	 * to the client.
	 *
	 * @param storeCode the store code
	 * @param guid The GUID of the desired CartOrder.
	 * @return The CartOrder.
	 */
	CartOrder findByStoreCodeAndGuid(String storeCode, String guid);

	/**
	 * Finds a {@link CartOrder} given the shopping cart guid.
	 *
	 * @param guid the guid
	 * @return the cart order
	 */
	CartOrder findByShoppingCartGuid(String guid);

	/**
	 * Get the billing address for the given CartOrder.
	 *
	 * @param cartOrder The CartOrder.
	 * @return The found Address or <code>null</code> if it is not set.
	 */
	Address getBillingAddress(CartOrder cartOrder);

	/**
	 * Get the shipping address for the given CartOrder.
	 *
	 * @param cartOrder The CartOrder.
	 * @return The found Address or <code>null</code> if it is not set.
	 */
	Address getShippingAddress(CartOrder cartOrder);

	/**
	 * Deleting a CartOrder will not delete its Addresses or ShoppingCart.
	 * Note: deleting an Address will not delete any CartOrders referring to it.
	 * Note: deleting a ShoppingCart will delete the CartOrder referring to it.
	 *
	 * @param cartOrder The CartOrder to remove.
	 */
	void remove(CartOrder cartOrder);

	/**
	 * Saves or updates the given CartOrder.
	 *
	 * @param cartOrder The cartOrder to save or update.
	 * @return The updated CartOrder.
	 */
	CartOrder saveOrUpdate(CartOrder cartOrder);

	/**
	 * Creates a {@link CartOrder} for the given cart, if it does not exists already.
	 * In case of multiple threads, the above statement is valid only for the first thread able to create a cart order due to
	 * unique index UNQ_CARTORDER_SC_GUID set in TCARTORDER table which prevents duplicate creations, thus
	 * db exception is thrown as a response in such attempts.
	 *
	 * The exception is handled in {@com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl.CartPostProcessor}
	 * and logged without further propagation.
	 *
	 * @param shoppingCart the the shopping cart
	 * @return <code>true</code>, iff a new {@link CartOrder} is created
	 */
	boolean createOrderIfPossible(ShoppingCart shoppingCart);


	/**
	 * Removes the cart order related to the given shopping cart. It does not fail
	 * if no such cart order exists.
	 *
	 * @param shoppingCart the shopping cart
	 */
	void removeIfExistsByShoppingCart(ShoppingCart shoppingCart);

	/**
	 * Remove the cart orders corresponding to the associated shopping cart GUIDs.
	 *
	 * @param shoppingCartGuids the shopping cart GUIDs
	 * @return the number of cart orders deleted
	 */
	int removeIfExistsByShoppingCartGuids(List<String> shoppingCartGuids);

	/**
	 * Find the GUIDs of all the cart orders owned by a customer in a certain store, given customer's GUID and store code.
	 *
	 * @param storeCode the store code
	 * @param customerGuid the customer GUID
	 * @return the list of cart order GUIDs
	 */
	List<String> findCartOrderGuidsByCustomerGuid(String storeCode, String customerGuid);

	/**
	 * Gets the last modified date for a CartOrder given its GUID.
	 *
	 * @param cartOrderGuid the cart order GUID
	 * @return the last modified date
	 */
	Date getCartOrderLastModifiedDate(String cartOrderGuid);

	/**
	 * Gets cart order GUID for given shopping cart GUID.
	 *
	 * @param shoppingCartGuid the shopping cart GUID
	 * @return cart order GUID or null
	 */
	String getCartOrderGuidByShoppingCartGuid(String shoppingCartGuid);

	/**
	 * Retrieves cart order coupon codes for given shopping cart GUID.
	 *
	 * @param shoppingCartGuid the shopping cart GUID
	 * @return a list with cart order coupon codes or empty list
	 */
	Collection<String> getCartOrderCouponCodesByShoppingCartGuid(String shoppingCartGuid);

	/**
	 * Gets shopping cart GUID for given store code and cart order GUID.
	 *
	 * @param storeCode the store code
	 * @param cartOrderGuid the cart order GUID
	 * @return shopping cart GUID or null
	 */
	String getShoppingCartGuid(String storeCode, String cartOrderGuid);
}
