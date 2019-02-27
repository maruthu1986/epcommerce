/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.selection.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxItemContainer;

/**
 * {@link TaxProviderSelectionStrategy} to select tax provider based on region.
 */
public class RegionTaxProviderSelectionStrategy extends AbstractTaxProviderSelectionStrategy {

	private Map<TaxRegion, TaxProviderPluginInvoker> providersByRegion;
	
	private TaxProviderPluginInvoker defaultTaxProvider;

	@Override
	public TaxProviderPluginInvoker findProvider(final TaxItemContainer container) {
		
		TaxAddress taxAddress = container.getDestinationAddress();
		
		if (taxAddress == null || taxAddress.getCountry() == null) {
			return getDefaultTaxProvider();
		}
		
		for (TaxRegion region : getProvidersByRegion().keySet()) {
			
			if (region.matches(taxAddress)) {
				return getProvidersByRegion().get(region);
			}
		}
		
		return getDefaultTaxProvider();
	}

	@Override
	protected List<TaxProviderPluginInvoker> getProviders() {
		return new ArrayList<>(providersByRegion.values());
	}

	public Map<TaxRegion, TaxProviderPluginInvoker> getProvidersByRegion() {
		return providersByRegion;
	}

	public void setProvidersByRegion(final Map<TaxRegion, TaxProviderPluginInvoker> providersByRegion) {
		this.providersByRegion = providersByRegion;
	}
	
	@Override
	public TaxProviderPluginInvoker getDefaultTaxProvider() {
		return defaultTaxProvider;
	}

	public void setDefaultTaxProvider(final TaxProviderPluginInvoker defaultTaxProvider) {
		this.defaultTaxProvider = defaultTaxProvider;
	}
}
