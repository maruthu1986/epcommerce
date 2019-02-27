/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.customers.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.admin.customers.AdminCustomersMessages;
import com.elasticpath.cmclient.admin.customers.views.AttributeListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.service.attribute.AttributeService;

/**
 * Action to delete a new attribute.
 */
public class DeleteAttributeAction extends Action {

	private final AttributeListView listView;

	/**
	 * Constructor.
	 *
	 * @param listView the customer list view
	 * @param text the action's text
	 * @param imageDescriptor the action's image
	 */
	public DeleteAttributeAction(final AttributeListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		AttributeService attributeService = ServiceLocator.getService(ContextIdNames.ATTRIBUTE_SERVICE);

		Attribute attribute = listView.getSelectedAttribute();

		Attribute attributeToEdit = attributeService.findByKey(attribute.getKey());
		if (attributeToEdit == null) {
			MessageDialog.openInformation(listView.getSite().getShell(), AdminCustomersMessages.get().EditAttribute,
				NLS.bind(AdminCustomersMessages.get().ProfileAttributeNoLongerExists,
				attribute.getKey(), attribute.getName()));
			listView.refreshViewerInput();
			return;
		}

		List<Long> uidsInUse = attributeService.getCustomerProfileAttributeInUseUidList();
		if (uidsInUse.contains(attributeToEdit.getUidPk())) {
			MessageDialog.openInformation(listView.getSite().getShell(), AdminCustomersMessages.get().ProfileAttributeInUseTitle,
				NLS.bind(AdminCustomersMessages.get().ProfileAttributeInUseMessage,
				attributeToEdit.getKey(), attributeToEdit.getName()));
			return;
		}

		boolean confirmed = MessageDialog.openConfirm(listView.getSite().getShell(), AdminCustomersMessages.get().DeleteProfileAttributeTitle,

				NLS.bind(AdminCustomersMessages.get().DeleteProfileAttributeText,
				attribute.getKey(), attribute.getName()));

		if (confirmed) {
			attributeService.remove(attributeToEdit);
			listView.refreshViewerInput();
		}
	}
}
