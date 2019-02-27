package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create Warehouse Dialog.
 */
public class CreateEditWarehouseDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String CREATE_EDIT_WAREHOUSE_DIALOG_CSS_TEMPLATE = "div[automation-id*='com.elasticpath.cmclient.admin.warehouses"
			+ ".AdminWarehousesMessages.%sWarehouse'][widget-type='Shell'] ";
	private static final String CREATE_EDIT_WAREHOUSE_PARENT_INPUT_CSS = "div[automation-id='com.elasticpath.cmclient.admin.warehouses"
			+ ".AdminWarehousesMessages";
	private static final String WAREHOUSE_CODE_INPUT_CSS = CREATE_EDIT_WAREHOUSE_PARENT_INPUT_CSS + ".WarehouseCode'] input";
	private static final String WAREHOUSE_NAME_INPUT_CSS = CREATE_EDIT_WAREHOUSE_PARENT_INPUT_CSS + ".WarehouseName'] input";
	private static final String WAREHOUSE_ADDRESS_LINE_1_INPUT_CSS = CREATE_EDIT_WAREHOUSE_PARENT_INPUT_CSS + ".AddressLine1'] input";
	private static final String WAREHOUSE_CITY_INPUT_CSS = CREATE_EDIT_WAREHOUSE_PARENT_INPUT_CSS + ".City'] input";
	private static final String STATE_COMBO_CSS = CREATE_EDIT_WAREHOUSE_PARENT_INPUT_CSS + ".StateProvinceRegion'][widget-type='CCombo']";
	private static final String ZIP_INPUT_CSS = CREATE_EDIT_WAREHOUSE_PARENT_INPUT_CSS + ".ZipPostalCode'] input";
	private static final String COUNTRY_COMBO_CSS = CREATE_EDIT_WAREHOUSE_PARENT_INPUT_CSS + ".Country'][widget-type='CCombo']";
	private static final String SAVE_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".AbstractEpDialog_ButtonSave'][style*='opacity: 1']";

	private final String createEditWarehouseDialogCss;

	/**
	 * Constructor.
	 *
	 * @param driver     WebDriver which drives this page.
	 * @param dialogName dialog name.
	 */
	public CreateEditWarehouseDialog(final WebDriver driver, final String dialogName) {
		super(driver);
		createEditWarehouseDialogCss = String.format(CREATE_EDIT_WAREHOUSE_DIALOG_CSS_TEMPLATE, dialogName);
	}

	/**
	 * Inputs warehouse code.
	 *
	 * @param warehouseCode String
	 */
	public void enterWarehouseCode(final String warehouseCode) {
		clearAndType(WAREHOUSE_CODE_INPUT_CSS, warehouseCode);
	}

	/**
	 * Inputs warehouse name.
	 *
	 * @param warehouseName String
	 */
	public void enterWarehouseName(final String warehouseName) {
		clearAndType(WAREHOUSE_NAME_INPUT_CSS, warehouseName);
	}

	/**
	 * Inputs address line 1.
	 *
	 * @param addressLine1 String
	 */
	public void enterAddressLine1(final String addressLine1) {
		clearAndType(WAREHOUSE_ADDRESS_LINE_1_INPUT_CSS, addressLine1);
	}

	/**
	 * Inputs city.
	 *
	 * @param city String
	 */
	public void enterCity(final String city) {
		clearAndType(WAREHOUSE_CITY_INPUT_CSS, city);
	}

	/**
	 * Selects state in combo box.
	 *
	 * @param state String
	 */
	public void selectState(final String state) {
		assertThat(selectComboBoxItem(STATE_COMBO_CSS, state))
				.as("Unable to find state - " + state)
				.isTrue();
	}

	/**
	 * Inputs zip.
	 *
	 * @param zip String
	 */
	public void enterZip(final String zip) {
		clearAndType(ZIP_INPUT_CSS, zip);
	}

	/**
	 * Selects country in combo box.
	 *
	 * @param country String
	 */
	public void selectCountry(final String country) {
		assertThat(selectComboBoxItem(COUNTRY_COMBO_CSS, country))
				.as("Unable to find country - " + country)
				.isTrue();
	}

	/**
	 * Clicks save button.
	 */
	public void clickSaveButton() {
		clickButton(SAVE_BUTTON_CSS, "Save");
		waitTillElementDisappears(By.cssSelector(createEditWarehouseDialogCss));
	}
}
