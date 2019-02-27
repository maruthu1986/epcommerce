package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Add Edit Sku Option Dialog.
 */
public abstract class AbstractAddEditSkuOptionDialog extends AbstractDialog {
	/**
	 * Constant for the sku option dialog shell css.
	 */
	public static final String ADD_SKU_OPTION_PARENT_CSS
			= "div[automation-id*='com.elasticpath.cmclient.catalog.CatalogMessages.SKUOptionAddDialog'][widget-type='Shell'] ";
	private static final String SKU_OPTION_CODE_INPUT_CSS = ADD_SKU_OPTION_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.SKUOptionAddDialog_Code'] input";
	private static final String ADD_BUTTON_CSS = ADD_SKU_OPTION_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonSave'][seeable='true']";

	private String displayName;
	private String code;

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	protected AbstractAddEditSkuOptionDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs skuOption code.
	 *
	 * @param skuOptionCode the skuOption code
	 */
	public void enterSkuOptionCode(final String skuOptionCode) {
		clearAndType(SKU_OPTION_CODE_INPUT_CSS, skuOptionCode);
		this.code = skuOptionCode;
	}

	/**
	 * Inputs skuOption display name.
	 *
	 * @param displayName                  the skuOption display name
	 * @param skuOptionDisplayNameInputCss the skuOption display name input css
	 */
	public void enterSkuOptionDisplayName(final String skuOptionDisplayNameInputCss, final String displayName) {
		clearAndType(skuOptionDisplayNameInputCss, displayName);
		this.displayName = displayName;
	}

	/**
	 * Clicks add button.
	 */
	public void clickAddButton() {
		clickButton(ADD_BUTTON_CSS, "Add");
		waitTillElementDisappears(By.cssSelector(ADD_SKU_OPTION_PARENT_CSS));
	}

	/**
	 * Gets display name.
	 *
	 * @return displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Gets sku option code.
	 *
	 * @return code
	 */
	public String getCode() {
		return code;
	}

}
