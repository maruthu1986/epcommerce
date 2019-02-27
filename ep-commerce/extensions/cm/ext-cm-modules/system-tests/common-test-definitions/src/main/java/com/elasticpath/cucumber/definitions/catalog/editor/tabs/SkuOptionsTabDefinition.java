package com.elasticpath.cucumber.definitions.catalog.editor.tabs;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.dialogs.AddEditSkuOptionDialog;
import com.elasticpath.selenium.dialogs.AddEditSkuOptionValueDialog;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.domainobjects.Product;
import com.elasticpath.selenium.editor.catalog.tabs.SkuOptionsTab;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.AbstractToolbar;
import com.elasticpath.selenium.toolbars.CatalogManagementActionToolbar;
import com.elasticpath.selenium.util.Utility;

/**
 * Sku Options Tab Definitions.
 */
public class SkuOptionsTabDefinition {

	private AddEditSkuOptionValueDialog addEditSkuOptionValueDialog;
	private AddEditSkuOptionDialog addEditSkuOptionDialog;
	private final AbstractToolbar catalogManagementActionToolbar;
	private final SkuOptionsTab skuOptionsTab;
	private final Product product;

	/**
	 * Constructor.
	 *
	 * @param product variable for the product
	 */
	public SkuOptionsTabDefinition(final Product product) {
		final WebDriver driver = SetUp.getDriver();
		this.catalogManagementActionToolbar = new CatalogManagementActionToolbar(driver);
		this.skuOptionsTab = new SkuOptionsTab(driver);
		this.product = product;
	}

	/**
	 * Create SkuOption.
	 *
	 * @param skuOptionCode the sku option code
	 * @param displayName   the display name
	 */
	@When("^I create a new sku option with sku code (.*) and display name (.*)$")
	public void createSkuOption(final String skuOptionCode, final String displayName) {
		addEditSkuOptionDialog = skuOptionsTab.clickAddSkuOptionButton();
		String skuOptionCodeRandom = skuOptionCode + Utility.getRandomUUID();
		this.product.setSKUOption(skuOptionCodeRandom);
		addEditSkuOptionDialog.enterSkuOptionCode(skuOptionCodeRandom);
		addEditSkuOptionDialog.enterSkuOptionDisplayName(displayName);
		addEditSkuOptionDialog.clickAddButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * verify newly created sku option exists.
	 */
	@Then("^newly created sku option is in the list$")
	public void verifyNewlyCreatedSkuOptionExists() {
		skuOptionsTab.verifySkuOption(addEditSkuOptionDialog.getCode());
	}

	/**
	 * Verify updated sku option name exists.
	 */
	@Then("^updated sku option name is in the list$")
	public void verifyUpdatedSkuOptionExists() {
		skuOptionsTab.verifySkuOption(addEditSkuOptionDialog.getDisplayName());
	}

	/**
	 * Edit sku option name.
	 */
	@When("^I edit the sku option name$")
	public void editSkuOptionName() {
		skuOptionsTab.selectSkuOption(addEditSkuOptionDialog.getCode());
		String skuOptionCodeRandom = "EditSkuDisplayName" + "_" + Utility.getRandomUUID();
		skuOptionsTab.clickEditSkuOptionButton();
		addEditSkuOptionDialog.enterSkuOptionDisplayName(skuOptionCodeRandom);
		addEditSkuOptionDialog.clickAddButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Delete new sku option.
	 */
	@When("^I delete the newly created sku option$")
	public void deleteNewSkuOption() {
		skuOptionsTab.selectTab("SkuOptions");
		skuOptionsTab.selectSkuOption(addEditSkuOptionDialog.getCode());
		skuOptionsTab.clickRemoveSkuOptionButton();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("CatalogMessages.CatalogSkuOptionsSection_RemoveSelectionDialog");
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify newly created sku option is deleted.
	 */
	@Then("^the newly created sku option is deleted$")
	public void verifyNewSkuOptionDelete() {
		skuOptionsTab.verifySkuOptionDelete(addEditSkuOptionDialog.getCode());
	}

	/**
	 * Create SkuOption.
	 *
	 * @param skuOptionCode the sku option code
	 * @param displayName   the display name
	 */
	@And("^I create a new sku option value with sku code (.*) and display name (.*)$")
	public void createSkuOptionValue(final String skuOptionCode, final String displayName) {
		addEditSkuOptionValueDialog = skuOptionsTab.clickAddSkuOptionValueButton();
		String skuOptionValueCodeRandom = skuOptionCode + Utility.getRandomUUID();
		addEditSkuOptionValueDialog.enterSkuOptionCode(skuOptionValueCodeRandom);
		addEditSkuOptionValueDialog.enterSkuOptionValueDisplayName(displayName);
		addEditSkuOptionValueDialog.clickAddButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}


	/**
	 * verify newly created sku option value exists.
	 */
	@Then("^newly created sku option value is in the list$")
	public void verifyNewlyCreatedSkuOptionValueExists() {
		skuOptionsTab.verifySkuOptionValue(addEditSkuOptionDialog.getCode(), addEditSkuOptionValueDialog.getCode());
	}
}
