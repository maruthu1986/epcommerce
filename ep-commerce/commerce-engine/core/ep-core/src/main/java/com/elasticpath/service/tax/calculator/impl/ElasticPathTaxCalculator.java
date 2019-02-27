/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.calculator.impl;

import java.math.BigDecimal;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.plugin.tax.builder.TaxRecordBuilder;
import com.elasticpath.plugin.tax.calculator.TaxCalculator;
import com.elasticpath.plugin.tax.common.TaxContextIdNames;
import com.elasticpath.plugin.tax.domain.TaxRecord;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItem;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItemContainer;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptorResult;
import com.elasticpath.plugin.tax.rate.dto.AppliedTaxValue;
import com.elasticpath.plugin.tax.resolver.TaxOperationResolvers;
import com.elasticpath.plugin.tax.resolver.TaxRateDescriptorResolver;
import com.elasticpath.service.tax.TaxCodeRetriever;
import com.elasticpath.service.tax.impl.ElasticPathTaxProviderPluginImpl;

/**
 * The ElasticPath tax calculator calculates taxes based on EP tax tables. Taxes for returns are based on the tax rates
 * in the tax journal for the original purchase.
 */
public class ElasticPathTaxCalculator implements TaxCalculator {

	private BeanFactory beanFactory;

	private TaxCodeRetriever taxCodeRetriever;

	@Override
	public TaxedItemContainer calculate(final TaxableItemContainer container, final TaxOperationResolvers taxOperationResolvers) {
		final MutableTaxedItemContainer result = getBeanFactory().getBean(TaxContextIdNames.MUTABLE_TAXED_ITEM_CONTAINER);
		result.initialize(container);

		final TaxRateDescriptorResolver taxRateDescriptorResolver = taxOperationResolvers.getResolver(TaxRateDescriptorResolver.class);
		
		for (TaxableItem taxableItem : container.getItems()) {
			final MutableTaxedItem taxedItem = getBeanFactory().getBean(TaxContextIdNames.MUTABLE_TAXED_ITEM);
			taxedItem.setTaxableItem(taxableItem);
			
			final TaxRateDescriptorResult taxRateDescriptorResult = taxRateDescriptorResolver.findTaxRateDescriptors(taxableItem, container);
			BigDecimal includeTaxAmount = BigDecimal.ZERO;

			for (TaxRateDescriptor taxRateDescriptor : taxRateDescriptorResult.getTaxRateDescriptors()) {
				final AppliedTaxValue appliedTax = taxRateDescriptor.applyTo(taxedItem);

				includeTaxAmount = includeTaxAmount.add(appliedTax.getIncludedTaxAmount());
				taxedItem.addTaxInPrice(appliedTax.getIncludedTaxAmount());

				final TaxRecord taxRecord = TaxRecordBuilder.newBuilder()
						.withTaxCode(taxedItem.getTaxCode())
						.withTaxName(taxRateDescriptor.getTaxRateName())
						.withTaxJurisdiction(taxRateDescriptor.getTaxJurisdiction())
						.withTaxRegion(taxRateDescriptor.getTaxRegion())
						.withTaxRate(taxRateDescriptor.getTaxRateValue())
						.withTaxValue(appliedTax.getTaxAmount())
						.withTaxProvider(ElasticPathTaxProviderPluginImpl.PROVIDER_NAME)
						.build();

				taxedItem.addTaxRecord(taxRecord);
			}
			
			taxedItem.setPriceBeforeTax(taxedItem.getPrice().subtract(includeTaxAmount));
			taxedItem.setQuantity(taxableItem.getQuantity());

			result.addTaxedItem(taxedItem);
			result.setTaxInclusive(taxRateDescriptorResult.isTaxInclusive());
		}

		return result;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected TaxCodeRetriever getTaxCodeRetriever() {
		return taxCodeRetriever;
	}

	public void setTaxCodeRetriever(final TaxCodeRetriever taxCodeRetriever) {
		this.taxCodeRetriever = taxCodeRetriever;
	}

}
