/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
/**
 * 
 */
package com.elasticpath.test.integration.cart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.sellingchannel.ShoppingItemFactory;
import com.elasticpath.service.shoppingcart.ShoppingItemService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests for persisting ShoppingCartItems with data, outside the Cart.
 */
public class ShoppingCartItemPersistenceIntegrationTest extends AbstractCartIntegrationTestParent {
	
	private ShoppingItemFactory cartItemFactory;
	private Price testPrice;
	
	/**
	 * set up.
	 *
	 * @throws Exception an exception
	 */
	@Before
	public void setUp() throws Exception {
		cartItemFactory = getBeanFactory().getBean("shoppingItemFactory");
		testPrice = createTestPrice();
	}
	
	/**
	 * Test persisting a ShoppingCartItem with data and retrieving it again with the data intact.
	 */
	@DirtiesDatabase
	@Test
	public void testPersistCartItemWithData() {
		Product product = persistProductWithSku();

		ShoppingItem shoppingCartItem = cartItemFactory.createShoppingItem(
				product.getDefaultSku(), testPrice, 1, 0,
				null);
				
		String key = "TESTKEY";
		String value = "TESTVALUE";

		shoppingCartItem.setFieldValue(key, value);
		
		ShoppingItem persistedCartItem = getShoppingCartItemService().saveOrUpdate(shoppingCartItem);
		assertEquals(value, persistedCartItem.getFieldValue(key));
		
		ShoppingItem retrievedCartItem = getShoppingCartItemService().findByGuid(persistedCartItem.getGuid(), null);
		assertEquals(value, retrievedCartItem.getFieldValue(key));
	}
	
	/**
	 * Test persisting a ShoppingCartItem with Price data and retrieving it again with the data intact.
	 */
	@DirtiesDatabase
	@Test
	public void testPersistCartItemWithPrice() {
		Product product = persistProductWithSku();
				
		ShoppingItem shoppingCartItem = cartItemFactory.createShoppingItem(
				product.getDefaultSku(), testPrice, 1, 0,
				null);

		// assertEquals(Money, Money) is not a valid test case, compare Money components or use Money.compareTo()
		ShoppingItem persistedCartItem = getShoppingCartItemService().saveOrUpdate(shoppingCartItem);

		// If and when this cast triggers an error, it means that the pricing fields have moved from the ShoppingItem concrete class, which infers
		// that these following assertions are no longer appropriate and can be moved.
		final ShoppingItemPricingSnapshot persistedItemPricingSnapshot = (ShoppingItemPricingSnapshot) persistedCartItem;

		assertEquals(testPrice.getListPrice(1).getAmount(), persistedItemPricingSnapshot.getListUnitPrice().getAmount());
		assertEquals(testPrice.getListPrice(1).getCurrency(), persistedItemPricingSnapshot.getListUnitPrice().getCurrency());
		assertEquals(testPrice.getSalePrice(1).getAmount(), persistedItemPricingSnapshot.getSaleUnitPrice().getAmount());
		assertEquals(testPrice.getComputedPrice(1).getAmount(), persistedItemPricingSnapshot.getPromotedUnitPrice().getAmount());

		ShoppingItem retrievedCartItem = getShoppingCartItemService().findByGuid(persistedCartItem.getGuid(), null);
		final ShoppingItemPricingSnapshot retrievedItemPricingSnapshot = (ShoppingItemPricingSnapshot) persistedCartItem;

		assertTrue(testPrice.getListPrice(1).compareTo(retrievedItemPricingSnapshot.getListUnitPrice()) == 0);
		assertTrue(testPrice.getSalePrice(1).compareTo(retrievedItemPricingSnapshot.getSaleUnitPrice()) == 0);
		assertTrue(testPrice.getComputedPrice(1).compareTo(retrievedItemPricingSnapshot.getPromotedUnitPrice()) == 0);
	}

	private Price createTestPrice() {
		int salePriceInteger = 5;
		BigDecimal listPrice = BigDecimal.TEN;
		BigDecimal salePrice = new BigDecimal(salePriceInteger);
		BigDecimal computedPrice = BigDecimal.ONE;
		
		return createPrice(listPrice, salePrice, computedPrice);
	}
	
	private Price createPrice(final BigDecimal listPrice, final BigDecimal salePrice, final BigDecimal promotedPrice) {
		final Currency currency = Currency.getInstance("USD");
		final Money listMoney = Money.valueOf(listPrice, currency);
		final Money saleMoney = Money.valueOf(salePrice, currency);
		final Money promotedMoney = Money.valueOf(promotedPrice, currency);

		PriceImpl price = new PriceImpl();
		price.setListPrice(listMoney);
		price.setSalePrice(saleMoney);
		price.setComputedPriceIfLower(promotedMoney);
		price.setCurrency(currency);
		return price;
	}

	/**
	 * @return the shoppingCartItemService
	 */
	public ShoppingItemService getShoppingCartItemService() {
		return getBeanFactory().getBean("shoppingItemService");
	}
}
