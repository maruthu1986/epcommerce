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
 * The Warehouse Permissions wizard page.
 */
public class WarehousePermissionsPage extends AbstractEPWizardPage<CmUser> {
	private final CmUser cmUser;

	/**
	 * Constructor.
	 *  @param pageName the page name
	 * @param title the page title
	 * @param message the message
	 * @param cmUser the CmUser
	 */
	protected WarehousePermissionsPage(final String pageName, final String title, final String message,
									   final CmUser cmUser) {
		super(1, false, pageName, title, message, new DataBindingContext());
		this.cmUser = cmUser;
	}

	@Override
	protected void bindControls() {
		//Do nothing
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite controlPane) {
		// Radio buttons
		final Button assignAllWarehousesRadio = controlPane.addRadioButton(AdminUsersMessages.get().WarehousePermissions_AssignAllWarehouses,
				EpState.EDITABLE, controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL));

		final Button assignSpecialWarehouseRadio = controlPane.addRadioButton(AdminUsersMessages.get().WarehousePermissions_AssignSpecialWarehouses,
				EpState.EDITABLE, controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL));

		// Group (1 columns, spanning 2 columns)
		final IEpLayoutComposite group = controlPane.addGroup(null, 1, false, controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
				true, true, 2, 1));

		WarehousePermissionsDualListBox warehousePermissions = new WarehousePermissionsDualListBox(group, getModel(), 
				AdminUsersMessages.get().WarehousePermissions_AvailableWarehouses,
				AdminUsersMessages.get().WarehousePermissions_AssignedWarehouses,
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
						true, true, 2, 1));
		warehousePermissions.createControls();
		
		if (!cmUser.isPersisted()) {
			assignAllWarehousesRadio.setSelection(true);
			boolean allAccess = assignAllWarehousesRadio.getSelection();
			cmUser.setAllWarehousesAccess(allAccess);
			group.getSwtComposite().setVisible(!allAccess);
		}

		assignAllWarehousesRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				boolean allAccess = assignAllWarehousesRadio.getSelection();
				cmUser.setAllWarehousesAccess(allAccess);
				group.getSwtComposite().setVisible(!allAccess);
			}

		});
		boolean allAccess = cmUser.isAllWarehousesAccess();
		assignAllWarehousesRadio.setSelection(allAccess);
		assignSpecialWarehouseRadio.setSelection(!allAccess);
		group.getSwtComposite().setVisible(!allAccess);
		/* MUST be called */
		this.setControl(controlPane.getSwtComposite());
	}

	@Override
	protected void populateControls() {
		//Do nothing
	}
}
