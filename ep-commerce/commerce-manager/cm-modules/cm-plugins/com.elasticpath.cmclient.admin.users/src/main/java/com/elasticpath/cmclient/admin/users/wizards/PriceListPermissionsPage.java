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
 * 
 * Page for manage accessable to cm user price lists.
 *
 */
public class PriceListPermissionsPage extends AbstractEPWizardPage<CmUser> {
	
	private final CmUser cmUser;

	/**
	 * Constructor.
	 *  @param pageName the page name
	 * @param title the page title
	 * @param message the message
	 * @param cmUser the CmUser
	 */
	protected PriceListPermissionsPage(final String pageName, final String title, final String message,
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
		final Button assignAllPriceListRadio = controlPane.addRadioButton(
				AdminUsersMessages.get().PriceListPermissions_AssignAllPL, EpState.EDITABLE,
				controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL));

		final Button assignSpecialPriceListRadio = controlPane.addRadioButton(
				AdminUsersMessages.get().PriceListPermissions_AssignSpecialPL, EpState.EDITABLE,
				controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL));

		// Group (1 columns, spanning 2 columns)
		final IEpLayoutComposite group = controlPane.addGroup(null, 1, false, controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
				true, true, 2, 1));

		PriceListPermissionsDualListBox priceListPermissions = new PriceListPermissionsDualListBox(group, getModel(), 
				AdminUsersMessages.get().PriceListPermissions_AvailablePL,
				AdminUsersMessages.get().PriceListPermissions_AssignedPL,
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true,
						2, 1));
		priceListPermissions.createControls();
		
		if (!cmUser.isPersisted()) {
			assignAllPriceListRadio.setSelection(true);
			boolean allAccess = assignAllPriceListRadio.getSelection();
			cmUser.setAllPriceListsAccess(allAccess);
			group.getSwtComposite().setVisible(!allAccess);
		}

		assignAllPriceListRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				boolean allAccess = assignAllPriceListRadio.getSelection();
				cmUser.setAllPriceListsAccess(allAccess);
				group.getSwtComposite().setVisible(!allAccess);
			}

		});

		boolean allAccess = cmUser.isAllPriceListsAccess();
		assignAllPriceListRadio.setSelection(allAccess);
		assignSpecialPriceListRadio.setSelection(!allAccess);
		group.getSwtComposite().setVisible(!allAccess);
		/* MUST be called */
		this.setControl(controlPane.getSwtComposite());
		
	}
	
	@Override
	protected void populateControls() {
		// Do nothing
	}
	

}
