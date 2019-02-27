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

import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxableItemContainer;
import com.elasticpath.plugin.tax.domain.impl.StringTaxDocumentId;
import com.elasticpath.plugin.tax.domain.impl.TaxOperationContextImpl;
import com.elasticpath.plugin.tax.domain.impl.TaxableItemImpl;

/**
 * Test cases for {@link MutableTaxableItemContainer} calculation.
 */
public class MutableTaxableItemContainerTest {

	private static final String ID_1 = "id1";
	private static final String ID_2 = "id2";
	private static final String LINE_ITEM_2 = "LineItem-2";
	private static final String LINE_ITEM_1 = "LineItem-1";
	private static final String TAX_CODE_1 = "taxCode1";
	private static final String ADDRESS_ZIPCODE_WA = "98110";
	private static final String ADDRESS_STATE_WA = "WA";
	private static final String ADDRESS_CITY = "Bainbridge Island";
	private static final String ADDRESS_STREET_NO = "Suite 220";
	private static final String ADDRESS_STREET = "100 Ravine Lane NE";
	private static final String SHIPMENT_ID_1 = "shipment_1";
	private static final String STORE_CODE_USA = "USA";
	private static final double DOUBLE_1000 = 1000.00;
	private static final double DOUBLE_1001 = 1000.01;

	private final MutableTaxableItemContainer container1 = new MutableTaxableItemContainer();
	private TaxAddress address1;
	private final TaxableItemImpl taxableItem1 = new TaxableItemImpl();

	private final MutableTaxableItemContainer container2 = new MutableTaxableItemContainer();
	private TaxAddress address2;
	private final TaxableItemImpl taxableItem2 = new TaxableItemImpl();

	/**
	 * Tests with same taxable Item but with same id. The id is shopping itme guid, which keeps changing all the time.
	 * The guid should not affect the tax result for the container.
	 *
	 */
	@Test
	public void testHashCodeWithDiffTaxableItemsDiffIndex() {

		container1.setStoreCode(STORE_CODE_USA);
		container1.setCurrency(Currency.getInstance(Locale.CANADA));

		container2.setStoreCode(STORE_CODE_USA);
		container2.setCurrency(Currency.getInstance(Locale.CANADA));

		String documentId = SHIPMENT_ID_1;

		container1.setTaxOperationContext(new TaxOperationContextImpl(
				"",
				TaxJournalType.PURCHASE,
				StringTaxDocumentId.fromString(documentId)));

		container2.setTaxOperationContext(new TaxOperationContextImpl(
				"",
				TaxJournalType.PURCHASE,
				StringTaxDocumentId.fromString(SHIPMENT_ID_1)));

		address1 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container1.setDestinationAddress(address1);
		container1.setOriginAddress(address1);

		address2 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container2.setDestinationAddress(address2);
		container2.setOriginAddress(address2);

		taxableItem1.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem1.setTaxCode(TAX_CODE_1);
		taxableItem1.setItemCode(LINE_ITEM_1);
		taxableItem1.setItemGuid(ID_1);

		taxableItem2.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem2.setTaxCode(TAX_CODE_1);
		taxableItem2.setItemCode(LINE_ITEM_2);
		taxableItem2.setItemGuid(ID_2);

		container1.setItems(Arrays.<TaxableItem> asList(taxableItem1, taxableItem2));
		container2.setItems(Arrays.<TaxableItem> asList(taxableItem2, taxableItem1));

		int hashCode1 = container1.hashCode();
		int hashCode2 = container2.hashCode();

		assertNotSame(hashCode1, hashCode2);
	}


	/**
	 * Tests with same taxable Item but with same id. The id is shopping itme guid, which keeps changing all the time.
	 * The guid should not affect the tax result for the container.
	 *
	 */
	@Test
	public void testHashCodeWithSameTaxableItemsDiffIndex() {

		container1.setStoreCode(STORE_CODE_USA);
		container1.setCurrency(Currency.getInstance(Locale.CANADA));

		container2.setStoreCode(STORE_CODE_USA);
		container2.setCurrency(Currency.getInstance(Locale.CANADA));

		String documentId = SHIPMENT_ID_1;

		container1.setTaxOperationContext(new TaxOperationContextImpl(
				"",
				TaxJournalType.PURCHASE,
				StringTaxDocumentId.fromString(documentId)));

		container2.setTaxOperationContext(new TaxOperationContextImpl(
				"",
				TaxJournalType.PURCHASE,
				StringTaxDocumentId.fromString(SHIPMENT_ID_1)));

		address1 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container1.setDestinationAddress(address1);
		container1.setOriginAddress(address1);

		address2 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container2.setDestinationAddress(address2);
		container2.setOriginAddress(address2);

		taxableItem1.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem1.setTaxCode(TAX_CODE_1);
		taxableItem1.setItemCode(LINE_ITEM_1);
		taxableItem1.setItemGuid(ID_1);

		taxableItem2.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem2.setTaxCode(TAX_CODE_1);
		taxableItem2.setItemCode(LINE_ITEM_1);
		taxableItem2.setItemGuid(ID_2);

		container1.setItems(Arrays.<TaxableItem> asList(taxableItem1, taxableItem2));
		container2.setItems(Arrays.<TaxableItem> asList(taxableItem2, taxableItem1));

		int hashCode1 = container1.hashCode();
		int hashCode2 = container2.hashCode();

		assertEquals(hashCode1, hashCode2);
	}

	/**
	 * Tests that a TaxableItemContainer is serializable. Since it is stored in a cache, which may
	 * serialize it, this test ensures that it can be successfully deserialized.
	 */
	@Test
	public void testTaxableItemContainerIsSerializable() {

		container1.setStoreCode(STORE_CODE_USA);
		container1.setCurrency(Currency.getInstance(Locale.CANADA));

		String documentId = SHIPMENT_ID_1;

		container1.setTaxOperationContext(new TaxOperationContextImpl(
			"",
			TaxJournalType.PURCHASE,
			StringTaxDocumentId.fromString(documentId)));

		address1 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container1.setDestinationAddress(address1);
		container1.setOriginAddress(address1);

		taxableItem1.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem1.setTaxCode(TAX_CODE_1);
		taxableItem1.setItemCode(LINE_ITEM_1);
		taxableItem1.setItemGuid(ID_1);
		taxableItem1.setCurrency(Currency.getInstance(Locale.CANADA));
		taxableItem1.setTaxCodeActive(true);

		taxableItem2.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem2.setTaxCode(TAX_CODE_1);
		taxableItem2.setItemCode(LINE_ITEM_1);
		taxableItem2.setItemGuid(ID_2);
		taxableItem2.setCurrency(Currency.getInstance(Locale.CANADA));
		taxableItem2.setTaxCodeActive(true);

		container1.setItems(Arrays.<TaxableItem> asList(taxableItem1, taxableItem2));

		MutableTaxableItemContainer serialized = (MutableTaxableItemContainer) SerializationUtils.clone(container1);
		assertEquals(serialized, container1);
	}

	/**
	 * Tests with same taxable Item but with diff id. The id is shopping itme guid, which keeps changing all the time.
	 * The guid should not affect the tax result for the container.
	 *
	 */
	@Test
	public void testHashCodeWithDiffTaxableId() {

		container1.setStoreCode(STORE_CODE_USA);
		container1.setCurrency(Currency.getInstance(Locale.CANADA));

		container2.setStoreCode(STORE_CODE_USA);
		container2.setCurrency(Currency.getInstance(Locale.CANADA));

		String documentId = SHIPMENT_ID_1;

		container1.setTaxOperationContext(new TaxOperationContextImpl(
				"",
				TaxJournalType.PURCHASE,
				StringTaxDocumentId.fromString(documentId)));

		container2.setTaxOperationContext(new TaxOperationContextImpl(
				"",
				TaxJournalType.PURCHASE,
				StringTaxDocumentId.fromString(SHIPMENT_ID_1)));

		address1 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container1.setDestinationAddress(address1);
		container1.setOriginAddress(address1);

		address2 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container2.setDestinationAddress(address2);
		container2.setOriginAddress(address2);

		taxableItem1.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem1.setTaxCode(TAX_CODE_1);
		taxableItem1.setItemCode(LINE_ITEM_1);
		taxableItem1.setItemGuid(ID_1);

		container1.setItems(Arrays.<TaxableItem> asList(taxableItem1));

		taxableItem2.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem2.setTaxCode(TAX_CODE_1);
		taxableItem2.setItemCode(LINE_ITEM_1);
		taxableItem2.setItemGuid(ID_2);
		container2.setItems(Arrays.<TaxableItem> asList(taxableItem2));

		int hashCode1 = container1.hashCode();
		int hashCode2 = container2.hashCode();

		assertEquals(hashCode1, hashCode2);
	}

	/**
	 * Tests with diff taxable Item.
	 */
	@Test
	public void testHashCodeWithDiffTaxableItem() {

		container1.setStoreCode(STORE_CODE_USA);
		container1.setCurrency(Currency.getInstance(Locale.CANADA));

		container2.setStoreCode(STORE_CODE_USA);
		container2.setCurrency(Currency.getInstance(Locale.CANADA));

		String documentId = SHIPMENT_ID_1;

		container1.setTaxOperationContext(new TaxOperationContextImpl(
				"",
				TaxJournalType.PURCHASE,
				StringTaxDocumentId.fromString(documentId)));

		container2.setTaxOperationContext(new TaxOperationContextImpl(
				"",
				TaxJournalType.PURCHASE,
				StringTaxDocumentId.fromString(SHIPMENT_ID_1)));

		address1 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container1.setDestinationAddress(address1);
		container1.setOriginAddress(address1);

		address2 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container2.setDestinationAddress(address2);
		container2.setOriginAddress(address2);

		taxableItem1.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem1.setTaxCode(TAX_CODE_1);
		taxableItem1.setItemCode(LINE_ITEM_1);
		taxableItem1.setItemGuid(ID_1);

		container1.setItems(Arrays.<TaxableItem> asList(taxableItem1));

		taxableItem2.setPrice(new BigDecimal(DOUBLE_1001));
		taxableItem2.setTaxCode(TAX_CODE_1);
		taxableItem2.setItemCode(LINE_ITEM_1);
		taxableItem2.setItemGuid(ID_1);
		container2.setItems(Arrays.<TaxableItem> asList(taxableItem2));

		int hashCode1 = container1.hashCode();
		int hashCode2 = container2.hashCode();

		assertNotSame(hashCode1, hashCode2);
	}

	/**
	 * Tests with diff taxable Document.
	 */
	@Test
	public void testHashCodeWithDiffDocumentId() {

		container1.setStoreCode(STORE_CODE_USA);
		container1.setCurrency(Currency.getInstance(Locale.CANADA));

		container2.setStoreCode(STORE_CODE_USA);
		container2.setCurrency(Currency.getInstance(Locale.CANADA));

		String documentId = SHIPMENT_ID_1;

		container1.setTaxOperationContext(new TaxOperationContextImpl(
				"",
				TaxJournalType.PURCHASE,
				StringTaxDocumentId.fromString(documentId)));

		container2.setTaxOperationContext(new TaxOperationContextImpl(
				"",
				TaxJournalType.PURCHASE,
				StringTaxDocumentId.fromString("shipment_2")));

		address1 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container1.setDestinationAddress(address1);
		container1.setOriginAddress(address1);

		address2 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container2.setDestinationAddress(address2);
		container2.setOriginAddress(address2);

		taxableItem1.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem1.setItemCode(LINE_ITEM_1);

		container1.setItems(Arrays.<TaxableItem> asList(taxableItem1));

		container2.setItems(Arrays.<TaxableItem> asList(taxableItem1));

		int hashCode1 = container1.hashCode();
		int hashCode2 = container2.hashCode();

		assertNotSame(hashCode1, hashCode2);
	}

	/**
	 * Tests with diff tax address.
	 */
	@Test
	public void testHashCodeWithDiffAddress() {

		container1.setStoreCode(STORE_CODE_USA);
		container1.setCurrency(Currency.getInstance(Locale.CANADA));

		container2.setStoreCode(STORE_CODE_USA);
		container2.setCurrency(Currency.getInstance(Locale.CANADA));

		String documentId = SHIPMENT_ID_1;

		container1.setTaxOperationContext(new TaxOperationContextImpl(
				"",
				TaxJournalType.PURCHASE,
				StringTaxDocumentId.fromString(documentId)));

		container2.setTaxOperationContext(new TaxOperationContextImpl(
				"",
				TaxJournalType.PURCHASE,
				StringTaxDocumentId.fromString(documentId)));

		// city is Ravone
		address1 = TaxItemTestUtil.createTaxAddress(
			"100 Ravone Lane NE",
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container1.setDestinationAddress(address1);
		container1.setOriginAddress(address1);

		// city is Ravine
		address2 = TaxItemTestUtil.createTaxAddress(
			ADDRESS_STREET,
			ADDRESS_STREET_NO,
			ADDRESS_CITY,
			ADDRESS_STATE_WA,
			ADDRESS_ZIPCODE_WA,
			STORE_CODE_USA);
		container2.setDestinationAddress(address2);
		container2.setOriginAddress(address2);

		taxableItem1.setPrice(new BigDecimal(DOUBLE_1000));
		taxableItem1.setItemCode(LINE_ITEM_1);

		container1.setItems(Arrays.<TaxableItem> asList(taxableItem1));

		container2.setItems(Arrays.<TaxableItem> asList(taxableItem1));

		int hashCode1 = container1.hashCode();
		int hashCode2 = container2.hashCode();

		assertNotSame(hashCode1, hashCode2);
	}

}
