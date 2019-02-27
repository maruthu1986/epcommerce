/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.selection.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.domain.TaxableItem;

/**
 * Selects multiple tax providers for one taxable container based each item's tax code.
 */
public class CompositeTaxProviderSelectionStrategy {

	private Map<TaxCode, TaxProviderPluginInvoker> providersByTaxCode;
	private TaxProviderPluginInvoker defaultTaxProvider;

	/**
	 * Groups the taxable items based on their tax codes in order to be handled by different tax providers.
	 *
	 * @param taxItems the original collection of taxable items
	 * @return a map of tax provide and the corresponding taxable items
	 */
	public Map<TaxProviderPluginInvoker, List<TaxableItem>> groupTaxableItems(final List<? extends TaxableItem> taxItems) {
		
		Map<TaxProviderPluginInvoker, List<TaxableItem>> providerItems = new HashMap<>();
		
		for (TaxableItem taxableItem : taxItems) {
			
			String taxCode = taxableItem.getTaxCode();
			
			TaxProviderPluginInvoker taxProvider = null;
			
			// the taxable item does not have tax code
			if (StringUtils.isNotBlank(taxCode)) {
				for (TaxCode code : getProvidersByTaxCode().keySet()) {
					
					if (code.matches(taxCode)) {
						taxProvider = getProvidersByTaxCode().get(code);
					}
				}
			}
			
			if (taxProvider == null) {
				taxProvider = getDefaultTaxProvider();
			}
			
			if (providerItems.get(taxProvider) == null) {
				providerItems.put(taxProvider, new ArrayList<>());
			}
			providerItems.get(taxProvider).add(taxableItem);
		}
		return providerItems;
	}
	
	public TaxProviderPluginInvoker getDefaultTaxProvider() {
		return defaultTaxProvider;
	}

	public void setDefaultTaxProvider(final TaxProviderPluginInvoker defaultTaxProvider) {
		this.defaultTaxProvider = defaultTaxProvider;
	}

	public Map<TaxCode, TaxProviderPluginInvoker> getProvidersByTaxCode() {
		return providersByTaxCode;
	}

	public void setProvidersByTaxCode(final Map<TaxCode, TaxProviderPluginInvoker> providersByTaxCode) {
		this.providersByTaxCode = providersByTaxCode;
	}
}
