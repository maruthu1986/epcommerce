/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.views.customer;

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

import com.elasticpath.cmclient.fulfillment.editors.customer.CustomerDetailsEditor;
import com.elasticpath.cmclient.fulfillment.editors.customer.CustomerDetailsEditorInput;
import com.elasticpath.domain.customer.Customer;

/**
 * An action used to open the customer details editor.
 */
public class OpenCustomerDetailsEditorAction extends Action implements IDoubleClickListener {

	private static final Logger LOG = Logger.getLogger(OpenCustomerDetailsEditorAction.class);

	private final TableViewer viewer;

	private final IWorkbenchPartSite workbenchPartSite;

	/**
	 * Constructs new <code>Action</code>.
	 * 
	 * @param viewer the table viewer
	 * @param workbenchPartSite the workbench site from the view
	 */
	public OpenCustomerDetailsEditorAction(final TableViewer viewer, final IWorkbenchPartSite workbenchPartSite) {
		this.viewer = viewer;
		this.workbenchPartSite = workbenchPartSite;
	}

	@Override
	public void run() {
		final ISelection selection = this.viewer.getSelection();
		final Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof Customer) {
			final Customer customer = (Customer) obj;
			final IEditorInput editorInput = new CustomerDetailsEditorInput(customer.getUidPk());

			try {
				this.workbenchPartSite.getPage().openEditor(editorInput, CustomerDetailsEditor.ID_EDITOR);
			} catch (final PartInitException e) {
				LOG.error("Can not open customer details editor", e); //$NON-NLS-1$
			}

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

}
