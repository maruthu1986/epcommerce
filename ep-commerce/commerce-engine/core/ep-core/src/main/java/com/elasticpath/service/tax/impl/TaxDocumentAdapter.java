/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.plugin.tax.builder.TaxRecordBuilder;
import com.elasticpath.plugin.tax.builder.TaxableItemBuilder;
import com.elasticpath.plugin.tax.common.TaxContextIdNames;
import com.elasticpath.plugin.tax.common.TaxItemObjectType;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxRecord;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxDocument;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItem;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItemContainer;
import com.elasticpath.plugin.tax.domain.impl.StringTaxDocumentId;

/**
 * Adapter from/to {@link TaxDocument} and {@link TaxJournalRecord}.
 */
public class TaxDocumentAdapter {

	private BeanFactory beanFactory;
	
	/**
	 * Adapts {@link TaxDocument}} data values to a {@link TaxJournalRecord}.
	 * 
	 * @param taxDocument the tax document
	 * @param taxOperationContext the tax operation
	 * @return a list of {@link TaxJournalRecord}
	 */
	public  List<TaxJournalRecord> toTaxJournalRecords(final TaxDocument taxDocument, final TaxOperationContext taxOperationContext) {
		
		List<TaxJournalRecord> taxJournals = new ArrayList<>();
		
		TaxedItemContainer taxedItemContainer = taxDocument.getTaxedItemContainer();
		String taxDocumentId = taxOperationContext.getDocumentId().toString();
		
		BigDecimal amountSign = getSign(taxDocument.getJournalType());
		
		for (TaxedItem item : taxedItemContainer.getItems()) {
			for (TaxRecord taxRecord : item.getTaxRecords()) {
				TaxJournalRecord taxJournalRecord = getBeanFactory().getBean(ContextIdNames.TAX_JOURNAL_RECORD);
				
				taxJournalRecord.setTaxName(taxRecord.getTaxName());
				taxJournalRecord.setTaxJurisdiction(taxRecord.getTaxJurisdiction());
				taxJournalRecord.setTaxRegion(taxRecord.getTaxRegion());
				taxJournalRecord.setTaxRate(taxRecord.getTaxRate());
				taxJournalRecord.setTaxProvider(taxRecord.getTaxProvider());
				taxJournalRecord.setTaxCode(taxRecord.getTaxCode());				
				taxJournalRecord.setItemCode(item.getItemCode());
				
				taxJournalRecord.setItemAmount(item.getPrice().multiply(amountSign));
				taxJournalRecord.setTaxAmount(taxRecord.getTaxValue().multiply(amountSign));
				
				taxJournalRecord.setDocumentId(taxDocumentId);
				taxJournalRecord.setJournalType(taxDocument.getJournalType().toString());
				taxJournalRecord.setTransactionType(taxOperationContext.getTransactionType().toString());
				taxJournalRecord.setOrderNumber(taxOperationContext.getOrderNumber());
				
				taxJournalRecord.setStoreCode(taxedItemContainer.getStoreCode());
				taxJournalRecord.setCurrency(taxedItemContainer.getCurrency().getCurrencyCode());
				taxJournalRecord.setTaxInclusive(taxedItemContainer.isTaxInclusive());
				
				taxJournalRecord.setItemGuid(getItemGuid(item, taxOperationContext));
				taxJournalRecord.setItemObjectType(getItemObjectType(item, taxOperationContext));

				taxJournals.add(taxJournalRecord);
			}
		}
		return taxJournals;
	}
	
	private BigDecimal getSign(final TaxJournalType journalType) {
		
		if (journalType == TaxJournalType.REVERSAL) {
			return BigDecimal.ONE.negate();
		}
			return BigDecimal.ONE;
	}

	private String getItemGuid(final TaxedItem item, final TaxOperationContext taxOperationContext) {
		if (StringUtils.equals(item.getItemCode(), TaxCode.TAX_CODE_SHIPPING)) {
			return taxOperationContext.getShippingItemReferenceId();
		} 
		return item.getItemGuid();				
	}
	
	private String getItemObjectType(final TaxedItem item, final TaxOperationContext taxOperationContext) {
		
		if (StringUtils.equals(item.getItemCode(), TaxCode.TAX_CODE_SHIPPING)) {
			switch (taxOperationContext.getItemObjectType()) {
			case ORDER_SKU:
				return TaxItemObjectType.ORDER_SHIPMENT.toString();
			case ORDER_RETURN_SKU:
				return TaxItemObjectType.ORDER_RETURN.toString();
			default:
				return StringUtils.EMPTY;
			}
		}
		
		return taxOperationContext.getItemObjectType().toString();
	}
	
	/**
	 * Adapts {@link TaxJournalRecord}} data values to a {@link TaxDocument}.
	 * 
	 * @param taxJournals the collection of tax journal records
	 * @param destinationAddress destination address
	 * @param originAddress origin address
	 * @return a tax document
	 */
	public TaxDocument toTaxDocument(final List<TaxJournalRecord> taxJournals,
										final TaxAddress destinationAddress,
										final TaxAddress originAddress) {

		if (CollectionUtils.isEmpty(taxJournals)) {
			return null;
		}
		
		MutableTaxDocument document = getBeanFactory().getBean(TaxContextIdNames.MUTABLE_TAX_DOCUMENT);
		document.setDocumentId(StringTaxDocumentId.fromString(taxJournals.get(0).getDocumentId()));
		document.setTaxProviderName(taxJournals.get(0).getTaxProvider());
		document.setJournalType(getJournalType(taxJournals.get(0).getJournalType()));
		
		MutableTaxedItemContainer taxedItemContainer = getBeanFactory().getBean(TaxContextIdNames.MUTABLE_TAXED_ITEM_CONTAINER);
		
		Currency currency = Currency.getInstance(taxJournals.get(0).getCurrency());
		
		taxedItemContainer.setCurrency(currency);
		taxedItemContainer.setStoreCode(taxJournals.get(0).getStoreCode());
		taxedItemContainer.setTaxInclusive(taxJournals.get(0).isTaxInclusive());
		taxedItemContainer.setDestinationAddress(destinationAddress);
		taxedItemContainer.setOriginAddress(originAddress);

		Map<String, List<TaxJournalRecord>> itemTaxJournals = getItemTaxJournals(taxJournals);
		
		for (Entry<String, List<TaxJournalRecord>> entry : itemTaxJournals.entrySet()) {
			
			MutableTaxedItem taxedItem = getBeanFactory().getBean(TaxContextIdNames.MUTABLE_TAXED_ITEM);
			
			TaxJournalRecord journalRecord = entry.getValue().get(0);
			
			taxedItem.setTaxableItem(TaxableItemBuilder.newBuilder()
														.withTaxCode(journalRecord.getTaxCode())
														.withCurrency(currency)
														.withItemCode(journalRecord.getItemCode())
														.withItemGuid(getItemGuid(journalRecord))
														.withItemAmount(journalRecord.getItemAmount().abs())
														.build());
			
			for (TaxJournalRecord taxJournal : entry.getValue()) {
				taxedItem.addTaxRecord(TaxRecordBuilder.newBuilder()
														.withTaxCode(taxJournal.getTaxCode())
														.withTaxName(taxJournal.getTaxName())
														.withTaxJurisdiction(taxJournal.getTaxJurisdiction())
														.withTaxRegion(taxJournal.getTaxRegion())
														.withTaxRate(taxJournal.getTaxRate())
														.withTaxValue(taxJournal.getTaxAmount().abs())
														.withTaxProvider(taxJournal.getTaxProvider())
														.build());
			
			}
			
			taxedItemContainer.addTaxedItem(taxedItem);
		}
			
		document.setTaxedItemContainer(taxedItemContainer);
		return document;
	}

	private String getItemGuid(final TaxJournalRecord journalRecord) {
		
		if (StringUtils.isBlank(journalRecord.getItemGuid())) {
			return journalRecord.getItemCode();
		}
		
		return journalRecord.getItemGuid();
	}

	private Map<String, List<TaxJournalRecord>> getItemTaxJournals(final List<TaxJournalRecord> taxJournals) {

		Map<String, List<TaxJournalRecord>> itemTaxJournals = new HashMap<>();
		
		for (TaxJournalRecord taxJournalRecord : taxJournals) {
			
			List<TaxJournalRecord> records = itemTaxJournals.get(taxJournalRecord.getItemCode());
			
			if (records == null) {
				records = new ArrayList<>();
			}
			records.add(taxJournalRecord);
			
			itemTaxJournals.put(taxJournalRecord.getItemCode(), records);
		}
		return itemTaxJournals;
	}

	private TaxJournalType getJournalType(final String journalType) {
		if (StringUtils.isBlank(journalType)) {
			return null;
		}
		
		try {
			return TaxJournalType.valueOf(journalType.toUpperCase(Locale.US));
		} catch (IllegalArgumentException exc) {
			return null; 
		}
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
