/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.tax.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.elasticpath.service.tax.impl.ApportioningCalculatorImpl.DiscountableItemSorter;


/**
 * Tests PriceSkuSortingStrategy.
 */
public class DiscountableItemSorterTest {

	private static final String ITEM_2 = "item2";
	private static final String ITEM_1 = "item1";

	/** */
	@Test
	public void testSortUniqueIdsWithEqualSkuCode() {
		DiscountableItem item1 = new DiscountableItem(ITEM_1,
				"skuCode", BigDecimal.TEN);
		DiscountableItem item2 = new DiscountableItem(ITEM_2,
				"skuCode", BigDecimal.ONE);
		DiscountableItemSorter strategy = new DiscountableItemSorter();
		List<DiscountableItem> sortedItems = strategy.sortByPriceSku(Arrays.asList(item1,
				item2));

		assertEquals(ITEM_1, sortedItems.get(0).getGuid());
		assertEquals(ITEM_2, sortedItems.get(1).getGuid());
	}
	
	/** */
	@Test
	public void testSortUniqueIdsWithEqualAmount() {
		DiscountableItem item1 = new DiscountableItem(ITEM_1,
				"skuCode2", BigDecimal.TEN);
		DiscountableItem item2 = new DiscountableItem(ITEM_2,
				"skuCode1", BigDecimal.TEN);
		DiscountableItemSorter strategy = new DiscountableItemSorter();
		List<DiscountableItem> sortedItems = strategy.sortByPriceSku(Arrays.asList(item1,
				item2));

		assertEquals(ITEM_1, sortedItems.get(0).getGuid());
		assertEquals(ITEM_2, sortedItems.get(1).getGuid());
	}

}
