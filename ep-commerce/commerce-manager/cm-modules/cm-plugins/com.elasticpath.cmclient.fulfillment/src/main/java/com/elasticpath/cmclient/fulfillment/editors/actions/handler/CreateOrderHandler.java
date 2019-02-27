/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.actions.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.fulfillment.FulfillmentFeatureEnablementPropertyTester;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.editors.actions.OpenEpBrowserContributionAction;
import com.elasticpath.cmclient.fulfillment.editors.order.dialog.StoreSelectionDialog;
import com.elasticpath.domain.store.Store;

/**
 * Create order handler that handles the create order tool bar icon and 
 * menu item click event.
 *
 */
public class CreateOrderHandler extends AbstractHandler {

	@Override
	public boolean isEnabled() {
		AuthorizationService authService = AuthorizationService.getInstance();
		return FulfillmentFeatureEnablementPropertyTester.ENABLE_CREATE_ORDER
			&& authService.isAuthorizedWithPermission(FulfillmentPermissions.ORDER_EDIT);
	}

	@Override
	public Object execute(final ExecutionEvent arg0) throws ExecutionException {
		StoreSelectionDialog storeSelectionDialog = new StoreSelectionDialog(
				PlatformUI.getWorkbench().getDisplay().getActiveShell());
		int result = storeSelectionDialog.open();
		if (result == Window.OK) {
			Store selectedStore = storeSelectionDialog.getSelectedStore();
			if (selectedStore == null) {
				return null;
			}
			OpenEpBrowserContributionAction openBrowserAction = new OpenEpBrowserContributionAction(
				storeSelectionDialog.getSelectedStore(), PlatformUI
				.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActivePart().getSite());
			openBrowserAction.run();
		}
		
		return storeSelectionDialog.getShell();
	}

}
