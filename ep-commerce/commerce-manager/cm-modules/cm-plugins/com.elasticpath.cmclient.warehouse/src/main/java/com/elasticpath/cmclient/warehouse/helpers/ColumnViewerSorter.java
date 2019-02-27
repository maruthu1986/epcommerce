/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.helpers;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * Adds ability to switch sorting direction for managed column.
 */
public class ColumnViewerSorter extends ViewerComparator {
	private static final int ASC = 1;

	private static final int NONE = 0;

	private static final int DESC = -1;

	private int direction;

	private final TableViewerColumn column;

	private final ColumnViewer viewer;

	/**
	 * The constructor.
	 * 
	 * @param viewer column viewer to sort.
	 * @param column column column in viewer to sort.
	 */
	public ColumnViewerSorter(final ColumnViewer viewer, final TableViewerColumn column) {
		this.column = column;
		this.viewer = viewer;
		this.column.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (ColumnViewerSorter.this.viewer.getComparator() == null) {
					setSorter(ColumnViewerSorter.this, ASC);
				} else {
					if (ColumnViewerSorter.this.viewer.getComparator().equals(ColumnViewerSorter.this)) {
						int tdirection = ColumnViewerSorter.this.direction;

						if (tdirection == ASC) {
							setSorter(ColumnViewerSorter.this, DESC);
						} else if (tdirection == DESC) {
							setSorter(ColumnViewerSorter.this, NONE);
						}
					} else {
						setSorter(ColumnViewerSorter.this, ASC);
					}
				}
			}
		});
	}

	/**
	 * Set sorter for column.
	 * 
	 * @param sorter new sorter.
	 * @param direction sorting sorection.
	 */
	public void setSorter(final ColumnViewerSorter sorter, final int direction) {
		if (direction == NONE) {
			column.getColumn().getParent().setSortColumn(null);
			column.getColumn().getParent().setSortDirection(SWT.NONE);
			viewer.setComparator(null);
		} else {
			column.getColumn().getParent().setSortColumn(column.getColumn());
			sorter.direction = direction;

			if (direction == ASC) {
				column.getColumn().getParent().setSortDirection(SWT.DOWN);
			} else {
				column.getColumn().getParent().setSortDirection(SWT.UP);
			}

			if (viewer.getComparator() == sorter) {
				viewer.refresh();
			} else {
				viewer.setComparator(sorter);
			}

		}
	}

	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2) {
		return direction * doCompare(viewer, element1, element2);
	}

	/**
	 * Lexicographically compares two elements.
	 * 
	 * @param viewer current viewer.
	 * @param element1 first element.
	 * @param element2 second element.
	 * @return a negative number if the first element is less than the second element; the value <code>0</code> if the first element is equal to
	 *         the second element; and a positive number if the first element is greater than the second element
	 */
	protected int doCompare(final Viewer viewer, final Object element1, final Object element2) {
		return super.compare(viewer, element1, element2);
	}
}
