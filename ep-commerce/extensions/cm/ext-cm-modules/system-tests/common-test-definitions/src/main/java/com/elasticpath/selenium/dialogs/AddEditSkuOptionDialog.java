package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.WebDriver;

/**
 * Add and Edit Sku Option Dialog.
 */
public class AddEditSkuOptionDialog extends AbstractAddEditSkuOptionDialog {
	private static final String SKU_OPTION_DISPLAY_NAME_INPUT_CSS = ADD_SKU_OPTION_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.SKUOptionAddDialog_DisplayName'][widget-type='CCombo']"
			+ "+[widget-type='Text'] input";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddEditSkuOptionDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs skuOption display name.
	 *
	 * @param displayName the skuOption display name
	 */
	public void enterSkuOptionDisplayName(final String displayName) {
		super.enterSkuOptionDisplayName(SKU_OPTION_DISPLAY_NAME_INPUT_CSS, displayName);
	}
}
