/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.EntityEditorInput;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.service.customer.CustomerService;

/**
 * Customer editor input.
 */
public class CustomerDetailsEditorInput extends EntityEditorInput<Long> {

	private final long customerUid;

	private Customer customerInstance;

	/**
	 * Constructor.
	 *
	 * @param customerUid the productSku
	 */
	public CustomerDetailsEditorInput(final long customerUid) {
		super(null, customerUid, Customer.class);
		this.customerUid = customerUid;
	}

	@Override
	public String getName() {
		final Customer customer = retrieveCustomer();
		String fullName = customer.getFullName();
		if (fullName == null || fullName.length() <= 0) {
			fullName = FulfillmentMessages.get().CustomerDetails_Anonymous;
		}
		return fullName;
	}

	@Override
	public String getToolTipText() {
		final Customer customer = retrieveCustomer();
		return
			NLS.bind(FulfillmentMessages.get().CustomerDetails_Tooltip,
			new Object[]{
				customer.getFullName(), customer.getUidPk() });
	}

	/**
	 * Retrieves the product to be displayed/edited from persistent storage. TODO: Only the information to be displayed on a given tab should be
	 * retrieved. See if there is a better way to do this such that the tab itself causes the product retrieval when the tab is displayed. Also,
	 * other tabs will need to add to the product as they fetch more data.
	 * 
	 * @return the <code>Product</code>
	 */
	private Customer retrieveCustomer() {
		if (customerInstance == null) {
			final CustomerService customerService = ServiceLocator.getService(ContextIdNames.CUSTOMER_SERVICE);
			final List<Long> customerList = new ArrayList<>(1);
			customerList.add(customerUid);
			final List<Customer> customers = customerService.findByUids(customerList);
			if (!customers.isEmpty()) {
				customerInstance = customers.get(0);
			}
		}
		return customerInstance;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		ImageDescriptor result;
		final Customer customer = retrieveCustomer();
		switch (customer.getStatus()) {
		case Customer.STATUS_ACTIVE :
			result = CoreImageRegistry.IMAGE_USER_SMALL;
			break;
		case Customer.STATUS_DISABLED :
			result = CoreImageRegistry.IMAGE_USER_DISABLED_SMALL;
			break;
		case Customer.STATUS_PENDING_APPROVAL :
			result = CoreImageRegistry.IMAGE_USER_PENDING_APPROVAL_SMALL;
			break;
			default :
				result = super.getImageDescriptor();
				break;
		}
		return result;
	}

	@Override
	public Object getAdapter(final Class adapter) {
		if (adapter == Customer.class) {
			return retrieveCustomer();
		}
		return super.getAdapter(adapter);
	}

}
