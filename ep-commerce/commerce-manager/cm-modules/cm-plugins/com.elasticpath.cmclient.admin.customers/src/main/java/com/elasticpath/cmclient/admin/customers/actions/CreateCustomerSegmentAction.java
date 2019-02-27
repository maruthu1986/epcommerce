/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.admin.customers.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.admin.customers.AdminCustomersMessages;
import com.elasticpath.cmclient.admin.customers.views.CustomerSegmentEditor;
import com.elasticpath.cmclient.admin.customers.views.CustomerSegmentEditorInput;
import com.elasticpath.cmclient.admin.customers.views.CustomerSegmentListView;
import com.elasticpath.domain.customer.CustomerGroup;

/**
 * Action to create a new customer segment.
 */
public class CreateCustomerSegmentAction extends Action {

	private final CustomerSegmentListView listView;

	/**
	 * Creates the create customer segment action.
	 *
	 * @param listView the customer segment view
	 * @param text the action's text
	 * @param imageDescriptor the action's image
	 */
	public CreateCustomerSegmentAction(final CustomerSegmentListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	/**
	 * Run the create new customer group action.
	 */
	@Override
	public void run() {
		final CustomerSegmentEditorInput editorInput = new CustomerSegmentEditorInput(
				AdminCustomersMessages.get().CustomerSegmentEditor_NewSegmentName, 0, CustomerGroup.class);

		try {
			listView.getSite().getWorkbenchWindow().getActivePage().openEditor(editorInput, CustomerSegmentEditor.ID_EDITOR);
		} catch (PartInitException e) {
			MessageDialog.openInformation(listView.getSite().getShell(), AdminCustomersMessages.get().EditCustomerSegment,
				NLS.bind(AdminCustomersMessages.get().CustomerSegmentNoLongerExists,
				AdminCustomersMessages.get().CustomerSegmentEditor_NewSegmentName));
		}			
	}

}
