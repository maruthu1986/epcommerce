package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Edit Attribute Dialog.
 */
public class EditAttributeDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String EDIT_ATTRUBUTE_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AttributeAddDialog_WinTitle_Edit'] ";
	private static final String ATTRUBUTE_NAME_INPUT_CSS = EDIT_ATTRUBUTE_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AttributeAddDialog_AttributeName'] > input";
	private static final String OK_BUTTON_CSS = EDIT_ATTRUBUTE_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonOK'][seeable='true']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public EditAttributeDialog(final WebDriver driver) {
		super(driver);
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
	 * Click ok button.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(EDIT_ATTRUBUTE_PARENT_CSS));
	}

}
