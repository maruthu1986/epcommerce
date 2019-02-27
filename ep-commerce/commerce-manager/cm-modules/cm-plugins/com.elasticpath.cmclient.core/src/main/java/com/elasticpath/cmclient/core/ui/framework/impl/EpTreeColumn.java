/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;

import com.elasticpath.cmclient.core.ui.framework.IEpTreeColumn;

/**
 * EP tree column implementation.
 */
public class EpTreeColumn implements IEpTreeColumn {

	private final TreeViewerColumn treeViewerColumn;

	/**
	 * Constructs new tree column.
	 * 
	 * @param epTreeViewer EP tree viewer
	 * @param headerText the column label
	 * @param initialWidth the width of the column
	 */
	public EpTreeColumn(final EpTreeViewer epTreeViewer, final String headerText, final int initialWidth) {
		this(epTreeViewer, headerText, initialWidth, SWT.LEFT);
	}

	/**
	 * Constructs new tree column.
	 * 
	 * @param epTreeViewer EP tree viewer
	 * @param headerText the column label
	 * @param initialWidth the width of the column
	 * @param style the style of the column
	 */
	public EpTreeColumn(final EpTreeViewer epTreeViewer, final String headerText, final int initialWidth, final int style) {
		this.treeViewerColumn = new TreeViewerColumn(epTreeViewer.getSwtTreeViewer(), style);
		this.treeViewerColumn.getColumn().setText(headerText);
		this.treeViewerColumn.getColumn().setWidth(initialWidth);
	}
	
	
	/**
	 * Gets the Eclipse tree column.
	 * 
	 * @return TreeColumn
	 */
	public TreeColumn getSwtTreeColumn() {
		return this.treeViewerColumn.getColumn();
	}

	/**
	 * Adds editing support to this column.
	 * 
	 * @param editingSupport EditingSupport
	 */
	public void setEditingSupport(final EditingSupport editingSupport) {
		this.treeViewerColumn.setEditingSupport(editingSupport);
	}

	/**
	 * Sets label provider.
	 * 
	 * @param labelProvider column label provider
	 */
	public void setLabelProvider(final ColumnLabelProvider labelProvider) {
		this.treeViewerColumn.setLabelProvider(labelProvider);
	}

	/**
	 * Gets the original tree viewer column.
	 * 
	 * @return TreeViewerColumn
	 * @see TreeViewerColumn
	 */
	public TreeViewerColumn getSwtTreeViewerColumn() {
		return this.treeViewerColumn;
	}

}
