/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;

/**
 * Wraps the Eclipse TableViewer.
 */
public interface IEpTableViewer {

	/**
	 * Adds new table column to the table.
	 * 
	 * @param headerText header text
	 * @param initialWidth column width
	 * @param type the type of the table column: TYPE_RADIO, TYPE_CHECKBOX, TYPE_NONE
	 * @return EP table column.
	 * @see IEpTableColumn#TYPE_CHECKBOX
	 * @see IEpTableColumn#TYPE_RADIO
	 * @see IEpTableColumn#TYPE_NONE
	 */
	IEpTableColumn addTableColumn(String headerText, int initialWidth, int type);

	/**
	 * Adds new table column. Header alignment is LEFT.
	 * 
	 * @param headerText the header text
	 * @param initialWidth the column width
	 * @return EP table column
	 */
	IEpTableColumn addTableColumn(String headerText, int initialWidth);

	/**
	 * Gets the table viewer.
	 * 
	 * @return Eclipse <code>TableViewer</code>
	 * @see TableViewer
	 */
	TableViewer getSwtTableViewer();

	/**
	 * Gets the table.
	 * 
	 * @return Eclipse Table
	 * @see Table
	 */
	Table getSwtTable();

	/**
	 * Sets a label provider for the table viewer. <br>
	 * <i>Note: Adds a striped style effect to the table viewer.</i>
	 * 
	 * @param labelProvider table label provider. Usually implementors of this interface should extend
	 *            {@link org.eclipse.jface.viewers.LabelProvider} as well.
	 */
	void setLabelProvider(ITableLabelProvider labelProvider);

	/**
	 * Sets the content provider.
	 * 
	 * @param contentProvider the content provider
	 */
	void setContentProvider(IStructuredContentProvider contentProvider);

	/**
	 * Sets the input to the table viewer.
	 * 
	 * @param input input object
	 */
	void setInput(Object input);

	/**
	 * Returns the number of columns in the table.
	 * 
	 * @return columns number
	 */
	int getColumnsCount();

	/**
	 * Sets the layout data of the list viewer.
	 * 
	 * @param layoutData the layout data object
	 */
	void setLayoutData(Object layoutData);

	/**
	 * Set whether editing is enabled.
	 * 
	 * @param enableEditMode true if edit mode is enabled.
	 */
	void setEnableEditMode(boolean enableEditMode);

	
}
