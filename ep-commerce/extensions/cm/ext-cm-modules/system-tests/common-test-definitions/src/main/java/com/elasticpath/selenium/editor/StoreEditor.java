package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Store Editor.
 */
public class StoreEditor extends AbstractPageObject {

	private static final String STORE_EDITOR_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.admin.stores"
			+ ".AdminStoresMessages";
	private static final String STORE_CODE_VALUE_CSS = STORE_EDITOR_PARENT_CSS + ".StoreCode'] > input";
	private static final String STORE_NAME_VALUE_CSS = STORE_EDITOR_PARENT_CSS + ".StoreName'] > input";
	private static final String STORE_URL_VALUE_CSS = STORE_EDITOR_PARENT_CSS + ".StoreUrl'] > input";
	private static final String STORE_TIMEZONE_COMBO_CSS = STORE_EDITOR_PARENT_CSS + ".StoreTimeZone'][widget-id='Operational "
			+ "Timezone'][widget-type='CCombo']";
	private static final String STORE_COUNTRY_COMBO_CSS = STORE_EDITOR_PARENT_CSS + ".StoreCountry'][widget-id='Store "
			+ "Country'][widget-type='CCombo']";
	private static final String STORE_SUB_COUNTRY_COMBO_CSS = STORE_EDITOR_PARENT_CSS + ".StoreSubCountry'][widget-id='Store "
			+ "Sub-Country'][widget-type='CCombo']";
	private static final String STORE_PRIMARY_PAYMENT_GATEWAY_COMBO_CSS = STORE_EDITOR_PARENT_CSS + ".PrimaryPaymentGateway'][widget-id='Payment "
			+ "Gateway'][widget-type='CCombo']";
	private static final String TAB_CSS = "div[widget-id='%s'][seeable='true']";
	private static final String STORE_RADIO_BUTTON_CSS = "div[widget-id='%s'][appearance-id='radio-button']";
	private static final String STORE_ADD_LANGUAGE_BUTTON_CSS = "div[widget-id='Language Selection']~ div div div[widget-id='>']";
	private static final String STORE_DEFAULT_LANGUAGE_COMBO_CSS = "div[widget-id='Default Language'][widget-type='CCombo']";
	private static final String STORE_ADD_CURRENCY_BUTTON_CSS = "div[widget-id='Currency Selection']~ div div div[widget-id='>']";
	private static final String STORE_DEFAULT_CURRENCY_COMBO_CSS = "div[widget-id='Default Currency'][widget-type='CCombo']";
	private static final String AVAILABLE_LANGUAGES_PARENT_CSS = "div[widget-id='Available Languages'][widget-type='Table'] ";
	private static final String AVAILABLE_LANGUAGES_COLUMN_CSS = AVAILABLE_LANGUAGES_PARENT_CSS + "div[column-id='%s']";
	private static final String AVAILABLE_CURRENCIES_PARENT_CSS = "div[widget-id='Available Currencies'][widget-type='Table'] ";
	private static final String AVAILABLE_CURRENCIES_COLUMN_CSS = AVAILABLE_CURRENCIES_PARENT_CSS + "div[column-id='%s']";
	private static final String SELECTED_LANGUAGES_PARENT_CSS = "div[widget-id='Selected Languages'][widget-type='Table'] ";
	private static final String SELECTED_LANGUAGES_COLUMN_CSS = SELECTED_LANGUAGES_PARENT_CSS + "div[column-id='%s']";
	private static final String WAREHOUSE_NAME_BUTTON_CSS = "div[widget-id='%s'][widget-type='Button'][seeable='true']";
	private static final String STORE_MARKETING_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages";
	private static final String STORE_MARKETING_EDIT_VALUE_BUTTON_CSS = STORE_MARKETING_PARENT_CSS + ".Store_Marketing_EditValue']";
	private static final String STORE_MARKETING_SETTINGS_PARENT_CSS = "div[widget-type='Table'][widget-id='Store Marketing Table']";
	private static final String STORE_MARKETING_SETTINGS_COLUMN_CSS = STORE_MARKETING_SETTINGS_PARENT_CSS + " div[column-id='%s']";
	private static final String STORE_MARKETING_SETTINGS_EDIT_VALUE_INPUT_CSS = "div[widget-id='Edit Setting'] div[widget-id='Value'] textarea";
	private static final String STORE_MARKETING_SETTINGS_EDIT_VALUE_SAVE_BUTTON_CSS = STORE_MARKETING_PARENT_CSS
			+ ".AbstractEpDialog_ButtonSave']";
	private static final String STORE_SYSTEM_SETTINGS_PARENT_CSS = "div[widget-type='Table'][widget-id='Store System']";
	private static final String STORE_SYSTEM_SETTINGS_COLUMN_CSS = STORE_SYSTEM_SETTINGS_PARENT_CSS + " div[column-id='%s']";
	private static final String STORE_SYSTEM_SETTINGS_EDIT_VALUE_INPUT_CSS = "div[widget-id='Edit Setting'] div[widget-id='Value'] textarea";
	private static final String STORE_SYSTEM_SETTINGS_EDIT_VALUE_SAVE_BUTTON_CSS = STORE_MARKETING_PARENT_CSS + ".AbstractEpDialog_ButtonSave']";
	private static final String STORE_SYSTEM_EDIT_VALUE_BUTTON_CSS = STORE_MARKETING_PARENT_CSS + ".Store_Marketing_EditValue']";

	private static final String STORE_CHANGE_STATE_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.admin.stores.AdminStoresMessages"
			+ ".ChangeStoreState'][seeable='true']";
	private static final String STORE_STATE_OPTION_CSS = "div[widget-id='%s'][seeable='true']";
	private static final String STORE_STATE_CHANGE_CONFIRMATION_BUTTON_CSS = "div[widget-id='Confirm Store State Change'] div[widget-id='OK']";
	private static final String STORE_ENABLED_DATA_POLICY_CSS = "div[automation-id='com.elasticpath.cmclient.admin.stores.AdminStoresMessages"
			+ ".EnableDataPolicies'] +div";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public StoreEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies if store editor is visible.
	 */
	public void verifyStoreEditor() {
		assertThat(getWaitDriver().waitForElementToBeInteractable(STORE_CODE_VALUE_CSS))
				.as("Store Editor is not visible")
				.isTrue();
	}

	/**
	 * Verify Store Code.
	 *
	 * @param storeCode the Store Code.
	 */
	public void verifyStoreCode(final String storeCode) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(STORE_CODE_VALUE_CSS)).getAttribute("value"))
				.as("Store code does not match in editor")
				.isEqualTo(storeCode);
	}

	/**
	 * Enter store code.
	 *
	 * @param storeCode String
	 */
	public void enterStoreCode(final String storeCode) {
		clearAndType(STORE_CODE_VALUE_CSS, storeCode);
	}

	/**
	 * Enter store name.
	 *
	 * @param storeName String
	 */
	public void enterStoreName(final String storeName) {
		clearAndType(STORE_NAME_VALUE_CSS, storeName);
	}

	/**
	 * Enter store url.
	 *
	 * @param storeUrl String
	 */
	public void enterStoreUrl(final String storeUrl) {
		clearAndType(STORE_URL_VALUE_CSS, storeUrl);
	}

	/**
	 * Select store timezone.
	 *
	 * @param timezone String
	 */
	public void selectStoreTimezone(final String timezone) {
		assertThat(selectComboBoxItem(STORE_TIMEZONE_COMBO_CSS, timezone))
				.as("Unable to find timezone - " + timezone)
				.isTrue();
	}

	/**
	 * Select store country.
	 *
	 * @param country String
	 */
	public void selectStoreCountry(final String country) {
		assertThat(selectComboBoxItem(STORE_COUNTRY_COMBO_CSS, country))
				.as("Unable to find country - " + country)
				.isTrue();
	}

	/**
	 * Select store sub country.
	 *
	 * @param subCountry String
	 */
	public void selectStoreSubCountry(final String subCountry) {
		assertThat(selectComboBoxItem(STORE_SUB_COUNTRY_COMBO_CSS, subCountry))
				.as("Unable to find sub country - " + subCountry)
				.isTrue();
	}

	/**
	 * Select store primary payment gateway.
	 *
	 * @param paymentGateway String
	 */
	public void selectStorePrimaryPaymentGateway(final String paymentGateway) {
		assertThat(selectComboBoxItem(STORE_PRIMARY_PAYMENT_GATEWAY_COMBO_CSS, paymentGateway))
				.as("Unable to find payment gateway - " + paymentGateway)
				.isTrue();
	}

	/**
	 * Clicks to select tab.
	 *
	 * @param tabName the tab name.
	 */
	public void clickTab(final String tabName) {
		String cssSelector = String.format(TAB_CSS, tabName);
		resizeWindow(cssSelector);
		click(getDriver().findElement(By.cssSelector(cssSelector)));
	}

	/**
	 * Select radio button by displayed name.
	 *
	 * @param radioButtonName String
	 */
	public void selectRadioButton(final String radioButtonName) {
		clickButton(String.format(STORE_RADIO_BUTTON_CSS, radioButtonName), radioButtonName);
	}

	/**
	 * Add store language.
	 *
	 * @param language String
	 */
	public void selectAndAddAvailableLanguage(final String language) {
		assertThat(selectItemInDialog(AVAILABLE_LANGUAGES_PARENT_CSS, AVAILABLE_LANGUAGES_COLUMN_CSS, language, ""))
				.as("Unable to find language - " + language)
				.isTrue();
		clickButton(STORE_ADD_LANGUAGE_BUTTON_CSS, "Add Language Button");
	}

	/**
	 * Set default store language.
	 *
	 * @param language String
	 */
	public void selectDefaultLanguage(final String language) {
		assertThat(selectComboBoxItem(STORE_DEFAULT_LANGUAGE_COMBO_CSS, language))
				.as("Unable to find language - " + language)
				.isTrue();
	}

	/**
	 * Add currency to store.
	 *
	 * @param currency String
	 */
	public void selectAndAddAvailableCurrency(final String currency) {
		assertThat(selectItemInDialog(AVAILABLE_CURRENCIES_PARENT_CSS, AVAILABLE_CURRENCIES_COLUMN_CSS, currency, ""))
				.as("Unable to find currency - " + currency)
				.isTrue();
		clickButton(STORE_ADD_CURRENCY_BUTTON_CSS, "Add Currency Button");
	}

	/**
	 * Set default store currency.
	 *
	 * @param currency String
	 */
	public void selectDefaultCurrency(final String currency) {
		assertThat(selectComboBoxItem(STORE_DEFAULT_CURRENCY_COMBO_CSS, currency))
				.as("Unable to find currency - " + currency)
				.isTrue();
	}

	/**
	 * Edit Store marketing setting.
	 *
	 * @param settingName String setting name
	 * @param newValue    String new value for setting
	 */
	public void editStoreMarketingSetting(final String settingName, final String newValue) {
		assertThat(selectItemInDialog(STORE_MARKETING_SETTINGS_PARENT_CSS, STORE_MARKETING_SETTINGS_COLUMN_CSS, settingName, "Name"))
				.as("Unable to find setting - " + settingName)
				.isTrue();
		clickButton(STORE_MARKETING_EDIT_VALUE_BUTTON_CSS, "Edit Value Button");
		clearAndType(STORE_MARKETING_SETTINGS_EDIT_VALUE_INPUT_CSS, newValue);
		clickButton(STORE_MARKETING_SETTINGS_EDIT_VALUE_SAVE_BUTTON_CSS, "Edit Value Save Button");
	}

	/**
	 * Verify selected language exists.
	 *
	 * @param expLanguage String
	 */
	public void verifySelectedLanguage(final String expLanguage) {
		assertThat(selectItemInDialog(SELECTED_LANGUAGES_PARENT_CSS, SELECTED_LANGUAGES_COLUMN_CSS, expLanguage, ""))
				.as("Unable to find selected language - " + expLanguage)
				.isTrue();
	}


	/**
	 * Edit Store system setting.
	 *
	 * @param settingName String setting name
	 * @param newValue    String new value for setting
	 */
	public void editStoreSystemSetting(final String settingName, final String newValue) {
		assertThat(selectItemInDialog(STORE_SYSTEM_SETTINGS_PARENT_CSS, STORE_SYSTEM_SETTINGS_COLUMN_CSS, settingName, "Name"))
				.as("Unable to find setting - " + settingName)
				.isTrue();
		clickButton(STORE_SYSTEM_EDIT_VALUE_BUTTON_CSS, "Edit Value Button");
		clearAndType(STORE_SYSTEM_SETTINGS_EDIT_VALUE_INPUT_CSS, newValue);
		clickButton(STORE_SYSTEM_SETTINGS_EDIT_VALUE_SAVE_BUTTON_CSS, "Edit Value Save Button");
	}

	/**
	 * Change a stores State.
	 *
	 * @param newStoreState String
	 */
	public void changeStoreState(final String newStoreState) {
		clickButton(STORE_CHANGE_STATE_BUTTON_CSS, "Store Change State Button");
		clickButton(String.format(STORE_STATE_OPTION_CSS, newStoreState), newStoreState + " Button");
		clickButton(STORE_STATE_CHANGE_CONFIRMATION_BUTTON_CSS, "Store State Change OK Button");
	}

	/**
	 * Verifies warehouseName.
	 *
	 * @param warehouseName is present
	 */
	public void verifyWarehouseName(final String warehouseName) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(String.format(WAREHOUSE_NAME_BUTTON_CSS, warehouseName))).isDisplayed())
				.as("Expected warehouse name is not present.")
				.isTrue();
	}

	/**
	 * Verify Data Policies is enabled/checked.
	 */
	public void verifyDataPolicyEnabled() {
		assertThat(isSelected(STORE_ENABLED_DATA_POLICY_CSS))
				.as("Data Policies is not enabled.")
				.isTrue();
	}
}