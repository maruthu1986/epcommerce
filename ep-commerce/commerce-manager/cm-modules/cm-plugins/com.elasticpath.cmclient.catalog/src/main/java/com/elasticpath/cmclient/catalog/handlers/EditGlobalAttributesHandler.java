/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.catalog.dialogs.catalog.AddEditGlobalAttributesDialog;
import com.elasticpath.cmclient.core.service.AuthorizationService;

/**
 * Edit Global Attributes handler.
 */
public class EditGlobalAttributesHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent arg0) throws ExecutionException {
		AddEditGlobalAttributesDialog dialog = new AddEditGlobalAttributesDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
		dialog.open();

		return dialog.getWindowManager();
	}
	
	@Override
	public boolean isEnabled() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.GLOBAL_ATTRIBUTE_EDIT);
	}
}