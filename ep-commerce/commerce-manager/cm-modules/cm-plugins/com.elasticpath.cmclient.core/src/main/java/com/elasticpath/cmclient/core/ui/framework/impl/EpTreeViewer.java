/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;

import com.elasticpath.cmclient.core.ui.framework.IEpTreeColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTreeViewer;

/**
 * EP tree viewer implementation.
 */
public class EpTreeViewer implements IEpTreeViewer {

	private final TreeViewer swtTreeViewer;

	private int columnsCount;

	/**
	 * Constructs new EP tree viewer.
	 * 
	 * @param swtTreeViewer the original tree viewer
	 */
	public EpTreeViewer(final TreeViewer swtTreeViewer) {
		this.swtTreeViewer = swtTreeViewer;
		// turn off the header mode by default. if there is more than 1 column it will be switched on
		swtTreeViewer.getTree().setHeaderVisible(false);
	}

	/**
	 * Sets the layout data.
	 * 
	 * @param layoutData layout data object
	 */
	public void setLayoutData(final Object layoutData) {
		this.swtTreeViewer.getTree().setLayoutData(layoutData);
	}

	/**
	 * Adds a column to the tree viewer. The first is the tree.
	 * 
	 * @param headerText the label of the column
	 * @param initialWidth the column width
	 * @return IEpTreeColumn
	 */
	public IEpTreeColumn addColumn(final String headerText, final int initialWidth) {
		if (this.columnsCount >= 1 && !this.swtTreeViewer.getTree().getHeaderVisible()) {
			// turn on the header mode as there are two and more columns
			this.swtTreeViewer.getTree().setHeaderVisible(true);
		}
		this.columnsCount++;
		return new EpTreeColumn(this, headerText, initialWidth);
	}
	
	/**
	 * Adds a column to the tree viewer. The first is the tree.
	 * 
	 * @param headerText the label of the column
	 * @param initialWidth the column width
	 * @param style the style of the column
	 * @return IEpTreeColumn
	 */
	public IEpTreeColumn addColumn(final String headerText, final int initialWidth, final int style) {
		if (this.columnsCount >= 1 && !this.swtTreeViewer.getTree().getHeaderVisible()) {
			// turn on the header mode as there are two and more columns
			this.swtTreeViewer.getTree().setHeaderVisible(true);
		}
		this.columnsCount++;
		return new EpTreeColumn(this, headerText, initialWidth, style);
	}
	

	/**
	 * Gets the original Tree object.
	 * 
	 * @return Tree
	 */
	public Tree getSwtTree() {
		return this.swtTreeViewer.getTree();
	}

	/**
	 * Gets the tree viewer.
	 * 
	 * @return TreeViewer
	 */
	public TreeViewer getSwtTreeViewer() {
		return this.swtTreeViewer;
	}

	/**
	 * Sets the content provider.
	 * 
	 * @param contentProvider IStructuredContentProvider
	 */
	public void setContentProvider(final IStructuredContentProvider contentProvider) {
		this.swtTreeViewer.setContentProvider(contentProvider);
	}

	/**
	 * Sets the input.
	 * 
	 * @param input the input object
	 */
	public void setInput(final Object input) {
		this.swtTreeViewer.setInput(input);
	}

	/**
	 * Sets label provider.
	 * 
	 * @param labelProvider the label provider
	 */
	public void setLabelProvider(final ITableLabelProvider labelProvider) {
		this.swtTreeViewer.setLabelProvider(labelProvider);
	}

	/**
	 * Returns the number of columns. Usually the tree viewer if not table-tree viewer does not have columns and returns 0.
	 * 
	 * @return columns number
	 */
	public int getColumnsCount() {
		return this.columnsCount;
	}

}
