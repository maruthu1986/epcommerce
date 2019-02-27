/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.builder;

import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxOverrideContext;
import com.elasticpath.plugin.tax.domain.impl.TaxOverrideContextImpl;

/**
 * Builder for {@link TaxOverrideContext}.
 */
public class TaxOverrideContextBuilder {
	
	private final TaxOverrideContextImpl mutableTaxOverrideContext;
	
	/**
     * Constructor.
     */
	public TaxOverrideContextBuilder() {
		mutableTaxOverrideContext = new TaxOverrideContextImpl();
	}
	
	/** Gets a new builder. 
	 * 
	 * @return a new builder.
	 */
	public static TaxOverrideContextBuilder newBuilder() {
		return new TaxOverrideContextBuilder();
	}
		
	/** Gets the instance built by the builder.
	 * 
	 * @return the built instance
	 */
	public TaxOverrideContext build() {
		return mutableTaxOverrideContext;
	}
	
	/** Sets the tax journal type.
	 * 
	 * @param taxJournalType the given tax journal type
	 * @return the builder
	 */
	public TaxOverrideContextBuilder withTaxOverrideJournalType(final TaxJournalType taxJournalType) {
		mutableTaxOverrideContext.setTaxOverrideJournalType(taxJournalType);
		return this;
	}
	
	/** Sets the tax override document id.
	 * 
	 * @param taxOverrideDocumentId the given tax override document reference id
	 * @return the builder
	 */
	public TaxOverrideContextBuilder withTaxOverrideDocumentId(final String taxOverrideDocumentId) {
		mutableTaxOverrideContext.setTaxOverrideDocumentId(taxOverrideDocumentId);
		return this;
	}
}
