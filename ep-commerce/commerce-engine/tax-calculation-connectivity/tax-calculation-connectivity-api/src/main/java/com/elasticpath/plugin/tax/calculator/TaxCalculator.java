/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.calculator;

import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;
import com.elasticpath.plugin.tax.resolver.TaxOperationResolvers;

/**
 * Interface for calculating taxes for a {@link TaxableItemContainer}.
 */
public interface TaxCalculator {

	/**
	 * Calculates taxes based on the supplied {@link TaxableItemContainer}.
	 *
	 * @param container the taxable item container
	 * @param taxOperationResolvers the tax operation resolvers required by tax provider plugins to retrieve tax
	 *                              information from the platform.
	 * @return a container with all the {@link com.elasticpath.plugin.tax.domain.TaxedItem}s corresponding to
	 *         the {@link com.elasticpath.plugin.tax.domain.TaxableItem}s
	 *         from the {@link com.elasticpath.plugin.tax.domain.TaxableItemContainer}
	 */
	TaxedItemContainer calculate(TaxableItemContainer container, TaxOperationResolvers taxOperationResolvers); 
}
