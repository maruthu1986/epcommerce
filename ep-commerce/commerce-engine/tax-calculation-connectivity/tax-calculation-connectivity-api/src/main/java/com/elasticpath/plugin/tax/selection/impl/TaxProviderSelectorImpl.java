/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.selection.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.domain.TaxItemContainer;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.selection.TaxProviderSelectionStrategy;
import com.elasticpath.plugin.tax.selection.TaxProviderSelector;

/**
 * Implementation of {@link TaxProviderSelector}. The tax provider for purchases is determined using the configured
 * {@link TaxProviderSelectionStrategy}. The provider for returns is the same provider as was used for purchases.
 */
public class TaxProviderSelectorImpl implements TaxProviderSelector {

	private static final Logger LOG = Logger.getLogger(TaxProviderSelectorImpl.class);
	
	private TaxProviderSelectionStrategy taxProviderSelectionStrategy;
	private TaxProviderPluginInvoker returnTaxProvider;
	
	@Override
	public TaxProviderPluginInvoker findProvider(final TaxItemContainer taxItemContainer, final TaxOperationContext taxOperationContext) {
		
		switch (taxOperationContext.getJournalType()) {
		case REVERSAL:
			return getReturnTaxProvider();
		case PURCHASE:
			return getTaxProviderSelectionStrategy().findProvider(taxItemContainer);
		default: 
			LOG.error("Error in tax calculation with wrong tax transaction type: " + taxOperationContext);
			throw new IllegalArgumentException("Error in tax calculation with wrong tax transaction type: " + taxOperationContext);
		}
	}

	@Override
	public TaxProviderPluginInvoker findProviderByName(final String taxProviderName) {
		if (StringUtils.equals(taxProviderName, getReturnTaxProvider().getName())) {
			return getReturnTaxProvider();
		}
		
		return getTaxProviderSelectionStrategy().findProviderByName(taxProviderName);
	}

	public TaxProviderPluginInvoker getReturnTaxProvider() {
		return returnTaxProvider;
	}

	public void setReturnTaxProvider(final TaxProviderPluginInvoker returnTaxProvider) {
		this.returnTaxProvider = returnTaxProvider;
	}

	public TaxProviderSelectionStrategy getTaxProviderSelectionStrategy() {
		return taxProviderSelectionStrategy;
	}

	public void setTaxProviderSelectionStrategy(final TaxProviderSelectionStrategy taxProviderSelectionStrategy) {
		this.taxProviderSelectionStrategy = taxProviderSelectionStrategy;
	}

}
