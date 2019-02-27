/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import java.util.Map;

import com.google.common.annotations.VisibleForTesting;

import org.apache.log4j.Logger;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elasticpath.cmclient.core.ui.EpLoginChangePasswordDialog;
import com.elasticpath.cmclient.core.ui.EpLoginDialog;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * This class opens a login dialog and provides
 * a reference to a {@link CmUser} instance of the logged-in user.
 */
public class LoginManager implements ILoginManager {

	private static final Logger LOG = Logger.getLogger(LoginManager.class);

	private CmUser cmUser;

	@Override
	public boolean login() {
		LOG.debug("Logging in..."); //$NON-NLS-1$

		EpLoginDialog loginDialog;

		do {
			loginDialog = new EpLoginDialog();

			if (loginDialog.open() != Window.OK) {
				return false;
			}
		} while (!loginDialog.isAuthenticatedAndAuthorized());

		//User is logged in. Add pop up to prevent from leaving the page
		EpExitConfirmation.enableService();

		updateCmUserLoginData();
		return handleRelogin(this.cmUser);
	}

	/**
	 * Performs logic required at login time that requires the spring context up and running and updates failed user login attempts.
	 */
	protected void updateCmUserLoginData() {
		LOG.debug("Updating CmUser record with login time"); //$NON-NLS-1$
		// Set the current CmUser and register their logindate
		final CmUserService service = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
		final CmUser cmUser = getFreshCmUserInstance();
		storeUserIdForAuditing(cmUser);

		this.cmUser = service.updateUserAfterLogin(cmUser);
	}

	private void storeUserIdForAuditing(final CmUser cmUser) {
		Map<String, String> metadata = ServiceLocator.getService(ContextIdNames.PERSISTENCELISTENER_METADATA_MAP);

		metadata.put(WebConstants.USER_GUID, cmUser.getGuid());
	}

	/**
	 * Get the LoginManager instance.
	 *
	 * @return the application instance
	 */
	public static LoginManager getInstance() {
		return CmSingletonUtil.getSessionInstance(LoginManager.class);
	}

	/**
	 * Gets the current CmUser.
	 *
	 * @return the current CmUser
	 */
	public static CmUser getCmUser() {

		return getInstance().getLocalCmUser();
	}

	/**
	 * Returns CM User Guid.
	 *
	 * @return the current CmUser Guid
	 */
	public static String getCmUserGuid() {
		return getCmUser().getGuid();
	}

	/**
	 * Returns CM User username.
	 *
	 * @return the current CmUser username
	 */
	public static String getCmUserUsername() {
		return getCmUser().getUserName();
	}

	/**
	 * Returns CM User id.
	 *
	 * @return the current CmUser id
	 */
	public static long getCmUserId() {
		return getCmUser().getUidPk();
	}

	private CmUser getLocalCmUser() {
		return this.cmUser;
	}

	/**
	 * Sets the LoginManager's CmUser. Primarily used for testing.
	 *
	 * @param cmUser the cmUser to set
	 */
	@VisibleForTesting
	public void setCmUser(final CmUser cmUser) {
		this.cmUser = cmUser;
	}

	/**
	 * Opens the login dialog.
	 *
	 * @return 0 ({@link IApplication#EXIT_OK}) if application workbench is created and started properly, otherwise
	 * 23 (@link IApplication#EXIT_RESTART) if restart was requested or
	 * 1 in case of error
	 */
	public Object showLoginDialog() {
		Display display = DisplayCreator.createDisplay();

		try {
			if (!login()) {
				return IApplication.EXIT_OK;
			}

			final int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());

			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}

			return IApplication.EXIT_OK;
		} catch (final Exception e) {
			LOG.error("Startup failed due to exception", e); //$NON-NLS-1$
			MessageDialog.openError(null, "Error", "Commerce Manager startup failed due to:\n\n" + e.getMessage()); //$NON-NLS-2$
			return 1;
		} finally {
			display.dispose();
		}
	}

	private CmUser getFreshCmUserInstance() {
		final CmUserService cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
		return cmUserService.findByUserNameWithAccessInfo(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	private boolean handleRelogin(final CmUser cmUser) {
		if (cmUser.isTemporaryPassword() || cmUser.isPasswordExpired()) {
			EpLoginChangePasswordDialog passwordDialog = new EpLoginChangePasswordDialog(null, cmUser);
			return passwordDialog.open() == Window.OK;
		}
		return true;
	}
}
