/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.actions;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import com.elasticpath.cmclient.core.actions.AbstractDynamicPullDownActionDelegate;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.WarehouseListener;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.warehouse.perspective.WarehousePerspectiveFactory;
import com.elasticpath.domain.store.Warehouse;

/**
 * Gets all the warehouses accessible by current CM and creates the pull down menu.
 */
public class WarehousePullDownDelegate extends AbstractDynamicPullDownActionDelegate<SwitchWarehouseAction, Warehouse>
		implements IPerspectiveListener, WarehouseListener {

	private static final Logger LOG = Logger.getLogger(WarehousePullDownDelegate.class);

	@Override
	protected void preInitialize(final IWorkbenchWindow workbenchWindow) {
		LOG.debug("WarehousePullDownDelegate initialized"); //$NON-NLS-1$
		workbenchWindow.addPerspectiveListener(this);
		CatalogEventService.getInstance().addWarehouseListener(this);		
	}

	@Override
	protected SwitchWarehouseAction createPullDownAction(final Warehouse menuObject) {
		return new SwitchWarehouseAction(menuObject);
	}

	@Override
	public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
		if (perspective.getId().equals(WarehousePerspectiveFactory.PERSPECTIVE_ID)) {			
			refresh();
		}
	}

	@Override
	public void warehouseChange(final ItemChangeEvent<Warehouse> event) {
		WarehousePerspectiveFactory.initWarehouses();		
	}

	@Override
	protected Warehouse getActiveMenuObject() {
		return WarehousePerspectiveFactory.getCurrentWarehouse();
	}

	@Override
	protected Collection<Warehouse> getAvailableMenuObjects() {
		List<Warehouse> warehouses = WarehousePerspectiveFactory.getWarehouses();
		warehouses.sort(Comparator.comparing(Warehouse::getName));
		
		return warehouses;
	}

	@Override
	protected boolean isEnabled() {
		return true;
	}

	@Override
	public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective, final String changeId) {
		// empty
	}
}
