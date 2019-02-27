/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores;

import java.lang.reflect.Field;

import com.elasticpath.cmclient.core.MessageException;
import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Utility class for message storing.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.VariableNamingConventions", "PMD.ExcessivePublicCount" })
public final class AdminStoresMessages {

	/** Property file binding. */
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.admin.stores.AdminStoresPluginResources"; //$NON-NLS-1$

	/** Empty string. */
	public static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/**
	 * Empty constructor.
	 */
	private AdminStoresMessages() {
	}

	public String StoreAdminSection_StoreAdmin;

	/** Global. */
	public String GmtLabel;

	public String StoreEditorTooltip;

	public String StoreEditor_OnSavePrompt;

	/** Store Configuration Page 1. */
	public String StoreCode;

	public String StoreName;

	public String StoreDescription;

	public String StoreUrl;

	public String StoreTimeZone;

	public String StoreCountry;

	public String StoreSubCountry;

	public String EnableDataPolicies;

	public String DefaultLanguage;

	public String DefaultCurrency;

	/** Payment Types Page 4. */

	public String PrimaryPaymentGateway;

	public String UninstalledCreditCardPaymentGatewayPluginWarning;

	public String SupportedCardTypes;

	public String PaypalExpress;

	public String GoogleCheckout;

	public String GiftCertificates;

	public String NotSupported;

	public String NotInUse;

	/** Additional Store Configuration Page 5. */

	public String StoreEnableCVVCard;

	public String StoreFullCreditCardNumbers;

	/** Store operations/actions. */
	public String CreateStore;

	public String EditStore;

	public String DeleteStore;

	/** Delete dialog. */
	public String ConfirmDeleteStoreMsgBoxTitle;

	public String ConfirmDeleteStoreMsgBoxText;

	/** Errors. */

	public String CanNotCreateStoreMsgBoxTitle;

	public String NoLongerExistStoreMsgBoxTitle;

	public String NoLongerExistStoreMsgBoxText;

	public String StoreInUseTitle;

	public String StoreInUseMessage;

	public String Error;

	public String OpenUrlError;

	public String UrlInvalid;

	public String UrlIsNotSet;

	public String SettingValidationMessage;

	public String CreateStoreErrorMsgBoxTitle;

	public String CreateStoreError_UrlNotUnique;

	/** Validation errors. */
	public String EpValidatorUrlIncorrect;

	public String EpValidatorHttpOrHttpsIsRequired;

	/** Warning messages. */
	public String CreateStoreWarningMsgBoxTitle;

	public String CreateStoreWarning_UrlNotUnique;

	/** Store Editor. */
	public String StoreEditor_SummaryPage_Title;

	public String StoreEditor_TaxesPage_Title;

	public String StoreOverview_Title;

	public String StoreState;

	public String StoreTaxes_Title;

	public String StoreTaxes_Description;

	public String StoreEditor_Localization_Title;

	public String StoreSelectionLanguageAvailable;

	public String StoreSelectionLanguageAssigned;

	public String StoreAssignedCatalog;

	public String StoreAssignedWarehouse;

	public String StoreAssignedTheme;

	public String StoreCatalog;

	public String StoreMarketing;

	public String StoreSystem;

	public String StoreMarketingSettings;

	public String StoreSystemSettings;

	public String StoreWarehouse;

	public String StoreTheme;

	public String StoreTheme_ErrorDialog_Title;

	public String NoneSelection;

	public String NewStoreName;

	public String NewStoreCode;

	/** Payment page. */
	public String Payments;

	public String CreditCardSettings_Group;

	public String FilteredNavigation;

	public String AdvancedSearch;

	public String SharedCustomerAccounts;

	public String CustomerAccountsAreSharedWithTheFollowingStores;

	public String AvailableStores;

	public String LinkedStores;

	/** Marketing tab. */
	public String Store_Marketing_Name;

	public String Store_Marketing_Type;

	public String Store_Marketing_DefaultValue;

	public String Store_Marketing_AssignedValue;

	public String Store_Marketing_EditValue;

	public String Store_Marketing_ClearValue;

	public String NotDefinedValue;

	public String EditSetting;

	/** Localization tab. */
	public String CannotRemoveLocale_Title;

	public String CannotRemoveCurrency_Title;

	public String CannotRemoveLocale_Message;

	public String CannotRemoveCurrency_Message;

	/** Store States. */
	public String StoreState_UnderConstruction;

	public String StoreState_Restricted;

	public String StoreState_Open;

	public String StoreStateChangeError_Title;

	public String StoreStateChangeError_Message;

	public String ConfirmStoreStateChange_Title;

	public String ConfirmStoreStateChange_Message;

	public String ChangeStoreState;

	public String StoreSelectionCurrencyAvailable;

	public String StoreSelectionCurrencyAssigned;

	public String Description;

	public String StoreCodeRequired;

	public String StoreNameRequired;

	public String StoreCreationValidationErrors;

	public String StoreDefaultsSelection;

	public String StoreLanguageSelection;

	public String StoreCurrencySelection;

	public String AlternativeTypes_Section;

	public String TaxConfiguration_TaxCodes;

	public String TaxConfiguration_TaxRegions;

	public String PrimarySection;

	public String GiftCertificates_Section;

	/**
	 * Return a message String given the message key.
	 *
	 * @param messageKey the message key (field) that holds the message String
	 * @return the message
	 */
	public String getMessage(final String messageKey) {
		try {
			final Field field = AdminStoresMessages.class.getField(messageKey);
			return (String) field.get(this);
		} catch (final Exception e) {
			throw new MessageException(e);
		}
	}
	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static AdminStoresMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, AdminStoresMessages.class);
	}


}
