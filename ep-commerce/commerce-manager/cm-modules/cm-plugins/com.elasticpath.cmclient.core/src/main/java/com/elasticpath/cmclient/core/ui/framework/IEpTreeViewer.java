/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;

/**
 * Abstraction interface for wrapping the TreeViewer Eclipse class.
 */
public interface IEpTreeViewer {

	/**
	 * Adds a column to the tree viewer.
	 * 
	 * @param headerText the column label
	 * @param initialWidth column initial width in pixels
	 * @return IEpTreeColumn
	 */
	IEpTreeColumn addColumn(String headerText, int initialWidth);

	/**
	 * Adds a column to the tree viewer.
	 * 
	 * @param headerText the column label
	 * @param initialWidth column initial width in pixels
	 * @param style style of the column
	 * @return IEpTreeColumn
	 */
	IEpTreeColumn addColumn(String headerText, int initialWidth, int style);
	
	
	/**
	 * Sets a label provider for the tree viewer. <br>
	 * <i>Note: Adds a striped style effect to the tree viewer.</i>
	 * 
	 * @param labelProvider tree label provider. Usually implementors of this interface should extend<br>
	 *            {@link org.eclipse.jface.viewers.LabelProvider} as well.
	 */
	void setLabelProvider(ITableLabelProvider labelProvider);

	/**
	 * Sets the content provider.
	 * 
	 * @param contentProvider the content provider
	 */
	void setContentProvider(IStructuredContentProvider contentProvider);

	/**
	 * Sets the input to the tree viewer.
	 * 
	 * @param input input object
	 */
	void setInput(Object input);

	/**
	 * Gets the tree viewer.
	 * 
	 * @return Eclipse <code>treeViewer</code>
	 * @see TreeViewer
	 */
	TreeViewer getSwtTreeViewer();

	/**
	 * Gets the tree.
	 * 
	 * @return Eclipse tree
	 * @see Tree
	 */
	Tree getSwtTree();

	/**
	 * Sets the layout data.
	 * 
	 * @param layoutData layout data object
	 */
	void setLayoutData(Object layoutData);

	/**
	 * Returns the number of columns. Usually the tree viewer if not table-tree viewer does not have columns and returns 0.
	 * 
	 * @return columns number
	 */
	int getColumnsCount();

}
