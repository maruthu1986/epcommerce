/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.settings.editors;

import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.sections.AbstractSettingsSection;
import com.elasticpath.cmclient.core.helpers.store.SettingModel;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.settings.SettingsMessages;
import com.elasticpath.cmclient.store.settings.StoreMarketingPermissions;

/**
 * Represent store marketing tab.
 */
public class SettingsMarketingPage extends AbstractCmClientEditorPage {

	/**
	 * Constructs the store marketing page.
	 * 
	 * @param editor the editor
	 */
	public SettingsMarketingPage(final AbstractCmClientFormEditor editor) {
		super(editor, "StoreMarketingPage", SettingsMessages.get().StoreMarketing); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new StoreMarketingSection(this, editor));
		addExtensionEditorSections(editor, managedForm, StorePlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return SettingsMessages.get().StoreMarketingSettings;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// empty		
	}

	/**
	 * Represents store marketing section.
	 */
	class StoreMarketingSection extends AbstractSettingsSection {

		private static final String SETTINGS_MARKETING_TABLE = "Settings Marketing"; //$NON-NLS-1$

		/**
		 * Constructs the marketing section.
		 * 
		 * @param formPage the form page
		 * @param editor the editor
		 */
		StoreMarketingSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
			super(formPage, editor, SETTINGS_MARKETING_TABLE);
			setEditable(AuthorizationService.getInstance().isAuthorizedWithPermission(StoreMarketingPermissions.MANAGE_STORE_SETTINGS)
					&& AuthorizationService.getInstance().isAuthorizedForStore(((StoreEditorModel) getModel()).getCode()));
		}

		@Override
		protected List<SettingModel> getInput() {
			return ((StoreEditorModel) getModel()).getMarketingSettings();
		}
	}
}
