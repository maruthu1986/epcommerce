/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;

/**
 * Represents the UI for order note tab.
 */
public class OrderNotePage extends AbstractOrderPage {

	private OrderNoteNotesSectionPart orderNoteNotesSectionPart;

	/**
	 * Constructor.
	 *
	 * @param editor the form editor
	 */
	public OrderNotePage(final AbstractCmClientFormEditor editor) {
		super(editor, "productDetails", FulfillmentMessages.get().OrderNotePage_Title); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		orderNoteNotesSectionPart = new OrderNoteNotesSectionPart(this, editor);
		managedForm.addPart(orderNoteNotesSectionPart);
		addExtensionEditorSections(editor, managedForm, FulfillmentPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return FulfillmentMessages.get().OrderNotePage_Form_Title;
	}

}
