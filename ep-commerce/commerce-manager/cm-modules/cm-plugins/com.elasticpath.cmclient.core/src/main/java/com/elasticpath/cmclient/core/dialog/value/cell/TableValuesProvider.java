/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

/**
 * Provides values for drop box selection editors such as {@link AdvancedTableCellEditor}.
 */
public interface TableValuesProvider {
	
	/**
	 * @param cellElement value from current cell {@link org.eclipse.jface.viewers.CellEditor#getValue()}
	 * @return values for selection.
	 */
	Object[] getValues(Object cellElement);

}
