/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.plugin.payment.PaymentGatewayType;

/**
 * Represents the model of store to be populated in <code>StoreEditor</code>.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class StoreEditorModel {

	private Store store;

	private Set<StoreEditorModel> sharedStoreEditorModels;

	private Set<StoreEditorModel> persistedSharedStoreEditorModels;

	private List<SettingModel> marketingSettings;

	private boolean availableToMarketingOnly;

	private List<SettingModel> systemSettings;

	private String theme;

	private String storeBrowsingSetting;

	private String storeAdvancedSearchSetting;

	private Boolean storeEnableDataPoliciesSetting;

	private StoreEditorModelHelper editorModelHelper;

	/**
	 * Creates the store editor model based on store object.
	 *
	 * @param store the store object
	 */
	StoreEditorModel(final Store store) {
		this.store = store;
	}

	/**
	 * Creates the store editor model based on store object.
	 * Takes into account the necessity to filter marketing settings using metadata
	 *
	 * @param store the Store
	 * @param availableToMarketingOnly true if only marketing related settings with metadata
	 * key = availableToMarketing and metadata value set to true should be loaded
	 */
	StoreEditorModel(final Store store, final boolean availableToMarketingOnly) {
		this.store = store;
		this.availableToMarketingOnly = availableToMarketingOnly;
	}

	/**
	 * Gets the store object.
	 *
	 * @return the store object.
	 */
	public Store getStore() {
		return store;
	}

	/**
	 * Sets the store.
	 *
	 * @param store the store
	 */
	void setStore(final Store store) {
		this.store = store;
		persistedSharedStoreEditorModels = null;
		sharedStoreEditorModels = null;
	}

	/**
	 * @return the Store's Code
	 */
	public String getStoreCode() {
		return getStore().getCode();
	}

	/**
	 * Gets the added shared store models.
	 *
	 * @return the added models
	 */
	Set<StoreEditorModel> getAddedModels() {
		final Set<StoreEditorModel> addedModels = new HashSet<StoreEditorModel>();
		for (StoreEditorModel addedModel : getSharedLoginStoreEntries()) {
			if (!persistedSharedStoreEditorModels.contains(addedModel)) {
				addedModels.add(addedModel);
			}
		}
		return addedModels;
	}

	/**
	 * Gets the removed shared store models.
	 *
	 * @return the removed models
	 */
	Set<StoreEditorModel> getRemovedModels() {
		final Set<StoreEditorModel> removedModels = new HashSet<StoreEditorModel>();
		for (StoreEditorModel removedModel : persistedSharedStoreEditorModels) {
			if (!sharedStoreEditorModels.contains(removedModel)) {
				removedModels.add(removedModel);
			}
		}
		return removedModels;
	}

	/**
	 * Gets set of entries corresponding to shared login stores.
	 *
	 * @return the set of <code>StoreEditorModel</code> entities
	 */
	public Set<StoreEditorModel> getSharedLoginStoreEntries() {
		if (persistedSharedStoreEditorModels == null) {
			sharedStoreEditorModels = new HashSet<StoreEditorModel>();
			Collection<Store> sharedLoginStores = getEditorModelHelper().loadSharedStores(store);
			for (Store sharedLoginStore : sharedLoginStores) {
				sharedStoreEditorModels.add(new StoreEditorModel(sharedLoginStore));
			}
			persistedSharedStoreEditorModels = new HashSet<StoreEditorModel>(sharedStoreEditorModels);
		}
		return sharedStoreEditorModels;
	}

	/**
	 * Gets the marketing settings.
	 *
	 * @return the list of marketing settings
	 */
	public List<SettingModel> getMarketingSettings() {
		return marketingSettings;
	}

	/**
	 * Sets the marketing settings.
	 *
	 * @param marketingSettings the marketing settings
	 */
	void setMarketingSettings(final List<SettingModel> marketingSettings) {
		this.marketingSettings = marketingSettings;
	}

	/**
	 * Gets the system setting values.
	 *
	 * @return the list of system setting values
	 */
	public List<SettingModel> getSystemSettings() {
		return systemSettings;
	}

	/**
	 * Gets the system settings.
	 *
	 * @param systemSettings the system settings
	 */
	void setSystemSettings(final List<SettingModel> systemSettings) {
		this.systemSettings = systemSettings;
	}

	/**
	 * Sets setting value for store theme setting.
	 *
	 * @param storeThemeSettingValue value to set
	 */
	public void setStoreThemeSetting(final String storeThemeSettingValue) {
		this.theme = storeThemeSettingValue;
	}

	/**
	 * Gets setting value for store theme setting.
	 *
	 * @return store theme setting value
	 */
	public String getStoreThemeSetting() {
		return theme;
	}

	/**
	 * Sets setting value for store browsing setting.
	 *
	 * @param storeBrowsingSetting value to set
	 */
	public void setStoreBrowsingSetting(final String storeBrowsingSetting) {
		this.storeBrowsingSetting = storeBrowsingSetting;
	}

	/**
	 * Gets setting value for store browsing setting.
	 *
	 * @return store browsing setting value
	 */
	public String getStoreBrowsingSetting() {
		return storeBrowsingSetting;
	}

	/**
	 * Sets the value for the advanced search setting.
	 *
	 * @param storeAdvancedSearchSetting value to set
	 */
	public void setStoreAdvancedSearchSetting(final String storeAdvancedSearchSetting) {
		this.storeAdvancedSearchSetting = storeAdvancedSearchSetting;
	}

	/**
	 * Gets the setting value for store advanced search.
	 *
	 * @return setting value for store advanced search
	 */
	public String getStoreAdvancedSearchSetting() {
		return storeAdvancedSearchSetting;
	}

	/**
	 * Sets the value for the store enable data policies setting.
	 *
	 * @param storeEnableDataPoliciesSetting value to set
	 */
	public void setStoreEnableDataPoliciesSettingEnabled(final Boolean storeEnableDataPoliciesSetting) {
		this.storeEnableDataPoliciesSetting = storeEnableDataPoliciesSetting;
	}

	/**
	 * Gets the setting value for store enable data policies.
	 *
	 * @return setting value for store enable data policies.
	 */
	public Boolean isStoreEnableDataPoliciesSettingEnabled() {
		return storeEnableDataPoliciesSetting;
	}


	/**
	 * Sets default locale.
	 *
	 * @param defaultLocale default locale
	 */
	public void setDefaultLocale(final Locale defaultLocale) {
		store.setDefaultLocale(defaultLocale);
	}

	/**
	 * Gets store's default Locale.
	 *
	 * @return default locale of the store
	 */
	public Locale getDefaultLocale() {
		return store.getDefaultLocale();
	}

	/**
	 * Gets UID of store.
	 *
	 * @return store UID
	 */
	public long getUidPk() {
		return store.getUidPk();
	}

	/**
	 * Sets store name.
	 *
	 * @param name store name
	 */
	public void setName(final String name) {
		store.setName(name);
	}

	/**
	 * Gets the name of store.
	 *
	 * @return the name of store
	 */
	public String getName() {
		return store.getName();
	}

	/**
	 * Sets store code.
	 *
	 * @param code store code
	 */
	public void setCode(final String code) {
		store.setCode(code);
	}

	/**
	 * Gets the code of store.
	 *
	 * @return store code
	 */
	public String getCode() {
		return store.getCode();
	}

	/**
	 * Sets store URL.
	 *
	 * @param url store URL
	 */
	public void setUrl(final String url) {
		store.setUrl(url);
	}

	/**
	 * Gets URL of store.
	 *
	 * @return store URL
	 */
	public String getUrl() {
		return store.getUrl();
	}

	/**
	 * Gets store's catalog.
	 *
	 * @return catalog
	 */
	public Catalog getCatalog() {
		return store.getCatalog();
	}

	/**
	 * Sets catalog to the store.
	 *
	 * @param catalog catalog
	 */
	public void setCatalog(final Catalog catalog) {
		store.setCatalog(catalog);
	}

	/**
	 * Sets types of credit cards supported by store.
	 *
	 * @param creditCardTypes set of credit card types
	 */
	public void setCreditCardTypes(final Set<CreditCardType> creditCardTypes) {
		store.setCreditCardTypes(creditCardTypes);
	}

	/**
	 * Gets types of credit cards supported by store.
	 *
	 * @return set of credit card types
	 */
	public Set<CreditCardType> getCreditCardTypes() {
		return store.getCreditCardTypes();
	}

	/**
	 * Checks whether store persistent or not.
	 *
	 * @return true if store is persistent, false otherwise
	 */
	public boolean isPersistent() {
		return store.isPersisted();
	}

	/**
	 * Retrieves the store's payment gateway for the given payment gateway type.
	 *
	 * @param paymentGatewayType the payment gateway type
	 * @return Map between PaymentGatewayType and PaymentGateway
	 */
	public PaymentGateway getPaymentGateway(final PaymentGatewayType paymentGatewayType) {
		return store.getPaymentGatewayMap().get(paymentGatewayType);
	}

	/**
	 * Removes the corresponding payment gateway from the store's set of PaymentGateways.
	 *
	 * @param paymentGatewayType the type of PaymentGateway to remove
	 */
	public void removePaymentGateway(final PaymentGatewayType paymentGatewayType) {
		PaymentGateway gateway = getPaymentGateway(paymentGatewayType);
		if (gateway != null) {
			Set<PaymentGateway> paymentGateways = new HashSet<PaymentGateway>(store.getPaymentGateways());
			paymentGateways.remove(gateway);

			store.setPaymentGateways(paymentGateways);
		}
	}

	/**
	 * Puts a {@link PaymentGateway} onto a store.
	 * If payment gateway is null, all {@link PaymentGateway}s of the applicable payment types will be removed.
	 *
	 * @param applicablePaymentGatewayTypes the applicable payment gateway types
	 * @param paymentGateway the payment gateway
	 */
	public void putPaymentGateway(final Collection<PaymentGatewayType> applicablePaymentGatewayTypes, final PaymentGateway paymentGateway) {
		if (paymentGateway != null && !applicablePaymentGatewayTypes.contains(paymentGateway.getPaymentGatewayType())) {
			throw new IllegalArgumentException(
				NLS.bind(CoreMessages.get().SelectedGatewayPaymentTypeNotInApplicableTypes,
				paymentGateway.getPaymentGatewayType(), applicablePaymentGatewayTypes));
		}

		for (PaymentGatewayType paymentGatewayType : applicablePaymentGatewayTypes) {
			removePaymentGateway(paymentGatewayType);
		}

		if (paymentGateway != null) {
			Set<PaymentGateway> paymentGateways = new HashSet<PaymentGateway>(store.getPaymentGateways());
			paymentGateways.add(paymentGateway);
			
			store.setPaymentGateways(paymentGateways);
		}
	}

	/**
	 * Verifies whether credit card is Cvv2 enabled or not.
	 * 
	 * @return true if credit card is Cvv2 enabled, false otherwise
	 */
	public boolean isCreditCardCvv2Enabled() {
		return store.isCreditCardCvv2Enabled();
	}

	/**
	 * Sets whether store has CVV2 verification enabled.
	 * 
	 * @param creditCardCvv2Enabled verification flag
	 */
	public void setCreditCardCvv2Enabled(final boolean creditCardCvv2Enabled) {
		store.setCreditCardCvv2Enabled(creditCardCvv2Enabled);
	}

	/**
	 * Checks whether store full credit card enabled.
	 * 
	 * @return true if store full credit card enabled, false otherwise
	 */
	public boolean isStoreFullCreditCardsEnabled() {
		return store.isStoreFullCreditCardsEnabled();
	}

	/**
	 * Sets whether the store saves full/unmasked credit card numbers on orders placed.
	 * 
	 * @param savingCreditCardWithOrdersEnabled saving credit card policy flag
	 */
	public void setStoreFullCreditCardsEnabled(final boolean savingCreditCardWithOrdersEnabled) {
		store.setStoreFullCreditCardsEnabled(savingCreditCardWithOrdersEnabled);
	}

	/**
	 * Gets warehouse.
	 * 
	 * @return warehouse
	 */
	public Warehouse getWarehouse() {
		return store.getWarehouse();
	}

	/**
	 * Sets warehouses.
	 * 
	 * @param warehouses warehouses to set
	 */
	public void setWarehouses(final List<Warehouse> warehouses) {
		store.setWarehouses(warehouses);
	}

	/**
	 * Sets store country.
	 * 
	 * @param country country
	 */
	public void setCountry(final String country) {
		store.setCountry(country);
	}

	/**
	 * Gets store country.
	 * 
	 * @return country
	 */
	public String getCountry() {
		return store.getCountry();
	}

	/**
	 * Sets store sub country.
	 * 
	 * @param subCountry sub country
	 */
	public void setSubCountry(final String subCountry) {
		store.setSubCountry(subCountry);
	}

	/**
	 * Gets store sub country.
	 * 
	 * @return sub country
	 */
	public String getSubCountry() {
		return store.getSubCountry();
	}

	/**
	 * Sets store time zone.
	 * 
	 * @param timeZone time zone
	 */
	public void setTimeZone(final TimeZone timeZone) {
		store.setTimeZone(timeZone);
	}

	/**
	 * Gets store time zone.
	 * 
	 * @return time zone
	 */
	public TimeZone getTimeZone() {
		return store.getTimeZone();
	}

	/**
	 * Sets store description.
	 * 
	 * @param description store description
	 */

	public void setDescription(final String description) {
		store.setDescription(description);
	}

	/**
	 * Gets store description.
	 * 
	 * @return store description
	 */
	public String getDescription() {
		return store.getDescription();
	}

	/**
	 * Sets store tax codes.
	 * 
	 * @param taxCodes tax codes
	 */
	public void setTaxCodes(final Set<TaxCode> taxCodes) {
		store.setTaxCodes(taxCodes);
	}

	/**
	 * Sets tax jurisdictions.
	 * 
	 * @param taxJurisdictions tax jurisdictions
	 */
	public void setTaxJurisdictions(final Set<TaxJurisdiction> taxJurisdictions) {
		store.setTaxJurisdictions(taxJurisdictions);
	}

	/**
	 * Sets tax jurisdictions.
	 * 
	 * @return tax jurisdictions
	 */
	public Set<TaxJurisdiction> getTaxJurisdictions() {
		return store.getTaxJurisdictions();
	}

	/**
	 * Gets store tax codes.
	 * 
	 * @return tax codes
	 */
	public Set<TaxCode> getTaxCodes() {
		return store.getTaxCodes();
	}

	/**
	 * Sets default currency.
	 * 
	 * @param defaultCurrency default currency
	 */
	public void setDefaultCurrency(final Currency defaultCurrency) {
		store.setDefaultCurrency(defaultCurrency);
	}

	/**
	 * Gets default currency.
	 * 
	 * @return default currency
	 */
	public Currency getDefaultCurrency() {
		return store.getDefaultCurrency();
	}

	/**
	 * Sets supported currencies.
	 * 
	 * @param supportedCurrencies supported currencies
	 * @throws DefaultValueRemovalForbiddenException if supported currencies do not contain the default
	 */
	public void setSupportedCurrencies(final Collection<Currency> supportedCurrencies) throws DefaultValueRemovalForbiddenException {
		store.setSupportedCurrencies(supportedCurrencies);
	}

	/**
	 * Gets the list of supported currencies.
	 * 
	 * @return supported currencies
	 */
	public List<Currency> getSupportedCurrencies() {
		return new ArrayList<Currency>(store.getSupportedCurrencies());
	}

	/**
	 * Sets supported locales.
	 * 
	 * @param supportedLocales suppored locales
	 * @throws DefaultValueRemovalForbiddenException if the new locales do not contain the default
	 */
	public void setSupportedLocales(final Collection<Locale> supportedLocales) throws DefaultValueRemovalForbiddenException {
		store.setSupportedLocales(supportedLocales);
	}

	/**
	 * Gets supported locales.
	 * 
	 * @return supported locales
	 */
	public Collection<Locale> getSupportedLocales() {
		return store.getSupportedLocales();
	}

	/**
	 * Gets the email sender name.
	 * 
	 * @return email sender name
	 */
	public String getEmailSenderName() {
		return store.getEmailSenderName();
	}

	/**
	 * Sets the email sender name.
	 * 
	 * @param assignedValue the assigned value
	 */
	public void setEmailSenderName(final String assignedValue) {
		store.setEmailSenderName(assignedValue);
	}

	/**
	 * Gets email sender address.
	 * 
	 * @return the email sender address
	 */
	public String getEmailSenderAddress() {
		return store.getEmailSenderAddress();
	}

	/**
	 * Sets the email sender address.
	 * 
	 * @param assignedValue the assigned value
	 */
	public void setEmailSenderAddress(final String assignedValue) {
		store.setEmailSenderAddress(assignedValue);
	}

	/**
	 * Gets the store admin email address.
	 * 
	 * @return the store admin email address.
	 */
	public String getStoreAdminEmailAddress() {
		return store.getStoreAdminEmailAddress();
	}

	/**
	 * Sets the store admin email address.
	 * 
	 * @param assignedValue the assigned value
	 */
	public void setStoreAdminEmailAddress(final String assignedValue) {
		store.setStoreAdminEmailAddress(assignedValue);
	}

	/**
	 * Gets the display out of stock property.
	 * 
	 * @return true if diaply products out of stock and false otherwise
	 */
	public boolean isDisplayOutOfStock() {
		return store.isDisplayOutOfStock();
	}

	/**
	 * Sets the display out of stock property.
	 * 
	 * @param outOfStock true if display products out of stock and false otherwise
	 */
	public void setDisplayOutOfStock(final Boolean outOfStock) {
		store.setDisplayOutOfStock(outOfStock);
	}

	/**
	 * Gets the content encoding.
	 * 
	 * @return the content encoding
	 */
	public String getContentEncoding() {
		return store.getContentEncoding();
	}

	/**
	 * Sets the content encoding.
	 * 
	 * @param assignedValue the assigned value
	 */
	public void setContentEncoding(final String assignedValue) {
		store.setContentEncoding(assignedValue);
	}

	/**
	 * Gets current StoreState.
	 * 
	 * @return StoreState constant instance.
	 */
	public StoreState getStoreState() {
		return store.getStoreState();
	}

	/**
	 * Sets current StoreState.
	 * 
	 * @param storeState the constant StoreState instance.
	 */
	public void setStoreState(final StoreState storeState) {
		store.setStoreState(storeState);
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof StoreEditorModel)) {
			return false;
		}
		final Store store = ((StoreEditorModel) other).store;
		if (store.getCode() != null) {
			return store.getCode().equals(this.store.getCode());
		}
		return this.store.getUidPk() == store.getUidPk();
	}

	@Override
	public int hashCode() {
		if (store.getCode() != null) {
			return store.getCode().hashCode();
		}
		return new Long(store.getUidPk()).intValue();
	}

	/**
	 * Checks whether it is required to load all settings with associated availableToMarketing
	 * metadata or only settings having metadata with value equal to true (available to marketing).
	 * 
	 * @return true if only settings with metadata {availableToMarketing, true} should be loaded
	 */
	public boolean loadSettingsAvailableToMarketingOnly() {
		return availableToMarketingOnly;
	}
	
	/**
	 * Checks the payment methods configured for the store being edited.
	 * {@link PaymentGatewayType#GIFT_CERTIFICATE} is not included in the check, only {@link PaymentGatewayType#PAYPAL_EXPRESS},
	 * {@link PaymentGatewayType#CREDITCARD}.
	 *
	 * @return True if at least payment method is configured for the store.
	 */
	boolean isPaymentMethodSelected() {
		PaymentGateway paypalGateway = getPaymentGateway(PaymentGatewayType.PAYPAL_EXPRESS);
		PaymentGateway hostedPageGateway = getPaymentGateway(PaymentGatewayType.HOSTED_PAGE);
		PaymentGateway creditCardGateway = getPaymentGateway(PaymentGatewayType.CREDITCARD);
		
		Set<CreditCardType> creditCardTypes = getCreditCardTypes();
		if ((creditCardGateway == null || creditCardTypes.isEmpty()) && paypalGateway == null && hostedPageGateway == null) {
			return false;
		}
		
		return true;
	}

	/** @return the helper */
	protected StoreEditorModelHelper getEditorModelHelper() {
		if (this.editorModelHelper == null) {
			this.editorModelHelper = StoreEditorModelHelper.createStoreEditorModelHelper();
		}
		return this.editorModelHelper;
	}


}
