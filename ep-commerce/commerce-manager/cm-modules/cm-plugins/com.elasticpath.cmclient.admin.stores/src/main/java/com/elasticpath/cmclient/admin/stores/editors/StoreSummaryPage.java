/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.editors;

import com.elasticpath.cmclient.admin.stores.AdminStoresPlugin;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * Represents the UI of the Store Summary Page.
 */
public class StoreSummaryPage extends AbstractStorePage {
	
	/**
	 * Creates StoreSummaryPage Instance.
	 * 
	 * @param editor <code>StoreEditor</code>
	 * @param authorized true if the current user is authorized to edit this store, false if not
	 */
	public StoreSummaryPage(final AbstractCmClientFormEditor editor, final boolean authorized) {
		super(editor, "StoreSummaryPage", AdminStoresMessages.get().StoreEditor_SummaryPage_Title, authorized); //$NON-NLS-1$
	}

	@Override
	protected String getFormTitle() {
		return AdminStoresMessages.get().StoreEditor_SummaryPage_Title;
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}
	
	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new StoreSummaryOverviewSectionPart(this, editor, this.isEditable()));
		getCustomPageData().put("isEditable", this.isEditable());
		addExtensionEditorSections(editor, managedForm, AdminStoresPlugin.PLUGIN_ID, getClass().getSimpleName());
	}

}
