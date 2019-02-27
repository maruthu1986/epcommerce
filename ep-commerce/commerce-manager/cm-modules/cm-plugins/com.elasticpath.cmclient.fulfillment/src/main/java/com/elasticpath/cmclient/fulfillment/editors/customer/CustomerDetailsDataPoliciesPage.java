/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;

/**
 * A page used within Customer info.
 */
public class CustomerDetailsDataPoliciesPage extends AbstractCmClientEditorPage {

	/**
	 * Constructs the page.
	 *
	 * @param editor the form editor
	 */
	public CustomerDetailsDataPoliciesPage(final AbstractCmClientFormEditor editor) {
		super(editor, "CustomerDetailsDataPoliciesPage", FulfillmentMessages.get().CustomerDataPolicies_Title); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new CustomerDataPoliciesSection(this, editor));

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
		return FulfillmentMessages.get().CustomerDataPolicies_Title;
	}
}
