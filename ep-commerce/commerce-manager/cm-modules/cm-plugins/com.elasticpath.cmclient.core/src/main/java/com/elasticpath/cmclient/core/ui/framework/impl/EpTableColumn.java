/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;

/**
 * Represents a column in a table.
 */
public class EpTableColumn implements IEpTableColumn {

	private final TableViewerColumn tableColumn;

	private final String headerText;

	private final EpTableViewer epTableViewer;

	private final int type;

	private EditingSupport editingSupport;

	/**
	 * Constructs new column.
	 * 
	 * @param epTableViewer EP table viewer
	 * @param type the type of the column style - RADIO or CHECKBOX
	 * @param headerText column's header text
	 * @param initialWidth column width
	 */
	public EpTableColumn(final EpTableViewer epTableViewer, final int type, final String headerText, final int initialWidth) {
		this.epTableViewer = epTableViewer;
		this.type = type;
		this.tableColumn = new TableViewerColumn(epTableViewer.getSwtTableViewer(), SWT.LEFT);
		this.tableColumn.getColumn().setWidth(initialWidth);
		this.tableColumn.getColumn().setText(headerText);
		this.headerText = headerText;

		//If width = -1, then set to auto-width
		if (initialWidth == -1) {
			this.tableColumn.getColumn().setWidth(0);
			this.tableColumn.getColumn().pack();
		}

	}

	/**
	 * Gets the original TableColumn object.
	 * 
	 * @return TableColumn
	 */
	public TableColumn getSwtTableColumn() {
		return this.tableColumn.getColumn();
	}

	/**
	 * @return the headerText
	 */
	protected String getHeaderText() {
		return this.headerText;
	}

	/**
	 * Sets the cell editor of this column.
	 * 
	 * @param editingSupport implementation of Eclipse cell editor
	 * @see EditingSupport
	 */
	public void setEditingSupport(final EditingSupport editingSupport) {
		this.editingSupport = editingSupport;
		if (epTableViewer.isEnableEditMode()) {
			this.tableColumn.setEditingSupport(this.editingSupport);
		}
	}

	@Override
	public EditingSupport getEditingSupport() {
		return this.editingSupport;
	}

	/**
	 * Sets the label provider to the table column.
	 * 
	 * @param labelProvider the label provider
	 */
	public void setLabelProvider(final ColumnLabelProvider labelProvider) {
		this.tableColumn.setLabelProvider(new EpTableLabelProviderWrapper(this.epTableViewer.getSwtTable().getShell(), this.type, this.epTableViewer
				.isEnableEditMode(), labelProvider));
	}

}
