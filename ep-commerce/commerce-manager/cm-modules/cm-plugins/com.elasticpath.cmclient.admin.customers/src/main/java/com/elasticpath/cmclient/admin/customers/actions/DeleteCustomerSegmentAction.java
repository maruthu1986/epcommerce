/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.admin.customers.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.admin.customers.AdminCustomersMessages;
import com.elasticpath.cmclient.admin.customers.event.CustomerSegmentEventService;
import com.elasticpath.cmclient.admin.customers.utils.CustomerSegmentUtils;
import com.elasticpath.cmclient.admin.customers.views.CustomerSegmentListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.service.customer.CustomerGroupService;

/**
 * Action to delete a customer segment.
 */
public class DeleteCustomerSegmentAction extends Action {

	private final CustomerSegmentListView listView;

	/**
	 * Constructor.
	 *
	 * @param listView the customer segment list view
	 * @param text the action's text
	 * @param imageDescriptor the action's image
	 */
	public DeleteCustomerSegmentAction(final CustomerSegmentListView listView, final String text, final ImageDescriptor imageDescriptor) {
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
		final CustomerGroup customerGroupToDelete = customerGroupService.findByGroupName(customerGroup.getName());
		if (customerGroupToDelete == null) {
			CustomerSegmentEventService.getInstance().fireCustomerSegmentChangeEvent(
					new ItemChangeEvent<>(this, customerGroup, EventType.REMOVE));
			MessageDialog.openInformation(listView.getSite().getShell(), AdminCustomersMessages.get().DeleteCustomerSegment,
				NLS.bind(AdminCustomersMessages.get().CustomerSegmentNoLongerExists,
				customerGroup.getName()));
			return;
		}

		boolean inUse = CustomerSegmentUtils.segmentsInUseByConditionalExpression(customerGroupToDelete)
						|| customerGroupService.checkIfInUse(customerGroupToDelete);
		if (inUse) {
			MessageDialog.openInformation(listView.getSite().getShell(), AdminCustomersMessages.get().CustomerSegmentInUseTitle,
				NLS.bind(AdminCustomersMessages.get().CustomerSegmentInUseText,
				customerGroupToDelete.getName()));
			return;
		}

		final boolean confirmed = MessageDialog.openConfirm(listView.getSite().getShell(), AdminCustomersMessages.get().DeleteCustomerSegmentTitle,

				NLS.bind(AdminCustomersMessages.get().DeleteCustomerSegmentText,
				customerGroup.getName()));
		if (confirmed) {
			customerGroupService.remove(customerGroupToDelete);
			CustomerSegmentEventService.getInstance().fireCustomerSegmentChangeEvent(
					new ItemChangeEvent<>(this, customerGroup, EventType.REMOVE));
		}
	}

}
