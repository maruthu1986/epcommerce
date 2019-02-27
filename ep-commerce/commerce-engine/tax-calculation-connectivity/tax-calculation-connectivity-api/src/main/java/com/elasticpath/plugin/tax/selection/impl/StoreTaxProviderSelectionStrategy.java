/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.selection.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.domain.TaxItemContainer;

/**
 * {@link TaxProviderSelectionStrategy} to select tax provider based on store.
 */
public class StoreTaxProviderSelectionStrategy extends AbstractTaxProviderSelectionStrategy {

	private Map<TaxStore, TaxProviderPluginInvoker> providersByStore;
	
	private TaxProviderPluginInvoker defaultTaxProvider;
	
	@Override
	public TaxProviderPluginInvoker findProvider(final TaxItemContainer container) {
		
		String storeCode = container.getStoreCode();
		
		if (StringUtils.isBlank(storeCode)) {
			return getDefaultTaxProvider();
		}
		
		for (TaxStore taxStore : getProvidersByStore().keySet()) {
			
			if (taxStore.matches(storeCode)) {
				return getProvidersByStore().get(taxStore);
			}
		}
	
		return getDefaultTaxProvider();
	}
	
	@Override
	protected List<TaxProviderPluginInvoker> getProviders() {
		return new ArrayList<>(providersByStore.values());
	}
	
	public Map<TaxStore, TaxProviderPluginInvoker> getProvidersByStore() {
		return providersByStore;
	}

	public void setProvidersByStore(final Map<TaxStore, TaxProviderPluginInvoker> providersByStore) {
		this.providersByStore = providersByStore;
	}
	
	@Override
	public TaxProviderPluginInvoker getDefaultTaxProvider() {
		return defaultTaxProvider;
	}

	public void setDefaultTaxProvider(final TaxProviderPluginInvoker defaultTaxProvider) {
		this.defaultTaxProvider = defaultTaxProvider;
	}

}
