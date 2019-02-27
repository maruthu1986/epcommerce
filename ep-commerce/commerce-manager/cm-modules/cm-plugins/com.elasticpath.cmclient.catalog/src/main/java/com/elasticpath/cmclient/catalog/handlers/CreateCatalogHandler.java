/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.catalog.dialogs.catalog.CreateCatalogDialog;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.catalog.CatalogService;

/**
 * Create Catalog handler.
 */
public class CreateCatalogHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent arg0) throws ExecutionException {
		Catalog catalog = ServiceLocator.getService(ContextIdNames.CATALOG);
		catalog.setMaster(true);

		final CreateCatalogDialog createCatalogDialog = new CreateCatalogDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), catalog);

		if (createCatalogDialog.open() == 0) {
			CatalogService catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
			catalog = catalogService.saveOrUpdate(catalog);

			// Fire an event to refresh the browse list view
			final ItemChangeEvent<Catalog> event = new ItemChangeEvent<>(this, catalog, ItemChangeEvent.EventType.ADD);
			CatalogEventService.getInstance().notifyCatalogChanged(event);
		}

		return createCatalogDialog.getWindowManager();
	}
	
	@Override
	public boolean isEnabled() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATALOG_MANAGE);
	}
}
