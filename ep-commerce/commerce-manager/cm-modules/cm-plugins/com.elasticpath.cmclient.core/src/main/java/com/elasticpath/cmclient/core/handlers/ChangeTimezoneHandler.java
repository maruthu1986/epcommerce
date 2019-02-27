/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ui.dialog.ChangeTimezoneDialog;

/**
 * Used to open up the change timezone settings dialog.
 */
public class ChangeTimezoneHandler extends AbstractHandler {

	@Override
	
	public Object execute(final ExecutionEvent arg0) throws ExecutionException {
		new ChangeTimezoneDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()).open();
		return null;
	}
}
