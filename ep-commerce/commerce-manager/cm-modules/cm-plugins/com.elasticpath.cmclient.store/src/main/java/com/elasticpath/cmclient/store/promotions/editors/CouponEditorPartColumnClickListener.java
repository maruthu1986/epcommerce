/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.commons.pagination.SortingField;

/**
 * Listens for clicks on the table columns on {@code CouponEditorPart} in order to manage sorting.
 */
public class CouponEditorPartColumnClickListener implements Listener {

	private final Table table;
	private final CouponPaginationControl<?> paginationControl;
	private final Map<TableColumn, SortingField> columnSortingFieldMap = new HashMap<>();

	/**
	 * Constructor.
	 *
	 * @param table             The table we listen to.
	 * @param paginationControl The control to use for sorting
	 */
	public CouponEditorPartColumnClickListener(final Table table, final CouponPaginationControl<?> paginationControl) {
		this.table = table;
		this.paginationControl = paginationControl;
	}

	@Override
	public void handleEvent(final Event event) {

		final TableColumn eventTableColumn = (TableColumn) event.widget;

		SortingField sortingField = columnSortingFieldMap.get(eventTableColumn);

		SortingDirection sortDirection;
		if (sortingField != null) {
			table.setRedraw(false);
			if (eventTableColumn.equals(table.getSortColumn())) {
				final int currentSortDirection = table.getSortDirection();
				if (currentSortDirection == SWT.UP) {
					sortDirection = SortingDirection.DESCENDING;
					table.setSortDirection(SWT.DOWN);
				} else {
					sortDirection = SortingDirection.ASCENDING;
					table.setSortDirection(SWT.UP);
				}
			} else {
				sortDirection = SortingDirection.ASCENDING;
				table.setSortDirection(SWT.UP);
			}
			table.setSortColumn(eventTableColumn);
			DirectedSortingField sortField = new DirectedSortingField(sortingField, sortDirection);
			paginationControl.sortBy(sortField);
			table.setRedraw(true);
		}
	}

	/**
	 * Registers a table column with a sorting field so that the listener can translate.
	 *
	 * @param swtColumn    The table column.
	 * @param sortingField The sorting field.
	 */
	public void registerSortingField(final TableColumn swtColumn,
									 final SortingField sortingField) {
		columnSortingFieldMap.put(swtColumn, sortingField);
	}

}
