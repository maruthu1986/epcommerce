package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Add Attribute Dialog.
 */
public class AddAttributeDialog extends AbstractDialog {
	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String ADD_ATTRUBUTE_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AttributeAddDialog_WinTitle_Add'] ";
	private static final String ATTRUBUTE_KEY_INPUT_CSS = ADD_ATTRUBUTE_PARENT_CSS + "div[widget-id='Attribute Key'] > input";
	private static final String ATTRUBUTE_NAME_INPUT_CSS = ADD_ATTRUBUTE_PARENT_CSS + "div[widget-id='Attribute Name'] > input";
	private static final String ATTRIBUTE_USAGE_COMBO_CSS = ADD_ATTRUBUTE_PARENT_CSS + "div[widget-id='Attribute Usage'][widget-type='CCombo']";
	private static final String ATTRIBUTE_TYPE_COMBO_CSS = ADD_ATTRUBUTE_PARENT_CSS + "div[widget-id='Attribute Type'][widget-type='CCombo']";
	private static final String CHECK_BOX_XPATH
			= "//div[@automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AttributeAddDialog_RequiredAttribute']"
			+ "/..//following-sibling::div[1]/div";
	private static final String ADD_BUTTON_CSS = ADD_ATTRUBUTE_PARENT_CSS + "div[widget-id='Add'][style*='opacity: 1']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddAttributeDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs attribute key.
	 *
	 * @param attributeKey the attribute key.
	 */
	public void enterAttributeKey(final String attributeKey) {
		clearAndType(ATTRUBUTE_KEY_INPUT_CSS, attributeKey);
	}

	/**
	 * Inputs attribute name.
	 *
	 * @param attributeName the attribute name.
	 */
	public void enterAttributeName(final String attributeName) {
		clearAndType(ATTRUBUTE_NAME_INPUT_CSS, attributeName);
	}

	/**
	 * Selects attribute usage.
	 *
	 * @param attributeUsage the attribute usage.
	 */
	public void selectAttributeUsage(final String attributeUsage) {
		assertThat(selectComboBoxItem(ATTRIBUTE_USAGE_COMBO_CSS, attributeUsage))
				.as("Unable to find attribute usage value - " + attributeUsage)
				.isTrue();
	}

	/**
	 * Selects attribute type.
	 *
	 * @param attributeType the attribute type.
	 */
	public void selectAttributeType(final String attributeType) {
		assertThat(selectComboBoxItem(ATTRIBUTE_TYPE_COMBO_CSS, attributeType))
				.as("Unable to find attribute type - " + attributeType)
				.isTrue();
	}

	/**
	 * Click checkbox.
	 *
	 * @param checkBoxName the name.
	 */
	public void clickCheckBox(final String checkBoxName) {
		String checkBoxfieldName = checkBoxName + ":";
		click(getDriver().findElement(By.xpath(String.format(CHECK_BOX_XPATH, checkBoxfieldName))));
	}

	/**
	 * Clicks add button.
	 */
	public void clickAddButton() {
		clickButton(ADD_BUTTON_CSS, "Add");
		waitTillElementDisappears(By.cssSelector(ADD_ATTRUBUTE_PARENT_CSS));
	}

}
