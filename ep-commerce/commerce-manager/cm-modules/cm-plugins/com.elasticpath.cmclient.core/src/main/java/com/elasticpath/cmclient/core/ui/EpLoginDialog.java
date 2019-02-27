/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;

import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthenticationService;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.impl.AuthenticationServiceImpl;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.settings.SettingsReader;

/**
 * The EP login dialog box.
 */
public class EpLoginDialog extends AbstractEpDialog {

	private static final Logger LOG = Logger.getLogger(EpLoginDialog.class);
	private static final String SETTING_GLOBAL_SENDER_ADDRESS = "COMMERCE/SYSTEM/EMAIL/emailGlobalSenderAddress";

	private final AuthenticationService authenticationService = AuthenticationServiceImpl.getInstance();
	private final DataBindingContext dbc = new DataBindingContext(SWTObservables.getRealm(Display.getCurrent()));

	private static final int INITIAL_SIZE_X = 450;
	private static final int INITIAL_SIZE_Y = 275;

	private Text passwordText;
	private Text userIdText;

	/**
	 * Creates the dialog with null parent shell.
	 */
	public EpLoginDialog() {
		this(null);
		setShellStyle(SWT.TITLE);
	}

	/**
	 * Creates the dialog.
	 *
	 * @param parentShell the parent shell for the dialog
	 */
	public EpLoginDialog(final Shell parentShell) {
		super(parentShell, 2, false);
	}

	private DataBindingContext getBindingContext() {
		return this.dbc;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, OK, CoreMessages.get().EpLoginDialog_LoginButton, true);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {

		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		dialogComposite.addLabelBold(CoreMessages.get().EpLoginDialog_LoginUserId, null);
		userIdText = dialogComposite.addTextField(getUserIdState(), fieldData);
		userIdText.setFocus();

		dialogComposite.addLabelBold(CoreMessages.get().EpLoginDialog_Password, null);
		passwordText = dialogComposite.addPasswordField(EpState.EDITABLE, fieldData);
	}

	/**
	 * Gets the state for user id controls.
	 *
	 * @return the EpState
	 */
	protected EpState getUserIdState() {
		return EpState.EDITABLE;
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
		populateLoginFields("", ""); //$NON-NLS-1$
	}

	/**
	 * Populates login filed with given values.
	 *
	 * @param userId   the user id
	 * @param password the user password
	 */
	protected void populateLoginFields(final String userId, final String password) {
		userIdText.setText(userId);
		passwordText.setText(password);
	}

	@Override
	protected void bindControls() {

		bindControl(userIdText);
		bindControl(passwordText);

	}

	@Override
	protected void okPressed() {
		// Ideally the OK button would not be enabled until validation passed, but right now there's a problem
		// with the validation framework in that if we listen to the control modification and then check for validation
		// errors, the check will be performed before the validation is updated by the dbc.
		dbc.updateModels();
		for (final Iterator<?> iterator = this.dbc.getValidationStatusMap().values().iterator(); iterator.hasNext();) {
			final IStatus currStatus = (IStatus) iterator.next();
			if (!currStatus.isOK()) {
				// do nothing
				return;
			}
		}
		processLoginRequest();

		super.okPressed();
	}

	private void processLoginRequest() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Saving connection info"); //$NON-NLS-1$
		}

		final String userId = userIdText.getText();
		final String password = passwordText.getText();

		// Do the login
		try {
			authenticationService.login(userId, password);
			if (!AuthorizationService.isAuthorized()) {
				MessageDialog.openError(getShell(), CoreMessages.get().EpLoginDialog_ErrorTitle_AuthorizationFailed,
					CoreMessages.get().EpLoginDialog_Error_AuthorizationFailed);
			}

		} catch (final BadCredentialsException badCredentialsException) {
			MessageDialog.openError(getShell(), CoreMessages.get().EpLoginDialog_ErrorTitle_AuthenticationFailed,
				CoreMessages.get().EpLoginDialog_Error_AuthenticationFailed);
		} catch (final LockedException lockedException) {

			String adminEmailAddress = getAdminEmailAddress();

			MessageDialog.openError(getShell(), CoreMessages.get().EpLoginDialog_LockedTitle_AccountLocked,
				NLS.bind(CoreMessages.get().EpLoginDialog_LockedMessage_AccountLocked,
				adminEmailAddress));

		} catch (final AuthenticationException authenticationException) {
			MessageDialog.openError(getShell(), CoreMessages.get().EpLoginDialog_ErrorTitle_ServerCommunication, authenticationException
				.getLocalizedMessage());
		}
	}

	@Override
	protected String getTitle() {
		return null;
	}

	@Override
	protected Image getTitleImage() {
		return CoreImageRegistry.getImage(CoreImageRegistry.BRANDING_IMAGE);
	}

	@Override
	protected String getWindowTitle() {
		return CoreMessages.get().EpLoginDialog_Title;
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	protected String getInitialMessage() {
		return " "; //$NON-NLS-1$
	}

	@Override
	protected Point getInitialSize() {
		return new Point(INITIAL_SIZE_X, INITIAL_SIZE_Y);
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	/**
	 * Check if CM user is authenticated and authorized.
	 *
	 * @return true if user is authenticated and authorized.
	 */
	public boolean isAuthenticatedAndAuthorized() {
		return authenticationService.isAuthenticated() && AuthorizationService.isAuthorized();
	}

	private String getAdminEmailAddress() {
		SettingsReader settingsReader = ServiceLocator.getService("settingsService");
		return settingsReader.getSettingValue(SETTING_GLOBAL_SENDER_ADDRESS).getValue();
	}

	private void bindControl(final Control control) {
		final boolean hideDecorationOnFirstValidation = true;

		EpControlBindingProvider.getInstance()
			.bind(getBindingContext(), control, EpValidatorFactory.STRING_255_REQUIRED,
				null, getDummyUpdateStrategy(), hideDecorationOnFirstValidation);
	}

	/*
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
