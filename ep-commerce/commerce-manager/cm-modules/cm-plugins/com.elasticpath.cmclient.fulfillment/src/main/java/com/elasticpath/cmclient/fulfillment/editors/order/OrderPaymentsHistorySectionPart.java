/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.domain.OrderPaymentPresenter;
import com.elasticpath.cmclient.fulfillment.domain.impl.OrderPaymentPresenterFactory;
import com.elasticpath.commons.util.PaymentsComparatorFactory;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;

/**
 * Represents the UI of order payment history section.
 */
public class OrderPaymentsHistorySectionPart extends AbstractCmClientEditorPageSectionPart implements SelectionListener {

	private static final String ORDER_PAYMENTS_HISTORY_TABLE = "Order Payments History Table"; //$NON-NLS-1$

	/**
	 * Not used.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// Empty method
	}

	/**
	 * Invoked on selection event.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		// Nothing to do.
	}

	private static final int COLUMN_WIDTH_DATETIME = 120;
	
	private static final int COLUMN_WIDTH_SHIPMENT_ID = 120;

	private static final int COLUMN_WIDTH_PAYMENTTYPE = 100;

	private static final int COLUMN_WIDTH_TRANSACTIONTYPE = 100;

	private static final int COLUMN_WIDTH_PAYMENTDETIAL = 200;

	private static final int COLUMN_WIDTH_AMOUNT = 100;

	private static final int COLUMN_WIDTH_STATUS = 80;

	private static final int COLUMN_WIDTH_TRANSACTIONID = 100;

	private static final int COLUMN_INDEX_DATETIME = 0;
	
	private static final int COLUMN_INDEX_SHIPMENT_ID = 1;

	private static final int COLUMN_INDEX_PAYMENTTYPE = 2;

	private static final int COLUMN_INDEX_TRANSACTIONTYPE = 3;

	private static final int COLUMN_INDEX_PAYMENTDETAIL = 4;

	private static final int COLUMN_INDEX_AMOUNT = 5;

	private static final int COLUMN_INDEX_STATUS = 6;

	private static final int COLUMN_INDEX_TRANSACTIONID = 7;

	private IEpTableViewer epTableViewer;

	/**
	 * Constructor.
	 * 
	 * @param formPage the formpage
	 * @param editor the CmClientFormEditor that contains the form
	 */
	public OrderPaymentsHistorySectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		// super(formPage, editor, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR); //| ExpandableComposite.EXPANDED);
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {

		final IEpLayoutComposite mainPane = CompositeFactory.createTableWrapLayoutComposite(client, 2, false);

		final IEpLayoutData tableData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false);

		epTableViewer = mainPane.addTableViewer(false, EpState.READ_ONLY, tableData, ORDER_PAYMENTS_HISTORY_TABLE);

		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderPaymentHistorySection_TableTitle_DateTime, COLUMN_WIDTH_DATETIME);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderPaymentHistorySection_TableTitle_ShipmentId, COLUMN_WIDTH_SHIPMENT_ID);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderPaymentHistorySection_TableTitle_PaymentType, COLUMN_WIDTH_PAYMENTTYPE);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderPaymentHistorySection_TableTitle_TransactionType, COLUMN_WIDTH_TRANSACTIONTYPE);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderPaymentHistorySection_TableTitle_PaymentDetails, COLUMN_WIDTH_PAYMENTDETIAL);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderPaymentHistorySection_TableTitle_Amount, COLUMN_WIDTH_AMOUNT);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderPaymentHistorySection_TableTitle_Status, COLUMN_WIDTH_STATUS);
		epTableViewer.addTableColumn(FulfillmentMessages.get().OrderPaymentHistorySection_TableTitle_TransactionID, COLUMN_WIDTH_TRANSACTIONID);

		epTableViewer.setContentProvider(new ViewContentProvider());
		epTableViewer.setLabelProvider(new ViewLabelProvider());
		
	}

	@Override
	protected String getSectionDescription() {
		return FulfillmentMessages.get().PaymentHistorySection_Description;
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().PaymentHistorySection_Title;
	}

	@Override
	protected void populateControls() {
		// Nothing to do.
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// Nothing to do.
	}

	/**
	 * Provides the column image for the payments table.
	 */
	@SuppressWarnings("PMD.CyclomaticComplexity")
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Get the column image.
		 * 
		 * @param element not used
		 * @param columnIndex the column to create an image for
		 * @return the image
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		/**
		 * Provide the content for each column.
		 * 
		 * @param element element object
		 * @param columnIndex index
		 * @return the value for that column
		 */
		@Override
		@SuppressWarnings("PMD.CyclomaticComplexity")
		public String getColumnText(final Object element, final int columnIndex) {
			final OrderPaymentPresenter presenter = new OrderPaymentPresenterFactory().getOrderPaymentPresenter((OrderPayment) element); 
			switch (columnIndex) {
			case COLUMN_INDEX_DATETIME:
				return presenter.getDisplayCreatedDate();
			case COLUMN_INDEX_SHIPMENT_ID:
				return presenter.getDisplayShipmentId();
			case COLUMN_INDEX_PAYMENTTYPE:
				return presenter.getDisplayPaymentMethod();
			case COLUMN_INDEX_TRANSACTIONTYPE:
				return presenter.getDisplayTransactionType();
			case COLUMN_INDEX_PAYMENTDETAIL:
				return presenter.getDisplayPaymentDetails();
			case COLUMN_INDEX_AMOUNT:
				if (presenter.getDisplayTransactionType().equals(OrderPayment.CREDIT_TRANSACTION)) {
					return decorateCredit(presenter.getDisplayPaymentAmount());
				}
				return presenter.getDisplayPaymentAmount();
			case COLUMN_INDEX_STATUS:
				return presenter.getDisplayStatus();
			case COLUMN_INDEX_TRANSACTIONID:
				return presenter.getDisplayTransactionId();
			default:
				return StringUtils.EMPTY; 
			}
		}

		private String decorateCredit(final String amount) {
			return '(' + amount + ')';
		}
	}

	/**
	 * Provides the content for the payments.
	 */
	class ViewContentProvider implements IStructuredContentProvider {

		/**
		 * Called when the viewer input is changed.
		 * 
		 * @param viewer the viewer
		 * @param oldInput the old input
		 * @param newInput the new input
		 */
		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// No action required
		}

		/**
		 * Dispose the content.
		 */
		@Override
		public void dispose() {
			// Nothing to dispose
		}

		/**
		 * Returns an array of elements to display.
		 * 
		 * @param inputElement the input element (An Order)
		 * @return an array of order payments
		 */
		@Override
		public Object[] getElements(final Object inputElement) {

			if (inputElement != null) {
				final HashSet<OrderPayment> payments = (HashSet<OrderPayment>) ((Order) inputElement).getOrderPayments();
				final List<OrderPayment> paymentsList = new LinkedList<>(payments);
				paymentsList.sort(PaymentsComparatorFactory.getOrderPaymentDateCompatator());
				return paymentsList.toArray();
			}

			return new Object[0];
		}
	}

	/**
	 * Refresh the section with order data.
	 * 
	 * @param order the order
	 */
	public void refreshData(final Order order) {
		epTableViewer.setInput(order);
	}
}
