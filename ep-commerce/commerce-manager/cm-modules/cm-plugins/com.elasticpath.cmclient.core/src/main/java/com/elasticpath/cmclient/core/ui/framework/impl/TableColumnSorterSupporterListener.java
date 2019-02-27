/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import com.elasticpath.commons.pagination.DirectedSortingField;

/**
 * The inteface for table column sorter supporter listener.
 */
public interface TableColumnSorterSupporterListener {
	
	/**
	 * The method triggered when the column header is selected.
	 * 
	 * @param directedSortingField the directed sorting field
	 */
	void columnHeaderSelected(DirectedSortingField directedSortingField);

	/**
	 * Get the current sorting field.
	 * 
	 * @return the current sorting field
	 */
	DirectedSortingField getCurrentSortingField();

}
