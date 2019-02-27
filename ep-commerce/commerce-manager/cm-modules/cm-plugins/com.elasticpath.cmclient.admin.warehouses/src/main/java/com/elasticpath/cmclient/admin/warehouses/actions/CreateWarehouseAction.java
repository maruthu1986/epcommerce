/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.warehouses.actions;

import com.elasticpath.cmclient.core.ServiceLocator;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.admin.warehouses.dialogs.WarehouseDialog;
import com.elasticpath.cmclient.admin.warehouses.views.WarehouseListView;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.WarehouseAddress;
import com.elasticpath.service.store.WarehouseService;

/**
 * Create action implementation.
 */
public class CreateWarehouseAction extends Action {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(CreateWarehouseAction.class);

	/** Warehouse list view. */
	private final WarehouseListView listView;

	/**
	 * Constructor.
	 * 
	 * @param listView the warehouses list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public CreateWarehouseAction(final WarehouseListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("CreateWarehouse Action called."); //$NON-NLS-1$

		Warehouse warehouse = (Warehouse) ServiceLocator.getService(ContextIdNames.WAREHOUSE);
		WarehouseAddress warehouseAddress = (WarehouseAddress) ServiceLocator.getService(ContextIdNames.WAREHOUSE_ADDRESS);
		warehouseAddress.setCountry(CorePlugin.getDefault().getDefaultLocale().getCountry());
		warehouse.setAddress(warehouseAddress);

		boolean dialogOk = WarehouseDialog.openCreateDialog(listView.getSite().getShell(), warehouse);
		if (dialogOk) {
			WarehouseService warehouseService = (WarehouseService) ServiceLocator.getService(
					ContextIdNames.WAREHOUSE_SERVICE);
			final Warehouse updatedWarehouse = warehouseService.saveOrUpdate(warehouse);
			listView.refreshViewerInput();
			
			CatalogEventService.getInstance().notifyWarehouseChanged(new ItemChangeEvent<>(this, updatedWarehouse, EventType.ADD));
		}
	}

}