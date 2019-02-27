/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.domain.order.Order;

/**
 * A page used within the Order details editor. Represents order's payments.
 */
public class OrderPaymentsPage extends AbstractOrderPage {

	private OrderPaymentsSummarySectionPart summarySection;

	private OrderPaymentsHistorySectionPart historySection;

	/**
	 * Constructor.
	 * 
	 * @param editor the form editor
	 */
	public OrderPaymentsPage(final AbstractCmClientFormEditor editor) {
		super(editor, "OrderPayments", FulfillmentMessages.get().OrderPaymentsPage_Title); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		// Create the sections
		summarySection = new OrderPaymentsSummarySectionPart(this, editor);
		historySection = new OrderPaymentsHistorySectionPart(this, editor);
		managedForm.addPart(summarySection);
		managedForm.addPart(historySection);
		addExtensionEditorSections(editor, managedForm, FulfillmentPlugin.PLUGIN_ID, this.getClass().getSimpleName());


		refreshData((Order) editor.getModel());
	}

	/**
	 * Refresh the order payments page with the given order object.
	 *
	 * @param order the new order object
	 */
	public void refreshData(final Order order) {
		summarySection.refreshData(order);
		historySection.refreshData(order);
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return FulfillmentMessages.get().OrderPaymentsPage_Form_Title;
	}

}