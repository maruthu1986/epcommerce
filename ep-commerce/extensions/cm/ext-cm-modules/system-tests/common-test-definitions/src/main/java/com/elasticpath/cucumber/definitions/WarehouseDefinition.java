package com.elasticpath.cucumber.definitions;

import java.util.Map;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.CreateEditWarehouseDialog;
import com.elasticpath.selenium.editor.StoreEditor;
import com.elasticpath.selenium.resultspane.StoresResultPane;
import com.elasticpath.selenium.resultspane.WarehousesPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ActivityToolbar;
import com.elasticpath.selenium.toolbars.ConfigurationActionToolbar;
import com.elasticpath.selenium.toolbars.ShippingReceivingActionToolbar;
import com.elasticpath.selenium.util.Utility;

/**
 * Warehouse step definitions.
 */
public class WarehouseDefinition {
	private final ConfigurationActionToolbar configurationActionToolbar;
	private WarehousesPane warehousesPane;
	private CreateEditWarehouseDialog createEditWarehouseDialog;
	private String warehouseName;
	private StoresResultPane storesResultPane;
	private StoreEditor storeEditor;
	private final ActivityToolbar activityToolbar;
	private final ShippingReceivingActionToolbar shippingReceivingActionToolbar;

	/**
	 * Constructor.
	 */
	public WarehouseDefinition() {
		configurationActionToolbar = new ConfigurationActionToolbar(SetUp.getDriver());
		activityToolbar = new ActivityToolbar((SetUp.getDriver()));
		shippingReceivingActionToolbar = new ShippingReceivingActionToolbar(SetUp.getDriver());
	}

	/**
	 * Click Warehouse link.
	 */
	@When("^I go to Warehouses$")
	public void clickUserRoles() {
		warehousesPane = configurationActionToolbar.clickWarehouses();
	}

	/**
	 * Create warehouse.
	 *
	 * @param warehouseMap the warehouse map.
	 */
	@When("^I create warehouse with following values$")
	public void createWarehouse(final Map<String, String> warehouseMap) {
		createEditWarehouseDialog = warehousesPane.clickCreateWarehouseButton();

		String warehouseCode = "wh" + Utility.getRandomUUID();
		this.warehouseName = warehouseMap.get("warehouse name") + "-" + warehouseCode;
		createEditWarehouseDialog.enterWarehouseCode(warehouseCode);
		createEditWarehouseDialog.enterWarehouseName(this.warehouseName);
		createEditWarehouseDialog.enterAddressLine1(warehouseMap.get("address line 1"));
		createEditWarehouseDialog.enterCity(warehouseMap.get("city"));
		createEditWarehouseDialog.selectCountry(warehouseMap.get("country"));
		createEditWarehouseDialog.selectState(warehouseMap.get("state"));
		createEditWarehouseDialog.enterZip(warehouseMap.get("zip"));
		createEditWarehouseDialog.clickSaveButton();
	}

	/**
	 * Delete new warehouse.
	 */
	@And("^I delete newly created warehouse$")
	public void deleteNewWarehouse() {
		warehousesPane.deleteWarehouse(this.warehouseName);
	}

	/**
	 * Verify new warehouse no longer exists.
	 */
	@Then("^newly created warehouse no longer exists$")
	public void verifyNewWarehouseIsDeleted() {
		warehousesPane.verifyWarehouseIsNotInList(this.warehouseName);
	}

	/**
	 * Verify new warehouse exists.
	 */
	@When("^the new warehouse should exist in the list$")
	public void verifyNewWarehouseExists() {
		warehousesPane.verifyWarehouseExists(this.warehouseName);
	}

	/**
	 * Editing the new Warehouse name.
	 *
	 * @param newWarehouseName editing the warehouse name to another
	 */
	@When("^I edit newly created warehouse name to (.+)$")
	public void editWarehouseName(final String newWarehouseName) {
		createEditWarehouseDialog = warehousesPane.clickEditWarehouseButton(this.warehouseName);
		this.warehouseName = newWarehouseName;
		createEditWarehouseDialog.enterWarehouseName(this.warehouseName);
		createEditWarehouseDialog.clickSaveButton();
	}

	/**
	 * Verify new warehouse name exists in store warehouse tab.
	 *
	 * @param storeCode the store code
	 */
	@When("^the new warehouse is in the warehouse list for store (.+)$")
	public void verifyInStoreWarehouseTab(final String storeCode) {
		storesResultPane = configurationActionToolbar.clickStores();
		storeEditor = storesResultPane.editStore(storeCode);
		storeEditor.clickTab("Warehouse");
		storeEditor.verifyWarehouseName(this.warehouseName);
	}

	/**
	 * Verify new warehouse name exists in warehouse select list.
	 */
	@When("^the new warehouse is in shipping receiving warehouse list$")
	public void verifyInSelectWarehouseList() {
		activityToolbar.clickShippingReceivingButton();
		shippingReceivingActionToolbar.verifyWarehouseIsPresentInList(this.warehouseName);
		activityToolbar.clickConfigurationButton();
		warehousesPane = configurationActionToolbar.clickWarehouses();
	}

}
