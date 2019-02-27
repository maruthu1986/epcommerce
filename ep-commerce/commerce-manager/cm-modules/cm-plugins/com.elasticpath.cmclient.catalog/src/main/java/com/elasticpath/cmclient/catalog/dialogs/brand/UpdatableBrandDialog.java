/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.dialogs.brand;

import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.service.changeset.ChangeSetMemberAction;


/**
 * A dialog used to handle the opening/saving of Brands from the ChangeSet Editor.
 */
public class UpdatableBrandDialog extends BrandDialog {

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpButtonsForButtonsBar(ButtonsBarType.SAVE, parent);
	}

	@Override
	protected void performSaveOperation() {

		Brand brand = getBrand();

		getBrandService().update(brand);
		final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);
		changeSetHelper.addObjectToChangeSet(brand, ChangeSetMemberAction.EDIT);
	}

}
