/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.customers.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.admin.customers.dialogs.AttributeDialog;
import com.elasticpath.cmclient.admin.customers.views.AttributeListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.service.attribute.AttributeService;

/**
 * Action to create a new attribute.
 */
public class CreateAttributeAction extends Action {

	private final AttributeListView listView;

	/**
	 * Creates the create attribute action.
	 * 
	 * @param listView the customer list view
	 * @param text the action's text
	 * @param imageDescriptor the action's image
	 */
	public CreateAttributeAction(final AttributeListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		Attribute attributeToAdd = ServiceLocator.getService(ContextIdNames.ATTRIBUTE);
		attributeToAdd.setAttributeUsage(AttributeUsageImpl.CUSTOMERPROFILE_USAGE);
		attributeToAdd.setAttributeType(AttributeType.SHORT_TEXT);
		boolean dialogOk = AttributeDialog.openCreateDialog(listView.getSite().getShell(), attributeToAdd);
		if (dialogOk) {
			AttributeService attributeService = ServiceLocator.getService(
					ContextIdNames.ATTRIBUTE_SERVICE);
			attributeService.add(attributeToAdd);
			listView.refreshViewerInput();
		}
	}
}
