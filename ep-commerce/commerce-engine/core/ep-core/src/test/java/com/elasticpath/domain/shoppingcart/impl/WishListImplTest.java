/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.domain.shoppingcart.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * The junit class for wishListImpl.
 */
public class WishListImplTest {
	
	private static final String SKU1 = "sku1";
	private WishListImpl wishList;
	
	/**
	 * The setup method.
	 */
	@Before
	public void setUp() {
		wishList  = new WishListImpl() {
			private static final long serialVersionUID = -4858143846698274756L;

			@Override
			public ShoppingItem addItem(final ShoppingItem item) {
				ShoppingItem shoppingItem = new ShoppingItemImpl();
				shoppingItem.setSkuGuid(item.getSkuGuid());
				getAllItems().add(shoppingItem);
				return item;
			}
		};
	}

	@Test
	public void testRemoveItemBySkuGuid() {
		ShoppingItem item = new ShoppingItemImpl();
		ProductSku sku1 = new ProductSkuImpl();
		sku1.initialize();
		sku1.setSkuCode(SKU1);
		item.setSkuGuid(sku1.getGuid());

		wishList.addItem(item);

		assertThat(wishList.getAllItems().isEmpty())
				.as("Sanity Check")
				.isFalse();
		wishList.removeItemBySkuGuid(sku1.getGuid());
		assertThat(wishList.getAllItems().isEmpty())
				.as("Item should have been removed")
				.isTrue();
	}

	@Test
	public void testRemoveMultipleItemsBySkuGuid() {
		ShoppingItem item = new ShoppingItemImpl();
		ProductSku sku1 = new ProductSkuImpl();
		sku1.initialize();
		sku1.setSkuCode(SKU1);
		item.setSkuGuid(sku1.getGuid());

		wishList.addItem(item);
		wishList.addItem(item);

		assertThat(wishList.getAllItems().size())
				.as("Sanity Check")
				.isEqualTo(2);
		wishList.removeItemBySkuGuid(sku1.getGuid());
		assertThat(wishList.getAllItems().isEmpty())
				.as("Item should have been removed")
				.isTrue();
	}
}
