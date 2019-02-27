/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.settings;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the settings tab.
 */
@SuppressWarnings("PMD.VariableNamingConventions")
public final class SettingsMessages {

	// Empty private constructor to ensure this class can never be constructed.
	private SettingsMessages() {		
	}

	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.store.settings.SettingsResources"; //$NON-NLS-1$

	// SearchView
	public String SearchView_SettingsTab;
	
	public String SearchView_SelectStoreGroup;

	public String CanNotCreateStoreMsgBoxTitle;
	
	public String StoreMarketing;
	
	public String StoreMarketingSettings;
	
	public String StoreEditorTooltip;
	
	public String NoLongerExistStoreMsgBoxText;
	
	public String NoLongerExistStoreMsgBoxTitle;

	public String SettingEditor_OnSavePrompt;

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static SettingsMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, SettingsMessages.class);
	}


}
