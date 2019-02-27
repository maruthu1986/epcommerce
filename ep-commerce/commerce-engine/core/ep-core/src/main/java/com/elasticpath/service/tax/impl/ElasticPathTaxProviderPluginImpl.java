/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.impl;

import java.util.Objects;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.calculator.TaxCalculator;
import com.elasticpath.plugin.tax.capability.TaxCalculationCapability;
import com.elasticpath.plugin.tax.common.TaxContextIdNames;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptor;
import com.elasticpath.plugin.tax.resolver.TaxOperationResolvers;
import com.elasticpath.plugin.tax.resolver.TaxRateDescriptorResolver;
import com.elasticpath.plugin.tax.spi.AbstractTaxProviderPluginSPI;

/**
 * ElasticPath tax provider plugin.
 */
public class ElasticPathTaxProviderPluginImpl extends AbstractTaxProviderPluginSPI implements TaxCalculationCapability {

	/**
	 * Elastic Path default Tax Provider name.
	 */
	public static final String PROVIDER_NAME = "ElasticPath";
	
	private BeanFactory beanFactory;
	private TaxCalculator taxCalculator;
	private TaxOperationResolvers taxOperationResolvers;

	@Override
	public String getName() {
		return PROVIDER_NAME;
	}
	
	@Override
	public TaxedItemContainer calculate(final TaxableItemContainer container) {
		return getTaxCalculator().calculate(container, getTaxOperationResolvers());
	}

	private TaxOperationResolvers getTaxOperationResolvers() {
		
		if (taxOperationResolvers == null) {
			taxOperationResolvers = new TaxOperationResolvers();
			
			TaxRateDescriptorResolver taxRateDescriptorResolver = getBeanFactory().getBean(TaxContextIdNames.TAX_RATE_DESCRIPTOR_RESOLVER);
			taxOperationResolvers.putResolver(TaxRateDescriptorResolver.class, taxRateDescriptorResolver);
		}
		return taxOperationResolvers;
	}

	public TaxCalculator getTaxCalculator() {
		return taxCalculator;
	}

	public void setTaxCalculator(final TaxCalculator taxCalculator) {
		this.taxCalculator = taxCalculator;
	}
	
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TaxRateDescriptor)) {
			return false;
		}
		TaxProviderPluginInvoker taxProvider = (TaxProviderPluginInvoker) obj;
		return Objects.equals(getName(), taxProvider.getName());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

}
