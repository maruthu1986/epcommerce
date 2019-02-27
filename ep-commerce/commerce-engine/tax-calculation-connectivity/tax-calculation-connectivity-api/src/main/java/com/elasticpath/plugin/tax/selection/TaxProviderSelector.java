/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.selection;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.domain.TaxItemContainer;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;

/**
 * Tax provider selection interface.
 */
public interface TaxProviderSelector {
	
	/**
	 * Finds a tax provider based on the contents of a given {@link TaxItemContainer} and a given {@link TaxOperationContext}.
     *
	 * @param taxItemContainer the taxable container
	 * @param taxOperationContext the tax operation context
	 * @return the tax provider
	 */
	TaxProviderPluginInvoker findProvider(TaxItemContainer taxItemContainer, TaxOperationContext taxOperationContext);

	/**
	 * Finds a tax provider by name. A <class>TaxCalculationException</class>} is thrown if the requested provider
     * is not found.
	 *
	 * @param taxProviderName the tax provider name
	 * @return taxProvider the tax provider.
	 */
	TaxProviderPluginInvoker findProviderByName(String taxProviderName);
}
