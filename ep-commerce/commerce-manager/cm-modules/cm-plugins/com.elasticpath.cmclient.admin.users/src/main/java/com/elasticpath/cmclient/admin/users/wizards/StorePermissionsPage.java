/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.users.wizards;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import com.elasticpath.cmclient.admin.users.AdminUsersMessages;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.domain.cmuser.CmUser;

/**
 * The Store Permissions wizard page.
 */
public class StorePermissionsPage extends AbstractEPWizardPage<CmUser> {

	private final CmUser cmUser;

	/**
	 * Constructor.
	 *  @param pageName the page name
	 * @param title the page title
	 * @param message the message
	 * @param cmUser the CmUser
	 */
	protected StorePermissionsPage(final String pageName, final String title, final String message,
								   final CmUser cmUser) {
		super(1, false, pageName, title, message, new DataBindingContext());
		this.cmUser = cmUser;
	}

	@Override
	protected void bindControls() {
		// Do nothing

	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite controlPane) {
		// Radio buttons
		final Button assignAllStoresRadio = controlPane.addRadioButton(AdminUsersMessages.get().StorePermissions_AssignAllStores, EpState.EDITABLE,
				controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL));

		final Button assignSpecialStoreRadio = controlPane.addRadioButton(AdminUsersMessages.get().StorePermissions_AssignSpecialStores,
				EpState.EDITABLE, controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL));

		// Group (1 columns, spanning 2 columns)
		final IEpLayoutComposite group = controlPane.addGroup(null, 1, false, controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
				true, true, 2, 1));

		StorePermissionsDualListBox storePermissions = new StorePermissionsDualListBox(group, getModel(), 
				AdminUsersMessages.get().StorePermissions_AvailableStores,
				AdminUsersMessages.get().StorePermissions_AssignedStores,
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true,
						2, 1));
		storePermissions.createControls();
		
		if (!cmUser.isPersisted()) {
			assignAllStoresRadio.setSelection(true);
			boolean allAccess = assignAllStoresRadio.getSelection();
			cmUser.setAllStoresAccess(allAccess);
			group.getSwtComposite().setVisible(!allAccess);
		}

		assignAllStoresRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				boolean allAccess = assignAllStoresRadio.getSelection();
				cmUser.setAllStoresAccess(allAccess);
				group.getSwtComposite().setVisible(!allAccess);
			}

		});

		boolean allAccess = cmUser.isAllStoresAccess();
		assignAllStoresRadio.setSelection(allAccess);
		assignSpecialStoreRadio.setSelection(!allAccess);
		group.getSwtComposite().setVisible(!allAccess);
		/* MUST be called */
		this.setControl(controlPane.getSwtComposite());
	}

	@Override
	protected void populateControls() {
		// Do nothing
	}
}
