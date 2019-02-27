/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.fulfillment.FulfillmentFeatureEnablementPropertyTester;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;

/**
 * A page used within the CSR Orders Tab Customer Orders.
 */
public class CustomerDetailsOrdersPage extends AbstractCmClientEditorPage {

	/**
	 * Constructs the page.
	 * 
	 * @param editor the form editor
	 */
	public CustomerDetailsOrdersPage(final AbstractCmClientFormEditor editor) {
		super(editor, "CustomerDetailsOrdersPage", FulfillmentMessages.get().OrdersPage_Title); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new CustomerDetailsOrderSection(this, editor));
		if (FulfillmentFeatureEnablementPropertyTester.ENABLE_CREATE_ORDER) {
			managedForm.addPart(new CustomerDetailsCreateOrderSection(this, editor));
		}
		addExtensionEditorSections(editor, managedForm, FulfillmentPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// nothing
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return FulfillmentMessages.get().OrdersPage_Form_Title;
	}
}
