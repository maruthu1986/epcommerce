/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.users.wizards;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.admin.users.AdminUsersMessages;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.domain.cmuser.UserRole;

/**
 * The Role Details wizard page.
 */
public class RoleDetailsPage extends AbstractEPWizardPage<UserRole> {

	private final UserRole userRole;

	private Text nameTextbox;

	private Text descriptionTextbox;

	/**
	 * Constructor.
	 *  @param pageName the page name
	 * @param title the page title
	 * @param message the message
	 * @param userRole the model object
	 */
	protected RoleDetailsPage(final String pageName, final String title, final String message,
							  final UserRole userRole) {
		super(2, false, pageName, title, message, new DataBindingContext());
		this.userRole = userRole;
	}

	@Override
	protected void bindControls() {
		final boolean hideDecorationOnFirstValidation = true;
		// Name
		EpControlBindingProvider.getInstance().bind(getDataBindingContext(), this.nameTextbox, userRole, "name", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);

		// Description
		EpControlBindingProvider.getInstance().bind(getDataBindingContext(), this.descriptionTextbox, userRole, "description", //$NON-NLS-1$
				EpValidatorFactory.MAX_LENGTH_255, null, hideDecorationOnFirstValidation);

		EpWizardPageSupport.create(RoleDetailsPage.this, getDataBindingContext());
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite controlPane) {

		controlPane.addLabelBoldRequired(AdminUsersMessages.get().RoleDetails_RoleName, EpState.EDITABLE,
				controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL));
		nameTextbox = controlPane.addTextField(EpState.EDITABLE, controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		controlPane.addLabelBold(AdminUsersMessages.get().RoleDetails_Description,
				controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL));
		descriptionTextbox = controlPane.addTextArea(EpState.EDITABLE, controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
				true));

		/* MUST be called */
		this.setControl(controlPane.getSwtComposite());
	}

	@Override
	protected void populateControls() {
		if (userRole.getName() == null) {
			nameTextbox.setText(""); //$NON-NLS-1$
		} else {
			nameTextbox.setText(userRole.getName());
		}
		if (userRole.getDescription() == null) {
			descriptionTextbox.setText(""); //$NON-NLS-1$
		} else {
			descriptionTextbox.setText(userRole.getDescription());
		}
	}
}
