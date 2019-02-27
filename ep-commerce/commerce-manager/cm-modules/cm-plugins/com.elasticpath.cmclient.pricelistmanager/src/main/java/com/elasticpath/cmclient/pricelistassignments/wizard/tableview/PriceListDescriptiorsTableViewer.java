/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistassignments.wizard.tableview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.comparator.PriceListDescriptorComparator;
import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.pricelistassignments.wizard.tabletooltip.PriceListDescriptorTableTooltip;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerImageRegistry;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.misc.CurrencyCodeComparator;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * Class represents table with {@link PriceListDescriptor} for
 * {@link com.elasticpath.cmclient.pricelistassignments.wizard.PriceListAssignmentWizardPriceListSelectPage}.
 */
public class PriceListDescriptiorsTableViewer {

	//private static final String ZERO_INDEX_ITEM = "All"; //$NON-NLS-1$
	private static final int TABLE_HEIGHT = 200;
	private static final int NAME_COLUMN_WIDTH = 265;
	private static final int CURRENCY_COLUMN_WIDTH = 75;
	private static final int DESCRIPTION_COLUMN_WIDTH = 420;
	private static final int TOOLTIP_SHIFT = 10;
	private static final int SELECTED_ICON_WIDTH = 25;

	private static final int ICON_COLUMN_INDEX = 0;

	private static final Logger LOG = Logger
		.getLogger(PriceListDescriptiorsTableViewer.class);

	private final SelectionChangeController selectionChangeController = new SelectionChangeController();

	private TableItem currentSelectedRow;

	private List<PriceListDescriptor> all;

	private PriceListDescriptor model;
	private Table table;

	/**
	 * Initializes table with list {@link PriceListDescriptor}. All available
	 * {@link PriceListDescriptor} are selected. Table has 4 columns - Selected Icon, Name, Currency
	 * Description
	 *
	 * @param epTableViewer   -
	 *                        table viewer that holds table.
	 * @param currenciesCombo -
	 *                        combo box with currencies.
	 * @param model           - selected {@link PriceListDescriptor} saved in the model.
	 * @param wizardContainer - container of the wizard
	 */
	public void initTable(final IEpTableViewer epTableViewer,
		final CCombo currenciesCombo, final PriceListDescriptor model,
		final IWizardContainer wizardContainer) {
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.heightHint = TABLE_HEIGHT;
		table = epTableViewer.getSwtTable();
		table.setLayoutData(gridData);
		createColumns(epTableViewer);
		epTableViewer.setContentProvider(new PriceListDescriptorsTableContentProvider());
		epTableViewer.setLabelProvider(new PriceListDescriptorsTableLableProvider());
		epTableViewer.setInput(getPriceListsSorted());
		this.model = model;

		table.addSelectionListener(selectionChangeController);

		currenciesCombo.setItems(getCurrencies());
		currenciesCombo.select(0);
		currenciesCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				int index = currenciesCombo.getSelectionIndex();
				if (index == 0) {
					epTableViewer.setInput(getPriceListsSorted());
				} else {
					String operator = getCurrencies()[index];
					epTableViewer.setInput(getPriceListsSortedFilteredBy(operator));
				}
				markSelected(table);
			}
		});

		markSelected(table);
		new PriceListDescriptorTableTooltip().addTableTooltip(table, wizardContainer, TOOLTIP_SHIFT, TOOLTIP_SHIFT);

		EPTestUtilFactory.getInstance().getTestIdUtil().setTestIdsToTableItems(table);
	}

	/**
	 * Get sorted unique currencies with "All" option.
	 *
	 * @return String array of sorted by  code currencies.
	 */
	private String[] getCurrencies() {
		List<String> currencyCodes = new ArrayList<>();
		currencyCodes.add(PriceListManagerMessages.get().All_Currencies);
		for (Currency curr : getUniqueCurrencies()) {
			currencyCodes.add(curr.getCurrencyCode());
		}
		return currencyCodes.toArray(new String[currencyCodes.size()]);
	}

	private Set<Currency> getUniqueCurrencies() {
		CurrencyCodeComparator comparator = ServiceLocator.getService(ContextIdNames.CURRENCYCODE_COMPARATOR);
		Set<Currency> uniqueCurrencies = new TreeSet<>(comparator);
		for (PriceListDescriptor priceListDescriptor : getPriceListsSorted()) {
			uniqueCurrencies.add(Currency.getInstance(priceListDescriptor.getCurrencyCode()));
		}
		return uniqueCurrencies;
	}

	private List<PriceListDescriptor> getPriceListsSortedFilteredBy(final String currency) {
		List<PriceListDescriptor> priceListDescritors = new ArrayList<>();
		for (PriceListDescriptor priceListDescritor : getPriceListsSorted()) {
			if (priceListDescritor.getCurrencyCode().equalsIgnoreCase(currency)) {
				priceListDescritors.add(priceListDescritor);
			}
		}
		return priceListDescritors;
	}

	private List<PriceListDescriptor> getPriceListsSorted() {
		if (all == null) {
			all = getAllObjects();
		}
		Collections.sort(all, new PriceListDescriptorComparator());
		return all;
	}

	/**
	 * Marks in the grid selected {@link PriceListDescriptor}.
	 *
	 * @param table
	 */
	private void markSelected(final Table table) {
		if (null != model) {
			TableItem[] items = table.getItems();
			TableItem itemtoselect = null;
			for (TableItem tableItem : items) {
				if (tableItem.getData().equals(model)) {
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


	private List<PriceListDescriptor> getAllObjects() {
		PriceListDescriptorService service = ServiceLocator.getService(ContextIdNames.PRICE_LIST_DESCRIPTOR_SERVICE);
		CmUser currentUser = LoginManager.getCmUser();
		if (currentUser.isAllPriceListsAccess()) {
			return service.getPriceListDescriptors(false);
		}
		return service.getPriceListDescriptors(currentUser.getPriceLists());
	}


	private void createColumns(final IEpTableViewer epTableViewer) {
		final String[] columnNames = getTableColumns();
		final int[] columnWidths = new int[]{SELECTED_ICON_WIDTH, NAME_COLUMN_WIDTH, CURRENCY_COLUMN_WIDTH,
			DESCRIPTION_COLUMN_WIDTH};

		for (int i = 0; i < columnNames.length; i++) {
			epTableViewer.addTableColumn(columnNames[i], columnWidths[i]);
		}
	}

	private String[] getTableColumns() {
		return new String[]{
			String.valueOf(""), //$NON-NLS-1$ //empty column name for selected icon column
			PriceListManagerMessages.get().PriceListDescriptor_Column_Name,
			PriceListManagerMessages.get().PriceListDescriptor_Column_Currency,
			PriceListManagerMessages.get().PriceListDescriptor_Column_Description

		};
	}

	/**
	 * The selection change controller.
	 */
	private final class SelectionChangeController extends SelectionAdapter {

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
	 * Class represents label provider for the table that holds Price List Descriptors.
	 */
	private class PriceListDescriptorsTableLableProvider extends LabelProvider
		implements ITableLabelProvider {

		private static final int COLUMN_NAME = 1;

		private static final int COLUMN_CURRENCY = 2;

		private static final int COLUMN_DESCRIPTION = 3;

		public Image getColumnImage(final Object object, final int index) {
			return null;
		}

		public String getColumnText(final Object object, final int index) {
			PriceListDescriptor model = (PriceListDescriptor) object;
			String result = ""; //$NON-NLS-1$
			switch (index) {
				case COLUMN_NAME:
					result = model.getName();
					break;
				case COLUMN_CURRENCY:
					result = model.getCurrencyCode();
					break;
				case COLUMN_DESCRIPTION:
					result = model.getDescription();
					break;
				default:
			}
			return result;
		}

	}

	/**
	 * Provides content to the Price List Descriptors table.
	 */
	private class PriceListDescriptorsTableContentProvider implements
		IStructuredContentProvider {

		/**
		 * Constructor. Registers this content provider with listener services.
		 */
		PriceListDescriptorsTableContentProvider() {
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
	 * Sets item selected in the view table.
	 *
	 * @param selectedItem - item selected.
	 */
	public void setSelectedItem(final PriceListDescriptor selectedItem) {
		this.model = selectedItem;
	}

	/**
	 * Changes the selection mode for the underlying catalog table.
	 *
	 * @param selectionEnabled true if the selection should be enabled
	 */
	public void setSelectionEnabled(final boolean selectionEnabled) {
		if (selectionEnabled) {
			table.addSelectionListener(selectionChangeController);
		} else {
			table.removeSelectionListener(selectionChangeController);
		}
	}

}