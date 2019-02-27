/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.views.customer;

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
public class CustomerSearchTableControlAdapter extends ControlAdapter {

	private final Table table;

	private final TableColumn[] columns;

	private final Composite parent;

	private final int[] columnWidths;

	private int totalColumnWidth;

	/**
	 * Constructs new control adapter.
	 * 
	 * @param parent the parent composite
	 * @param table the SWT table
	 * @param columns the columns
	 * @param columnWidths the columns widths array
	 */
	public CustomerSearchTableControlAdapter(final Composite parent, final Table table, final TableColumn[] columns, final int[] columnWidths) {
		this.parent = parent;
		this.columns = columns.clone();
		this.table = table;
		this.columnWidths = columnWidths.clone();
		for (final int width : this.columnWidths) {
			this.totalColumnWidth += width;
		}
	}

	@Override
	public void controlResized(final ControlEvent event) {
		final Rectangle area = this.parent.getClientArea();
		final Point size = this.table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		final ScrollBar vBar = this.table.getVerticalBar();
		int width = area.width - this.table.computeTrim(0, 0, 0, 0).width - vBar.getSize().x;
		if (size.y > area.height + this.table.getHeaderHeight()) {
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			final Point vBarSize = vBar.getSize();
			width -= vBarSize.x;
		}
		final Point oldSize = this.table.getSize();
		if (oldSize.x > area.width) {
			// table is getting smaller so make the columns
			// smaller first and then resize the table to
			// match the client area width
			this.setColumnsSizes(width);
			this.table.setSize(area.width, area.height);
		} else {
			// table is getting bigger so make the table
			// bigger first and then make the columns wider
			// to match the client area width
			this.table.setSize(area.width, area.height);
			this.setColumnsSizes(width);
		}
	}

	private void setColumnsSizes(final int newTotalWidth) {
		final double percentageChange = (double) newTotalWidth / (double) this.totalColumnWidth;
		int intermediateWidth = 0;
		for (int columnNumber = 0; columnNumber < this.columns.length - 1; columnNumber++) {
			final TableColumn column = this.columns[columnNumber];
			final int newColumnWidth = (int) (this.columnWidths[columnNumber] * percentageChange);
			column.setWidth(newColumnWidth);
			this.columnWidths[columnNumber] = newColumnWidth;
			intermediateWidth += newColumnWidth;
		}
		final TableColumn column = this.columns[this.columns.length - 1];
		column.setWidth(newTotalWidth - intermediateWidth);

	}

}
