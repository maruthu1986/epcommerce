/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.commons.util.ReturnsAndExchangesComparatorFactory;
import com.elasticpath.domain.order.OrderReturn;

/**
 * Represents the UI of the order return and exchange page.
 */
public class OrderReturnsPage extends AbstractOrderPage {

	/** Page identifier. */
	public static final String PAGE_ID = "OrderReturnsPage"; //$NON-NLS-1$

	private IManagedForm managedForm;

	/**
	 * Constructor.
	 * 
	 * @param editor <code>FormEditor</code>
	 */
	public OrderReturnsPage(final AbstractCmClientFormEditor editor) {
		super(editor, PAGE_ID, FulfillmentMessages.get().OrderReturnsPage_Title);
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		// Create the sections
		List<OrderReturn> orderReturnsList = new LinkedList<>(((OrderEditor) editor).getModel().getReturns());
		orderReturnsList.sort(ReturnsAndExchangesComparatorFactory.getReturnsAndExchangesCompatator());
		this.managedForm = managedForm;

		for (OrderReturn orderReturn : orderReturnsList) {
			addReturn(orderReturn);
		}
		getCustomPageData().put("orderReturnsList", orderReturnsList);
		addExtensionEditorSections(editor, managedForm, FulfillmentPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	/**
	 * Add return/exchange section.
	 * 
	 * @param orderReturn return or exchange to be displayed on the section.
	 */
	private void addReturn(final OrderReturn orderReturn) {
		if (managedForm == null) {
			return; // form still is not created, so there is no need to add new section. It will be added on addEditorSections() call
		}
		orderReturn.recalculateOrderReturn();
		managedForm.addPart(new OrderReturnsReturnSectionPart(this, getEditor(), orderReturn));
		managedForm.refresh();
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return FulfillmentMessages.get().OrderReturnsPage_Title;
	}

}
