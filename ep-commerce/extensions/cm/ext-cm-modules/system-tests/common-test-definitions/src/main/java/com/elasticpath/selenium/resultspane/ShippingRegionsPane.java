package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateEditShippingRegionDialog;
import com.elasticpath.selenium.util.Constants;

/**
 * Shipping Regions pane.
 */
public class ShippingRegionsPane extends AbstractPageObject {

	private static final String SHIPPINGREGIONS_LIST_PARENT_CSS = "div[widget-id='Shipping Region'][widget-type='Table'] ";
	private static final String SHIPPINGREGIONS_LIST_CSS = SHIPPINGREGIONS_LIST_PARENT_CSS + "div[column-id='%s']";
	private static final String CREATE_SHIPPINGREGIONS_BUTTON_CSS = "div[widget-id='Create Shipping Region'][seeable='true']";
	private static final String DELETE_SHIPPINGREGIONS_BUTTON_CSS = "div[widget-id='Delete Shipping Region'][seeable='true']";
	private static final String EDIT_SHIPPINGREGIONS_BUTTON_CSS = "div[widget-id='Edit Shipping Region'][seeable='true']";

	/**
	 * Constructor for the Shipping Regions Pane.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ShippingRegionsPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies if Shipping Regions exists.
	 *
	 * @param shippingRegionsName String
	 */
	public void verifyShippingRegionsIsInList(final String shippingRegionsName) {
		assertThat(selectItemInCenterPaneWithoutPagination(SHIPPINGREGIONS_LIST_PARENT_CSS, SHIPPINGREGIONS_LIST_CSS, shippingRegionsName,
				"Shipping Region Name"))
				.as("Shipping Region does not exist in the list - " + shippingRegionsName)
				.isTrue();
	}

	/**
	 * Verifies Shipping Regions is not in the list.
	 *
	 * @param shippingRegionsName String
	 */
	public void verifyShippingRegionsIsNotInList(final String shippingRegionsName) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(verifyItemIsNotInCenterPaneWithoutPagination(SHIPPINGREGIONS_LIST_PARENT_CSS, SHIPPINGREGIONS_LIST_CSS, shippingRegionsName,
				"Shipping Region Name"))
				.as("Delete failed, Shipping Region is still in the list - " + shippingRegionsName)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Clicks Create Shipping Regions button.
	 *
	 * @return CreateShippingRegionsDialog
	 */
	public CreateEditShippingRegionDialog clickCreateShippingRegionsButton() {
		final String dialogName = "Create";
		clickButton(CREATE_SHIPPINGREGIONS_BUTTON_CSS, "Create Shipping Region", String.format(CreateEditShippingRegionDialog
				.SHIPPING_REGION_PARENT_CSS_TEMPLATE, dialogName));
		return new CreateEditShippingRegionDialog(getDriver(), dialogName);
	}

	/**
	 * Selects and Deletes the Shipping Regions.
	 *
	 * @param shippingRegionsName String
	 */
	public void deleteShippingRegions(final String shippingRegionsName) {
		verifyShippingRegionsIsInList(shippingRegionsName);
		clickDeleteShippingRegionsButton();
		new ConfirmDialog(getDriver()).clickOKButton("AdminShippingMessages.DeleteShippingRegion");
	}

	/**
	 * Clicks Delete Shipping Regions button.
	 */
	public void clickDeleteShippingRegionsButton() {
		clickButton(DELETE_SHIPPINGREGIONS_BUTTON_CSS, "Delete Shipping Region");
	}

	/**
	 * Selects and Edits the Shipping Regions.
	 *
	 * @param shippingRegionsName String
	 * @return CreateShippingRegionsDialog
	 */
	public CreateEditShippingRegionDialog clickEditShippingRegionsButton(final String shippingRegionsName) {
		final String dialogName = "Edit";
		verifyShippingRegionsIsInList(shippingRegionsName);
		clickButton(EDIT_SHIPPINGREGIONS_BUTTON_CSS, "Edit Shipping Region", String.format(CreateEditShippingRegionDialog
				.SHIPPING_REGION_PARENT_CSS_TEMPLATE, dialogName));
		return new CreateEditShippingRegionDialog(getDriver(), dialogName);
	}
}
