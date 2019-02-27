/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.selection.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;

/**
 * Select tax provider by its name as configured in an injected map of tax providers.
 */
public class MapTaxProviderSelector {

	private Map<String, TaxProviderPluginInvoker> providerMap;
	
	private TaxProviderPluginInvoker defaultTaxProvider;
	
	/**
	 * Finds a tax provider based on a key.
	 *
	 * @param key the tax provider key, could be the tax provider's name
	 * @return the tax provider, or null if not found
	 */
	public TaxProviderPluginInvoker findProviderByKey(final String key) {
		if (StringUtils.isBlank(key) || getProviderMap().get(key) == null) {
			return getDefaultTaxProvider();
		}
		
		return getProviderMap().get(key);
	}

	public TaxProviderPluginInvoker getDefaultTaxProvider() {
		return defaultTaxProvider;
	}

	public void setDefaultTaxProvider(final TaxProviderPluginInvoker defaultTaxProvider) {
		this.defaultTaxProvider = defaultTaxProvider;
	}

	public Map<String, TaxProviderPluginInvoker> getProviderMap() {
		return providerMap;
	}

	public void setProviderMap(final Map<String, TaxProviderPluginInvoker> providerMap) {
		this.providerMap = providerMap;
	}

}
