/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.users.wizards;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.users.AdminUsersMessages;
import com.elasticpath.cmclient.admin.users.AdminUsersPlugin;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.domain.cmuser.CmUser;

/**
 * Defines the User Profile section within the User Details wizard page.
 */
class UserDetailsProfileSectionPart extends AbstractCmClientFormSectionPart {

	/**
	 * 
	 */
	private final UserDetailsPage userDetailsPage;

	private IEpLayoutComposite controlPane;

	private Text userNameText;

	private CCombo statusCombo;

	private Text firstNameText;

	private Text lastNameText;

	private Text emailText;

	private final String activeText = AdminUsersMessages.get().Active;

	private final String inactiveText = AdminUsersMessages.get().Inactive;

	private static final int ACTIVE_INDEX = 0;

	private static final int INACTIVE_INDEX = 1;

	/**
	 * Constructor.
	 * 
	 * @param parent the parent form
	 * @param toolkit the form toolkit
	 * @param dataBindingContext the form databinding context
	 * @param userDetailsPage parent page
	 */
	UserDetailsProfileSectionPart(final UserDetailsPage userDetailsPage, final Composite parent, final FormToolkit toolkit,
			final DataBindingContext dataBindingContext) {
		super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
		this.userDetailsPage = userDetailsPage;
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		controlPane = CompositeFactory.createGridLayoutComposite(parent, 2, false);
		final IEpLayoutData labelData = controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING);
		final int textFieldWidth = 200;

		controlPane.addLabelBoldRequired(AdminUsersMessages.get().UserName, EpState.EDITABLE, labelData);
		
		//User name is not editable for current user.
		if (userDetailsPage.getCmUser().equals(LoginManager.getCmUser())) {
			userNameText = controlPane.addTextField(EpState.DISABLED, fieldData);
		} else {
			userNameText = controlPane.addTextField(EpState.EDITABLE, fieldData);
		}
		
		controlPane.addLabelBoldRequired(AdminUsersMessages.get().Status, EpState.EDITABLE, labelData);
		statusCombo = controlPane.addComboBox(EpState.EDITABLE, fieldData);
		statusCombo.add(activeText, 0);
		statusCombo.add(inactiveText, 1);
		statusCombo.select(ACTIVE_INDEX);
		// disable setting the status if current user
		if (userDetailsPage.getCmUser().equals(LoginManager.getCmUser())) {
			statusCombo.setEnabled(false);
		}

		controlPane.addLabelBoldRequired(AdminUsersMessages.get().FirstName, EpState.EDITABLE, labelData);
		firstNameText = controlPane.addTextField(EpState.EDITABLE, fieldData);
		((GridData) firstNameText.getLayoutData()).widthHint = textFieldWidth;

		controlPane.addLabelBoldRequired(AdminUsersMessages.get().LastName, EpState.EDITABLE, labelData);
		lastNameText = controlPane.addTextField(EpState.EDITABLE, fieldData);

		controlPane.addLabelBoldRequired(AdminUsersMessages.get().Email, EpState.EDITABLE, labelData);
		emailText = controlPane.addTextField(EpState.EDITABLE, fieldData);
	}

	@Override
	protected String getSectionTitle() {
		return AdminUsersMessages.get().UserDetails_UserProfile;
	}

	@Override
	protected void populateControls() {
		if (userDetailsPage.isNewUser()) {
			return;
		}

		CmUser cmUser = userDetailsPage.getModel();

		userNameText.setText(cmUser.getUserName());
		firstNameText.setText(cmUser.getFirstName());
		lastNameText.setText(cmUser.getLastName());
		emailText.setText(cmUser.getEmail());

		// passwordText.setText();
		// confirmPasswordText.setText("");

		// statusCombo
		if (cmUser.isEnabled()) {
			statusCombo.select(ACTIVE_INDEX);
		} else {
			statusCombo.select(INACTIVE_INDEX);
		}
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final CmUser cmUser = userDetailsPage.getModel();
		// Since validation is performed at time of control binding by default, ensure that
		// Control Decorations indicating failed validation are not shown on bind.
		final boolean hideDecorationOnFirstValidation = true;
		EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		// Username
		bindingProvider.bind(bindingContext, this.userNameText, cmUser, "userName", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_NOSPACES_REQUIRED, null, hideDecorationOnFirstValidation);

		// FirstName
		bindingProvider.bind(bindingContext, this.firstNameText, cmUser, "firstName", //$NON-NLS-1$
				EpValidatorFactory.STRING_100_REQUIRED, null, hideDecorationOnFirstValidation);

		// LastName
		bindingProvider.bind(bindingContext, this.lastNameText, cmUser, "lastName", //$NON-NLS-1$
				EpValidatorFactory.STRING_100_REQUIRED, null, hideDecorationOnFirstValidation);

		// Email
		bindingProvider.bind(bindingContext, this.emailText, cmUser, "email", //$NON-NLS-1$
				EpValidatorFactory.EMAIL_REQUIRED, null, hideDecorationOnFirstValidation);

		// Status
		bindingProvider.bind(bindingContext, this.statusCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				switch (statusCombo.getSelectionIndex()) {
				case ACTIVE_INDEX:
					cmUser.setEnabled(true);
					break;
				case INACTIVE_INDEX:
					cmUser.setEnabled(false);
					break;
				default:
					return new Status(IStatus.WARNING, AdminUsersPlugin.PLUGIN_ID, "Cannot set the CmUser status."); //$NON-NLS-1$
				}
				return Status.OK_STATUS;
			}
		}, true);

		EpWizardPageSupport.create(userDetailsPage, bindingContext);
	}
}
