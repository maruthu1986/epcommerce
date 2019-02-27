/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.dialog;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthenticationService;
import com.elasticpath.cmclient.core.service.impl.AuthenticationServiceImpl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.security.CmPasswordPolicy;
import com.elasticpath.commons.security.ValidationError;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Dialog to change a user's password. This may or may not be the logged in CM user
 */
public class ChangePasswordDialog extends AbstractEpDialog implements ModifyListener {

	private static final Logger LOG = Logger.getLogger(ChangePasswordDialog.class);

	private final CmUserService cmUserService;

	private final PasswordEncoder passwordEncoder;

	private final AuthenticationService authenticationService;

	private final CmPasswordPolicy cmPasswordPolicy;

	private boolean requireOldPassword;

	private final boolean sendConfirmationEmail;

	private final CmUser oldUserContainer;

	private CmUser userToChange;

	private Text oldPassword;

	private Text newPassword;

	private Text passwordConfirm;

	private final DataBindingContext dataBindingContext;

	/**
	 * Default constructor.
	 *
	 * @param parentShell the parent shell
	 * @param userToChange the {@link CmUser} to change the password for
	 * @param sendEmail whether the send a confirmation email that a password has been changed
	 */
	public ChangePasswordDialog(final Shell parentShell, final CmUser userToChange, final boolean sendEmail) {
		super(parentShell, 2, false);
		cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
		passwordEncoder = ServiceLocator.getService(ContextIdNames.CM_PASSWORDENCODER);
		oldUserContainer = ServiceLocator.getService(ContextIdNames.CMUSER);
		cmPasswordPolicy = ServiceLocator.getService("cmPasswordPolicy"); //$NON-NLS-1$
		authenticationService = AuthenticationServiceImpl.getInstance();

		setUserToChange(userToChange);
		sendConfirmationEmail = sendEmail;
		dataBindingContext = new DataBindingContext();
	}

	@Override
	protected void bindControls() {
		final boolean hideDecorationOnFirstValidation = true;
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		binder.bind(dataBindingContext, oldPassword, EpValidatorFactory.REQUIRED, null, getDummyUpdateStrategy(), hideDecorationOnFirstValidation);

		final IValidator passwordConfirmValidator = new IValidator() {
			@Override
			public IStatus validate(final Object value) {
				if (passwordConfirm.getText().equals(newPassword.getText())) {
					return Status.OK_STATUS;
				}
				return new Status(IStatus.ERROR,
						CorePlugin.PLUGIN_ID, IStatus.ERROR, CoreMessages.get().ChangePasswordDialog_Error_NewPasswordsNoMatch,
						null);
			}
		};

		final IValidator requiredConfirmValidator = new CompoundValidator(passwordConfirmValidator, EpValidatorFactory.REQUIRED);
		binder.bind(dataBindingContext, passwordConfirm, requiredConfirmValidator, null, getDummyUpdateStrategy(), hideDecorationOnFirstValidation);

		EpDialogSupport.create(this, dataBindingContext);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite parent) {
		final IEpLayoutComposite controlPane = parent;
		final IEpLayoutData labelData = controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER, false, false);
		final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false);

		if (requireOldPassword) {
			controlPane.addLabelBoldRequired(CoreMessages.get().ChangePasswordDialog_OldPassword, EpState.EDITABLE, labelData);
			oldPassword = controlPane.addPasswordField(EpState.EDITABLE, fieldData);
			oldPassword.addModifyListener(this);
		}

		controlPane.addLabelBoldRequired(CoreMessages.get().ChangePasswordDialog_NewPassword, EpState.EDITABLE, labelData);
		newPassword = controlPane.addPasswordField(EpState.EDITABLE, fieldData);

		controlPane.addLabelBoldRequired(CoreMessages.get().ChangePasswordDialog_ConfirmPassword, EpState.EDITABLE, labelData);
		passwordConfirm = controlPane.addPasswordField(EpState.EDITABLE, fieldData);
	}

	@Override
	protected String getInitialMessage() {
		if (requireOldPassword) {
			return CoreMessages.get().ChangePasswordDialog_Description_RequireOldPassword;
		}
		return CoreMessages.get().ChangePasswordDialog_Description_NoOldPassword;
	}

	@Override
	protected String getTitle() {
		if (requireOldPassword) {
			return CoreMessages.get().ChangePasswordDialog_Title_Self;
		}
		return CoreMessages.get().ChangePasswordDialog_Title_AnotherUser;
	}

	@Override
	protected Image getWindowImage() {
		return CoreImageRegistry.getImage(CoreImageRegistry.CHANGE_PASSWORD);
	}

	@Override
	protected String getWindowTitle() {
		if (requireOldPassword) {
			return CoreMessages.get().ChangePasswordDialog_WindowTitle_Self;
		}
		return CoreMessages.get().ChangePasswordDialog_WindowTitle_AnotherUser;
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
		// do nothing
	}

	@Override
	protected void okPressed() {

		if (requireOldPassword && !passwordEncoder.isPasswordValid(userToChange.getPassword(), oldPassword.getText(), null)) {
			setErrorMessage(CoreMessages.get().ChangePasswordDialog_Error_OldPasswordIncorrect);
			return;
		}

		final String newPassword = this.newPassword.getText();
		copyRequiredForRecoveryFields(userToChange, oldUserContainer);

		userToChange.setClearTextPassword(newPassword);
		ValidationResult result = cmPasswordPolicy.validate(userToChange);

		if (result.isValid()) {
			userToChange.setCheckedClearTextPassword(newPassword);
			cmUserService.update(userToChange);
			// need to save the connection info so we can continue to use the services
			authenticationService.login(userToChange.getUsername(), newPassword);
			boolean keepNewPassword = true;

			if (sendConfirmationEmail) {
				try {
					cmUserService.sendPasswordChangedEvent(userToChange.getGuid(), newPassword);
					MessageDialog.openInformation(getShell(), CoreMessages.get().ChangePasswordDialog_Confirmation_DialogTitle,
							CoreMessages.get().ChangePasswordDialog_Confirmation_DialogDescription);
				} catch (RuntimeException e) {
					LOG.debug("Error sending password confirmation email", e); //$NON-NLS-1$
					keepNewPassword = MessageDialog.openConfirm(getShell(), CoreMessages.get().ChangePasswordDialog_Error_EmailError_DialogTitle,
							CoreMessages.get().ChangePasswordDialog_Error_EmailError_DialogDescription);
				}
			}

			if (!keepNewPassword) {
				copyRequiredForRecoveryFields(oldUserContainer, userToChange);
				cmUserService.update(userToChange);
				// can't use the old credentials anymore
				authenticationService.login(userToChange.getUsername(), oldPassword.getText());
			}
		} else {
			ValidationError error = result.getErrors().values().iterator().next();
			setErrorMessage(
				NLS.bind(CoreMessages.get().getMessage(error.getKey()),
				error.getParams()));
			return;
		}

		super.okPressed();
	}

	private void copyRequiredForRecoveryFields(final CmUser source, final CmUser destination) {
		destination.setPassword(source.getPassword());
	}


	@Override
	public void modifyText(final ModifyEvent event) {
		Text source = (Text) event.getSource();
		if (source.equals(oldPassword)) {
			setErrorMessage(null);
		}
	}

	/**
	 * Sets the user to change. This does not update the UI, but will bypass old password checking if the dialog is already open.
	 *
	 * @param userToChange the user to change the password for
	 */
	public final void setUserToChange(final CmUser userToChange) {
		this.userToChange = userToChange;
		requireOldPassword = userToChange != null && userToChange.getUidPk() == LoginManager.getCmUserId();
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
}
