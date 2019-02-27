/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain;


/**
 * Interface defining a container for {@link TaxableItem}s and associated context information.
 */
public interface TaxableItemContainer extends TaxItemContainer {
	
	/**
	 * Gets the {@link TaxOperationContext}.
	 * 
	 * @return the tax operation context
	 */
	TaxOperationContext getTaxOperationContext();

}
