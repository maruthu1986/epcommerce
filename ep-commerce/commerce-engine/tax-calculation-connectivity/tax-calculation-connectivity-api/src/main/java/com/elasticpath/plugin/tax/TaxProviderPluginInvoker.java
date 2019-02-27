/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax;

import com.elasticpath.plugin.tax.capability.TaxProviderCapability;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;

/**
 * API for invoking tax provider plugins via the capability pattern.
 */
public interface TaxProviderPluginInvoker {

	/**
	 * Provides access to tax provider capabilities not exposed through the base API.
	 *
	 * @param capability the requested capability
	 * @param <T> a class or interface implementing {@link TaxProviderCapability}
	 * @return the capability requested, if available on this instance. Null otherwise.
	 */
	<T extends TaxProviderCapability> T getCapability(Class<T> capability);

	/**
	 * Returns the name of the tax provider.
	 *
	 * @return the name of the tax provider
	 */
	String getName();
	
	/**
	 * Calculates taxes based on the supplied {@link TaxableItemContainer}.
	 * 
	 * @param container the taxable item container
	 * @return a container with all the {@link com.elasticpath.plugin.tax.domain.TaxedItem}s corresponding to 
	 *         the {@link com.elasticpath.plugin.tax.domain.TaxableItem}s 
	 *         from the {@link com.elasticpath.plugin.tax.domain.TaxableItemContainer}
	 */
	TaxedItemContainer calculateTaxes(TaxableItemContainer container);
}
