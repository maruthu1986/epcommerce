/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.commons.pagination.SortingField;

/**
 * The table column sort control.
 */
public class TableColumnSorterControl {
	
	private final Table table;
	private final TableColumnSorterSupporterListener tableColumnSorterSupporter;
	
	/**
	 * The constructor.
	 * 
	 * @param table the table to display sorted data
	 * @param tableColumnSorterSupporter the table column sorter supporter listener
	 */
	public TableColumnSorterControl(final Table table, final TableColumnSorterSupporterListener tableColumnSorterSupporter) {
		this.table = table;
		this.tableColumnSorterSupporter = tableColumnSorterSupporter;
	}

	/**
	 * register column listener.
	 * 
	 * @param tableColumn the table column
	 * @param sortingField the sorting field
	 */
	public void registerColumnListener(final TableColumn tableColumn, final SortingField sortingField) {
		if (sortingField != null) {
			tableColumn.addSelectionListener(new ColumnSelectionListener(sortingField, tableColumn));
		}
	}
	
	/**
	 * The column header is selected.
	 * 
	 * @param sortingField the sorting field
	 * @param tableColumn the table column
	 */
	protected void columnHeaderSelected(final SortingField sortingField, final TableColumn tableColumn) {
		SortingDirection sortingDirection = getSortingDirection(sortingField);
		DirectedSortingField directedSortingField = new DirectedSortingField(sortingField, sortingDirection);
		
		tableColumnSorterSupporter.columnHeaderSelected(directedSortingField);
		
		updateTableSortDirection(tableColumn, sortingDirection);
	}

	/**
	 * Update the small icon on the table column for the sort direction.
	 *  
	 * @param tableColumn the table column
	 * @param sortingDirection the sort direction
	 */
	public void updateTableSortDirection(final TableColumn tableColumn, final SortingDirection sortingDirection) {
		table.setSortDirection(getSWTSortingStyle(sortingDirection));
		table.setSortColumn(tableColumn); // UI update sort column
	}

	private int getSWTSortingStyle(final SortingDirection sortingDirection) {
		if (sortingDirection.equals(SortingDirection.ASCENDING)) {
			return SWT.UP;
		}
		return SWT.DOWN;
	}

	private SortingDirection getSortingDirection(final SortingField sortingField) {
		DirectedSortingField currentSortingField = tableColumnSorterSupporter.getCurrentSortingField();
		if (sortingField.equals(currentSortingField.getSortingField())
				&& currentSortingField.getSortingDirection().equals(SortingDirection.ASCENDING)) {
			return SortingDirection.DESCENDING;
		}
		return SortingDirection.ASCENDING;
	}
	
	/**
	 * The column selection listener.
	 */
	class ColumnSelectionListener implements SelectionListener {
		private final SortingField sortingField;
		private final TableColumn tableColumn;
		/**
		 * The constructor.
		 * 
		 * @param sortingField the sorting field
		 * @param tableColumn the table column
		 */
		ColumnSelectionListener(final SortingField sortingField, final TableColumn tableColumn) {
			this.sortingField = sortingField;
			this.tableColumn = tableColumn;
		}
		@Override
		public void widgetDefaultSelected(final SelectionEvent arg0) {
			// nothing to do	
		}
		@Override
		public void widgetSelected(final SelectionEvent event) {
			columnHeaderSelected(sortingField, tableColumn);
		}
	}
}
