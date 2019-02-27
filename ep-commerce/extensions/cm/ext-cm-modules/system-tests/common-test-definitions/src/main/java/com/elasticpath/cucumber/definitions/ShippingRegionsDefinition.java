package com.elasticpath.cucumber.definitions;

import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.CreateEditShippingRegionDialog;
import com.elasticpath.selenium.resultspane.ShippingRegionsPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ConfigurationActionToolbar;
import com.elasticpath.selenium.util.Utility;

/**
 * Shipping Regions step definitions.
 */
public class ShippingRegionsDefinition {

	private final ConfigurationActionToolbar configurationActionToolbar;
	private ShippingRegionsPane shippingRegionsPane;
	private CreateEditShippingRegionDialog createEditShippingRegionsDialog;
	private static String uniqueShippingRegionName = "";

	/**
	 * Constructor for Shipping Regions.
	 */
	public ShippingRegionsDefinition() {
		configurationActionToolbar = new ConfigurationActionToolbar(SetUp.getDriver());
	}

	/**
	 * Click Shipping Region.
	 */
	@When("^I go to Shipping Regions$")
	public void clickShippingRegions() {
		shippingRegionsPane = configurationActionToolbar.clickShippingRegions();
	}

	/**
	 * Create ShippingRegion.
	 *
	 * @param country    selecting a country
	 * @param regionName the name of the region name.
	 */
	@When("^I create a Shipping Region for country (.+) named (.+)$")
	public void createShippingRegion(final String country, final String regionName) {
		uniqueShippingRegionName = regionName + Utility.getRandomUUID();
		createEditShippingRegionsDialog = shippingRegionsPane.clickCreateShippingRegionsButton();

		createEditShippingRegionsDialog.enterRegionName(uniqueShippingRegionName);
		createEditShippingRegionsDialog.selectShippingRegionCountry(country);
		createEditShippingRegionsDialog.clickMoveRightCreate();
		createEditShippingRegionsDialog.clickSave();
	}

	/**
	 * Editing the new Shipping Region.
	 *
	 * @param country       selecting a country
	 * @param newRegionName editing the region name to another
	 */
	@When("^I edit newly created shipping region to the country (.+) named (.+)$")
	public void editShippingRegion(final String country, final String newRegionName) {
		createEditShippingRegionsDialog = shippingRegionsPane.clickEditShippingRegionsButton(uniqueShippingRegionName);
		createEditShippingRegionsDialog.clickMoveAllLeftEdit();

		uniqueShippingRegionName = newRegionName + Utility.getRandomUUID();
		createEditShippingRegionsDialog.enterRegionName(uniqueShippingRegionName);
		createEditShippingRegionsDialog.selectShippingRegionCountry(country);
		createEditShippingRegionsDialog.clickMoveRightEdit();
		createEditShippingRegionsDialog.clickSave();
	}

	/**
	 * Verify new shipping region exists.
	 */
	@When("^the new shipping region name should exist in the list$")
	public void verifyShippingRegionsExists() {
		shippingRegionsPane.verifyShippingRegionsIsInList(uniqueShippingRegionName);
	}

	/**
	 * Verify new shipping region no longer exists.
	 */
	@When("^the newly created shipping region no longer exists$")
	public void verifyNewShippingRegionIsDeleted() {
		shippingRegionsPane.verifyShippingRegionsIsNotInList(uniqueShippingRegionName);
	}

	/**
	 * Deleting created Shipping Region names.
	 */
	@When("^I delete newly created shipping region$")
	public void deleteShippingRegion() {
		shippingRegionsPane.deleteShippingRegions(uniqueShippingRegionName);
	}

}
