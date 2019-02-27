/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
/**
 * 
 */
package com.elasticpath.test.integration.cart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.sellingchannel.director.CartDirectorService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Integration tests related to adding items to the shopping cart.
 */
public class AddToCartIntegrationTest extends AbstractCartIntegrationTestParent {
	
	@Autowired
	private CartDirectorService cartDirectorService;

	@Autowired
	private ShoppingCartService shoppingCartService;
	
	/**
	 * Test that when a {@code ShoppingCartItem} referring to a particular sku is added to the cart, 
	 * it will check whether the sku is already represented as an item in the cart and, if so, it will
	 * simply increase the quantity of that sku. This must happen even if we have not explicitly stated
	 * that the add request is for the purpose of updating the quantity. 
	 * This case represents what would happen if the user added something to their cart and then instead of
	 * "editing" the shopping cart item they simply added the item again.
	 */
	@DirtiesDatabase
	@Test
	public void testAddSkuIncreasesQuantityRegardlessOfUpdateCommand() {
		Product product = persistProductWithSku();
		ShoppingCart shoppingCart = createShoppingCart(createCustomerSession());
		String skuCode = product.getDefaultSku().getSkuCode();
		ShoppingItemDto dto = new ShoppingItemDto(skuCode, 1);
		cartDirectorService.addItemToCart(shoppingCart, dto);
		ShoppingItem item = cartDirectorService.addItemToCart(shoppingCart, dto);
		assertEquals(2, item.getQuantity());		
	}
	
	/**
	 * Test persisting a ShoppingCartItem with data along with the Cart, then 
	 * retrieving the cart and its item again with the data intact.
	 */
	@DirtiesDatabase
	@Test
	public void testPersistCartItemWithData() {
		final String key = "TESTKEY";
		final String value = "TESTVALUE";

		Product product = persistProductWithSku();
		CustomerSession customerSession = createCustomerSession();
		ShoppingCart shoppingCart = createShoppingCart(customerSession);
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);

		final ShoppingItem addedCartItem = cartDirectorService.addItemToCart(shoppingCart, dto);
		addedCartItem.setFieldValue(key, value);
		assertEquals(value, shoppingCart.getRootShoppingItems().iterator().next().getFieldValue(key));
		shoppingCartService.saveOrUpdate(shoppingCart);

		ShoppingCart retrievedShoppingCart = shoppingCartService.findOrCreateByShopper(customerSession.getShopper());
		assertTrue(retrievedShoppingCart.getRootShoppingItems().size() == 1);
		assertEquals(value, retrievedShoppingCart.getRootShoppingItems().iterator().next().getFieldValue(key));
	}
}
