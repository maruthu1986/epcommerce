/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import com.elasticpath.cmclient.core.ui.framework.IEpListViewer;

/**
 * Implementation of IEpListViewer.
 */
public class EpListViewer implements IEpListViewer {

	private final Table table;

	private final TableViewer tableViewer;

	private final TableViewerColumn listColumn;

	private final boolean editMode;

	private final Composite parentComposite;

	/**
	 * Constructs the viewer.
	 * 
	 * @param tableViewer the original table viewer
	 * @param parentComposite the parent composite for resizing the column
	 * @param editMode edit mode flag
	 */
	public EpListViewer(final TableViewer tableViewer, final Composite parentComposite, final boolean editMode) {
		this.tableViewer = tableViewer;
		this.table = tableViewer.getTable();
		this.editMode = editMode;
		this.parentComposite = parentComposite;

		this.listColumn = new TableViewerColumn(tableViewer, SWT.LEFT);
		// this.listColumn.getColumn().setWidth(50);

		new EpListViewerControlAdapter(tableViewer, parentComposite);
	}

	/**
	 * Gets the table.
	 * 
	 * @return Eclipse Table
	 * @see Table
	 */
	public Table getSwtTable() {
		return this.table;
	}

	/**
	 * Gets the table viewer.
	 * 
	 * @return Eclipse <code>TableViewer</code>
	 * @see TableViewer
	 */
	public TableViewer getSwtTableViewer() {
		return this.tableViewer;
	}

	/**
	 * Sets the content provider.
	 * 
	 * @param contentProvider the content provider
	 */
	public void setContentProvider(final IStructuredContentProvider contentProvider) {
		this.tableViewer.setContentProvider(contentProvider);
	}

	/**
	 * Sets the input.
	 * 
	 * @param input Object
	 */
	public void setInput(final Object input) {
		this.tableViewer.setInput(input);
	}

	/**
	 * Sets the label provider.
	 * 
	 * @param labelProvider ColumnLabelProvider
	 */
	public void setLabelProvider(final ILabelProvider labelProvider) {
		if (this.editMode) {
			this.listColumn.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public Image getImage(final Object element) {
					return labelProvider.getImage(element);
				}

				@Override
				public String getText(final Object element) {
					return labelProvider.getText(element);
				}

			});
		} else {
			this.tableViewer.setLabelProvider(new EpTableColoringLabelProvider(new InternalListViewerLabelProvider(labelProvider), 1));
		}
	}

	/**
	 * Adds editing support for the only column of this list(table).
	 * 
	 * @param support EditingSupport
	 */
	public void setEditingSupport(final EditingSupport support) {
		this.listColumn.setEditingSupport(support);
	}

	/**
	 * Internal implementation for mapping ILabelProvider and ITableLabelProvider.
	 */
	private class InternalListViewerLabelProvider extends LabelProvider implements ITableLabelProvider {

		private final ILabelProvider labelProvider;

		/**
		 * @param labelProvider
		 */
		InternalListViewerLabelProvider(final ILabelProvider labelProvider) {
			this.labelProvider = labelProvider;
		}

		/**
		 * Gets the image of the list's column.
		 * 
		 * @param element
		 * @param columnIndex
		 * @return Image
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {
			return this.labelProvider.getImage(element);
		}

		/**
		 * Gets the text of the list's column.
		 * 
		 * @param element
		 * @param columnIndex
		 * @return String
		 */
		public String getColumnText(final Object element, final int columnIndex) {
			return this.labelProvider.getText(element);
		}
	}

	/**
	 * Sets the layout data.
	 * 
	 * @param layoutData the layout data object
	 */
	public void setLayoutData(final Object layoutData) {
		this.parentComposite.setLayoutData(layoutData);
	}

}
