/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Control adapter for stretching the columns to the table width.
 */
public class EpListViewerControlAdapter extends ControlAdapter {

	/**
	 * 
	 */
	private static final int MARGIN = 5;

	private final Table table;

	private final TableColumn[] columns;

	private final Composite parent;

	/**
	 * Constructs new control adapter.
	 * 
	 * @param viewer TableViewer
	 * @param parent the parent composite
	 */
	public EpListViewerControlAdapter(final TableViewer viewer, final Composite parent) {
		this.parent = parent;
		this.columns = viewer.getTable().getColumns().clone();
		this.table = viewer.getTable();
		this.table.addControlListener(this);
	}

	@Override
	public void controlResized(final ControlEvent event) {
		final Rectangle area = this.parent.getClientArea();
		final Point size = this.table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		final ScrollBar vBar = this.table.getVerticalBar();
		int width = area.width - this.table.computeTrim(0, 0, 0, 0).width; // - vBar.getSize().x;
		if (size.y > area.height + this.table.getHeaderHeight()) {
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			final Point vBarSize = vBar.getSize();
			width -= vBarSize.x;
		}
		// final Point oldSize = this.table.getSize();
		// if (oldSize.x > area.width) {
		// table is getting smaller so make the columns
		// smaller first and then resize the table to
		// match the client area width
		this.setColumnsSizes(width + MARGIN);
		// this.table.setSize(area.width - 5, area.height - 5);
		// } else {
		// table is getting bigger so make the table
		// bigger first and then make the columns wider
		// to match the client area width
		// this.table.setSize(area.width - 5, area.height - 5);
		// this.setColumnsSizes(width + MARGIN);
		// }
	}

	/**
	 * @param columns
	 * @param newTotalWidth
	 */
	private void setColumnsSizes(final int newTotalWidth) {
		// support only list (table with one column)
		if (this.columns.length == 1) {
			final TableColumn column = this.columns[0];
			column.setWidth(newTotalWidth);
		}
	}

}
