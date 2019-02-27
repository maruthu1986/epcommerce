/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.dialogs.categorytype;

import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 *	A dialog used to handle the opening/saving of CategoryTypes from the ChangeSet Editor .
 */
public class UpdatableCategoryTypeDialog extends CategoryTypeDialog {

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpButtonsForButtonsBar(ButtonsBarType.SAVE, parent);
	}
	
	@Override
	protected void performSaveOperation() {
		getCategoryTypeService().update(getCategoryType());
		changeSetHelper.addObjectToChangeSet(getCategoryType(), ChangeSetMemberAction.EDIT);
	}

}
