/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain;

import java.util.List;

/**
 * Interface for container of {@link TaxedItem}s.
 */
public interface TaxedItemContainer extends TaxItemContainer {

	/**
     * Gets list of {@link TaxedItem}s.
     *
	 * @return a list of taxed items
	 */
	@Override
	List<? extends TaxedItem> getItems();
	
}
