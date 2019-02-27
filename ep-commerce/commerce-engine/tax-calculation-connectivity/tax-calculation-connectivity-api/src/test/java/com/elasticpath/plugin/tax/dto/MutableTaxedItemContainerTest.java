/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;

import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxableItemContainer;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItem;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItemContainer;
import com.elasticpath.plugin.tax.domain.impl.TaxableItemImpl;

/**
 * Test cases for {@link MutableTaxedItemContainer} calculation.
 */
public class MutableTaxedItemContainerTest {
	private static final double DOUBLE_1000 = 1000.00;
	private static final String ADDRESS_COUNTRY = "USA";
	private static final String ADDRESS_ZIPCODE_WA = "98110";
	private static final String ADDRESS_STATE_WA = "WA";
	private static final String ADDRESS_CITY = "Bainbridge Island";
	private static final String ADDRESS_STREET_NO = "Suite 220";
	private static final String ADDRESS_STREET = "100 Ravine Lane NE";
	private static final String TAX_CODE_1 = "taxCode1";
	private static final String LINE_ITEM_1 = "LineItem-1";
	private static final String ID_1 = "id1";
	private static final String LINE_ITEM_2 = "LineItem-2";
	private static final String ID_2 = "id2";

	private final MutableTaxableItemContainer container1 = new MutableTaxableItemContainer();

	private final MutableTaxedItemContainer container2 = new MutableTaxedItemContainer();

	private final TaxableItemImpl taxableItem1 = new TaxableItemImpl();
	private final TaxableItemImpl taxableItem2 = new TaxableItemImpl();

	/**
	 * Tests the order of taxable items of taxable container is kept after building the taxed container from it.
	 */
	@Test
	public void testHashCodeWithDiffTaxableItemsDiffIndex() {

		taxableItem1.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem1.setTaxCode(TAX_CODE_1);
		taxableItem1.setItemCode(LINE_ITEM_1);
		taxableItem1.setItemGuid(ID_1);

		taxableItem2.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem2.setTaxCode(TAX_CODE_1);
		taxableItem2.setItemCode(LINE_ITEM_2);
		taxableItem2.setItemGuid(ID_2);

		container1.setItems(Arrays.<TaxableItem> asList(taxableItem1, taxableItem2));

		assertNotSame(taxableItem1.hashCode(), taxableItem2.hashCode());

		for (TaxableItem taxableItem : container1.getItems()) {
			MutableTaxedItem taxedItem = new MutableTaxedItem();
			taxedItem.setTaxableItem(taxableItem);

			container2.addTaxedItem(taxedItem);
		}

		for (int index = 0; index < container2.getItems().size(); index++) {
			TaxableItem item1 =  container1.getItems().get(index);
			TaxableItem item2 =  container2.getItems().get(index).getTaxableItem();

			assertEquals(item1.getItemGuid(), item2.getItemGuid());
			assertEquals(item1.hashCode(), item2.hashCode());
		}
	}

	/**
	 * Tests that a MutableTaxedItemContainer is serializable. Since it is stored in a cache, which may
	 * serialize it, this test ensures that it can be successfully deserialized.
	 */
	@Test
	public void testTaxedItemContainerIsSerializable() {

		taxableItem1.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem1.setTaxCode(TAX_CODE_1);
		taxableItem1.setTaxCodeActive(true);
		taxableItem1.setItemCode(LINE_ITEM_1);
		taxableItem1.setItemGuid(ID_1);
		taxableItem1.setCurrency(Currency.getInstance(Locale.US));

		taxableItem2.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem2.setTaxCode(TAX_CODE_1);
		taxableItem2.setTaxCodeActive(true);
		taxableItem2.setItemCode(LINE_ITEM_2);
		taxableItem2.setItemGuid(ID_2);
		taxableItem2.setCurrency(Currency.getInstance(Locale.US));
		for (TaxableItem taxableItem : Arrays.asList(taxableItem1, taxableItem2)) {
			MutableTaxedItem taxedItem = new MutableTaxedItem();
			taxedItem.setTaxableItem(taxableItem);
			container2.addTaxedItem(taxedItem);
		}
		container2.setStoreCode("storeCode");
		container2.setCurrency(Currency.getInstance(Locale.CANADA));
		TaxAddress address1 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			ADDRESS_COUNTRY);
		container2.setDestinationAddress(address1);
		container2.setOriginAddress(address1);
		container2.setTaxInclusive(true);

		MutableTaxedItemContainer serialized = (MutableTaxedItemContainer) SerializationUtils.clone(container2);
		assertEquals(container2, serialized);
	}
}
