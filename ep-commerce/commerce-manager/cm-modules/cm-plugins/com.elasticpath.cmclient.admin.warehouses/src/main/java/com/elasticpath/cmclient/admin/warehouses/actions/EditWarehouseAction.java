/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.warehouses.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.admin.warehouses.AdminWarehousesMessages;
import com.elasticpath.cmclient.admin.warehouses.dialogs.WarehouseDialog;
import com.elasticpath.cmclient.admin.warehouses.views.WarehouseListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.store.WarehouseService;

/**
 * Edit action implementation.
 */
public class EditWarehouseAction extends Action {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(EditWarehouseAction.class);

	/** Warehouses list view. */
	private final WarehouseListView listView;

	/**
	 * Constructor.
	 *
	 * @param listView the warehouses list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public EditWarehouseAction(final WarehouseListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("EditWarehouse Action called."); //$NON-NLS-1$

		WarehouseService warehouseService = (WarehouseService) ServiceLocator.getService(ContextIdNames.WAREHOUSE_SERVICE);

		Warehouse selectedWarehouse = listView.getSelectedWarehouse();
		Warehouse selectedWarehouseToEdit = (Warehouse) warehouseService.getObject(selectedWarehouse.getUidPk());

		if (selectedWarehouseToEdit == null) {
			MessageDialog.openInformation(listView.getSite().getShell(), AdminWarehousesMessages.get().EditWarehouse,
				NLS.bind(AdminWarehousesMessages.get().WarehouseNoLongerExists,
				selectedWarehouse.getName()));
			listView.refreshViewerInput();
			return;
		}

		boolean dialogOk = WarehouseDialog.openEditDialog(listView.getSite().getShell(), selectedWarehouseToEdit);
		if (dialogOk) {
			final Warehouse updatedWarehouse = warehouseService.saveOrUpdate(selectedWarehouseToEdit);
			listView.refreshViewerInput();
			CatalogEventService.getInstance().notifyWarehouseChanged(new ItemChangeEvent<>(this, updatedWarehouse, EventType.CHANGE));
		}
	}

}