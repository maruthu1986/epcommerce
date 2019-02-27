/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;

/**
 * Abstract customization of <code>EditingSupport</code> for inline table editing.
 */
public abstract class AbstractInlineEditingSupport extends EditingSupport {
	private final DataBindingContext bindingContext;

	private final TextCellEditor cellEditor;

	private EpValueBinding binding;

	private final ColumnViewerEditorActivationListenerHelper activationListener = new ColumnViewerEditorActivationListenerHelper();

	/**
	 * @param viewer the column viewer
	 * @param bindingContext the data binding context
	 */
	public AbstractInlineEditingSupport(final ColumnViewer viewer, final DataBindingContext bindingContext) {
		super(viewer);
		this.cellEditor = new TextCellEditor((Composite) viewer.getControl());
		this.bindingContext = bindingContext;
	}

	@Override
	protected boolean canEdit(final Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		return cellEditor;
	}

	@Override
	protected Object getValue(final Object element) {
		// not needed
		return null;
	}

	@Override
	protected void setValue(final Object element, final Object value) {
		// not needed
	}

	@Override
	protected abstract void initializeCellEditorValue(final CellEditor cellEditor, final ViewerCell cell);

	@Override
	protected void saveCellEditorValue(final CellEditor cellEditor, final ViewerCell cell) {
		binding.getBinding().updateTargetToModel();
		getViewer().update(cell.getElement(), null);
	}

	/**
	 * @return the binding
	 */
	public EpValueBinding getBinding() {
		return binding;
	}

	/**
	 * @param binding the binding to set
	 */
	public void setBinding(final EpValueBinding binding) {
		this.binding = binding;
	}

	/**
	 * @return the activationListener
	 */
	public ColumnViewerEditorActivationListenerHelper getActivationListener() {
		return activationListener;
	}

	/**
	 * @return the bindingContext
	 */
	public DataBindingContext getBindingContext() {
		return bindingContext;
	}

	/**
	 * Simple helper class for {@link AbstractInlineEditingSupport}.
	 */
	public class ColumnViewerEditorActivationListenerHelper extends ColumnViewerEditorActivationListener {

		@Override
		public void afterEditorActivated(final ColumnViewerEditorActivationEvent event) {
			// do nothing
		}

		@Override
		public void afterEditorDeactivated(final ColumnViewerEditorDeactivationEvent event) {
			if (binding != null) {
				EpControlBindingProvider.removeEpValueBinding(bindingContext, binding);
				binding.getBinding().dispose();
				binding = null;
			}
			
			getBindingContext().updateTargets();
		}

		@Override
		public void beforeEditorActivated(final ColumnViewerEditorActivationEvent event) {
			// do nothing
		}

		@Override
		public void beforeEditorDeactivated(final ColumnViewerEditorDeactivationEvent event) {
			// do nothing
		}
	}
}