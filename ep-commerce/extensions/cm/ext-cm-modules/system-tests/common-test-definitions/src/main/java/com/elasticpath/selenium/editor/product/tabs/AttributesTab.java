package com.elasticpath.selenium.editor.product.tabs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.EditDecimalValueAttributeDialog;

/**
 * Attributes Tab class.
 */
public class AttributesTab extends AbstractPageObject {

	private static final String PRODUCT_EDITOR_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String ATTRIBUTES_ITEM_PARENT_CSS = "div[widget-id='Attributes'][widget-type='Table'] ";
	private static final String ATTRIBUTES_ITEM_COLUMN_CSS = ATTRIBUTES_ITEM_PARENT_CSS + "div[column-id='%s']";
	private static final String ATTRIBUTES_ITEM_ROW_CSS = ATTRIBUTES_ITEM_PARENT_CSS + "div[row-id='%s'] ";
	private static final String ATTRIBUTES_ITEM_EDIT_BUTTON = PRODUCT_EDITOR_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AttributePage_ButtonEdit']";
	private static final String ATTRIBUTES_ITEM_CLEAR_BUTTON = PRODUCT_EDITOR_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AttributePage_ButtonReset']";
	private static final String TAB_CSS =
			"div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.Product%sPage_Title'][seeable='true']";

	/**
	 * constructor.
	 *
	 * @param driver WebDriver which drives this webpage.
	 */
	public AttributesTab(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks on tab to select it.
	 *
	 * @param tabName the tab name.
	 */
	public void selectTab(final String tabName) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(TAB_CSS, tabName))));
	}

	/**
	 * Clears the value of a given attribute.
	 *
	 * @param attributeName String
	 */
	public void clickClearAttribute(final String attributeName) {
		assertThat(selectItemInDialog(ATTRIBUTES_ITEM_PARENT_CSS, ATTRIBUTES_ITEM_COLUMN_CSS, attributeName, "Name"))
				.as("Unable to find attribute - " + attributeName)
				.isTrue();
		clickButton(ATTRIBUTES_ITEM_CLEAR_BUTTON, "Clear Attribute Value");
	}

	/**
	 * Click edit attribute button for decimal value.
	 *
	 * @return the dialog.
	 */
	public EditDecimalValueAttributeDialog clickEditAttributeButtonDecimalValue() {
		clickButton(ATTRIBUTES_ITEM_EDIT_BUTTON, "Edit Attribute Value...");
		return new EditDecimalValueAttributeDialog(getDriver());
	}

	/**
	 * Select product attribute row.
	 *
	 * @param attributeName String
	 */
	public void selectProductAttributeRow(final String attributeName) {
		assertThat(selectItemInDialog(ATTRIBUTES_ITEM_PARENT_CSS, ATTRIBUTES_ITEM_COLUMN_CSS, attributeName, "Name"))
				.as("Unable to find attribute - " + attributeName)
				.isTrue();
	}

	/**
	 * Verifies product attribute value.
	 *
	 * @param attributeName  product attribute name.
	 * @param attributeValue product attribute value.
	 */
	public void verifyProductAttributeValue(final String attributeName, final String attributeValue) {
		selectProductAttributeRow(attributeName);
		String cleanedValue = attributeName.replace("'", "\\'");
		assertThat(getDriver().findElement(By.cssSelector(String.format(ATTRIBUTES_ITEM_ROW_CSS, cleanedValue)
				+ String.format("div[column-id='%s']", attributeValue))).getText())
				.as("Product Attribute value validation failed")
				.isEqualTo(attributeValue);
	}
}
