/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.plugin.tax.capability;

import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;

/**
 * Capability that should be implmented by plugins that support tax calculations.
 */
public interface TaxCalculationCapability extends TaxProviderCapability {

	/**
	 * Calculates taxes based on the supplied {@link com.elasticpath.plugin.tax.domain.TaxableItemContainer}.
	 *
	 * @param container the taxable item container
	 * @return a container with all the {@link com.elasticpath.plugin.tax.domain.TaxedItem}s corresponding to
	 *         the {@link com.elasticpath.plugin.tax.domain.TaxableItem}s
	 *         from the {@link com.elasticpath.plugin.tax.domain.TaxableItemContainer}
	 */
	TaxedItemContainer calculate(TaxableItemContainer container);
}
