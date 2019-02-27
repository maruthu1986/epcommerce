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
 * The Catalog Permissions wizard page.
 */
public class CatalogPermissionsPage extends AbstractEPWizardPage<CmUser> {

	private final CmUser cmUser;

	/**
	 * Constructor.
	 *  @param pageName the page name
	 * @param title the page title
	 * @param message the message
	 * @param cmUser the CmUser
	 */
	protected CatalogPermissionsPage(final String pageName, final String title, final String message,
									 final CmUser cmUser) {
		super(1, false, pageName, title, message, new DataBindingContext());
		this.cmUser = cmUser;
	}

	@Override
	protected void bindControls() {
		// do nothing

	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite controlPane) {
		// Radio buttons
		final Button assignAllCatalogsRadio = controlPane.addRadioButton(AdminUsersMessages.get().CatalogPermissions_AssignAllCatalogs,
				EpState.EDITABLE, controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL));

		final Button assignSpecialCatalogsRadio = controlPane.addRadioButton(AdminUsersMessages.get().CatalogPermissions_AssignSpecialCatalogs,
				EpState.EDITABLE, controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL));

		// Group (1 columns, spanning 2 columns)
		final IEpLayoutComposite group = controlPane.addGroup(null, 1, false, controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
				true, true, 2, 1));

		CatalogPermissionsDualListBox catalogPermissions = new CatalogPermissionsDualListBox(group, CatalogPermissionsPage.this.getModel(), 
				AdminUsersMessages.get().CatalogPermissions_AvailableCatalogs,
				AdminUsersMessages.get().CatalogPermissions_AssignedCatalogs,
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
						true, 2, 1));
		catalogPermissions.createControls();
		
		if (!cmUser.isPersisted()) {
			assignAllCatalogsRadio.setSelection(true);
			boolean allAccess = assignAllCatalogsRadio.getSelection();
			cmUser.setAllCatalogsAccess(allAccess);
			group.getSwtComposite().setVisible(!allAccess);
		}

		assignAllCatalogsRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				CmUser cmUser = getModel();
				boolean allAccess = assignAllCatalogsRadio.getSelection();
				cmUser.setAllCatalogsAccess(allAccess);
				group.getSwtComposite().setVisible(!allAccess);
			}

		});

		boolean allAccess = cmUser.isAllCatalogsAccess();
		assignAllCatalogsRadio.setSelection(allAccess);
		assignSpecialCatalogsRadio.setSelection(!allAccess);
		group.getSwtComposite().setVisible(!allAccess);
		/* MUST be called */
		this.setControl(controlPane.getSwtComposite());

	}

	@Override
	protected void populateControls() {
		// do nothing
	}
}
