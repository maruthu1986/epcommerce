/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;

import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;

/**
 * Implements the EP table viewer interface.
 */
public class EpTableViewer implements IEpTableViewer {

	private final TableViewer viewer;

	private final List<EpTableColumn> tableColumns;

	private boolean enableEditMode;

	/**
	 * Constructs new EP table viewer.
	 * 
	 * @param viewer TableViewer implementation
	 * @param enableEditMode enables/disables edit mode look and feel
	 */
	public EpTableViewer(final TableViewer viewer, final boolean enableEditMode) {
		this.viewer = viewer;
		this.enableEditMode = enableEditMode;
		this.tableColumns = new ArrayList<EpTableColumn>();
	}

	/**
	 * Adds new table column to the table.
	 * 
	 * @param headerText header text
	 * @param initialWidth column width
	 * @param type the type of the table column: NONE, TEXT, RADIO, CHECK, COMBO
	 * @return EP table column. One of the constants defined in <code>IEpTableColumn</code> - CENTER, LEFT, RIGHT
	 */
	public IEpTableColumn addTableColumn(final String headerText, final int initialWidth, final int type) {
		final EpTableColumn column = new EpTableColumn(this, type, headerText, initialWidth);
		this.tableColumns.add(column);
		this.updateColumnProperties();
		return column;
	}

	/**
	 * Adds a new table column with left text alignment.
	 * 
	 * @param headerText text of the header
	 * @param initialWidth column width
	 * @return EP table column
	 */
	public IEpTableColumn addTableColumn(final String headerText, final int initialWidth) {
		return this.addTableColumn(headerText, initialWidth, IEpTableColumn.TYPE_NONE);
	}

	/**
	 *
	 */
	private void updateColumnProperties() {
		final List<String> columnProps = new ArrayList<String>();
		for (final EpTableColumn column : this.tableColumns) {
			columnProps.add(column.getHeaderText());
		}
		this.viewer.setColumnProperties(columnProps.toArray(new String[this.tableColumns.size()]));
	}

	/**
	 * Gets the original Table object.
	 * 
	 * @return Table
	 */
	public Table getSwtTable() {
		return this.viewer.getTable();
	}

	/**
	 * Gets the original table viewer object.
	 * 
	 * @return TableViewer
	 */
	public TableViewer getSwtTableViewer() {
		return this.viewer;
	}

	/**
	 * Adds custom label provider.
	 * 
	 * @param labelProvider the label provider.
	 */
	public void setLabelProvider(final ITableLabelProvider labelProvider) {
		if (this.enableEditMode) {
			this.viewer.setLabelProvider(labelProvider); // do not set striped style of the table if in edit mode
		} else {
			this.viewer.setLabelProvider(new EpTableColoringLabelProvider(labelProvider, this.getColumnsCount()));
		}
	}

	/**
	 * Sets the content provider.
	 * 
	 * @param contentProvider the content provider
	 */
	public void setContentProvider(final IStructuredContentProvider contentProvider) {
		this.viewer.setContentProvider(contentProvider);
	}

	/**
	 * Sets the input to the table viewer.
	 * 
	 * @param input input object
	 */
	public void setInput(final Object input) {
		this.viewer.setInput(input);
	}

	/**
	 * Returns the number of columns.
	 * 
	 * @return integer
	 */
	public int getColumnsCount() {
		return this.tableColumns.size();
	}

	/**
	 * @return the enableEditMode
	 */
	protected boolean isEnableEditMode() {
		return this.enableEditMode;
	}

	/**
	 * Sets the layout data of the list viewer.
	 * 
	 * @param layoutData the layout data object
	 */
	public void setLayoutData(final Object layoutData) {
		this.getSwtTable().setLayoutData(layoutData);
	}
	
	/**
	 * Set whether editing is enabled.
	 * 
	 * @param enableEditMode true if edit mode is enabled.
	 */
	public void setEnableEditMode(final boolean enableEditMode) {
		this.enableEditMode = enableEditMode;
	}

}
