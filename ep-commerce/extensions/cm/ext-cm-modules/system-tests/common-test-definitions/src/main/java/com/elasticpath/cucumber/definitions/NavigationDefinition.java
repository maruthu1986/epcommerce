package com.elasticpath.cucumber.definitions;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ActivityToolbar;

/**
 * Navigation step definitions.
 */
public class NavigationDefinition {
	private final ActivityToolbar activityToolbar;

	/**
	 * Constructor.
	 */
	public NavigationDefinition() {
		activityToolbar = new ActivityToolbar(SetUp.getDriver());
	}

	/**
	 * Click catalog management.
	 */
	@When("^(?:I have access to|I go to) Catalog Management$")
	public void clickCatalogManagement() {
		activityToolbar.clickCatalogManagementButton();
	}

	/**
	 * Click configuration.
	 */
	@When("^(?:I have access to|I go to) Configuration$")
	public void clickConfiguration() {
		activityToolbar.clickConfigurationButton();
	}

	/**
	 * Click price list mananger.
	 */
	@When("^(?:I have access to|I go to) Price List Manager$")
	public void clickPriceListManager() {
		activityToolbar.clickPriceListManagementButton();
	}

	/**
	 * Click promotions shipping button.
	 */
	@When("^(?:I have access to|I go to) Promotions and Shipping$")
	public void clickPromotionsShippingButton() {
		activityToolbar.clickPromotionsShippingButton();
	}

	/**
	 * Click shipping and receiving.
	 */
	@When("^(?:I have access to|I go to) Shipping/Receiving$")
	public void clickShippingReceiving() {
		activityToolbar.clickShippingReceivingButton();
	}

	/**
	 * Click reporting.
	 */
	@When("^(?:I have access to|I go to) Reporting$")
	public void clickReporting() {
		activityToolbar.clickReportingButton();
	}

	/**
	 * Click customer service.
	 */
	@When("^(?:I have access to|I go to) Customer Service$")
	public void clickCustomerService() {
		activityToolbar.clickCustomerServiceButton();
	}

	/**
	 * Click change set.
	 */
	@When("^(?:I have access to|I go to) Change Set$")
	public void clickChangeSet() {
		activityToolbar.clickChangeSetButton();
	}

	/**
	 * Verify Catalog management button is not present.
	 */
	@Then("^I should not have access to Catalog Management$")
	public void verifyCatalogManagementButtonIsNotPresent() {
		activityToolbar.verifyCatalogManagementButtonIsNotPresent();
	}

	/**
	 * Verify price list manager button is not present.
	 */
	@Then("^I should not have access to Price List Manager$")
	public void verifyPriceListManagerButtonIsNotPresent() {
		activityToolbar.verifyPriceListManagerButtonIsNotPresent();
	}

	/**
	 * Verify configuration button is not present.
	 */
	@Then("^I should not have access to Configuration$")
	public void verifyConfigurationButtonIsNotPresent() {
		activityToolbar.verifyConfigurationButtonIsNotPresent();
	}

	/**
	 * Verify promotions shipping button is not present.
	 */
	@Then("^I should not have access to Promotions and Shipping")
	public void verifyPromotionsShippingButtonIsNotPresent() {
		activityToolbar.verifyPromotionsShippingButtonIsNotPresent();
	}

	/**
	 * Verify shipping receiving button is not present.
	 */
	@Then("^I should not have access to Shipping Receiving")
	public void verifyShippingReceivingButtonIsNotPresent() {
		activityToolbar.verifyShippingReceivingButtonIsNotPresent();
	}

	/**
	 * Verify customer service button is not present.
	 */
	@Then("^I should not have access to Customer Service")
	public void verifyCustomerServiceButtonIsNotPresent() {
		activityToolbar.verifyCustomerServiceButtonIsNotPresent();
	}

	/**
	 * Go to the user menu.
	 */
	@Then("^I go to the User Menu")
	public void clickOnUserMenu() {
		activityToolbar.clickUserMenu();
	}

	/**
	 * Verify changeset button is not present.
	 */
	@Then("I should not have access to changesets")
	public void verifyChangesetButtonIsNotPresent() {
		activityToolbar.verifyChangesetButtonNotPresent();
	}
}
