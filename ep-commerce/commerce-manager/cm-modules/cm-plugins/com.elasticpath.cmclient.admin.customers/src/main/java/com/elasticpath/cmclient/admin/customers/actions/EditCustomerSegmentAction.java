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
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.service.customer.CustomerGroupService;

/**
 * Action to edit a customer segment.
 */
public class EditCustomerSegmentAction extends Action {

	private final CustomerSegmentListView listView;

	/**
	 * Constructor.
	 *
	 * @param listView the customer segment list view
	 * @param text the action's text
	 * @param imageDescriptor the action's image
	 */
	public EditCustomerSegmentAction(final CustomerSegmentListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	/**
	 * Run the action.
	 */
	@Override
	public void run() {
		final CustomerGroupService customerGroupService =
				ServiceLocator.getService(ContextIdNames.CUSTOMER_GROUP_SERVICE);
		final CustomerGroup customerGroup = listView.getSelectedCustomerGroup();
		CustomerGroup customerGroupToEdit = customerGroupService.load(customerGroup.getUidPk());

		if (customerGroupToEdit == null) {
			MessageDialog.openInformation(listView.getSite().getShell(), AdminCustomersMessages.get().EditCustomerSegment,
				NLS.bind(AdminCustomersMessages.get().CustomerSegmentNoLongerExists,
				customerGroup.getName()));
			listView.refreshViewerInput();
		} else {
			final CustomerSegmentEditorInput editorInput = new CustomerSegmentEditorInput(
					customerGroupToEdit.getName(), customerGroupToEdit.getUidPk(),
					CustomerGroup.class);
			try {
				listView.getSite().getWorkbenchWindow().getActivePage().openEditor(editorInput, CustomerSegmentEditor.ID_EDITOR);
			} catch (PartInitException e) {
				MessageDialog.openInformation(listView.getSite().getShell(), AdminCustomersMessages.get().EditCustomerSegment,
					NLS.bind(AdminCustomersMessages.get().CustomerSegmentNoLongerExists,
					customerGroup.getName()));
			}			
		}
	}
}
