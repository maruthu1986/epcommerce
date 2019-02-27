/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.editors;

import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.AdminStoresPlugin;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * Represents the UI of the Store Warehouse Page.
 */
public class StoreWarehousePage extends AbstractStorePage {

	
	/**
	 * Creates StoreWarehousePage Instance.
	 *
	 * @param editor <code>StoreEditor</code>
	 * @param authorized whether the current user is authorized to edit the current store
	 */
	public StoreWarehousePage(final AbstractCmClientFormEditor editor, final boolean authorized) {
		super(editor, "StoreWarehousePage", AdminStoresMessages.get().StoreWarehouse, authorized); //$NON-NLS-1$
	}

	@Override
	protected String getFormTitle() {
		return AdminStoresMessages.get().StoreWarehouse;
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new StoreWarehouseAssignedWarehouseSectionPart(this, editor, this.isEditable()));
		getCustomPageData().put("isEditable", this.isEditable());
		addExtensionEditorSections(editor, managedForm, AdminStoresPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}
}
