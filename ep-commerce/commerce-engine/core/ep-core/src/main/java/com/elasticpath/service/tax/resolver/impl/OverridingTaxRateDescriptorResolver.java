/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.resolver.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.plugin.tax.common.TaxContextIdNames;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.impl.StringTaxDocumentId;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptorResult;
import com.elasticpath.plugin.tax.rate.dto.MutableTaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.dto.MutableTaxRateDescriptorResult;
import com.elasticpath.plugin.tax.rate.impl.TaxRateApplier;
import com.elasticpath.plugin.tax.resolver.TaxRateDescriptorResolver;
import com.elasticpath.service.tax.TaxDocumentService;
import com.elasticpath.service.tax.impl.OverridingTaxRateDescriptorResult;

/**
 * A {@link TaxRateDescriptorResolver} implementation that retrieves the tax rates for a {@link TaxableItem}
 * based on the {@link TaxOperationContext} contents from EP current tax rates storage or
 * from EP tax journal history. 
 * <p>
 * The resolver uses a delegate to retrieve the current tax rates from EP persistence first. 
 * If the tax operation has tax rate overriding data in the case of order return, or tax reversal, 
 * then the resolver finds the tax rates from EP tax journal history to override the current tax rates. 
 */
public class OverridingTaxRateDescriptorResolver implements TaxRateDescriptorResolver {

	private TaxRateDescriptorResolver delegate;
	private TaxDocumentId taxDocumentId;
	private TaxDocumentService taxDocumentService;
	private BeanFactory beanFactory;
	
	@Override
	public TaxRateDescriptorResult findTaxRateDescriptors(final TaxableItem taxableItem, final TaxableItemContainer container) {
		TaxRateDescriptorResult taxRateDescriptorResult = delegate.findTaxRateDescriptors(taxableItem, container);
		
		final TaxOperationContext taxOperationContext = container.getTaxOperationContext();
		
		if (taxOperationContext.getDocumentId() == null || taxOperationContext.getTaxOverrideContext() == null) {
			return taxRateDescriptorResult;
		}

		setTaxDocumentId(StringTaxDocumentId.fromString(taxOperationContext.getTaxOverrideContext().getTaxOverrideDocumentId()));
		
		List<TaxJournalRecord> taxJournalRecords = findTaxJournalRecords(taxDocumentId, taxableItem);
		
		// no tax journal records found for this document id and the taxable item, so do not override tax rate
		if (CollectionUtils.isEmpty(taxJournalRecords)) {
			return taxRateDescriptorResult;
		}
		
		// found tax journal records, need to over ride the tax rate and the tax jurisdiction and tax region
		MutableTaxRateDescriptorResult mutableTaxRateDescriptorResult = null; 
		
		if (taxRateDescriptorResult instanceof MutableTaxRateDescriptorResult) {
			mutableTaxRateDescriptorResult = (MutableTaxRateDescriptorResult) taxRateDescriptorResult;
		} else {
			mutableTaxRateDescriptorResult = getBeanFactory().getBean(TaxContextIdNames.MUTABLE_TAX_RATE_DESCRIPTOR_RESULT);
		}
		
		Collection<TaxRateDescriptor> taxRates = new HashSet<>();
		for (TaxJournalRecord record : taxJournalRecords) {
			taxRates.add(createTaxRate(mutableTaxRateDescriptorResult, record, mutableTaxRateDescriptorResult.isTaxInclusive()));
		}
		
		// Override the tax inclusive
		mutableTaxRateDescriptorResult.setTaxInclusive(taxJournalRecords.get(0).isTaxInclusive());
		
		return new OverridingTaxRateDescriptorResult(mutableTaxRateDescriptorResult, taxRates);
	}

	private List<TaxJournalRecord> findTaxJournalRecords(final TaxDocumentId taxDocumentId, final TaxableItem taxableItem) {
		
		if (taxDocumentId == null) {
			return Collections.emptyList();
		}
		return getTaxDocumentService().find(taxDocumentId, taxableItem.getItemCode());
	}

	private TaxRateDescriptor createTaxRate(final TaxRateDescriptorResult taxRateDescriptorResult, 
											final TaxJournalRecord record, 
											final boolean isTaxInclusive) {
		
		MutableTaxRateDescriptor taxRateDescriptor = new MutableTaxRateDescriptor();
		taxRateDescriptor.setId(record.getTaxName());
		taxRateDescriptor.setValue(record.getTaxRate());
		taxRateDescriptor.setTaxRateApplier(createNewTaxRateApplier(isTaxInclusive));
		taxRateDescriptor.setTaxJurisdiction(record.getTaxJurisdiction());
		taxRateDescriptor.setTaxRegion(record.getTaxRegion());
		taxRateDescriptor.setTaxRateDescriptorResult((MutableTaxRateDescriptorResult) taxRateDescriptorResult);
		return taxRateDescriptor;
	}
	
	@Override
	public TaxRateDescriptorResult findTaxJurisdiction(final TaxableItemContainer container) {
		return delegate.findTaxJurisdiction(container);
	}

	private TaxRateApplier createNewTaxRateApplier(final boolean isTaxInclusive) {
		if (isTaxInclusive) {
			return getBeanFactory().getBean("taxInclusiveRateApplier");
		}
		return getBeanFactory().getBean("taxExclusiveRateApplier");
	}

	public void setTaxDocumentId(final TaxDocumentId taxDocumentId) {
		this.taxDocumentId = taxDocumentId;
	}
	
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public TaxRateDescriptorResolver getDelegate() {
		return delegate;
	}

	public void setDelegate(final TaxRateDescriptorResolver delegate) {
		this.delegate = delegate;
	}

	public TaxDocumentService getTaxDocumentService() {
		return taxDocumentService;
	}

	public void setTaxDocumentService(final TaxDocumentService taxDocumentService) {
		this.taxDocumentService = taxDocumentService;
	}

}
