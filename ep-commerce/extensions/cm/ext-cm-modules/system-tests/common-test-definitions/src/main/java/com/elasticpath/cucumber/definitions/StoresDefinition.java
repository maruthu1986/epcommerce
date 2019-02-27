package com.elasticpath.cucumber.definitions;

import java.util.Map;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.editor.StoreEditor;
import com.elasticpath.selenium.resultspane.StoresResultPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ConfigurationActionToolbar;
import com.elasticpath.selenium.util.Utility;

/**
 * System Configuration step definitions.
 */
public class StoresDefinition {
	private final ConfigurationActionToolbar configurationActionToolbar;
	private StoresResultPane storesResultPane;
	private StoreEditor storeEditor;
	private String storeName;
	private String storeCode;
	private String language;
	private String currency;
	private String newLanguage;

	/**
	 * Constructor.
	 */
	public StoresDefinition() {
		configurationActionToolbar = new ConfigurationActionToolbar(SetUp.getDriver());
	}

	/**
	 * Clicks on Stores.
	 */
	@When("^I go to Stores$")
	public void clickStores() {
		storesResultPane = configurationActionToolbar.clickStores();
	}

	/**
	 * Edit store.
	 *
	 * @param storeCode the store code
	 */
	@When("^I edit store (.+) in editor$")
	public void editStore(final String storeCode) {
		storeEditor = storesResultPane.editStore(storeCode);
	}

	/**
	 * Verifies store code in editor.
	 *
	 * @param storeCode the store code
	 */
	@Then("^The store code (.+) should match in the store editor$")
	public void verifyStoreCode(final String storeCode) {
		storeEditor.verifyStoreCode(storeCode);
	}

	/**
	 * Creates a new store.
	 *
	 * @param storeMap The store map
	 */
	@When("^I create store with following values$")
	public void fillInStoreSummary(final Map<String, String> storeMap) {
		storeEditor = storesResultPane.clickCreateStoreButton();
		this.storeCode = "st" + Utility.getRandomUUID();
		this.storeName = storeMap.get("store name") + this.storeCode;
		this.language = storeMap.get("language");
		this.currency = storeMap.get("currency");
		storeEditor.enterStoreCode(this.storeCode);
		storeEditor.enterStoreName(this.storeName);
		storeEditor.enterStoreUrl("http://" + this.storeCode + ".elasticpath.com");
		storeEditor.selectStoreTimezone(storeMap.get("timezone"));
		storeEditor.selectStoreCountry(storeMap.get("store country"));
		storeEditor.selectStoreSubCountry(storeMap.get("store sub country"));
		storeEditor.clickTab("System");
		storeEditor.editStoreSystemSetting("Store HTML Encoding", "UTF-8");
		storeEditor.clickTab("Payments");
		storeEditor.selectStorePrimaryPaymentGateway(storeMap.get("payment gateway"));
		configurationActionToolbar.clickSaveButton();
		storeEditor.clickTab("Warehouse");
		storeEditor.selectRadioButton(storeMap.get("warehouse"));
		storeEditor.clickTab("Catalog");
		storeEditor.selectRadioButton(storeMap.get("catalog"));
		storeEditor.clickTab("Localization");
		storeEditor.selectAndAddAvailableLanguage(this.language);
		storeEditor.selectDefaultLanguage(this.language);
		storeEditor.selectAndAddAvailableCurrency(this.currency);
		storeEditor.selectDefaultCurrency(this.currency);
		storeEditor.clickTab("Marketing");
		storeEditor.editStoreMarketingSetting("Store Admin Email Address", "testadmin@example.com");
		storeEditor.editStoreMarketingSetting("Store From Email (Friendly Name)", "Test Admin");
		storeEditor.editStoreMarketingSetting("Store From Email (Sender Address)", "testadmin@example.com");
		configurationActionToolbar.clickSaveButton();
	}

	/**
	 * Verify store exists in list.
	 */
	@Then("^the store should exist in the list$")
	public void verifyNewStoreExistsInList() {
		storesResultPane.verifyStoreExists(this.storeName);
	}

	/**
	 * Edit store name.
	 *
	 * @param newStoreName String
	 */
	@When("^I edit the store name to (.+)$")
	public void editStoreName(final String newStoreName) {
		storeEditor = storesResultPane.editStore(this.storeCode);
		storeEditor.clickTab("Summary");
		this.storeName = newStoreName;
		storeEditor.enterStoreName(newStoreName);
		configurationActionToolbar.clickSaveButton();
	}

	/**
	 * Adds a new language to the selected languages table.
	 *
	 * @param newLanguage String
	 */
	@When("^I add new language (.+) to the store$")
	public void addLanguageToStore(final String newLanguage) {
		storeEditor = storesResultPane.editStore(this.storeCode);
		storeEditor.clickTab("Localization");
		storeEditor.selectAndAddAvailableLanguage(newLanguage);
		configurationActionToolbar.clickSaveButton();
		this.newLanguage = newLanguage;
	}

	/**
	 * Verifies new language added to selected language table.
	 */
	@Then("^I should see the new language in the store list$")
	public void verifyNewLanguageSelected() {
		storeEditor.verifySelectedLanguage(this.newLanguage);
	}

	/**
	 * Change store state.
	 *
	 * @param newStoreState String
	 */
	@When("^I change the store state to (.+)$")
	public void changeStoreState(final String newStoreState) {
		storeEditor.clickTab("Summary");
		storeEditor.changeStoreState(newStoreState);
	}

	/**
	 * Verifies store state in list.
	 *
	 * @param expState String
	 */
	@Then("^the store list should show (.+) state$")
	public void verifyStoreState(final String expState) {
		storesResultPane.verifyStoreState(expState, this.storeCode);
	}

	/**
	 * Deletes the store.
	 */
	@When("^I delete the store$")
	public void deleteStore() {
		storesResultPane.deleteStore(this.storeCode);
	}

	/**
	 * Verifies store no longer exists.
	 */
	@Then("^store should not exist$")
	public void verifyStoreDoesntExist() {
		storesResultPane.closePane("Stores");
		clickStores();
		storesResultPane.verifyStoreDoesntExists(this.storeCode);
	}

	/**
	 * Verifies store data policies enable state is true.
	 */
	@Then("^Data Policies for the store is enabled$")
	public void verifyStoreDataPolicyEnabled() {
		storeEditor.verifyDataPolicyEnabled();
	}
}
