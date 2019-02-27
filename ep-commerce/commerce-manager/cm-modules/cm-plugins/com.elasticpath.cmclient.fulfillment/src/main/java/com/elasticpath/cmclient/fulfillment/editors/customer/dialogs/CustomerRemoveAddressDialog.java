/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.customer.dialogs;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.service.customer.CustomerService;

/**
 * Dialog for removing Customer Addresses.
 * 
 */
public class CustomerRemoveAddressDialog extends MessageDialog {
	
	private static final String COMMA = ", "; //$NON-NLS-1$

	private static final String RETURN = "\n"; //$NON-NLS-1$

	private static final String EMPTY_SPACE = " "; //$NON-NLS-1$

	private static final String[] BUTTONS = { 
			JFaceResources.getString(IDialogLabelKeys.OK_LABEL_KEY),
			JFaceResources.getString(IDialogLabelKeys.CANCEL_LABEL_KEY) };

	private static final int BUTTON_INDEX_OK = 0;

	private static CustomerService customerService;

	private final Customer customer;
	
	private final CustomerAddress address;

	/**
	 * Dialog for confirmation to remove address from a Customer.
	 *
	 * @param parentShell the parent shell
	 * @param customer the customer
	 * @param address the address for update
	 */
	public CustomerRemoveAddressDialog(final Shell parentShell, final Customer customer, final CustomerAddress address) {
		super(parentShell, FulfillmentMessages.get().AddressDialog_RemoveTitle, null,
				FulfillmentMessages.get().AddressDialog_RemoveMessage, QUESTION, BUTTONS, 0);
		this.customer = customer;
		this.address = address;
	}
	
	@Override
	public int open() {
		message = message + "\n\n" //$NON-NLS-1$
				+ buildAddressString(address);
					
					
		final int result = super.open();
		if (result == BUTTON_INDEX_OK) {
			if (customerService == null) {
				customerService = ServiceLocator.getService(ContextIdNames.CUSTOMER_SERVICE);
			}
			final List <CustomerAddress> addresses = customer.getAddresses();
			addresses.remove(address);
			customer.setAddresses(addresses);
		}
		return result;
	}
	
	private String buildAddressString(final CustomerAddress address) {
		StringBuilder builder = new StringBuilder();

		builder.append(address.getFirstName());
		builder.append(EMPTY_SPACE);
		builder.append(address.getLastName());
		builder.append(RETURN);
		builder.append(address.getStreet1());
		
		if (!StringUtils.isBlank(address.getStreet2())) {
			builder.append(EMPTY_SPACE);
			builder.append(address.getStreet2());
		}
		
		builder.append(COMMA);
		builder.append(address.getCity());
		builder.append(COMMA);
		
		if (!StringUtils.isBlank(address.getSubCountry())) {
			builder.append(address.getSubCountry());
			builder.append(COMMA);
		}
		
		builder.append(address.getZipOrPostalCode());
		builder.append(COMMA);
		builder.append(address.getCountry());
		builder.append(RETURN);
		builder.append(address.getPhoneNumber());

		return builder.toString();
	}
}
