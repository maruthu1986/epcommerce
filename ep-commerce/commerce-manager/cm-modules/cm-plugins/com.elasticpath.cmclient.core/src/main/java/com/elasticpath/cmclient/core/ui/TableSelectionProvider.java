/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * A utility selection provider to connect a table {@link SelectionChangedEvent} with the SelectionService. For example, by
 * adding this selection provider to an editor site's selection service, table row selections will notify the selection
 * service of this event.
 * <pre>
 * TableSelectionProvider tableSelectionProvider = new TableSelectionProvider();
 * epTableViewer.getSwtTableViewer().addSelectionChangedListener(tableSelectionListener);
 * editorPage.getSite().setSelectionProvider(tableSelectionProvider);
 * </pre>
 */
public class TableSelectionProvider implements ISelectionProvider, ISelectionChangedListener {
	
	/**
	 * The selection filter.
	 */
	public interface SelectionFilter {

		/**
		 * Checks whether the selection is applicable.
		 * 
		 * @param selection the selection instance
		 * @param source the selection source
		 * @return true if applicable
		 */
		boolean isApplicable(ISelection selection, Object source);

	}

	private final List<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();
	private ISelection selection = StructuredSelection.EMPTY;
	private SelectionFilter selectionFilter;
	
	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	/**
	 * Provides the current selection to selection change listeners.
	 * 
	 * @return the current selection, null if nothing selected.
	 */
	public ISelection getSelection() {
		return selection;
	}

	@Override
	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setSelection(final ISelection selection) {
		this.selection = selection;
		
		// send events to each selection change listener on this object
		final SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(this, selection);
		Object[] listenersArray = listeners.toArray();
		for (int i = 0; i < listenersArray.length; i++) {
			final ISelectionChangedListener selectionChangedListener = (ISelectionChangedListener) listenersArray[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					selectionChangedListener.selectionChanged(selectionChangedEvent);
				}
			});
		}
	}

	/**
	 */
	private boolean isSelectionApplicable(final ISelection selection, final Object source) {
		return selectionFilter == null || selectionFilter.isApplicable(selection, source);
	}

	/**
	 * Receives selection change events from tables.
	 * 
	 * @param tableSelectionChangeEvent the selection change event fired by table viewer
	 */
	public void selectionChanged(final SelectionChangedEvent tableSelectionChangeEvent) {
		if (isSelectionApplicable(tableSelectionChangeEvent.getSelection(), tableSelectionChangeEvent.getSource())) {
			setSelection(tableSelectionChangeEvent.getSelection());
		} else {
			setSelection(StructuredSelection.EMPTY);
		}
	}

	/**
	 * Sets a selection filter.
	 * 
	 * @param selectionFilter the selection filter instance
	 */
	public void setSelectionFilter(final SelectionFilter selectionFilter) {
		this.selectionFilter = selectionFilter;
	}
	
};
