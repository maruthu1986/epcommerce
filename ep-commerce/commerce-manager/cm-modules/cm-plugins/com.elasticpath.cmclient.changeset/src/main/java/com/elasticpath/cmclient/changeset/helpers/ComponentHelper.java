/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.helpers;

import org.eclipse.jface.dialogs.MessageDialog;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetSupportedEditors;
import com.elasticpath.cmclient.changeset.support.SupportedComponent;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.changeset.ChangeSetService;

/**
 * Component helper class for dealing with object descriptors.
 */
public class ComponentHelper {

	/**
	 * Opens an editor for the specified descriptor and editor.
	 * 
	 * @param groupId the change set group id.
	 * @param objectDescriptor the object descriptor
	 */
	public void openComponent(final String groupId, final BusinessObjectDescriptor objectDescriptor) {
		if (!objectExists(groupId, objectDescriptor)) {
			MessageDialog.openWarning(null, ChangeSetMessages.get().ComponentHelper_ObjectDeletedTitle,
					ChangeSetMessages.get().ComponentHelper_ObjectDeletedMessage);
			return;
		}
		SupportedComponent component = ChangeSetSupportedEditors.getDefault().findSupportedComponent(objectDescriptor);
		component.openComponent(objectDescriptor);
	}

	/**
	 * Verifies whether that object with the given object descriptor exists.
	 * 
	 * @param groupId the change set group id.
	 * @param objectDescriptor the object descriptor
	 * @return true if the object exists
	 */
	private boolean objectExists(final String groupId, final BusinessObjectDescriptor objectDescriptor) {
		final ChangeSetService changeSetService = ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
		
		return changeSetService.objectExists(groupId, objectDescriptor);
	}
}
