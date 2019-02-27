/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.tax;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.plugin.tax.common.TaxItemObjectType;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.service.tax.TaxDocumentService;
import com.elasticpath.service.tax.impl.ElasticPathTaxProviderPluginImpl;

public class OrderTaxTestVerifier {

	protected static final String CANADA_CODE = "CA";
	protected static final String BC_CODE = "BC";

	protected static final String PST_TAX_NAME = "PST";
	protected static final String GST_TAX_NAME = "GST";

	protected static final BigDecimal BC_PST_TAX_RATE = BigDecimal.valueOf(0.07);
	protected static final BigDecimal CANADA_GST_RATE = BigDecimal.valueOf(0.05);

	protected static final String[] EXCLUDE_FIELD_NAMES = new String[] {
		"uidPk", "transactionDate", "transactionType", "journalType", "taxAmount", "itemAmount"
	};

	@Autowired
	TaxDocumentService taxDocumentService;

	@Autowired
	private PricingSnapshotService pricingSnapshotService;

	@Autowired
	private TaxSnapshotService taxSnapshotService;

	public void verifyTaxDocumentForOrderShipment(final OrderShipment orderShipment, final Store store) {
		for (OrderSku item : orderShipment.getShipmentOrderSkus()) {
			verifyTaxJournalRecordForLineItem(orderShipment, item, store);
		}
		
		if (orderShipment.getOrderShipmentType().equals(ShipmentType.PHYSICAL)) {
			List<TaxJournalRecord> taxJournalRecordsOfShipping = taxDocumentService.find(orderShipment.getTaxDocumentId(), TaxCode.TAX_CODE_SHIPPING);
			verifyTaxJournalRecordForShipping((PhysicalOrderShipment) orderShipment, taxJournalRecordsOfShipping);
		}
	}
	
	public void verifyTaxDocumentForOrderReturn(final OrderReturn orderReturn, final Store store) {
		for (OrderReturnSku item : orderReturn.getOrderReturnSkus()) {
			verifyOrderReturnTaxJournalRecordForLineItem(orderReturn, item, store);
		}
		
		if (orderReturn.getPhysicalReturn()) {
			List<TaxJournalRecord> taxJournalRecordsOfShipping = taxDocumentService.find(orderReturn.getTaxDocumentId(), TaxCode.TAX_CODE_SHIPPING);
			verifyOrderReturnTaxJournalRecordForShipping(orderReturn, taxJournalRecordsOfShipping);
		}
	}
	
	/**
	 * Verify the cancelled order shipment has two sets of tax journals, one is purchase, another one is return.
	 */
	public void verifyTaxDocumentReversal(final TaxDocumentId taxDocumentId) {
		
		List<TaxJournalRecord> purchaseTaxRecords = taxDocumentService.find(taxDocumentId, TaxJournalType.PURCHASE);
		List<TaxJournalRecord> returnTaxRecords = taxDocumentService.find(taxDocumentId, TaxJournalType.REVERSAL);

		assertEquals(purchaseTaxRecords.size(), returnTaxRecords.size());

		for (TaxJournalRecord purchaseTaxRecord : purchaseTaxRecords) {
			
			String itemCode = purchaseTaxRecord.getItemCode();
			String itemTaxName = purchaseTaxRecord.getTaxName();
			
			for (TaxJournalRecord returnTaxRecord : returnTaxRecords) {
				if (returnTaxRecord.getItemCode().equals(itemCode) && returnTaxRecord.getTaxName().equals(itemTaxName)) {
					
					String purchaseTaxJournal = ReflectionToStringBuilder.toStringExclude(purchaseTaxRecord, EXCLUDE_FIELD_NAMES);
					String returnTaxJournal = ReflectionToStringBuilder.toStringExclude(returnTaxRecord, EXCLUDE_FIELD_NAMES);
					
					assertEquals(purchaseTaxJournal.substring(purchaseTaxJournal.indexOf('[')),
							returnTaxJournal.substring(returnTaxJournal.indexOf('[')));
					
					assertEquals(purchaseTaxRecord.getItemAmount(), returnTaxRecord.getItemAmount().abs());
					assertEquals(purchaseTaxRecord.getTaxAmount(), returnTaxRecord.getTaxAmount().abs());
				}
			}
		}
	}
	
	/**
	 * Verify the tax document has the same tax records as expected tax excluding fields "uidPk", "transactionDate", 
	 * "documentId", "itemGuid", "taxProvider", "storeCode", "currency", "taxInclusive", "orderNumber".
	 */
	public void verifyOrderTaxDocumentWithExpectedRecords(final String orderNumber, 
														  final List<TaxJournalRecord> expectedTaxJournals,
														  final Set<String> excludeFieldNames) {
		
		final List<TaxJournalRecord> persistedTaxRecords = taxDocumentService.findByOrderNumber(orderNumber);

		verifyTaxJournalRecordsMatch(expectedTaxJournals, persistedTaxRecords, excludeFieldNames);
	}

	/**
	 * Verifies that the actual {@link TaxJournalRecord}s match the expected {@link TaxJournalRecord}s, ignoring the given excluded field names.
	 *
	 * @param expectedTaxJournals the expected {@link TaxJournalRecord}s.
	 * @param actualTaxRecords the actual {@link TaxJournalRecord}s found.
	 * @param excludeFieldNames a {@link Set} of field names to exclude when verifying equality.
	 */
	protected void verifyTaxJournalRecordsMatch(final Collection<TaxJournalRecord> expectedTaxJournals,
												final Collection<TaxJournalRecord> actualTaxRecords,
												final Set<String> excludeFieldNames) {
		
		// Generate String representations of the TaxJournalRecords excluding the fields specified
		// Generating Strings allow the lists to be simply sorted as well as displayed in case the test fails

		final List<String> expectedTaxRecordStrings = generateTaxJournalRecordStrings(expectedTaxJournals, excludeFieldNames);
		final List<String> actualTaxRecordStrings = generateTaxJournalRecordStrings(actualTaxRecords, excludeFieldNames);

		// Now sort them, sorting them not only allows assertEquals() below to work
		// but also helps users process any failure since both lists will be in the same order

		Collections.sort(expectedTaxRecordStrings);
		Collections.sort(actualTaxRecordStrings);

		assertEquals("Persisted tax journals do not match expected tax journals;", expectedTaxRecordStrings, actualTaxRecordStrings);
	}

	/**
	 * Generates a {@link List} of String representations of the given {@link TaxJournalRecord}s, excluding the fields specified.
	 *
	 * @param taxRecords the tax records to generate String representations for.
	 * @param excludeFieldNames the names of {@link TaxJournalRecord} fields to skip over when generating the String representation.
	 * @return a {@link List} of String representations of the given {@link TaxJournalRecord}s, excluding the fields specified.
	 */
	protected List<String> generateTaxJournalRecordStrings(final Collection<TaxJournalRecord> taxRecords, final Set<String> excludeFieldNames) {
		final List<String> actualTaxRecordStrings = new ArrayList<>();
		for (TaxJournalRecord actualTaxRecord : taxRecords) {
			actualTaxRecord.setTaxRate(actualTaxRecord.getTaxRate().setScale(2));
			actualTaxRecordStrings.add(StringUtils.substringAfter(
					ReflectionToStringBuilder.toStringExclude(actualTaxRecord, excludeFieldNames), "["));
		}
		return actualTaxRecordStrings;
	}

	private void verifyTaxJournalRecordForShipping(final PhysicalOrderShipment orderShipment,
			final List<TaxJournalRecord> taxJournalRecordsOfShipping) {

		assertEquals(orderShipment.getTaxDocumentId().toString(), taxJournalRecordsOfShipping.get(0).getDocumentId());
		assertEquals(orderShipment.getBeforeTaxShippingCost(), taxJournalRecordsOfShipping.get(0).getItemAmount());
		assertEquals(orderShipment.getShippingTax(), getItemTaxAmount(taxJournalRecordsOfShipping));

		for (TaxJournalRecord record : taxJournalRecordsOfShipping) {

			assertEquals(orderShipment.getShipmentNumber(), record.getItemGuid());
			assertEquals(TaxCode.TAX_CODE_SHIPPING, record.getItemCode());
			assertEquals(TaxCode.TAX_CODE_SHIPPING, record.getTaxCode());
			assertEquals(TaxJournalType.PURCHASE.toString(), record.getJournalType());
			assertEquals(ElasticPathTaxProviderPluginImpl.PROVIDER_NAME, record.getTaxProvider());
			assertEquals(TaxItemObjectType.ORDER_SHIPMENT.toString(), record.getItemObjectType());

			verifyTaxJurisdiction(record);
		}
	}

	private void verifyOrderReturnTaxJournalRecordForShipping(final OrderReturn orderReturn, final List<TaxJournalRecord> taxJournalRecordsOfShipping) {

		assertEquals(orderReturn.getTaxDocumentId().toString(), taxJournalRecordsOfShipping.get(0).getDocumentId());
		assertEquals(orderReturn.getShippingCost(), taxJournalRecordsOfShipping.get(0).getItemAmount().abs());
		assertEquals(orderReturn.getShippingTax(), getItemTaxAmount(taxJournalRecordsOfShipping));

		for (TaxJournalRecord record : taxJournalRecordsOfShipping) {

			assertEquals(orderReturn.getRmaCode(), record.getItemGuid());
			assertEquals(TaxCode.TAX_CODE_SHIPPING, record.getItemCode());
			assertEquals(TaxCode.TAX_CODE_SHIPPING, record.getTaxCode());
			assertEquals(TaxJournalType.REVERSAL.toString(), record.getJournalType());
			assertEquals(ElasticPathTaxProviderPluginImpl.PROVIDER_NAME, record.getTaxProvider());
			assertEquals(TaxItemObjectType.ORDER_RETURN.toString(), record.getItemObjectType());

			verifyTaxJurisdiction(record);
		}
	}

	private void verifyTaxJournalRecordForLineItem(final OrderShipment orderShipment, final OrderSku item, final Store store) {

		List<TaxJournalRecord> taxJournalRecordsOfItem = taxDocumentService.find(orderShipment.getTaxDocumentId(), item.getSkuCode());
		assertEquals("Tax Journal Records should have been created",
				taxJournalRecordsOfItem.size(), store.getTaxJurisdictions().iterator().next().getTaxCategorySet().size());

		assertEquals(item.getInvoiceItemAmount().setScale(2), taxJournalRecordsOfItem.get(0).getItemAmount());

		final ShoppingItemPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForOrderSku(item);
		final ShoppingItemTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForOrderSku(item, pricingSnapshot);
		assertEquals(taxSnapshot.getTaxAmount().setScale(2), getItemTaxAmount(taxJournalRecordsOfItem));

		for (TaxJournalRecord record : taxJournalRecordsOfItem) {

			assertEquals(orderShipment.getTaxDocumentId().toString(), record.getDocumentId());
			assertEquals(item.getGuid(), record.getItemGuid());
			assertEquals(item.getSkuCode(), record.getItemCode());
			assertEquals(item.getTaxCode(), record.getTaxCode());
			assertEquals(item.getCurrency().getCurrencyCode(), record.getCurrency());
			assertEquals(TaxJournalType.PURCHASE.toString(), record.getJournalType());
			assertEquals(item.getTaxCode(), record.getTaxCode());
			assertEquals(ElasticPathTaxProviderPluginImpl.PROVIDER_NAME, record.getTaxProvider());

			verifyTaxJurisdiction(record);
		}
	}

	private void verifyOrderReturnTaxJournalRecordForLineItem(final OrderReturn orderReturn, final OrderReturnSku item, final Store store) {

		List<TaxJournalRecord> taxJournalRecordsOfItem = taxDocumentService.find(orderReturn.getTaxDocumentId(), item.getOrderSku().getSkuCode());
		assertEquals(taxJournalRecordsOfItem.size(), store.getTaxJurisdictions().iterator().next().getTaxCategorySet().size());

		assertEquals(item.getReturnAmount().setScale(2), taxJournalRecordsOfItem.get(0).getItemAmount().abs());
		assertEquals(item.getTax().setScale(2), getItemTaxAmount(taxJournalRecordsOfItem));

		for (TaxJournalRecord record : taxJournalRecordsOfItem) {

			assertEquals(orderReturn.getTaxDocumentId().toString(), record.getDocumentId());
			assertEquals(item.getGuid(), record.getItemGuid());
			assertEquals(item.getOrderSku().getSkuCode(), record.getItemCode());
			assertEquals(item.getOrderSku().getTaxCode(), record.getTaxCode());
			assertEquals(item.getOrderSku().getCurrency().getCurrencyCode(), record.getCurrency());
			assertEquals(TaxJournalType.REVERSAL.toString(), record.getJournalType());
			assertEquals(ElasticPathTaxProviderPluginImpl.PROVIDER_NAME, record.getTaxProvider());

			verifyTaxJurisdiction(record);
		}
	}

	private void verifyTaxJurisdiction(final TaxJournalRecord record) {
		final String taxName = record.getTaxName();

		if (StringUtils.equals(taxName, PST_TAX_NAME)) {
			assertEquals(CANADA_CODE, record.getTaxJurisdiction());
			assertEquals(BC_CODE, record.getTaxRegion());
			assertEquals(BC_PST_TAX_RATE, record.getTaxRate().setScale(2));
		} else if (StringUtils.equals(taxName, GST_TAX_NAME)) {
			assertEquals(CANADA_CODE, record.getTaxJurisdiction());
			assertEquals(CANADA_CODE, record.getTaxRegion());
			assertEquals(CANADA_GST_RATE, record.getTaxRate().setScale(2));
		}
	}

	private BigDecimal getItemTaxAmount(final List<TaxJournalRecord> records) {
		BigDecimal amount = BigDecimal.ZERO;

		for (TaxJournalRecord record : records) {
			amount = amount.add(record.getTaxAmount().abs());
		}

		return amount;
	}
}
