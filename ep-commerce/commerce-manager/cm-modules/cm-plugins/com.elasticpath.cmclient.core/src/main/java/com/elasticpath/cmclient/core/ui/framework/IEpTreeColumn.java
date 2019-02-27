/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewerColumn;

/**
 * Represents a column of the tree viewer. Multiple columns make the tree viewer like a table-tree viewer.
 */
public interface IEpTreeColumn {

	/**
	 * Sets the cell editor for this column.
	 * 
	 * @param editingSupport the Eclipse cell editor
	 * @see EditingSupport
	 */
	void setEditingSupport(EditingSupport editingSupport);

	/**
	 * Sets a column label provider.
	 * 
	 * @param labelProvider the label provider
	 */
	void setLabelProvider(ColumnLabelProvider labelProvider);

	/**
	 * Gets the original tree viewer column.
	 * 
	 * @return TreeViewerColumn
	 * @see TreeViewerColumn
	 */
	TreeViewerColumn getSwtTreeViewerColumn();
}
