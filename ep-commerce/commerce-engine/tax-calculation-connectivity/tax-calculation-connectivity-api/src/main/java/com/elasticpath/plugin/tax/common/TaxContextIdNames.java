/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.common;

/**
 *  Tax bean id constants.
 *
 */
public final class TaxContextIdNames {
	
	/** bean id for implementation of {@link com.elasticpath.plugin.tax.domain.impl.MutableTaxedItemContainer}. */
	public static final String MUTABLE_TAXED_ITEM_CONTAINER = "mutableTaxedItemContainer";
	
	/** bean id for implementation of {@link com.elasticpath.plugin.tax.domain.impl.MutableTaxedItem}. */
	public static final String MUTABLE_TAXED_ITEM = "mutableTaxedItem";
	
	/** bean id for implementation of {@link com.elasticpath.plugin.tax.domain.impl.MutableTaxableItemContainer}. */
	public static final String MUTABLE_TAXABLE_ITEM_CONTAINER = "mutableTaxableItemContainer";
	
	/** bean id for implementation of {@link com.elasticpath.plugin.tax.domain.impl.TaxableItemImpl}. */
	public static final String TAXABLE_ITEM = "taxableItem";

	/** bean id for implementation of {@link com.elasticpath.plugin.tax.rate.dto.MutableTaxRateDescriptorResult}. */
	public static final String MUTABLE_TAX_RATE_DESCRIPTOR_RESULT = "mutableTaxRateDescriptorResult";
	
	/** bean id for implementation of {@link com.elasticpath.plugin.tax.rate.dto.MutableTaxRateDescriptor}. */
	public static final String MUTABLE_TAX_RATE_DESCRIPTOR = "mutableTaxRateDescriptor";
	
	/** bean id for implementation of {@link com.elasticpath.plugin.tax.rate.impl.TaxInclusiveRateApplier}. */
	public static final String TAX_INCLUSIVE_RATE_APPLIER = "taxInclusiveRateApplier";
	
	/** bean id for implementation of {@link com.elasticpath.plugin.tax.rate.impl.TaxExclusiveRateApplier}. */
	public static final String TAX_EXCLUSIVE_RATE_APPLIER = "taxExclusiveRateApplier";
	
	/** bean id for implementation of {@link com.elasticpath.plugin.tax.resolver.TaxRateDescriptorResolver}. */
	public static final String TAX_RATE_DESCRIPTOR_RESOLVER = "taxRateDescriptorResolver";

	/** bean id for implementation of {@link com.elasticpath.plugin.tax.resolver.TaxDocumentResolver}. */
	public static final String TAX_DOCUMENT_RESOLVER = "taxDocumentResolver";
	
	/** bean id for implementation of {@link com.elasticpath.plugin.tax.domain.impl.MutableTaxDocument}. */
	public static final String MUTABLE_TAX_DOCUMENT = "mutableTaxDocument";

	private TaxContextIdNames() {
		// Do not instantiate this class
	}
}
