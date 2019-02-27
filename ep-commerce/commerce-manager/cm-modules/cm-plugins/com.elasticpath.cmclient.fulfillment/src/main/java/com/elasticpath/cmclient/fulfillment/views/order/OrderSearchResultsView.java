/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.fulfillment.views.order;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractSortListView;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.cmclient.fulfillment.event.OrderEventListener;
import com.elasticpath.cmclient.fulfillment.helpers.OrderSearchRequestJob;
import com.elasticpath.domain.order.Order;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * View to show and allow the manipulation of the available Users in CM.
 */
public class OrderSearchResultsView extends AbstractSortListView implements OrderEventListener {

	private static final Logger LOG = Logger.getLogger(OrderSearchResultsView.class);

	/** The view ID. */
	public static final String VIEW_ID = OrderSearchResultsView.class.getName();

	private static final String ORDER_SEARCH_RESULT_TABLE = "Order Search Result Table"; //$NON-NLS-1$

	private OrderSearchRequestJob orderSearchRequestJob;

	private static final Object[] EMPTY_ARRAY = new Object[0];

	private Object[] objects;

	/**
	 * Construct a OrderSearchResultsView.
	 */
	public OrderSearchResultsView() {
		super(true, ORDER_SEARCH_RESULT_TABLE);
		OrderSearchResultsView.this.updateNavigationComponents();
	}


	@Override
	public void dispose() {
		super.dispose();
		FulfillmentEventService.getInstance().unregisterOrderEventListener(this);
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		FulfillmentEventService.getInstance().registerOrderEventListener(this);
	}


	@Override
	protected String getPluginId() {
		return FulfillmentPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Initialize the order search results view.");  //$NON-NLS-1$
		}

		this.getViewer().addDoubleClickListener(new OpenOrderEditorAction(this.getViewer(), this.getSite()));
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {

		final String[] columnNames = new String[] { FulfillmentMessages.get().OrderSearchResultsView_OrderNumber,
				FulfillmentMessages.get().OrderSearchResultsView_Store, FulfillmentMessages.get().OrderSearchResultsView_CustomerName,
				FulfillmentMessages.get().OrderSearchResultsView_Date, FulfillmentMessages.get().OrderSearchResultsView_Total,
				FulfillmentMessages.get().OrderSearchResultsView_Status };
		final SortBy[] sortBy = new SortBy[] {
				StandardSortBy.ORDER_NUMBER,
				StandardSortBy.STORE_CODE,
				StandardSortBy.CUSTOMER_NAME,
				StandardSortBy.DATE,
				StandardSortBy.TOTAL,
				StandardSortBy.STATUS};

		final int[] columnWidths = new int[] { 100, 100, 120, 200, 100, 100 };

		for (int i = 0; i < columnNames.length; i++) {
			IEpTableColumn addTableColumn = table.addTableColumn(columnNames[i], columnWidths[i]);
			registerTableColumn(addTableColumn, sortBy[i]);
		}

		getSite().setSelectionProvider(table.getSwtTableViewer());
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
		return new OrderListViewLabelProvider();
	}


	// ---- DOCorderChanged
	/**
	 * Update the search result view with the given event.
	 *
	 * @param event The event contains the type and results.
	 */
	@Override
	public void searchResultsUpdate(final SearchResultEvent<Order> event) {
		orderSearchRequestJob = (OrderSearchRequestJob) event.getSource();
		getViewer().getTable().getDisplay().asyncExec(() -> {
			setResultsStartIndex(event.getStartIndex());
			setResultsCount(event.getTotalNumberFound());

			if (event.getItems().isEmpty() && event.getStartIndex() <= 0) {
				OrderSearchResultsView.this.showMessage(CoreMessages.get().NoSearchResultsError);
			} else {
				OrderSearchResultsView.this.hideErrorMessage();
			}

			OrderSearchResultsView.this.objects = event.getItems().toArray();
			getViewer().setInput(OrderSearchResultsView.this.objects);
			OrderSearchResultsView.this.updateSortingOrder(orderSearchRequestJob.getSearchCriteria());
			OrderSearchResultsView.this.updateNavigationComponents();
			OrderSearchResultsView.this.refreshViewerInput();
		});
	}
	// ---- DOCorderChanged

	@Override
	public void orderChanged(final ItemChangeEvent<Order> event) {
		final Order changedOrder = event.getItem();
		for (int index = 0; index < objects.length; index++) {
			Order currentOrder = (Order) objects[index];
			if (currentOrder.getUidPk() == changedOrder.getUidPk()) {
				objects[index] = changedOrder;
				getViewer().setInput(objects);
				getViewer().refresh();
				break;
			}
		}
	}

	@Override
	protected void navigateFirst() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigateFirst();

		orderSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigateNext() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigateNext();

		orderSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigatePrevious() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigatePrevious();

		orderSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigateLast() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigateLast();

		orderSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigateTo(final int pageNumber) {
		getViewer().setInput(EMPTY_ARRAY);

		orderSearchRequestJob.executeSearchFromIndex(null, getStartIndexByPageNumber(pageNumber, getResultsPaging()));
		refreshViewerInput();
	}

	@Override
	public void paginationChange(final int newValue) {
		getViewer().setInput(EMPTY_ARRAY);
		super.paginationChange(newValue);
		if (orderSearchRequestJob != null) {
			super.navigateFirst();
			orderSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		}
		refreshViewerInput();
	}

	@Override
	public AbstractSearchRequestJob< ? extends Persistable> getSearchRequestJob() {
		return orderSearchRequestJob;
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
