/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthenticationService;
import com.elasticpath.cmclient.core.service.impl.AuthenticationServiceImpl;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.security.CmPasswordPolicy;
import com.elasticpath.commons.security.ValidationError;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * EP login change password dialog box.
 */
public class EpLoginChangePasswordDialog extends AbstractEpDialog implements ModifyListener {

	private final CmUserService cmUserService;

	private final PasswordEncoder passwordEncoder;

	private final AuthenticationService authenticationService;

	private final CmPasswordPolicy cmPasswordPolicy;

	private CmUser userToChange;

	private Text descriptionText;

	private Text userIdText;

	private Text oldPasswordText;

	private Text newPasswordText;

	private Text passwordConfirmText;

	private final DataBindingContext dbc;

	private EpValueBinding newPasswordBinder;

	/**
	 * Default constructor.
	 *
	 * @param parentShell the parent shell
	 * @param userToChange the {@link CmUser} to change the password for
	 */
	public EpLoginChangePasswordDialog(final Shell parentShell, final CmUser userToChange) {
		super(parentShell, 1, false);
		//This style removes dialog buttons: [O] [X]
		setShellStyle(SWT.TITLE);
		cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
		passwordEncoder = ServiceLocator.getService(ContextIdNames.CM_PASSWORDENCODER);
		cmPasswordPolicy = ServiceLocator.getService("cmPasswordPolicy"); //$NON-NLS-1$

		authenticationService = AuthenticationServiceImpl.getInstance();
		setUserToChange(userToChange);
		this.dbc = new DataBindingContext(SWTObservables.getRealm(Display.getCurrent()));
	}

	@Override
	protected void bindControls() {
		final boolean hideDecorationOnFirstValidation = true;
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		binder.bind(dbc, oldPasswordText, EpValidatorFactory.REQUIRED, null, getDummyUpdateStrategy(), hideDecorationOnFirstValidation);

		final IValidator passwordConfirmValidator = value -> {
			if (passwordConfirmText.getText().equals(newPasswordText.getText())) {
				return Status.OK_STATUS;
			}
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, CoreMessages.get().ChangePasswordDialog_Error_NewPasswordsNoMatch,
				null);
		};
		newPasswordBinder = binder.bind(dbc, passwordConfirmText, passwordConfirmValidator, null, getDummyUpdateStrategy(),
				hideDecorationOnFirstValidation);
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, OK, CoreMessages.get().EpLoginDialog_LoginButton, true);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite parent) {
		descriptionText = parent.addTextArea(EpState.READ_ONLY, parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));
		descriptionText.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));

		parent.addEmptyComponent(null);
		final IEpLayoutComposite controlPane = parent.addGridLayoutComposite(2, false, parent.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.FILL, true, false));
		final IEpLayoutData labelData = controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false, false);
		final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false);

		controlPane.addLabelBoldRequired(CoreMessages.get().EpLoginDialog_LoginUserId, EpState.EDITABLE, labelData);
		userIdText = controlPane.addTextField(EpState.DISABLED, fieldData);

		String oldPassword = CoreMessages.get().EpLoginChangePasswordDialog_CurrentPassword;
		if (userToChange.isTemporaryPassword()) {
			oldPassword = CoreMessages.get().EpLoginChangePasswordDialog_TemporaryPassword;
		}

		controlPane.addLabelBoldRequired(oldPassword, EpState.EDITABLE, labelData);
		oldPasswordText = controlPane.addPasswordField(EpState.EDITABLE, fieldData);
		oldPasswordText.addModifyListener(this);

		controlPane.addLabelBoldRequired(CoreMessages.get().EpLoginChangePasswordDialog_NewPassword, EpState.EDITABLE, labelData);
		newPasswordText = controlPane.addPasswordField(EpState.EDITABLE, fieldData);
		newPasswordText.addModifyListener(this);

		controlPane.addLabelBoldRequired(CoreMessages.get().EpLoginChangePasswordDialog_ConfirmNewPassword, EpState.EDITABLE, labelData);
		passwordConfirmText = controlPane.addPasswordField(EpState.EDITABLE, fieldData);
		passwordConfirmText.addModifyListener(this);
	}

	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		if (userToChange.isTemporaryPassword()) {
			return CoreMessages.get().EpLoginChangePasswordDialog_TemporaryPasswordMessage;
		}
		return CoreMessages.get().EpLoginChangePasswordDialog_ExpiredPasswordMessage;
	}

	@Override
	protected Image getWindowImage() {
		return CoreImageRegistry.getImage(CoreImageRegistry.CHANGE_PASSWORD);
	}

	@Override
	protected String getWindowTitle() {
		if (userToChange.isTemporaryPassword()) {
			return CoreMessages.get().EpLoginChangePasswordDialog_TemporaryPasswordTitle;
		}
		return CoreMessages.get().EpLoginChangePasswordDialog_ExpiredPasswordTitle;
	}

	@Override
	protected String getPluginId() {
		return CorePlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return null;
	}

	@Override
	protected void populateControls() {
		descriptionText.setText(CoreMessages.get().EpLoginChangePasswordDialog_PasswordRestrictions);
		userIdText.setText(userToChange.getUserName());
		this.setComplete(false);
	}

	@Override
	protected void okPressed() {

		if (!passwordEncoder.isPasswordValid(userToChange.getPassword(), oldPasswordText.getText(), null)) {
			String oldPasswordIncorrect = CoreMessages.get().EpLoginChangePasswordDialog_CurrentPasswordIncorrect;
			if (userToChange.isTemporaryPassword()) {
				oldPasswordIncorrect = CoreMessages.get().EpLoginChangePasswordDialog_TemporaryPasswordIncorrect;
			}
			setErrorMessage(oldPasswordIncorrect);
			return;
		}

		final String newPassword = this.newPasswordText.getText();

		userToChange.setClearTextPassword(newPassword);
		ValidationResult result = cmPasswordPolicy.validate(userToChange);

		if (result.isValid()) {
			userToChange.setCheckedClearTextPassword(newPassword);
			cmUserService.update(userToChange);
			// need to save the connection info so we can continue to use the services
			authenticationService.login(userToChange.getUsername(), newPassword);
		} else {
			ValidationError error = result.getErrors().values().iterator().next();
			setErrorMessage(
				NLS.bind(CoreMessages.get().getMessage(error.getKey()),
				error.getParams()));
			return;
		}

		super.okPressed();
	}

	@Override
	public void modifyText(final ModifyEvent event) {
		Text source = (Text) event.getSource();
		if (source.equals(oldPasswordText)) {
			setErrorMessage(null);
			return;
		} else if (source.equals(newPasswordText)) {
			newPasswordBinder.getBinding().updateTargetToModel();
		} else if (source.equals(passwordConfirmText)) {
			dbc.updateModels();
		}

		boolean isOk = true;
		for (Object status : dbc.getValidationStatusMap().values()) {
			final IStatus currStatus = (IStatus) status;
			if (!currStatus.isOK()) {
				isOk = false;
				break;
			}
		}
		
		this.setComplete(isOk);
	}

	/**
	 * Sets the user to change. This does not update the UI, but will bypass old password checking if the dialog is already open.
	 * 
	 * @param userToChange the user to change the password for
	 */
	public final void setUserToChange(final CmUser userToChange) {
		this.userToChange = userToChange;
	}

	/**
	 * Create a dummy observable value instead of the bean observable when the observed value is not a bean property. The value will be set using a
	 * custom update strategy
	 * 
	 * @return the observable value
	 */
	private ObservableUpdateValueStrategy getDummyUpdateStrategy() {
		return new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				return Status.OK_STATUS;
			}
		};
	}

	@Override
	public boolean isResizable() {
		return true;
	}
}
