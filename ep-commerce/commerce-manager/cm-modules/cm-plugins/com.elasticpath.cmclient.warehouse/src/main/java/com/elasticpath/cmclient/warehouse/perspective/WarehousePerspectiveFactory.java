/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse.perspective;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.PerspectiveDefaults;
import com.elasticpath.cmclient.jobs.views.WarehouseJobListView;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.views.SearchView;
import com.elasticpath.cmclient.warehouse.views.orderreturn.OrderReturnSearchResultsView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.store.WarehouseService;

/**
 * Factory for specifying the layout of the perspective.
 */
public class WarehousePerspectiveFactory implements IPerspectiveFactory {

	private Warehouse currentWarehouse;
	private List<Warehouse> warehouses;

	/**
	 * Warehouse perspective ID.
	 */
	public static final String PERSPECTIVE_ID = "com.elasticpath.cmclient.warehouse.perspective"; //$NON-NLS-1$

	/**
	 * Called by Eclipse to layout the perspective.
	 * 
	 * @param layout the page layout
	 */
	@Override
	public void createInitialLayout(final IPageLayout layout) {
		final String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		initWarehouses();
		if (getInstance().warehouses.isEmpty()) {
			MessageDialog.openInformation(null, WarehouseMessages.get().ThereAreNoWarehousesMsgBoxTitle,
					WarehouseMessages.get().ThereAreNoWarehousesMsgBoxText);
		} else {
			layout.addView(SearchView.ID_SEARCH_VIEW, IPageLayout.LEFT, PerspectiveDefaults.LEFT_RATIO, editorArea);
			IFolderLayout right = layout.createFolder("searchResults", IPageLayout.TOP, PerspectiveDefaults.TOP_RATIO, editorArea); //$NON-NLS-1$
			right.addView(OrderReturnSearchResultsView.VIEW_ID);
			right.addPlaceholder(WarehouseJobListView.VIEW_ID);

			layout.getViewLayout(SearchView.ID_SEARCH_VIEW).setCloseable(false);
			layout.getViewLayout(OrderReturnSearchResultsView.VIEW_ID).setMoveable(false);			
		}

		layout.addFastView(IPageLayout.ID_PROGRESS_VIEW);
	}

	/**
	 * Gets a session instance of <code>WarehousePerspectiveFactory</code>.
	 *
	 * @return session instance of <code>WarehousePerspectiveFactory</code>
	 */
	public static WarehousePerspectiveFactory getInstance() {
		return CmSingletonUtil.getSessionInstance(WarehousePerspectiveFactory.class);
	}

	/**
	 * Initialize Warehouses.
	 */
	@SuppressWarnings("PMD.NonThreadSafeSingleton")
	public static void initWarehouses() {
		WarehousePerspectiveFactory instance = getInstance();
		instance.warehouses = ((WarehouseService) ServiceLocator.getService(ContextIdNames.WAREHOUSE_SERVICE)).findAllWarehouses();
		AuthorizationService.getInstance().filterAuthorizedWarehouses(instance.warehouses);
		
		if (instance.currentWarehouse != null) {
			for (Warehouse warehouse : instance.warehouses) {
				if (warehouse.getName().equals(instance.currentWarehouse.getName())) {
					instance.currentWarehouse = warehouse;
					return;
				}
			}
		}
		instance.currentWarehouse = getDefaultWarehouse();
	}

	private static Warehouse getDefaultWarehouse() {
		if (getInstance().warehouses.isEmpty()) {
			return null;
		}
		return getInstance().warehouses.get(0);
	}

	/**
	 * @return the currentWarehouse
	 */
	public static Warehouse getCurrentWarehouse() {
		return getInstance().currentWarehouse;
	}

	/**
	 * @param currentWarehouse the currentWarehouse to set
	 */
	public static void setCurrentWarehouse(final Warehouse currentWarehouse) {
		getInstance().currentWarehouse = currentWarehouse;
	}

	/**
	 * @return the warehouses
	 */
	public static List<Warehouse> getWarehouses() {
		return getInstance().warehouses;
	}
}
