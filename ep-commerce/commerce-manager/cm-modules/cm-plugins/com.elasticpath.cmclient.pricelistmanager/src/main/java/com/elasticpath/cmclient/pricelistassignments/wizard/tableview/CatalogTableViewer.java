/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistassignments.wizard.tableview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.comparator.CatalogComparator;
import com.elasticpath.cmclient.core.helpers.extenders.EPTableColumnCreator;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerImageRegistry;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPlugin;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.catalog.CatalogService;

/**
 *
 * Handles the management of a catalog list table viewer.
 */
public class CatalogTableViewer {

	private static final int TABLE_HEIGHT = 200;
	private static final int COLUMN_WIDTH = 300;
	private static final int SELECTED_ICON_WIDTH = 25;
	
	private static final int ICON_COLUMN_INDEX = 0;
	
	private static final Logger LOG = Logger
			.getLogger(CatalogTableViewer.class);
	
	private final ChangeSelectionController changeSelectionController = new ChangeSelectionController();
	private TableItem currentSelectedRow;
	private Table table;
	
	/**
	 * Initializes table with list {@link Catalog}. All available
	 * {@link Catalog} are selected. Table has 2 columns - Selected Icon and Name
	 * 
	 * @param epTableViewer -
	 *            table viewer that holds table.
	 * 
	 * @param model - selected {@link Catalog} saved in the model.
	 * @param wizardContainer - container of the wizard
	 */
	public void initTable(final IEpTableViewer epTableViewer,
			final Catalog model,
			final IWizardContainer wizardContainer) {
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		gridData.heightHint = TABLE_HEIGHT;
		table = epTableViewer.getSwtTable();
		table.setLayoutData(gridData);
		createColumns(epTableViewer);
		epTableViewer.setContentProvider(new CatalogTableContentProvider());
		epTableViewer.setLabelProvider(new CatalogTableLableProvider());
		epTableViewer.setInput(getCatalogsSorted());
		
		table.addSelectionListener(changeSelectionController);
		
		markSelected(table, model);
	} 

	private List<Catalog> getCatalogsSorted() {
		List<Catalog> all = getAllObjects();
		Collections.sort(all, new CatalogComparator());
		return all;
	}

	/**
	 * Marks in the grid selected {@link Catalog}.
	 * 
	 * @param table
	 * @param objectToMark
	 */
	private void markSelected(final Table table,
			final Catalog objectToMark) {
		if (null != objectToMark) {
			TableItem[] items = table.getItems();
			TableItem itemtoselect = null;
			for (TableItem tableItem : items) {
				if (tableItem.getData().equals(objectToMark)) {
					itemtoselect = tableItem;
					break;
				}
			}
			if (null != itemtoselect) {
				itemtoselect.setImage(ICON_COLUMN_INDEX, PriceListManagerImageRegistry.getImage(
						PriceListManagerImageRegistry.IMAGE_ROW_SELECTED_SMALL));
				currentSelectedRow = itemtoselect;
				// select this item
				table.setSelection(itemtoselect);
				//scroll item to the top of the table
				table.setTopIndex(table.getSelectionIndex());
			}
		}
	}

	
	private List<Catalog> getAllObjects() {

		CmUser currentUser = LoginManager.getCmUser();
		CatalogService service = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
		List<Catalog> catalogs = service.findAllCatalogs();
		if (!currentUser.isAllCatalogsAccess()) {
			List<Catalog> retainCatalogs = new ArrayList<>();
			for (Catalog cat : catalogs) {
				if (AuthorizationService.getInstance().isAuthorizedForCatalog(cat)) {
					retainCatalogs.add(cat);
				}				
			}
			return retainCatalogs;
		}
		return catalogs;
	}

	private void createColumns(final IEpTableViewer epTableViewer) {
		final String[] columnNames = getTableColumns();
		final int[] columnWidths = new int[] { SELECTED_ICON_WIDTH, COLUMN_WIDTH };
		
		for (int i = 0; i < columnNames.length; i++) {
			int width = -1;
			if (columnWidths.length > i) {
				width = columnWidths[i];
			}
			epTableViewer.addTableColumn(columnNames[i], width);
		}
	}

	private String[] getTableColumns() {
		ArrayList<String> strings =  new ArrayList<>();
		strings.add("");
		strings.add(PriceListManagerMessages.get().Catalog_Column_Name);

		for (EPTableColumnCreator tableExtender : findTableExtenders(getClass().getSimpleName(), PriceListManagerPlugin.PLUGIN_ID)) {
			strings.addAll(tableExtender.visitColumnNames());
		}
		return strings.toArray(new String[strings.size()]);
	}

	private List<EPTableColumnCreator> findTableExtenders(final String tableId, final String pluginId) {
		return PluginHelper.findTables(tableId, pluginId);
	}


	/**
	 * A controller for when a selection gets changed.
	 */
	private final class ChangeSelectionController extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			if (currentSelectedRow != null) {
				currentSelectedRow.setImage(ICON_COLUMN_INDEX, PriceListManagerImageRegistry.getImage(
						PriceListManagerImageRegistry.IMAGE_EMPTY_ICON_SMALL));
			}								
			TableItem selectedRow = (TableItem) event.item;
			selectedRow.setImage(ICON_COLUMN_INDEX, PriceListManagerImageRegistry.getImage(
					PriceListManagerImageRegistry.IMAGE_ROW_SELECTED_SMALL));
			currentSelectedRow = selectedRow;
		}
	}

	/**
	 * Class represents label provider for the table that holds catalogs.
	 */
	private class CatalogTableLableProvider extends LabelProvider
			implements ITableLabelProvider {
		
		private static final int COLUMN_NAME = 1;

		public Image getColumnImage(final Object object, final int index) {
			return null;
		}

		public String getColumnText(final Object object, final int index) {

			Optional<EPTableColumnCreator> extendedTableColumn = findTableExtenders(getClass().getSimpleName(), PriceListManagerPlugin.PLUGIN_ID)
					.stream().filter(Objects::nonNull).findFirst();
			Catalog model = (Catalog) object;
			String result = ""; //$NON-NLS-1$
			switch (index) {
			case COLUMN_NAME:
				result = model.getName();
				break;
			default:
				if (extendedTableColumn.isPresent()) {
					result = extendedTableColumn.get().visitColumn(object, index);
				}
				break;
			}
			return result;
		}
	}



	/**
	 * Provides content to the {@link CatalogTableViewer}.
	 */
	private class CatalogTableContentProvider implements
			IStructuredContentProvider {

		/**
		 * Constructor. Registers this content provider with listener services.
		 */
		CatalogTableContentProvider() {
			// none
		}

		public void inputChanged(final Viewer viewer, final Object oldInput,
				final Object newInput) {
			LOG.debug("input has been changed"); //$NON-NLS-1$
		}

		public void dispose() {
			// nothing
		}

		public Object[] getElements(final Object inputElement) {
			return ((List<?>) inputElement).toArray();
		}
	}

	/**
	 * Changes the selection mode for the underlying catalog table.
	 * 
	 * @param selectionEnabled true if the selection should be enabled
	 */
	public void setSelectionEnabled(final boolean selectionEnabled) {
		if (selectionEnabled) {
			table.addSelectionListener(changeSelectionController);
		} else {
			table.removeSelectionListener(changeSelectionController);
		}
	}

}