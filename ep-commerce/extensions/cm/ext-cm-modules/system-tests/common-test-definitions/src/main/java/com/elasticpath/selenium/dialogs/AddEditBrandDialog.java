package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Add Edit Brand Dialog.
 */
public class AddEditBrandDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String ADD_EDIT_BRAND_DIALOGCSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages"
			+ ".BrandAddEditDialog_%sBrand'] ";
	private static final String BRAND_CODE_INPUT_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.catalog"
			+ ".CatalogMessages.BrandAddEditDialog_BrandCode'] input";
	private static final String BRAND_NAME_INPUT_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.catalog"
			+ ".CatalogMessages.BrandAddEditDialog_BrandName'][widget-type='CCombo']+[widget-type='Text'] input";
	private static final String ADD_BUTTON_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".AbstractEpDialog_ButtonSave'][seeable='true']";
	private final String addEditBrandDialogCSS;
	private final String brandCodeInputCSS;
	private final String brandNameInputCSS;
	private final String addButtonCSS;
	private String brandCode;

	/**
	 * Constructor for AddEditBrandDialog.
	 *
	 * @param driver     WebDriver which drives this page
	 * @param dialogName String for wild card dialog name
	 */
	public AddEditBrandDialog(final WebDriver driver, final String dialogName) {
		super(driver);
		addEditBrandDialogCSS = String.format(ADD_EDIT_BRAND_DIALOGCSS_TEMPLATE, dialogName);
		brandCodeInputCSS = addEditBrandDialogCSS + BRAND_CODE_INPUT_CSS_TEMPLATE;
		brandNameInputCSS = addEditBrandDialogCSS + BRAND_NAME_INPUT_CSS_TEMPLATE;
		addButtonCSS = addEditBrandDialogCSS + ADD_BUTTON_CSS_TEMPLATE;
	}

	/**
	 * Enters Brand Code.
	 *
	 * @param brandCode the brandCode.
	 */
	public void enterBrandCode(final String brandCode) {
		clearAndType(brandCodeInputCSS, brandCode);
		this.brandCode = brandCode;
	}

	/**
	 * Enters Brand Name.
	 *
	 * @param brandName the brandName.
	 */
	public void enterBrandName(final String brandName) {
		clearAndType(brandNameInputCSS, brandName);
	}

	/**
	 * Clicks Add.
	 */
	public void clickAddButton() {
		clickButton(addButtonCSS, "Add");
		waitTillElementDisappears(By.cssSelector(addEditBrandDialogCSS));
	}

	/**
	 * Clicks Add without waiting for dialog to close.
	 */
	public void clickAddButtonNoWait() {
		clickButton(addButtonCSS, "Add");
	}

	/**
	 * Gets current brand code.
	 *
	 * @return brandCode.
	 */
	public String getBrandCode() {
		return brandCode;
	}

	/**
	 * Verifies error message is displayed in the add/edit dialog.
	 *
	 * @param expErrorMessage String
	 */
	public void verifyErrorMessageDisplayedInAddEditDialog(final String expErrorMessage) {
		try {
			setWebDriverImplicitWait(2);
			getDriver().findElement(By.cssSelector(String.format("div[widget-id*='" + expErrorMessage + "'] ", expErrorMessage)));
		} catch (Exception e) {
			assertThat(false)
					.as("Expected error message not present - " + expErrorMessage)
					.isEqualTo(true);
		}
		setWebDriverImplicitWaitToDefault();
	}
}
