package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Sku Details Editor.
 */
public class SkuDetailsEditor extends AbstractPageObject {

	private static final String SKU_DETAILS_INPUT_CSS = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages."
			+ "ProductEditorSingleSkuOverview_SkuConfiguration'] > input";
	private static final String SKU_EDITOR_CLOSE_ICON_CSS = "[appearance-id='ctab-item'][active-tab='true'] > "
			+ "div[style*='.gif']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public SkuDetailsEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies sku details.
	 *
	 * @param skuDetails the product sku
	 */
	public void verifySkuDetails(final String skuDetails) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(SKU_DETAILS_INPUT_CSS)).getAttribute("value"))
				.as("Sku Code validation failed")
				.isEqualTo(skuDetails);
	}

	/**
	 * Close Sku Details Editor.
	 */
	public void closeSkuDetailsEditor() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(SKU_EDITOR_CLOSE_ICON_CSS)));
	}

}
