/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

import java.util.Comparator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Sort column listener.
 */ 
class ColumnClickListener implements Listener {
	
	private final IEpTableViewer baseAmountTableViewer;
	
	private final Map<TableColumn, Comparator<BaseAmountDTO>> columnComparatorMap;
	
	private final BaseAmountTableContentProvider baseAmountTableContentProvider;
	
	private final PriceListEditorController controller;
	
	/**
	 * Constructor.
	 * @param baseAmountTableViewer table viewer
	 * @param columnComparatorMap map with registered comparators per each column
	 * @param baseAmountTableContentProvider structured content provider, that provide sorted, with comparator, 
	 * result for table  
	 * @param controller input for table.
	 */
	ColumnClickListener(final IEpTableViewer baseAmountTableViewer,
			final Map<TableColumn, Comparator<BaseAmountDTO>> columnComparatorMap,
			final BaseAmountTableContentProvider baseAmountTableContentProvider,
			final PriceListEditorController controller) {
		this.baseAmountTableViewer = baseAmountTableViewer;
		this.columnComparatorMap = columnComparatorMap;
		this.baseAmountTableContentProvider = baseAmountTableContentProvider;
		this.controller = controller;
		
	}

	@Override
	public void handleEvent(final Event event) {
		final Table table = baseAmountTableViewer.getSwtTable();
		final TableColumn eventTableColumn = (TableColumn) event.widget;			
		final Comparator<BaseAmountDTO> comparator = columnComparatorMap.get(eventTableColumn);
		
		if (comparator != null) {
			table.setRedraw(false);
			if (eventTableColumn.equals(table.getSortColumn())) {
				final int currentSortDirection = table.getSortDirection();
				if (currentSortDirection == SWT.UP) {
					baseAmountTableContentProvider.setUpdown(-1);
					table.setSortDirection(SWT.DOWN);					
				} else {
					baseAmountTableContentProvider.setUpdown(1);
					table.setSortDirection(SWT.UP);
				}				
			} else {
				baseAmountTableContentProvider.setUpdown(1);
				table.setSortDirection(SWT.UP);
			}
			table.setSortColumn(eventTableColumn);
			baseAmountTableContentProvider.setDefaultComparator(comparator);
			baseAmountTableViewer.setInput(controller);
			table.setRedraw(true);
		}	        
		
	}
	
}
