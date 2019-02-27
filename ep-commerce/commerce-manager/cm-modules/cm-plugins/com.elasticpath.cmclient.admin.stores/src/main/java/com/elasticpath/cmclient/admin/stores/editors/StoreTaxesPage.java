/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.editors;

import com.elasticpath.cmclient.admin.stores.AdminStoresPlugin;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * Represents the UI of the Store Taxes Page.
 */
public class StoreTaxesPage extends AbstractStorePage {

	/**
	 * Creates StoreTaxesPage Instance.
	 * 
	 * @param editor <code>StoreEditor</code>
	 * @param authorized whether the current user is authorized to edit the store
	 */
	public StoreTaxesPage(final AbstractCmClientFormEditor editor, final boolean authorized) {
		super(editor, "StoreTaxesPage", AdminStoresMessages.get().StoreEditor_TaxesPage_Title, authorized); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new StoreTaxesSectionPart(this, editor, this.isEditable()));
		getCustomPageData().put("isEditable", this.isEditable());
		addExtensionEditorSections(editor, managedForm, AdminStoresPlugin.PLUGIN_ID, getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return AdminStoresMessages.get().StoreEditor_TaxesPage_Title;
	}

}
