/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.store;

import java.util.HashMap;
import java.util.Map;


/**
 * Implementation of <code>SettingsFactory</code> interface for store settings.
 */
public class StoreSettingsFactory implements SettingsFactory {

	/** Path for friendly name setting. */
	public static final String FRIENDLY_NAME_SETTING = "STORE/FRIENDLYNAME"; //$NON-NLS-1$

	/** Path for sender address setting. */
	public static final String SENDER_ADDRESS_SETTING = "STORE/SENDREADDRESS"; //$NON-NLS-1$

	/** Path for admin email address setting. */
	public static final String ADMIN_EMAIL_ADDRESS_SETTING = "STORE/ADMINADSRESS"; //$NON-NLS-1$

	/** Path for display out of stock setting. */
	public static final String DISPLAY_OUT_OF_STOCK_SETTING = "STORE/OUTOFSTOCK"; //$NON-NLS-1$

	/** Path for HTML encoding setting. */
	public static final String HTML_ENCODING_SETTING = "STORE/HTMLENCODING"; //$NON-NLS-1$

	private static final String TYPE_STRING = "String"; //$NON-NLS-1$

	private static final String TYPE_BOOLEAN = "boolean"; //$NON-NLS-1$

	private final StoreEditorModel storeModel;

	private final Map<String, AbstractStoreSettingModel> settingsModelMap = new HashMap<String, AbstractStoreSettingModel>();

	/**
	 * Creates the store settings factory.
	 * 
	 * @param storeModel the store model
	 */
	public StoreSettingsFactory(final StoreEditorModel storeModel) {
		this.storeModel = storeModel;

		initializeModelMap();
	}

	private void initializeModelMap() {
		final AbstractStoreSettingModel[] settingsList = { new FriendlyNameSetting(storeModel), new SenderAddressSetting(storeModel),
				new AdminEmailAddressSetting(storeModel), new DisplayOutOfStockSetting(storeModel), new HTMLEncodingSetting(storeModel) };

		for (AbstractStoreSettingModel model : settingsList) {
			settingsModelMap.put(model.getPath(), model);
		}
	}

	@Override
	public SettingModel createSetting(final String path, final String storeCode) {
		return settingsModelMap.get(path);
	}

	/**
	 * Represents the friendly name setting.
	 */
	private class FriendlyNameSetting extends AbstractStoreSettingModel {

		FriendlyNameSetting(final StoreEditorModel editorModel) {
			super(editorModel, FRIENDLY_NAME_SETTING, "Store From Email (Friendly Name)", TYPE_STRING, ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		public String getAssignedValue() {
			return getStoreModel().getEmailSenderName();
		}

		public void setAssignedValue(final String assignedValue) {
			getStoreModel().setEmailSenderName(assignedValue);
		}

	}

	/**
	 * Represents the sender address setting.
	 */
	private class SenderAddressSetting extends AbstractStoreSettingModel {

		SenderAddressSetting(final StoreEditorModel editorModel) {
			super(editorModel, SENDER_ADDRESS_SETTING, "Store From Email (Sender Address)", TYPE_STRING, ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		public String getAssignedValue() {
			return getStoreModel().getEmailSenderAddress();
		}

		public void setAssignedValue(final String assignedValue) {
			getStoreModel().setEmailSenderAddress(assignedValue);
		}
	}

	/**
	 * Represents the admin email address setting.
	 */
	private class AdminEmailAddressSetting extends AbstractStoreSettingModel {

		AdminEmailAddressSetting(final StoreEditorModel editorModel) {
			super(editorModel, ADMIN_EMAIL_ADDRESS_SETTING, "Store Admin Email Address", TYPE_STRING, ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		public String getAssignedValue() {
			return getStoreModel().getStoreAdminEmailAddress();
		}

		public void setAssignedValue(final String assignedValue) {
			getStoreModel().setStoreAdminEmailAddress(assignedValue);
		}
	}

	/**
	 * Represents the display out of stock setting.
	 */
	private class DisplayOutOfStockSetting extends AbstractStoreSettingModel {

		DisplayOutOfStockSetting(final StoreEditorModel editorModel) {
			super(editorModel, DISPLAY_OUT_OF_STOCK_SETTING, "Display Out Of Stock Products", TYPE_BOOLEAN, "true"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		public String getAssignedValue() {
			return Boolean.toString(getStoreModel().isDisplayOutOfStock());
		}

		public void setAssignedValue(final String assignedValue) {
			getStoreModel().setDisplayOutOfStock(Boolean.valueOf(assignedValue));
		}
	}

	/**
	 * Represents the HTML encoding of stock setting.
	 */
	private class HTMLEncodingSetting extends AbstractStoreSettingModel {

		HTMLEncodingSetting(final StoreEditorModel editorModel) {
			super(editorModel, HTML_ENCODING_SETTING, "Store HTML Encoding", TYPE_STRING, "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		public String getAssignedValue() {
			return getStoreModel().getContentEncoding();
		}

		public void setAssignedValue(final String assignedValue) {
			getStoreModel().setContentEncoding(assignedValue);
		}
	}
}
