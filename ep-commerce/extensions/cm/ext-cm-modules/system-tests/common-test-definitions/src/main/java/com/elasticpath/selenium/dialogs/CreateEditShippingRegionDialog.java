package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create and Edit Shipping Region dialogs.
 */
public class CreateEditShippingRegionDialog extends AbstractDialog {

	private final String shippingRegionParentCSS;
	private final String shippingRegionsNameInputCSS;
	private final String shippingRegionsCountryCSS;
	private final String saveButtonCSS;
	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String SHIPPING_REGION_PARENT_CSS_TEMPLATE = "div[automation-id*='com.elasticpath.cmclient.admin"
			+ ".shipping.AdminShippingMessages.%sShippingRegionTitle'] ";
	private static final String SHIPPING_REGIONS_NAME_INPUT_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.admin.shipping"
			+ ".AdminShippingMessages.RegionNameLabel'] input";
	private static final String SHIPPING_REGIONS_COUNTRY_CSS_TEMPLATE = "div[column-id='%s']";
	private static final String SAVE_BUTTON_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".AbstractEpDialog_ButtonSave']";
	private static final String EDIT_SHIPPING_REGION = "shipping.AdminShippingMessages.EditShippingRegion";
	private static final String CREATE_SHIPPING_REGION = "shipping.AdminShippingMessages.CreateShippingRegion";

	/**
	 * Constructor for CreateEditShippingRegionDialog.
	 *
	 * @param driver     WebDriver which drives this page
	 * @param dialogName String for wild card dialog name
	 */
	public CreateEditShippingRegionDialog(final WebDriver driver, final String dialogName) {
		super(driver);

		shippingRegionParentCSS = String.format(SHIPPING_REGION_PARENT_CSS_TEMPLATE, dialogName);
		shippingRegionsNameInputCSS = shippingRegionParentCSS + SHIPPING_REGIONS_NAME_INPUT_CSS_TEMPLATE;
		shippingRegionsCountryCSS = shippingRegionParentCSS + SHIPPING_REGIONS_COUNTRY_CSS_TEMPLATE;
		saveButtonCSS = shippingRegionParentCSS + SAVE_BUTTON_CSS_TEMPLATE;
	}

	/**
	 * Inputs Region Name.
	 *
	 * @param regionName String
	 */
	public void enterRegionName(final String regionName) {
		clearAndType(shippingRegionsNameInputCSS, regionName);
	}

	/**
	 * Selects country from available attributes list.
	 *
	 * @param country the string
	 */
	public void selectShippingRegionCountry(final String country) {
		assertThat(selectItemInDialog(shippingRegionParentCSS, shippingRegionsCountryCSS, country, ""))
				.as("Unable to find available country - " + country)
				.isTrue();
	}

	/**
	 * Clicks '>' (move right).
	 */
	public void clickMoveRightCreate() {
		clickMoveRight(CREATE_SHIPPING_REGION);
	}

	/**
	 * Clicks '>' (move right).
	 */
	public void clickMoveRightEdit() {
		clickMoveRight(EDIT_SHIPPING_REGION);
	}

	/**
	 * Clicks '<<' (move all left).
	 */
	public void clickMoveAllLeftEdit() {
		clickMoveAllLeft(EDIT_SHIPPING_REGION);
	}

	/**
	 * Clicks save button.
	 */
	@Override
	public void clickSave() {
		clickButton(saveButtonCSS, "Save");
		waitTillElementDisappears(By.cssSelector(shippingRegionParentCSS));
	}
}
