/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;

/**
 * A page used within the Customer details editor. Represents user's addresses.
 */
public class CustomerDetailsAdressesPage extends AbstractCmClientEditorPage {

	/**
	 * Constructor.
	 * 
	 * @param editor the form editor
	 */
	public CustomerDetailsAdressesPage(final AbstractCmClientFormEditor editor) {
		super(editor, "CustomerDetailsAddressesPage", FulfillmentMessages.get().AddressPage_Title); //$NON-NLS-1$
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return FulfillmentMessages.get().AddressPage_Form_Title;
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		// Create the sections
		managedForm.addPart(new CustomerDetailsAddressMainSection(this, editor));
		managedForm.addPart(new CustomerDetailsAddressDefaultSection(this, editor));
		addExtensionEditorSections(editor, managedForm, FulfillmentPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		//Do nothing
	}
}
