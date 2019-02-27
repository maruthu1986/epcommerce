/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.actions.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.warehouse.WarehousePermissions;
import com.elasticpath.cmclient.warehouse.dialogs.CompleteShipmentDialog;
import com.elasticpath.cmclient.warehouse.perspective.WarehousePerspectiveFactory;

/**
 * Action handler that opens complete shipment dialog.
 * 
 * @see CompleteShipmentDialog
 */
public class CompleteShipmentActionHandler extends AbstractHandler {
	
	/**
	 * Opens Complete shipment dialog.
	 * 
	 * @param event event that carries information about the current state of the application
	 * @return true if the user presses the OK button, false otherwise.
	 * @throws ExecutionException throws exception if exception occurred during the execution of a command.
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		return CompleteShipmentDialog.openDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
	}
	
	@Override
	public boolean isEnabled() {
		return AuthorizationService.getInstance().isAuthorizedForWarehouse(WarehousePerspectiveFactory.getCurrentWarehouse())
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(WarehousePermissions.WAREHOUSE_ORDER_SHIPMENT_COMPLETE);
	}
	
}
