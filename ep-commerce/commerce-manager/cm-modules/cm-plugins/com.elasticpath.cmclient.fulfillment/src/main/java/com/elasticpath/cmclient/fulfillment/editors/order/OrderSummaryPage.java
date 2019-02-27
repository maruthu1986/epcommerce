/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;

/**
 * Represents the UI of the order summary page.
 */
public class OrderSummaryPage extends AbstractOrderPage {

	/**
	 * Constructor.
	 * 
	 * @param editor <code>FormEditor</code>
	 */
	public OrderSummaryPage(final AbstractCmClientFormEditor editor) {
		super(editor, "OrderSummaryPage", FulfillmentMessages.get().OrderSummaryPage_Title); //$NON-NLS-1$
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return FulfillmentMessages.get().OrderSummaryPage_Form_Title;
	}

	@Override
	// ---- DOCaddEditorSections
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		// Create the sections
		managedForm.addPart(new OrderSummaryOverviewSectionPart(this, editor));
		managedForm.addPart(new OrderSummaryCustomerInformationSectionPart(this, editor));
		managedForm.addPart(new OrderSummaryBillingAddressSectionPart(this, editor));
		addExtensionEditorSections(editor, managedForm, FulfillmentPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}
	// ---- DOCaddEditorSections
}
