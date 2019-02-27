/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.views.customer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractSortListView;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.cmclient.fulfillment.event.CustomerEventListener;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.cmclient.fulfillment.helpers.CustomerSearchRequestJob;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.store.StoreService;

/**
 * Customer search results view.
 */
public class CustomerSearchResultsView extends AbstractSortListView implements CustomerEventListener {

	/**
	 * Customer search results view ID.
	 */
	public static final String VIEW_ID = CustomerSearchResultsView.class.getName();

	private static final Logger LOG = Logger.getLogger(CustomerSearchResultsView.class);

	// table column constants
	private static final int COLUMN_IMAGE = 0;

	private static final int COLUMN_USERID = 1;

	private static final int COLUMN_STORE_REGISTERED = 2;

	private static final int COLUMN_FIRSTNAME = 3;

	private static final int COLUMN_LASTNAME = 4;

	private static final int COLUMN_EMAIL = 5;

	private static final int COLUMN_ADDRESS = 6;

	private static final int COLUMN_TELEPHONE = 7;

	private static final String CUSTOMER_SEARCH_RESULT_TABLE = "Customer Search Result Table";

	private CustomerSearchRequestJob customerSearchRequestJob;

	private static final Object[] EMPTY_ARRAY = new Object[0];

	private Object[] objects;

	private StoreService storeService;

	/**
	 *
	 */
	public CustomerSearchResultsView() {
		super(true, CUSTOMER_SEARCH_RESULT_TABLE);
		CustomerSearchResultsView.this.updateNavigationComponents();
	}

	
	@Override
	public void dispose() {
		super.dispose();
		FulfillmentEventService.getInstance().unregisterCustomerEventListener(this);
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		FulfillmentEventService.getInstance().registerCustomerEventListener(this);
	}


	@Override
	protected Object getModel() {
		return null;
	}

	/**
	 * Label provider class for the results table.
	 */
	private final class CustomerSearchResultsViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			final Customer customer = (Customer) element;
			Image result = null;
			if (columnIndex == COLUMN_IMAGE) {
				result = getCustomerImage(customer);
			}
			return result;
		}

		private Image getCustomerImage(final Customer customer) {
			Image result = null;
			switch (customer.getStatus()) {
			case Customer.STATUS_ACTIVE :
				result = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_USER_SMALL);
				break;
			case Customer.STATUS_DISABLED :
				result = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_USER_DISABLED_SMALL);
				break;
			case Customer.STATUS_PENDING_APPROVAL :
				result = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_USER_PENDING_APPROVAL_SMALL);
				break;
			default :
				// no image
			}
			return result;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			String result = ""; //$NON-NLS-1$
			final Customer customer = (Customer) element;

			switch (columnIndex) {
				case COLUMN_ADDRESS:
					result = this.formAddressString(customer.getPreferredBillingAddress());
					break;
				case COLUMN_USERID:
					result = String.valueOf(customer.getUserId());
					break;
				case COLUMN_STORE_REGISTERED:
					final Store customerStore = getStoreService().findStoreWithCode(customer.getStoreCode());
					result = customerStore.getName();
					break;
				case COLUMN_EMAIL:
					result = customer.getEmail();
					break;
				case COLUMN_FIRSTNAME:
					result = customer.getFirstName();
					break;
				case COLUMN_LASTNAME:
					result = customer.getLastName();
					break;
				case COLUMN_TELEPHONE:
					result = customer.getPhoneNumber();
				break;
			default: // nothing as result by default is ""
			}
			return result;
		}

		private String formAddressString(final CustomerAddress address) {			
			if (address != null) {
				return address.toPlainString();
			}
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	public void searchResultsUpdate(final SearchResultEvent<Customer> event) {
		customerSearchRequestJob = (CustomerSearchRequestJob) event.getSource();
		getViewer().getTable().getDisplay().syncExec(() -> {
			CustomerSearchResultsView.this.objects = event.getItems().toArray();
			CustomerSearchResultsView.this.setResultsCount(event.getTotalNumberFound());
			CustomerSearchResultsView.this.getViewer().getTable().clearAll();

			if (event.getItems().isEmpty() && event.getStartIndex() <= 0) {
				CustomerSearchResultsView.this.showMessage(CoreMessages.get().NoSearchResultsError);
			} else {
				CustomerSearchResultsView.this.hideErrorMessage();
			}

			CustomerSearchResultsView.this.setResultsStartIndex(event.getStartIndex());
			CustomerSearchResultsView.this.refreshViewerInput();
			CustomerSearchResultsView.this.updateSortingOrder(customerSearchRequestJob.getSearchCriteria());
			CustomerSearchResultsView.this.updateNavigationComponents();
		});
	}

	@Override
	public void customerChanged(final ItemChangeEvent<Customer> event) {
		final Customer changedCustomer = event.getItem();
		for (final TableItem currTableItem : getViewer().getTable().getItems()) {
			final Customer currCustomer = (Customer) currTableItem.getData();
			if (currCustomer.getUidPk() == changedCustomer.getUidPk()) {
				currTableItem.setData(changedCustomer);
				getViewer().update(changedCustomer, null);
				break;
			}
		}
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
		return new CustomerSearchResultsViewLabelProvider();
	}

	@Override
	protected void initializeTable(final IEpTableViewer epTableViewer) {
		final int[] widths = new int[] { 21, 200, 135, 90, 90, 130, 270, 90 };
		final String[] columnNames = new String[] {
				StringUtils.EMPTY,
				FulfillmentMessages.get().CustomerSearchResultsView_UserId,
				FulfillmentMessages.get().CustomerSearchResultsView_StoreRegistered,
				FulfillmentMessages.get().CustomerSearchResultsView_FirstName,
				FulfillmentMessages.get().CustomerSearchResultsView_LastName,
				FulfillmentMessages.get().CustomerSearchResultsView_EmailUserId,
				FulfillmentMessages.get().CustomerSearchResultsView_DefaultBillingAddress,
				FulfillmentMessages.get().CustomerSearchResultsView_TelephoneNum
				
		};
		final SortBy[] sortTypes = new SortBy[] {
				null,
				StandardSortBy.USER_ID,
				StandardSortBy.STORE_CODE,
				StandardSortBy.FIRST_NAME,
				StandardSortBy.LAST_NAME,
				StandardSortBy.EMAIL,
				StandardSortBy.ADDRESS,
				StandardSortBy.PHONE
		};
		for (int i = 0; i < columnNames.length; i++) {
			IEpTableColumn addTableColumn = epTableViewer.addTableColumn(columnNames[i], widths[i]);
			registerTableColumn(addTableColumn, sortTypes[i]);
		}
		
		getSite().setSelectionProvider(epTableViewer.getSwtTableViewer());
	}

	@Override
	protected String getPluginId() {
		return FulfillmentPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Initialize the customer search results view."); //$NON-NLS-1$
		}
		final OpenCustomerDetailsEditorAction action = new OpenCustomerDetailsEditorAction(getViewer(), this.getSite());
		addDoubleClickAction(action);
	}

	@Override
	protected void navigateFirst() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigateFirst();

		customerSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigateNext() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigateNext();

		customerSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigatePrevious() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigatePrevious();

		customerSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigateLast() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigateLast();

		customerSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}
	
	@Override
	protected void navigateTo(final int pageNumber) {
		getViewer().setInput(EMPTY_ARRAY);
		
		customerSearchRequestJob.executeSearchFromIndex(null, getStartIndexByPageNumber(pageNumber, getResultsPaging()));
		refreshViewerInput();
	}
	
	@Override
	public AbstractSearchRequestJob < ? extends Persistable > getSearchRequestJob() {
		return customerSearchRequestJob;
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}


	/**
	 * Gets the store service.
	 *
	 * @return the store service
	 */
	protected StoreService getStoreService() {
		if (storeService == null) {
			storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
		}
		return storeService;
	}
}
