/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.editors;

import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.AdminStoresPlugin;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * Represents the UI of the Store Localization Page.
 */
public class StoreLocalizationPage extends AbstractStorePage {

	/**
	 * Creates StoreLocalizationPage Instance.
	 * 
	 * @param editor <code>StoreEditor</code>
	 * @param authorized true if the current user is authorized to edit the store, false if not
	 */
	public StoreLocalizationPage(final AbstractCmClientFormEditor editor, final boolean authorized) {
		super(editor, "StoreLocalizationPage", AdminStoresMessages.get().StoreEditor_Localization_Title, authorized);  //$NON-NLS-1$
	}

	@Override
	protected String getFormTitle() {
		return AdminStoresMessages.get().StoreEditor_Localization_Title;
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new StoreLocalizationOverviewSectionPart(this, editor, this.isEditable()));
		getCustomPageData().put("isEditable", this.isEditable());
		addExtensionEditorSections(editor, managedForm, AdminStoresPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}
	
}
