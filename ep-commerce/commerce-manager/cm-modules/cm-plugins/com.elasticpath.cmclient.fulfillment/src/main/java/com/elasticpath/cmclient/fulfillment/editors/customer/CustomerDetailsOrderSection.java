/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.views.order.OpenOrderEditorAction;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.service.order.OrderService;

/**
 * Represents the order section that displays all order a customer has placed.
 */
public class CustomerDetailsOrderSection extends AbstractCmClientEditorPageSectionPart implements SelectionListener {

	private static final String CUSTOMER_DETAILS_ORDER_TABLE = "Customer Details Order Table"; //$NON-NLS-1$

	private transient List<Order> orders;

	private transient Button viewDetailsButton;

	private transient Button refreshOrdersButton;

	private static final int STORE_WIDTH = 100;

	private static final int ID_WIDTH = 75;

	private static final int DATE_WIDTH = 175;

	private static final int STATUS_WIDTH = 75;

	private static final int TOTAL_WIDTH = 90;

	private static final int MAX_TABLE_HEIGHT = 300;

	private final transient Customer customer;

	private transient IEpTableViewer epTableViewer;
	
	private static final OrderService ORDER_SERVICE = 
		(OrderService) ServiceLocator.getService(ContextIdNames.ORDER_SERVICE);

	/**
	 * constructor.
	 * 
	 * @param formPage the form page
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 */
	public CustomerDetailsOrderSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR);
		this.customer = (Customer) editor.getModel();
		this.orders = ORDER_SERVICE.findOrderByCustomerGuid(this.customer.getGuid(), true);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		final IEpLayoutComposite mainPane = CompositeFactory.createTableWrapLayoutComposite(client, 2, false);

		final IEpLayoutData tableData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		epTableViewer = mainPane.addTableViewer(false, EpControlFactory.EpState.EDITABLE, tableData, CUSTOMER_DETAILS_ORDER_TABLE);

		((TableWrapData) epTableViewer.getSwtTable().getLayoutData()).maxHeight = MAX_TABLE_HEIGHT;

		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderSection_Store, STORE_WIDTH);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderSection_ID, ID_WIDTH);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderSection_Date, DATE_WIDTH);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderSection_Status, STATUS_WIDTH);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderSection_Total, TOTAL_WIDTH);

		epTableViewer.setLabelProvider(new OrderLabelProvider());
		epTableViewer.setContentProvider(new OrderContentProvider());
		epTableViewer.setInput(this.orders);
		epTableViewer.getSwtTableViewer().setSorter(new ViewerSorter() {
			@Override
			public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
				return ((Order) obj2).getCreatedDate().compareTo(((Order) obj1).getCreatedDate());
			}
		});

		epTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> CustomerDetailsOrderSection.this.viewDetailsButton.setEnabled(true));

		epTableViewer.getSwtTableViewer().addDoubleClickListener(event -> {
			final OpenOrderEditorAction editorAction = new OpenOrderEditorAction(epTableViewer.getSwtTableViewer(),
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getSite());
			editorAction.run();
		});

		final IEpLayoutData buttonPaneData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		final IEpLayoutComposite buttonsPane = mainPane.addTableWrapLayoutComposite(1, true, buttonPaneData);
		final IEpLayoutData buttonData = mainPane.createLayoutData();

		this.viewDetailsButton = buttonsPane.addPushButton(FulfillmentMessages.get().OrderSection_ViewOrderButton, FulfillmentImageRegistry
				.getImage(FulfillmentImageRegistry.IMAGE_VIEW_ORDER), EpControlFactory.EpState.EDITABLE, buttonData);

		this.refreshOrdersButton = buttonsPane.addPushButton(FulfillmentMessages.get().OrderSection_RefreshOrdersButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_REFRESH), EpControlFactory.EpState.EDITABLE, buttonData);
	}

	@Override
	protected void populateControls() {
		this.viewDetailsButton.addSelectionListener(this);
		this.viewDetailsButton.setEnabled(false);
		this.refreshOrdersButton.addSelectionListener(this);
	}

	/**
	 * This label provider returns the text that should appear in each column for a given <code>Order</code> object. This also
	 * determines the icon that should appear in the first column.
	 */
	class OrderLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final int THREE = 3;

		private static final int FOUR = 4;

		/**
		 * Gets the image for each column.
		 * 
		 * @param element the image
		 * @param columnIndex the index for the column
		 * @return Image the image
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {

			return null;
		}

		/**
		 * Gets the text for each column.
		 * 
		 * @param element the text
		 * @param columnIndex the index for the column
		 * @return String the text
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final Order order = (Order) element;

			switch (columnIndex) {
			case 0:
				return order.getStore().getName();
			case 1:
				return order.getOrderNumber();
			case 2:
				return DateTimeUtilFactory.getDateUtil().formatAsDateTime(order.getCreatedDate());
			case THREE:
				return order.getStatus().getName();
			case FOUR:
				return order.getCurrency() + " " + order.getTotal(); //$NON-NLS-1$
			default:
				return "unknown @ column" + columnIndex; //$NON-NLS-1$
			}
		}
	}

	/**
	 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in adapters or
	 * simply return objects as-is. These objects may be sensitive to the current input of the view, or ignore it and always show
	 * the same content.
	 */
	class OrderContentProvider implements IStructuredContentProvider {

		/**
		 * Gets the order from the list of order for each row.
		 * 
		 * @param inputElement the input order element
		 * @return Object[] the returned input
		 */
		@Override
		@SuppressWarnings("synthetic-access")
		public Object[] getElements(final Object inputElement) {
			return ((List<Order>) inputElement).toArray();
		}

		/**
		 * dispose the provider.
		 */
		@Override
		public void dispose() {
			// does nothing
		}

		/**
		 * Notify the provider the input has changed.
		 * 
		 * @param viewer the epTableViewer
		 * @param oldInput the previous input
		 * @param newInput the current selected input
		 */
		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// does nothing
		}
	}

	@Override
	protected String getSectionDescription() {
		return ""; //$NON-NLS-1$
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().OrderSection_Title;
	}

	/**
	 * Not used.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// nothing
	}

	/**
	 * Invoked on selection event.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == this.refreshOrdersButton) {
			this.orders = ORDER_SERVICE.findOrderByCustomerGuid(this.customer.getGuid(), true);
			this.epTableViewer.setInput(this.orders);
			this.epTableViewer.getSwtTableViewer().refresh();
		} else if (event.getSource() == this.viewDetailsButton) {
			final OpenOrderEditorAction editorAction = new OpenOrderEditorAction(this.epTableViewer.getSwtTableViewer(), PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().getActiveEditor().getSite());
			editorAction.run();
		}
	}
}
