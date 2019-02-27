/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.List;

import com.elasticpath.cmclient.admin.stores.AdminStoresPlugin;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.sections.AbstractSettingsSection;
import com.elasticpath.cmclient.core.helpers.store.SettingModel;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;

/**
 * Represent store system tab.
 */
public class StoreSystemPage extends AbstractStorePage {

	/**
	 * Constructs the store system page.
	 * 
	 * @param editor the editor
	 * @param authorized whether the current user is authorized to edit the current store
	 */
	public StoreSystemPage(final AbstractCmClientFormEditor editor, final boolean authorized) {
		super(editor, "StoreSystemPage", AdminStoresMessages.get().StoreSystem, authorized); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new StoreSystemSection(this, editor, this.isEditable()));
		getCustomPageData().put("isEditable", this.isEditable());
		addExtensionEditorSections(editor, managedForm, AdminStoresPlugin.PLUGIN_ID, getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return AdminStoresMessages.get().StoreSystemSettings;
	}

	/**
	 * Represents store system section.
	 */
	class StoreSystemSection extends AbstractSettingsSection {

		private static final String STORE_SYSTEM_TABLE = "Store System"; //$NON-NLS-1$

		/**
		 * Constructs the system section.
		 * 
		 * @param formPage the form page
		 * @param editor the editor
		 * @param editable whether the section should be editable
		 */
		StoreSystemSection(final FormPage formPage, final AbstractCmClientFormEditor editor, final boolean editable) {
			super(formPage, editor, STORE_SYSTEM_TABLE);
			this.setEditable(editable);
		}

		@Override
		protected List<SettingModel> getInput() {
			return ((StoreEditorModel) getModel()).getSystemSettings();
		}
	}
}
