/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.List;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.AdminStoresPlugin;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.sections.AbstractSettingsSection;
import com.elasticpath.cmclient.core.helpers.store.SettingModel;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;

/**
 * Represent store marketing tab.
 */
public class StoreMarketingPage extends AbstractStorePage {

	/**
	 * Constructs the store marketing page.
	 * 
	 * @param editor the editor
	 * @param authorized whether the current user is authorized to edit the current store
	 */
	public StoreMarketingPage(final AbstractCmClientFormEditor editor, final boolean authorized) {
		super(editor, "StoreMarketingPage", AdminStoresMessages.get().StoreMarketing, authorized); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new StoreMarketingSection(this, editor, this.isEditable()));
		getCustomPageData().put("isEditable", this.isEditable());
		addExtensionEditorSections(editor, managedForm, AdminStoresPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return AdminStoresMessages.get().StoreMarketingSettings;
	}

	/**
	 * Represents store marketing section.
	 */
	class StoreMarketingSection extends AbstractSettingsSection {

		private static final String STORE_MARKETING_TABLE = "Store Marketing Table"; //$NON-NLS-1$

		/**
		 * Constructs the marketing section.
		 * 
		 * @param formPage the form page
		 * @param editor the editor
		 * @param editable whether this section should be editable
		 */
		StoreMarketingSection(final FormPage formPage, final AbstractCmClientFormEditor editor, final boolean editable) {
			super(formPage, editor, STORE_MARKETING_TABLE);
			this.setEditable(editable);
		}

		@Override
		protected List<SettingModel> getInput() {
			List<SettingModel> result = ((StoreEditorModel) getModel()).getMarketingSettings();
			result.sort((object1, object2) -> object1.getName().compareToIgnoreCase(object2.getName()));
			return result;
		}
	}
}
