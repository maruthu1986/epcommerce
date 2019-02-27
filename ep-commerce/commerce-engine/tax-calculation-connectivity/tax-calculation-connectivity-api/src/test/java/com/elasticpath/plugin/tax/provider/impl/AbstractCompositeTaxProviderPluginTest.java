/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.provider.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxableItemContainer;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItem;
import com.elasticpath.plugin.tax.domain.impl.TaxableItemImpl;

/**
 * Test cases for {@link AbstractCompositeTaxProviderPluginTest}.
 */
public class AbstractCompositeTaxProviderPluginTest {

	private static final int INT_3 = 3;

	private static final double DOUBLE_1000 = 1000.00;
	
	private final MutableTaxableItemContainer container = new MutableTaxableItemContainer();
	private final TaxableItemImpl taxableItem1 = new TaxableItemImpl();
	private final TaxableItemImpl taxableItem2 = new TaxableItemImpl();
	private final TaxableItemImpl taxableItem3 = new TaxableItemImpl();
	private final MutableTaxedItem taxedItem1 = new MutableTaxedItem();
	private final MutableTaxedItem taxedItem2 = new MutableTaxedItem();
	private final MutableTaxedItem taxedItem3 = new MutableTaxedItem();
	
	private final CompositeTaxProviderPlugin compositeTaxProvider = new CompositeTaxProviderPlugin();
	
	/**
	 * Tests with same taxable Item but with same id. The id is shopping itme guid, which keeps changing all the time. 
	 * The guid should not affect the tax result for the container. 
	 * 
	 */
	@Test
	public void testKeepTaxItemOrdering() {
		
		taxableItem1.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem1.setTaxCode("taxCode1");
		taxableItem1.setItemCode("LineItem-1");
		taxableItem1.setItemGuid("id1");
		
		taxableItem2.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem2.setTaxCode("taxCode1");
		taxableItem2.setItemCode("LineItem-2");
		taxableItem2.setItemGuid("id2");
		
		taxableItem3.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem3.setTaxCode("taxCode1");
		taxableItem3.setItemCode("LineItem-3");
		taxableItem3.setItemGuid("id3");
		
		container.setItems(Arrays.<TaxableItem> asList(taxableItem1, taxableItem2, taxableItem3));
		
		Map<String, Integer> taxableItemOrders = compositeTaxProvider.getTaxItemOrdering(container);
		
		assertEquals(INT_3, taxableItemOrders.size());
		
		taxedItem1.setTaxableItem(taxableItem1);
		taxedItem2.setTaxableItem(taxableItem2);
		taxedItem3.setTaxableItem(taxableItem3);
		
		List<TaxedItem> rawTaxedItems = Arrays.<TaxedItem> asList(taxedItem2, taxedItem3, taxedItem1);
		
		List<TaxedItem> orderedTaxedItems = compositeTaxProvider.orderTaxedItem(taxableItemOrders, rawTaxedItems);
		
		assertEquals(INT_3, orderedTaxedItems.size());
		
		assertEquals("id1", orderedTaxedItems.get(0).getItemGuid());
		assertEquals("id2", orderedTaxedItems.get(1).getItemGuid());
		assertEquals("id3", orderedTaxedItems.get(2).getItemGuid());
	}
}
