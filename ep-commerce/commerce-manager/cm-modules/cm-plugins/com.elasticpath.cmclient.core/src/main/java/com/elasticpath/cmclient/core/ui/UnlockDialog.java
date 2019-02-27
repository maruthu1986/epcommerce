/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;

/**
 * Dialog to unlock the user after 15 minute of idle lock.
 */
public class UnlockDialog extends EpLoginDialog {

	/**
	 * Creates Unlock Dialog with null parent shell.
	 */
	public UnlockDialog() {
		this(null);
	}

	/**
	 * Creates Unlock Dialog.
	 * 
	 * @param parentShell the active shell.
	 */
	public UnlockDialog(final Shell parentShell) {
		super(parentShell);
		this.setShellStyle(SWT.TITLE);
	}

	@Override
	protected EpState getUserIdState() {
		return EpState.DISABLED;
	}

	@Override
	protected String getTitle() {
		return CoreMessages.get().UnlockDialog_Title;
	}

	@Override
	protected Image getWindowImage() {
		return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_UNLOCK);
	}

	@Override
	protected String getWindowTitle() {
		return CoreMessages.get().UnlockDialog_WindowTitle;
	}
	
	@Override
	protected void populateControls() {
		populateLoginFields(LoginManager.getCmUserUsername(), ""); //$NON-NLS-1$
	}

	@Override
	protected void handleShellCloseEvent() {
		//user should not have ability to close this window
	}
}
