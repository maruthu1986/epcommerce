package com.elasticpath.selenium.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


/**
 * Add and Edit Product SKU Wizard.
 */
public class AddSkuWizard extends AbstractWizard {

	private static final String ADD_SKU_PARENT_CSS = "div[widget-id='Add SKU'][widget-type='Shell'] ";
	private static final String SKU_CODE_INPUT_CSS = ADD_SKU_PARENT_CSS + "div[widget-id='SKU Code'] > input";
	private static final String SKU_OPTIONS_COMBO_CSS = ADD_SKU_PARENT_CSS + "div[widget-id='%s'][widget-type='CCombo']";
	private static final String SHIPPABLE_TYPE_SHIPPABLE_BUTTON_CSS = ADD_SKU_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductEditorSingleSkuOverview_Shippable'][seeable='true']";
	private static final String SHIPPABLE_TYPE_DIGITAL_ASSET_BUTTON_CSS = ADD_SKU_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductEditorSingleSkuOverview_DigitalAsset'][seeable='true']";
	private static final String NEXT_BUTTON = ADD_SKU_PARENT_CSS + "div[widget-id='Next >'][seeable='true']";
	private static final String FINISH_BUTTON = ADD_SKU_PARENT_CSS + "div[widget-id='Finish'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddSkuWizard(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs sku code.
	 *
	 * @param skuCode the sku code.
	 */
	public void enterSkuCode(final String skuCode) {
		clearAndType(SKU_CODE_INPUT_CSS, skuCode);
	}

	/**
	 * Selects SKU Options in combo box.
	 *
	 * @param skuOption      for sku
	 * @param skuOptionValue for sku
	 */
	public void selectSkuOptions(final String skuOption, final String skuOptionValue) {
		assertThat(selectComboBoxItem(String.format(SKU_OPTIONS_COMBO_CSS, skuOption), skuOptionValue))
				.as("Unable to find SKU Options - " + skuOption)
				.isTrue();
	}

	/**
	 * Selects shippable type.
	 *
	 * @param shippableType the shippable type.
	 */
	public void selectShippableType(final String shippableType) {
		if ("Shippable".equalsIgnoreCase(shippableType)) {
			click(getDriver().findElement(By.cssSelector(SHIPPABLE_TYPE_SHIPPABLE_BUTTON_CSS)));
		} else if ("Digital Asset".equalsIgnoreCase(shippableType)) {
			click(getDriver().findElement(By.cssSelector(SHIPPABLE_TYPE_DIGITAL_ASSET_BUTTON_CSS)));
			//TODO - will remove once button is enabled on click
			click(getDriver().findElement(By.cssSelector(SHIPPABLE_TYPE_DIGITAL_ASSET_BUTTON_CSS)));
		} else {
			assertThat("Shippable".equalsIgnoreCase(shippableType) || "Digital Asset".equalsIgnoreCase(shippableType))
					.as("Invalid shippable type entered - " + shippableType)
					.isTrue();
		}
	}

	/**
	 * Clicks Next.
	 */
	public void clickNextButton() {
		clickButton(NEXT_BUTTON, "Next");
	}

	/**
	 * Clicks Finish.
	 */
	public void clickFinish() {
		clickButton(FINISH_BUTTON, "Finish");
		waitTillElementDisappears(By.cssSelector(FINISH_BUTTON));
	}
}
