package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Edit Item Details Dialog.
 */
public class EditItemDetailsDialog extends AbstractDialog {
	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String EDIT_ITEM_DIALOG_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.fulfillment.FulfillmentMessages"
			+ ".EditItemDetails_WindowTitle'] ";
	private static final String CONFIGURABLE_FIELD_VALUE_TABLE_CSS = EDIT_ITEM_DIALOG_PARENT_CSS
			+ "div[widget-id='Field Value Table'][widget-type='Table'][seeable='true']";
	private static final String CONFIGURABLE_FIELD_COLUMN_CSS = "div[column-id='%s']";
	private static final String CONFIGURABLE_FIELD_VALUE_COLUMN_CSS = "~div[column-num='1']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public EditItemDetailsDialog(final WebDriver driver) {
		super(driver);
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(EDIT_ITEM_DIALOG_PARENT_CSS));
	}

	/**
	 * Verifies configurable fields values.
	 *
	 * @param configurableFieldsMap configurableFieldsMap.
	 */
	public void verifyConfigurableFieldValues(final Map<String, String> configurableFieldsMap) {
		configurableFieldsMap.forEach((key, value) -> {
			String configurableFieldColumnCSS = String.format(CONFIGURABLE_FIELD_COLUMN_CSS, key);
			selectItemInDialog(CONFIGURABLE_FIELD_VALUE_TABLE_CSS, configurableFieldColumnCSS, key, "Property Key");
			assertThat(getDriver().findElement(By.cssSelector(configurableFieldColumnCSS + CONFIGURABLE_FIELD_VALUE_COLUMN_CSS)).getText())
					.as("Expected configurable field value not match")
					.isEqualTo(value);
		});

	}
}
