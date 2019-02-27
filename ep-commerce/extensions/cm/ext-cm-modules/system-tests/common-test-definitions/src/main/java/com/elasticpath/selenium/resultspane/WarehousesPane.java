package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateEditWarehouseDialog;
import com.elasticpath.selenium.util.Constants;

/**
 * Warehouse pane.
 */
public class WarehousesPane extends AbstractPageObject {

	private static final String WAREHOUSE_LIST_PARENT_CSS = "div[widget-id='Warehouse List'][widget-type='Table'] ";
	private static final String WAREHOUSE_LIST_CSS = WAREHOUSE_LIST_PARENT_CSS + "div[column-id='%s']";
	private static final String CREATE_WAREHOUSE_BUTTON_CSS = "div[widget-id='Create Warehouse'][seeable='true']";
	private static final String DELETE_WAREHOUSE_BUTTON_CSS = "div[widget-id='Delete Warehouse'][seeable='true']";
	private static final String EDIT_WAREHOUSE_BUTTON_CSS = "div[widget-id='Edit Warehouse'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public WarehousesPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies if warehouse exists.
	 *
	 * @param warehouseName String
	 */
	public void verifyWarehouseExists(final String warehouseName) {
		assertThat(selectItemInCenterPaneWithoutPagination(WAREHOUSE_LIST_PARENT_CSS, WAREHOUSE_LIST_CSS, warehouseName, "Warehouse Name"))
				.as("Warehouse does not exist in the list - " + warehouseName)
				.isTrue();
	}

	/**
	 * Verifies warehouse is not in the list.
	 *
	 * @param warehouseName String
	 */
	public void verifyWarehouseIsNotInList(final String warehouseName) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(verifyItemIsNotInCenterPaneWithoutPagination(WAREHOUSE_LIST_PARENT_CSS, WAREHOUSE_LIST_CSS, warehouseName, "Warehouse Name"))
				.as("Delete failed, warehouse does is still in the list - " + warehouseName)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Selects and deletes the warehouse.
	 *
	 * @param warehouseName String
	 */
	public void deleteWarehouse(final String warehouseName) {
		verifyWarehouseExists(warehouseName);
		clickDeleteWarehouseButton();
		new ConfirmDialog(getDriver()).clickOKButton("AdminWarehousesMessages.ConfirmDeleteWarehouse");
	}

	/**
	 * Clicks Create Warehouse button.
	 *
	 * @return WarehousePane
	 */
	public CreateEditWarehouseDialog clickCreateWarehouseButton() {
		final String dialogName = "Create";
		clickButton(CREATE_WAREHOUSE_BUTTON_CSS, "Create Warehouse", String.format(CreateEditWarehouseDialog
						.CREATE_EDIT_WAREHOUSE_DIALOG_CSS_TEMPLATE,
				dialogName));
		return new CreateEditWarehouseDialog(getDriver(), dialogName);
	}

	/**
	 * Clicks Delete Warehouse button.
	 */
	public void clickDeleteWarehouseButton() {
		clickButton(DELETE_WAREHOUSE_BUTTON_CSS, "Delete Warehouse");
	}

	/**
	 * Selects and Edits the Warehouse.
	 *
	 * @param warehouseName String
	 * @return CreateEditWarehouseDialog
	 */
	public CreateEditWarehouseDialog clickEditWarehouseButton(final String warehouseName) {
		final String dialogName = "Edit";
		verifyWarehouseExists(warehouseName);
		clickButton(EDIT_WAREHOUSE_BUTTON_CSS, "Edit Warehouse", String.format(CreateEditWarehouseDialog.CREATE_EDIT_WAREHOUSE_DIALOG_CSS_TEMPLATE,
				dialogName));
		return new CreateEditWarehouseDialog(getDriver(), dialogName);
	}

}
