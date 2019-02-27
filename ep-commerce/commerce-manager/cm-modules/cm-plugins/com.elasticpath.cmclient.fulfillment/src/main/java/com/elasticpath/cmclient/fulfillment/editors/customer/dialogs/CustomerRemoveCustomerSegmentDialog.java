/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.fulfillment.editors.customer.dialogs;

import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.service.customer.CustomerService;

/**
 * Dialog for removing a customer segment.
 */
public class CustomerRemoveCustomerSegmentDialog extends MessageDialog {

	private static final String[] BUTTONS = { 
			JFaceResources.getString(IDialogLabelKeys.OK_LABEL_KEY),
			JFaceResources.getString(IDialogLabelKeys.CANCEL_LABEL_KEY) };

	private static final int BUTTON_INDEX_OK = 0;

	private static CustomerService customerService;

	private final Customer customer;

	private final CustomerGroup customerGroup;

	/**
	 * Dialog for confirmation to remove segment from a customer.
	 *
	 * @param parentShell the parent shell
	 * @param customer the customer
	 * @param customerGroup the customer group to remove
	 */
	public CustomerRemoveCustomerSegmentDialog(final Shell parentShell, final Customer customer, final CustomerGroup customerGroup) {
		super(parentShell, FulfillmentMessages.get().CustomerSegmentsPageDialog_RemoveConfirm, null,
				FulfillmentMessages.get().CustomerSegmentsPageDialog_RemoveMessage, QUESTION, BUTTONS, 0);
		this.customer = customer;
		this.customerGroup = customerGroup;
	}


	/**
	 * Window will configure the shell after construction. Set things like Image or Title.
	 *
	 * @param newShell the shell
	 */
	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setImage(FulfillmentImageRegistry.CUSTOMER_SEGMENT_ICON.createImage());
	}
	/**
	 * Open the dialog box, and remove customer's segment if OK is pressed.
	 *
	 * @return result button selection
	 */
	@Override
	public int open() {
		message = FulfillmentMessages.get().CustomerSegmentsPageDialog_RemoveMessage + "\n\n" //$NON-NLS-1$
				+ customerGroup.getName();

		final int result = super.open();
		if (result == BUTTON_INDEX_OK) {
			if (customerService == null) {
				customerService = ServiceLocator.getService(ContextIdNames.CUSTOMER_SERVICE);
			}
			customer.removeCustomerGroup(customerGroup);
		}
		return result;
	}

}
