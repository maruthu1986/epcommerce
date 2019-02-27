/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.admin.customers.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.admin.customers.AdminCustomersMessages;
import com.elasticpath.cmclient.admin.customers.AdminCustomersPlugin;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * A page used within the Customer details editor. Represents customer segment summary.
 */
public class CustomerSegmentSummaryPage extends AbstractCmClientEditorPage {

	/**
	 * Constructs the page.
	 * 
	 * @param editor the form editor
	 */
	public CustomerSegmentSummaryPage(final AbstractCmClientFormEditor editor) {
		super(editor, "CustomerSegmentSummaryPage", AdminCustomersMessages.get().CustomerSegmentEditor_SummaryPage); //$NON-NLS-1$
	}

	/**
	 * Adds the editor sections to the managed form.
	 * 
	 * @param editor the EP form editor
	 * @param managedForm the Eclipse managed form
	 */
	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new CustomerSegmentSummarySection(this, editor));
		addExtensionEditorSections(editor, managedForm, AdminCustomersPlugin.PLUGIN_ID, this.getClass().getSimpleName());
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
		return AdminCustomersMessages.get().CustomerSegmentEditor_SummaryPage;
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
