/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.cucumber.tax;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.manager.impl.TaxManagerImpl;

/**
 * Extension of the default Tax Manager that holds a copy of the TaxableItemContainer for test inspection.
 */
public class TestInspectingTaxManagerImpl extends TaxManagerImpl {

	@Inject
	@Named("taxableItemContainerHolder")
	private ScenarioContextValueHolder<TaxableItemContainer> taxableItemContainerHolder;

	/**
	 * Grab the TaxableItemContainer for later inspection by tests.
	 *
	 * @param taxableContainer the TaxableItemContainer to inspect
	 * @return the result from the superclass calculate method
	 */
	@Override
	public TaxDocument calculate(final TaxableItemContainer taxableContainer) {
		taxableItemContainerHolder.set(taxableContainer);
		return super.calculate(taxableContainer);
	}
}