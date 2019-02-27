/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ui.dialog.ChangePaginationDialog;

/**
 * Used to open up the change pagination settings dialog.
 */
public class ChangePaginationHandler extends AbstractHandler {

	@Override
	
	public Object execute(final ExecutionEvent arg0) throws ExecutionException {
		new ChangePaginationDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()).open();
		return null;
	}
}
