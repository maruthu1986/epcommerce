/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.warehouses.views;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.admin.warehouses.AdminWarehousesImageRegistry;
import com.elasticpath.cmclient.admin.warehouses.AdminWarehousesMessages;
import com.elasticpath.cmclient.admin.warehouses.AdminWarehousesPlugin;
import com.elasticpath.cmclient.admin.warehouses.actions.CreateWarehouseAction;
import com.elasticpath.cmclient.admin.warehouses.actions.DeleteWarehouseAction;
import com.elasticpath.cmclient.admin.warehouses.actions.EditWarehouseAction;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.store.WarehouseService;

/**
 * The Warehouse list view controller.
 */
public class WarehouseListView extends AbstractListView {

	/** The View's ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.warehouses.views.WarehouseListView"; //$NON-NLS-1$

	private static final String WAREHOUSE_LIST_TABLE = "Warehouse List"; //$NON-NLS-1$

	/** Column indices. */

	private static final int INDEX_WAREHOUSENAME = 0;

	private static final int INDEX_PICKDELAY = 1;

	private static final int INDEX_STREET = 2;

	private static final int INDEX_CITY = 3;

	private static final int INDEX_COUNTRY = 4;

	/** Actions. */
	private Action createWarehouseAction;

	private Action editWarehouseAction;

	private Action deleteWarehouseAction;

	/** Column widths. */
	private static final int[] COLUMN_WIDTHS = new int[]{200, 100, 150, 100, 100};

	/** Instance of <code>WarehouseService</code>. */
	private final WarehouseService warehouseService;

	/**
	 * The constructor.
	 */
	public WarehouseListView() {
		super(false, WAREHOUSE_LIST_TABLE);
		warehouseService = (WarehouseService) ServiceLocator.getService(ContextIdNames.WAREHOUSE_SERVICE);
	}

	@Override
	protected String getPluginId() {
		return AdminWarehousesPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {

		Separator warehouseActionGroup = new Separator("warehouseActionGroup"); //$NON-NLS-1$
		getToolbarManager().add(warehouseActionGroup);

		createWarehouseAction = new CreateWarehouseAction(this, AdminWarehousesMessages.get().CreateWarehouse,
				AdminWarehousesImageRegistry.IMAGE_WAREHOUSE_CREATE);
		createWarehouseAction.setToolTipText(AdminWarehousesMessages.get().CreateWarehouse);
		editWarehouseAction = new EditWarehouseAction(this, AdminWarehousesMessages.get().EditWarehouse,
				AdminWarehousesImageRegistry.IMAGE_WAREHOUSE_EDIT);
		editWarehouseAction.setToolTipText(AdminWarehousesMessages.get().EditWarehouse);
		addDoubleClickAction(editWarehouseAction);
		deleteWarehouseAction = new DeleteWarehouseAction(this, AdminWarehousesMessages.get().DeleteWarehouse,
				AdminWarehousesImageRegistry.IMAGE_WAREHOUSE_DELETE);
		deleteWarehouseAction.setToolTipText(AdminWarehousesMessages.get().DeleteWarehouse);

		ActionContributionItem createWarehouseActionContributionItem = new ActionContributionItem(createWarehouseAction);
		ActionContributionItem editWarehouseActionContributionItem = new ActionContributionItem(editWarehouseAction);
		ActionContributionItem deleteWarehouseActionContributionItem = new ActionContributionItem(deleteWarehouseAction);

		createWarehouseActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editWarehouseActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		deleteWarehouseActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(warehouseActionGroup.getGroupName(), editWarehouseActionContributionItem);
		getToolbarManager().appendToGroup(warehouseActionGroup.getGroupName(), createWarehouseActionContributionItem);
		getToolbarManager().appendToGroup(warehouseActionGroup.getGroupName(), deleteWarehouseActionContributionItem);

		// set initial button state and add event listener to manage state as warehouse items are selected
		setButtonActionState();
		this.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				setButtonActionState();
			}
		});
	}

	/**
	 * Manages the state of the button (Create-, Edit- and Delete Warehouse) actions.
	 */
	protected void setButtonActionState() {
		// if the user is able to view the warehouses the plugin has been enabled
		// and therefore has Manage Warehouse role
		createWarehouseAction.setEnabled(true);

		// the Warehouse Dialog will manage edit permission
		// just make sure a warehouse has been selected
		Warehouse warehouse = getSelectedWarehouse();
		editWarehouseAction.setEnabled(warehouse != null);
		
		// make sure a warehouse has been selected 
		// and the CM user has permission to edit the warehouse
		deleteWarehouseAction.setEnabled(isSelectedWarehouseAuthorized());
	}
	
	/**
	 * Checks the authorization level of the selected warehouse.
	 * 
	 * @return True only if a warehouse is selected and the CM user is authorized to edit it.
	 */
	protected boolean isSelectedWarehouseAuthorized() {
		boolean authorized = false;
		
		Warehouse warehouse = getSelectedWarehouse();
		if (warehouse != null) {
			authorized = AuthorizationService.getInstance().isAuthorizedForWarehouse(warehouse);
		}
		
		return authorized;
	}

	@Override
	protected Object[] getViewInput() {
		List<Warehouse> warehouseList = warehouseService.findAllWarehouses();
		
		warehouseList.sort((warehouse1, warehouse2) -> warehouse1.getName().compareTo(warehouse2.getName()));
		
		return warehouseList.toArray(new Warehouse[warehouseList.size()]);
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {
		String[] columnNames = new String[] {
				AdminWarehousesMessages.get().WarehouseName, AdminWarehousesMessages.get().PickDelay, AdminWarehousesMessages.get().Street,
				AdminWarehousesMessages.get().City, AdminWarehousesMessages.get().Country };

		for (int i = 0; i < columnNames.length; i++) {
			table.addTableColumn(columnNames[i], COLUMN_WIDTHS[i]);
		}

	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new WarehouseListViewLabelProvider();
	}

	/**
	 * Label provider.
	 */
	protected class WarehouseListViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		private final Geography geography;

		/** Default constructor. */
		public WarehouseListViewLabelProvider() {
			geography = ServiceLocator.getService(ContextIdNames.GEOGRAPHY);
		}

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			Warehouse warehouse = (Warehouse) element;

			switch (columnIndex) {
			case WarehouseListView.INDEX_WAREHOUSENAME:
				return warehouse.getName();
			case WarehouseListView.INDEX_PICKDELAY:
				return String.valueOf(warehouse.getPickDelay());
			case WarehouseListView.INDEX_STREET:
				return warehouse.getAddress().getStreet1();
			case WarehouseListView.INDEX_CITY:
				return warehouse.getAddress().getCity();
			case WarehouseListView.INDEX_COUNTRY:
				return geography.getCountryDisplayName(warehouse.getAddress().getCountry(), CorePlugin.getDefault().getDefaultLocale());
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}

	/**
	 * Gets the currently-selected warehouse.
	 * 
	 * @return the currently-selected Warehouse, null if nothing selected.
	 */
	public Warehouse getSelectedWarehouse() {
		IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		Warehouse warehouse = null;
		if (!selection.isEmpty()) {
			warehouse = (Warehouse) selection.getFirstElement();
		}
		return warehouse;
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}