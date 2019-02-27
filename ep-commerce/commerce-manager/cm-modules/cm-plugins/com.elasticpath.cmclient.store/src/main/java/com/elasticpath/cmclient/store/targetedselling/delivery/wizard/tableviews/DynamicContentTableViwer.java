/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.wizard.tableviews;

import java.util.Collections;
import java.util.List;

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

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.comparator.DynamicContentComparator;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingImageRegistry;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.tabletooltip.DynamicContentTableTooltip;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.service.contentspace.DynamicContentService;

/**
 * 
 * Class represents table with DynamicContent for
 * DynamicContentDeliveryWizardDynamicContentSelectPage.
 */
public class DynamicContentTableViwer {

	private static final int TABLE_HEIGHT = 200;
	private static final int COLUMN_WIDTH = 150;
	private static final int TOOLTIP_SHIFT = 10;
	private static final int SELECTED_ICON_WIDTH = 25;
	
	private static final int ICON_COLUMN_INDEX = 0;
	
	private static final Logger LOG = Logger
			.getLogger(DynamicContentTableViwer.class);
	
	private TableItem currentSelectedRow;
	private IEpTableViewer tableViewer;
	private final SelectionController selectionController = new SelectionController();
	
	/**
	 * Initializes table with list {@link DynamicContent}. All available
	 * {@link DynamicContent} are selected. Table has 4 columns - Selected Icon, Name,
	 * Description and ContentWrapper Name
	 * 
	 * @param epTableViewer -
	 *            table viewer that holds table.
	 * 
	 * @param currentDynamicContent - selected {@link DynamicContent} saved in the model.
	 * @param wizardContainer - container of the wizard
	 */
	public void initTable(final IEpTableViewer epTableViewer,
			final DynamicContent currentDynamicContent,
			final IWizardContainer wizardContainer) {
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.heightHint = TABLE_HEIGHT;
		tableViewer = epTableViewer;
		Table table = epTableViewer.getSwtTable();
		table.setLayoutData(gridData);
		createColumns(epTableViewer);
		epTableViewer.setContentProvider(new DynamicContentTableContentProvider());
		epTableViewer.setLabelProvider(new DynamiccontentTableLableProvider());
		epTableViewer.setInput(getAllDynamicContentsSorted());
		
		markSelected(table, currentDynamicContent);
		new DynamicContentTableTooltip().addTableTooltip(table, wizardContainer, TOOLTIP_SHIFT, TOOLTIP_SHIFT);
	} 

	/**
	 * Registers and unregisters selection listener.
	 * @param isEnabled is listener enabled
	 */
	public void setSelectionEnabled(final boolean isEnabled) {
		if (isEnabled) {
			tableViewer.getSwtTable().addSelectionListener(selectionController);
		} else {
			tableViewer.getSwtTable().removeSelectionListener(selectionController);
		}
	}
	
	/**
	 * @return {@link IEpTableViewer}
	 */
	public IEpTableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * The selection controller. 
	 */
	private class SelectionController extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			if (currentSelectedRow != null) {
				currentSelectedRow.setImage(ICON_COLUMN_INDEX, TargetedSellingImageRegistry.getImage(
						TargetedSellingImageRegistry.IMAGE_EMPTY_ICON));					
			}								
			TableItem selectedRow = (TableItem) event.item;
			selectedRow.setImage(ICON_COLUMN_INDEX, TargetedSellingImageRegistry.getImage(
					TargetedSellingImageRegistry.IMAGE_ROW_SELECTED));
			currentSelectedRow = selectedRow;
		}
	}
	
	private List<DynamicContent> getAllDynamicContentsSorted() {
		List<DynamicContent> all = getAllDynamicContents();
		Collections.sort(all, new DynamicContentComparator());
		return all;
	}

	/**
	 * Marks in the grid selected {@link DynamicContent}.
	 *
	 * @param table table
	 * @param currentDynamicContent currentDynamicContent
	 */
	private void markSelected(final Table table,
			final DynamicContent currentDynamicContent) {
		if (null != currentDynamicContent) {
			TableItem[] items = table.getItems();
			TableItem itemtoselect = null;
			for (TableItem tableItem : items) {
				if (tableItem.getData().equals(currentDynamicContent)) {
					itemtoselect = tableItem;
					break;
				}
			}
			if (null != itemtoselect) {
				itemtoselect.setImage(ICON_COLUMN_INDEX, TargetedSellingImageRegistry.getImage(
						TargetedSellingImageRegistry.IMAGE_ROW_SELECTED));
				currentSelectedRow = itemtoselect;
				// select this item
				table.setSelection(itemtoselect);
				//sroll item to the top of the table
				table.setTopIndex(table.getSelectionIndex());
			}
		}
	}

	
	private static List<DynamicContent> getAllDynamicContents() {
		DynamicContentService dynamicContentService = ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_SERVICE);
		return dynamicContentService.findAll();
	}

	private void createColumns(final IEpTableViewer epTableViewer) {
		final String[] columnNames = getTableColumns();
		final int[] columnWidths = new int[] { SELECTED_ICON_WIDTH, COLUMN_WIDTH, COLUMN_WIDTH,
				COLUMN_WIDTH };
		
		for (int i = 0; i < columnNames.length; i++) {
			epTableViewer.addTableColumn(columnNames[i], columnWidths[i]);
		}
	}

	private String[] getTableColumns() {
		return new String[] {
				String.valueOf(""), //$NON-NLS-1$ //empty column name for selected icon column
				TargetedSellingMessages.get().DCDeliveryWizard_DynamicContent_Columne_Name,
				TargetedSellingMessages.get().DCDeliveryWizard_DynamicContent_Columne_Description,
				TargetedSellingMessages.get().DCDeliveryWizard_DynamicContent_Columne_ContentWrapper
		};
	}

	/**
	 * Class represents label provider for the table that holds Dynamic
	 * Contents.
	 */
	private class DynamiccontentTableLableProvider extends LabelProvider
			implements ITableLabelProvider {
		
		private static final int COLUMN_NAME = 1;

		private static final int COLUMN_DESCRIPTION = 2;

		private static final int COLUMN_CONTENT_WRAPPER = 3;


		@Override
		public Image getColumnImage(final Object object, final int index) {
			if (index == 0) {
				return TargetedSellingImageRegistry.getImage(
						TargetedSellingImageRegistry.IMAGE_EMPTY_ICON);
			}
			return null;
		}

		@Override
		public String getColumnText(final Object object, final int index) {
			DynamicContent model = (DynamicContent) object;
			String result = ""; //$NON-NLS-1$
			switch (index) {
				case COLUMN_NAME:
					result = model.getName();
					break;
				case COLUMN_DESCRIPTION:
					result = model.getDescription();
					break;
				case COLUMN_CONTENT_WRAPPER:
					result = model.getContentWrapperId();
					break;
				default:
			}
			return result;
		}

	}

	/**
	 * Provides content to the {@link DynamicContentTableViwer}.
	 */
	private class DynamicContentTableContentProvider implements
			IStructuredContentProvider {

		/**
		 * Constructor. Registers this content provider with listener services.
		 */
		DynamicContentTableContentProvider() {
			// none
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput,
								 final Object newInput) {
			LOG.debug("input has been changed"); //$NON-NLS-1$
		}

		@Override
		public void dispose() {
			// nothing
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			return ((List<?>) inputElement).toArray();
		}
	}

}