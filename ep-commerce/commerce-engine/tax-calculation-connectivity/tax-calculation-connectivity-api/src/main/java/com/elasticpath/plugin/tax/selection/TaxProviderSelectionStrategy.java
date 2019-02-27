/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.selection;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.domain.TaxItemContainer;

/**
 * Tax provider selection strategy interface.
 */
public interface TaxProviderSelectionStrategy {

	/**
	 * Finds a tax provider based on the content of a given {@link TaxItemContainer}.
	 * 
	 * @param taxItemContainer the taxable container
	 * @return the tax provider
	 */
	TaxProviderPluginInvoker findProvider(TaxItemContainer taxItemContainer);

	/**
	 * Finds a tax provider by name. A <class>TaxCalculationException</class>} is thrown if the requested provider
     * is not found.
	 * 
	 * @param taxProviderName the tax provider name
	 * @return the tax provider
	 */
	TaxProviderPluginInvoker findProviderByName(String taxProviderName);

	/**
	 * Gets the default tax provider.
	 * 
	 * @return the default tax provider
	 */
	TaxProviderPluginInvoker getDefaultTaxProvider();

}
