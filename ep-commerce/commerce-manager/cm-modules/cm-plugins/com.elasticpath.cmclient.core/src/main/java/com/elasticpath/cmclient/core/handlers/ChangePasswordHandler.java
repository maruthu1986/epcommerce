/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ui.dialog.ChangePasswordDialog;

/**
 * Action to open up the password change dialog.
 */
public class ChangePasswordHandler extends AbstractHandler {

	@Override
	
	public Object execute(final ExecutionEvent arg0) throws ExecutionException {
		new ChangePasswordDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
			LoginManager.getCmUser(), true)
			.open();
		return null;
	}
}
