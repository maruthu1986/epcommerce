/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.actions;

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.actions.AbstractDynamicPullDownAction;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.perspective.WarehousePerspectiveFactory;
import com.elasticpath.cmclient.warehouse.views.SearchView;
import com.elasticpath.domain.store.Warehouse;

/**
 * This action should be called when new warehouse was selected.
 */
public class SwitchWarehouseAction extends AbstractDynamicPullDownAction<Warehouse> {

	private final Warehouse warehouse;

	/**
	 * Switch to the given warehouse.
	 *
	 * @param warehouse warehouse.
	 */
	public SwitchWarehouseAction(final Warehouse warehouse) {
		super(warehouse.getName(), AS_RADIO_BUTTON);
		this.warehouse = warehouse;
	}

	@Override
	public void run() {
		// keep a reference to the selected warehouse
		WarehousePerspectiveFactory.setCurrentWarehouse(warehouse);

		// redraw the view title to contain the newly selected warehouse name
		SearchView view = (SearchView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SearchView.ID_SEARCH_VIEW);
		view.setPartName(
			NLS.bind(WarehouseMessages.get().Warehouse_Title,
			warehouse.getName()));
		view.refresh();
	}

	@Override
	public Warehouse getPullDownObject() {
		return warehouse;
	}
}
