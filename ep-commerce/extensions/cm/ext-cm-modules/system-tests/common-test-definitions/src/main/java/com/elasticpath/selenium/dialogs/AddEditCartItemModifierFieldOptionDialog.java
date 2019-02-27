package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Add Edit Cart Item Modifier Field Option Dialog.
 */
public class AddEditCartItemModifierFieldOptionDialog extends AbstractDialog {
	/**
	 * Constant for the sku option dialog shell css.
	 */
	public static final String ADD_FIELD_OPTION_PARENT_CSS
			= "div[automation-id*='com.elasticpath.cmclient.catalog.CatalogMessages."
			+ "AddEditCartItemModifierFieldOptionDialog_WinIitle'][widget-type='Shell'] ";
	private static final String FIELD_OPTION_CODE_INPUT_CSS = ADD_FIELD_OPTION_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AddEditCartItemModifierFieldOptionDialog_Value'] input";
	private static final String DISPLAY_NAME_INPUT_CSS = ADD_FIELD_OPTION_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AddEditCartItemModifierFieldOptionDialog_DisplayName']"
			+ "[widget-type='CCombo']+[widget-type='Text'] input";
	private static final String ADD_BUTTON_CSS = ADD_FIELD_OPTION_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AddEditCartItemModifierFieldOptionDialog_Add'][seeable='true']";
	private static final String OK_BUTTON_CSS = ADD_FIELD_OPTION_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonOK']";

	private String displayName;
	private String code;

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	protected AddEditCartItemModifierFieldOptionDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs fieldOption code.
	 *
	 * @param fieldOptionCode the fieldOption code
	 */
	public void enterFieldOptionCode(final String fieldOptionCode) {
		clearAndType(FIELD_OPTION_CODE_INPUT_CSS, fieldOptionCode);
		this.code = fieldOptionCode;
	}

	/**
	 * Inputs fieldOption display name.
	 *
	 * @param displayName the fieldOption display name
	 */
	public void enterFieldOptionDisplayName(final String displayName) {
		this.displayName = displayName;
		clearAndType(DISPLAY_NAME_INPUT_CSS, displayName);
	}

	/**
	 * Clicks add button.
	 */
	public void clickAddButton() {
		clickButton(ADD_BUTTON_CSS, "Add");
		waitTillElementDisappears(By.cssSelector(ADD_FIELD_OPTION_PARENT_CSS));
	}

	/**
	 * Clicks ok button.
	 */
	public void clickOkButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(ADD_FIELD_OPTION_PARENT_CSS));
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
