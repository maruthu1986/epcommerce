/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse.views.orderreturn;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.editors.EntityEditorInput;
import com.elasticpath.cmclient.warehouse.editors.orderreturn.OrderReturnEditor;
import com.elasticpath.domain.order.OrderReturn;

/**
 * An action used to open the <code>OrderReturn</code> details editor.
 */
public class OpenOrderReturnEditorAction extends Action implements IDoubleClickListener {

	private static final Logger LOG = Logger.getLogger(OpenOrderReturnEditorAction.class);

	private final TableViewer viewer;

	private final IWorkbenchPartSite workbenchPartSite;

	/**
	 * Constructs new <code>Action</code>.
	 * 
	 * @param viewer the table viewer
	 * @param workbenchPartSite the workbench site from the view
	 */
	public OpenOrderReturnEditorAction(final TableViewer viewer, final IWorkbenchPartSite workbenchPartSite) {
		super();
		this.viewer = viewer;
		this.workbenchPartSite = workbenchPartSite;
	}

	@Override
	public void run() {
		LOG.debug("open order return editor action"); //$NON-NLS-1$
		final ISelection selection = this.viewer.getSelection();
		final Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof OrderReturn) {
			showEditor((OrderReturn) obj, workbenchPartSite);
		}

	}

	/**
	 * Show order return editor for the specified <code>OrderReturn</code>.
	 * 
	 * @param orderReturn order return to be shown.
	 * @param workbenchPartSite workbench on which editor will be shown.
	 */
	public static void showEditor(final OrderReturn orderReturn, final IWorkbenchPartSite workbenchPartSite) {
		final IEditorInput editorInput = new EntityEditorInput(getOrderToolTipText(orderReturn), orderReturn.getUidPk(), orderReturn.getClass());
		try {
			LOG.debug(orderReturn.getUidPk() + " OrderReturn should be displayed on page"); //$NON-NLS-1$
			workbenchPartSite.getPage().openEditor(editorInput, OrderReturnEditor.ID_EDITOR);
		} catch (final PartInitException e) {
			LOG.error("Can not open order return editor", e); //$NON-NLS-1$
		}
	}

	/**
	 * Called when a double click user event occurs.
	 * 
	 * @param event double click event
	 */
	@Override
	public void doubleClick(final DoubleClickEvent event) {
		this.run();
	}

	private static String getOrderToolTipText(final OrderReturn orderReturn) {
		return orderReturn.getRmaCode();
	}

}
