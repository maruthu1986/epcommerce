/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.warehouse.views.orderreturn;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.warehouse.WarehouseImageRegistry;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.WarehousePlugin;
import com.elasticpath.cmclient.warehouse.event.OrderReturnChangeEvent;
import com.elasticpath.cmclient.warehouse.event.OrderReturnEventListener;
import com.elasticpath.cmclient.warehouse.event.WarehouseEventService;
import com.elasticpath.cmclient.warehouse.helpers.OrderReturnSearchRequestJob;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnType;

/**
 * View to show and allow the manipulation of the available Users in CM.
 */
public class OrderReturnSearchResultsView extends AbstractListView implements OrderReturnEventListener {
	private static final Logger LOG = Logger.getLogger(OrderReturnSearchResultsView.class);

	/** The view ID. */
	public static final String VIEW_ID = OrderReturnSearchResultsView.class.getName();

	private static final String ORDER_RETURN_SEARCH_RESULT_TABLE = "Order return Search Result"; //$NON-NLS-1$

	// Column indices
	private static final int INDEX_IMAGE = 0;

	private static final int INDEX_RMA_CODE = 1;

	private static final int INDEX_ORDER_NUMBER = 2;

	private static final int INDEX_RMA_DATE = 3;

	private static final int INDEX_STATUS = 4;

	private OrderReturnSearchRequestJob orderReturnSearchRequestJob;

	private static final Object[] EMPTY_ARRAY = new Object[0];

	private Object[] objects;

	/**
	 * The Constructor.
	 */
	public OrderReturnSearchResultsView() {
		super(true, ORDER_RETURN_SEARCH_RESULT_TABLE);
	}

	@Override
	public void dispose() {
		super.dispose();
		WarehouseEventService.getInstance().unregisterOrderReturnEventListener(this);
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		WarehouseEventService.getInstance().registerOrderReturnEventListener(this);
	}

	@Override
	protected String getPluginId() {
		return WarehousePlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {
		this.getViewer().addDoubleClickListener(new OpenOrderReturnEditorAction(this.getViewer(), this.getSite()));
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {

		final String[] columnNames = new String[] {
				"", //$NON-NLS-1$
				WarehouseMessages.get().OrderSearchResultsView_RMANumber, WarehouseMessages.get().OrderSearchResultsView_OrderNumber,
				WarehouseMessages.get().OrderSearchResultsView_RMADate, WarehouseMessages.get().OrderSearchResultsView_Status };

		final int[] columnWidths = new int[] { 20, 100, 100, 250, 300 };

		for (int i = 0; i < columnNames.length; i++) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}
		table.getSwtTableViewer().setSorter(new ViewerSorter() { // sorts order returns by RMA Code
					@Override
					public int compare(final Viewer viewer, final Object element1, final Object element2) {
						OrderReturn orderReturn1 = (OrderReturn) element1;
						OrderReturn orderReturn2 = (OrderReturn) element2;
						return orderReturn1.getRmaCode().compareTo(orderReturn2.getRmaCode());
					}
				});
	}

	@Override
	protected Object[] getViewInput() {
		if (objects == null) {
			return new Object[0];
		}
		return objects.clone();
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new OrderReturnListViewLabelProvider();
	}

	/**
	 * Label provider for view.
	 */
	protected class OrderReturnListViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Get the image to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the Image to put in the column
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			final OrderReturn orderReturn = (OrderReturn) element;
			ImageDescriptor imageDescriptor = null;
			if (columnIndex == INDEX_IMAGE) {
				if (orderReturn.getReturnType() == OrderReturnType.EXCHANGE) {
					imageDescriptor = WarehouseImageRegistry.IMAGE_EXCHANGE_SMALL;
				} else if (orderReturn.getReturnType() == OrderReturnType.RETURN) {
					imageDescriptor = WarehouseImageRegistry.IMAGE_RETURN_SMALL;
				}
			}
			return WarehouseImageRegistry.getImage(imageDescriptor);
		}

		/**
		 * Get the text to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the String to put in the column
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final OrderReturn orderReturn = (OrderReturn) element;
			switch (columnIndex) {
			case OrderReturnSearchResultsView.INDEX_IMAGE:
				return ""; //$NON-NLS-1$
			case OrderReturnSearchResultsView.INDEX_RMA_CODE:
				return orderReturn.getRmaCode();
			case OrderReturnSearchResultsView.INDEX_ORDER_NUMBER:
				return orderReturn.getOrder().getOrderNumber();
			case OrderReturnSearchResultsView.INDEX_RMA_DATE:
				return DateTimeUtilFactory.getDateUtil().formatAsDate(orderReturn.getCreatedDate());
			case OrderReturnSearchResultsView.INDEX_STATUS:
				return WarehouseMessages.get().getLocalizedOrderReturnStatus(orderReturn.getReturnStatus());
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}

	/**
	 * Update the search result view with the given event.
	 * 
	 * @param event The event contains the type and results.
	 */
	@Override
	public void searchResultsUpdate(final SearchResultEvent<OrderReturn> event) {
		orderReturnSearchRequestJob = (OrderReturnSearchRequestJob) event.getSource();
		getViewer().getTable().getDisplay().syncExec(() -> {
			OrderReturnSearchResultsView.this.objects = event.getItems().toArray();
			LOG.debug("total number found: " + event.getTotalNumberFound()); //$NON-NLS-1$
			OrderReturnSearchResultsView.this.setResultsCount(event.getTotalNumberFound());
			OrderReturnSearchResultsView.this.getViewer().getTable().clearAll();

			if (event.getItems().isEmpty() && event.getStartIndex() <= 0) {
				OrderReturnSearchResultsView.this.showMessage(CoreMessages.get().NoSearchResultsError);
			} else {
				OrderReturnSearchResultsView.this.hideErrorMessage();
			}
			LOG.debug("start index: " + event.getStartIndex()); //$NON-NLS-1$
			OrderReturnSearchResultsView.this.setResultsStartIndex(event.getStartIndex());
			OrderReturnSearchResultsView.this.refreshViewerInput();
			OrderReturnSearchResultsView.this.updateNavigationComponents();
		});
	}

	@Override
	protected void navigateFirst() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigateFirst();
		orderReturnSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigateNext() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigateNext();
		orderReturnSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigatePrevious() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigatePrevious();
		orderReturnSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigateLast() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigateLast();
		orderReturnSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}
	
	@Override
	protected void navigateTo(final int pageNumber) {
		getViewer().setInput(EMPTY_ARRAY);
		
		orderReturnSearchRequestJob.executeSearchFromIndex(null, getStartIndexByPageNumber(pageNumber, getResultsPaging()));
		refreshViewerInput();
	}

	@Override
	public void orderReturnChanged(final OrderReturnChangeEvent event) {
		final OrderReturn changedOrder = event.getOrderReturn();
		for (final org.eclipse.swt.widgets.TableItem currTableItem : getViewer().getTable().getItems()) {
			final OrderReturn currOrderReturn = (OrderReturn) currTableItem.getData();
			if (currOrderReturn.getUidPk() == changedOrder.getUidPk()) {
				currTableItem.setData(changedOrder);
				getViewer().update(changedOrder, null);
				break;
			}
		}
	}

	@Override
	protected void initializeToolbarNavigationComponents(final IToolBarManager toolBarManager) {
		super.initializeToolbarNavigationComponents(toolBarManager);
		updateNavigationComponents();
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
