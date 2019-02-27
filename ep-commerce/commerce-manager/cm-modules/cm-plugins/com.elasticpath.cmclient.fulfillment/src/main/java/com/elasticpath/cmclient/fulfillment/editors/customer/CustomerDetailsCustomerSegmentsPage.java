/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;

/**
 * A page used within the Customer details editor. Represents customer segments.
 */
public class CustomerDetailsCustomerSegmentsPage extends AbstractCmClientEditorPage {

	/**
	 * Constructs the page.
	 * 
	 * @param editor the form editor
	 */
	public CustomerDetailsCustomerSegmentsPage(final AbstractCmClientFormEditor editor) {
		super(editor, "CustomerDetailsCustomerSegmentsPage", FulfillmentMessages.get().CustomerSegmentsPage_Title); //$NON-NLS-1$
	}

	/**
	 * Adds the editor sections to the managed form.
	 * 
	 * @param editor the EP form editor
	 * @param managedForm the Eclipse managed form
	 */
	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new CustomerDetailsCustomerSegmentsSection(this, editor));
		addExtensionEditorSections(editor, managedForm, FulfillmentPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	/**
	 * Gets the form columns for the UI representation.
	 * 
	 * @return integer
	 */
	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	/**
	 * Gets the page form title.
	 * 
	 * @return string
	 */
	@Override
	protected String getFormTitle() {
		return FulfillmentMessages.get().CustomerSegmentsPage_FormTitle;
	}

	/**
	 * Adds actions to the toolbar.
	 * 
	 * @param toolBarManager form toolbar manager
	 */
	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// add nothing
	}
}
