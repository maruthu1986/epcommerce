/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.shoppingcart;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.collections.Predicate;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Tests that the Shopping Item Predicate Utils class works as expected.
 */
public class ShoppingItemPredicateUtilsTest {

	@Rule public final JUnitRuleMockery context = new JUnitRuleMockery();
	@Mock private ProductSkuLookup productSkuLookup;

	/**
	 * Test configurable items not equal.
	 */
	@Test
	public void testConfigurableItemsNotEqual() {
		ShoppingItem item1 = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isConfigurable(final ProductSkuLookup lookup) {
				return true;
			}
		};

		context.checking(new Expectations() { {
			allowing(productSkuLookup).findByGuid(null);
		} });

		Predicate matchingShoppingItemPredicate = ShoppingItemPredicateUtils.matchingShoppingItemPredicate(item1, productSkuLookup);
		assertFalse("Configurable items should never be equal", matchingShoppingItemPredicate.evaluate(item1));
	}

	/**
	 * Test random object not equal.
	 */
	@Test
	public void testRandomObjectNotEqual() {
		ShoppingItem item = createShoppingItem("SKU");
		ProductSku sku = new ProductSkuImpl();
		Predicate matchingShoppingItemPredicate = ShoppingItemPredicateUtils.matchingShoppingItemPredicate(item, productSkuLookup);
		assertFalse("Another type of object should not match a shopping item", matchingShoppingItemPredicate.evaluate(sku));
	}
	
	/**
	 * Test same object is equal.
	 */
	@Test
	public void testSameObjectIsEqual() {
		ShoppingItem item = createShoppingItem("SKU");

		context.checking(new Expectations() { {
			allowing(productSkuLookup).findByGuid("SKU");
		} });

		Predicate matchingShoppingItemPredicate = ShoppingItemPredicateUtils.matchingShoppingItemPredicate(item, productSkuLookup);
		assertTrue("A shopping item should match itself", matchingShoppingItemPredicate.evaluate(item));
	}
	
	/**
	 * Test different shopping items not equal.
	 */
	@Test
	public void testDifferentShoppingItemsNotEqual() {
		ShoppingItem item1 = createShoppingItem("SKU-1");
		ShoppingItem item2 = createShoppingItem("SKU-2");

		context.checking(new Expectations() { {
			allowing(productSkuLookup).findByGuid("SKU-1");
			allowing(productSkuLookup).findByGuid("SKU-2");
		} });

		Predicate matchingShoppingItemPredicate = ShoppingItemPredicateUtils.matchingShoppingItemPredicate(item1, productSkuLookup);
		assertFalse("Shopping items with different skus should not match", matchingShoppingItemPredicate.evaluate(item2));
	}

	private ShoppingItem createShoppingItem(final String skuCode) {
		ProductSku productSku = new ProductSkuImpl();
		productSku.setGuid(skuCode);
		productSku.setSkuCode(skuCode);
		ShoppingItem item = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isConfigurable(final ProductSkuLookup skuLookup) {
				return false;
			}

			@Override
			public boolean isMultiSku(final ProductSkuLookup skuLookup) {
				return false;
			}
		};
		item.setSkuGuid(productSku.getGuid());
		return item;
	}
	
}
