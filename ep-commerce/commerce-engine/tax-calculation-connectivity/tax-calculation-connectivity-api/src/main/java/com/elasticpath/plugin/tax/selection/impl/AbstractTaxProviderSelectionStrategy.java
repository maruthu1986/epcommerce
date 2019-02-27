/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.selection.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.exception.TaxCalculationException;
import com.elasticpath.plugin.tax.selection.TaxProviderSelectionStrategy;

/**
 * An abstract implementation of {@link TaxProviderSelectionStrategy} to hold common methods used by 
 * {@link RegionTaxProviderSelectionStrategy} and {@link StoreTaxProviderSelectionStrategy}.
 */
public abstract class AbstractTaxProviderSelectionStrategy implements TaxProviderSelectionStrategy {

	private static final Logger LOG = Logger.getLogger(AbstractTaxProviderSelectionStrategy.class);
	
	@Override
	public TaxProviderPluginInvoker findProviderByName(final String taxProviderName) {
		TaxProviderPluginInvoker taxProvider = getProvidersMap().get(taxProviderName);
		if (taxProvider == null) {
			LOG.error("No tax provider with name " + taxProviderName);
			throw new TaxCalculationException("No tax provider with name " + taxProviderName);
		}
		return taxProvider;
	}
	
	/**
	 * Gets a map of tax providers and their names.
	 *
	 * @return a map of tax providers and their names
	 */
	protected Map<String, TaxProviderPluginInvoker> getProvidersMap() {
		
		Map<String, TaxProviderPluginInvoker> providerMap = new HashMap<>();
		
		List<TaxProviderPluginInvoker> providers = getProviders();
		providers.add(getDefaultTaxProvider());
		
		for (TaxProviderPluginInvoker taxProvider : providers) {
			providerMap.put(taxProvider.getName(), taxProvider);
		}

		return Collections.unmodifiableMap(providerMap);
	}
	
	/**
	 * Gets a collection of tax providers. 
	 *
	 * @return a collection of tax providers
	 */
	protected abstract List<TaxProviderPluginInvoker> getProviders();
	
}
