package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Add Edit Tax Rate steps.
 */
public class AddEditTaxRateDialog extends AbstractDialog {

	private final String taxRateParentCSS;
	private final String subCountryCSS;
	private final String saveButtonCSS;
	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String TAX_RATE_PARENT_CSS_TEMPLATE = "div[automation-id*='com.elasticpath.cmclient.admin.taxes.TaxesMessages"
			+ ".TaxValue%sDialogTitle'][widget-type='Shell'] ";
	private static final String TAX_RATE_VALUE_TABLE_CSS = "div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages"
			+ ".TaxValuesLabel'][widget-type='Group'] div[widget-id='Property Table']";
	private static final String TAX_SERVICE_VALUE_CSS = "div[widget-id='SERVICE'] div[column-num='1']";
	private static final String SAVE_BUTTON_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".AbstractEpDialog_ButtonSave']";
	private static final String SUB_COUNTRY_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".TaxCategoryType_Subcountry'][widget-type='Text'] input";

	/**
	 * Constructor for the edit tax rate dialog.
	 *
	 * @param driver     WebDriver which drives this page
	 * @param dialogName String for wild card dialog name
	 */
	public AddEditTaxRateDialog(final WebDriver driver, final String dialogName) {
		super(driver);

		taxRateParentCSS = String.format(TAX_RATE_PARENT_CSS_TEMPLATE, dialogName);
		subCountryCSS = taxRateParentCSS + SUB_COUNTRY_CSS_TEMPLATE;
		saveButtonCSS = taxRateParentCSS + SAVE_BUTTON_CSS_TEMPLATE;
	}

	/**
	 * Clicks save button.
	 */
	@Override
	public void clickSave() {
		clickButton(saveButtonCSS, "Save");
		waitTillElementDisappears(By.cssSelector(String.format(taxRateParentCSS, "Add")));
	}

	/**
	 * Inputs Sub Country Tax Name.
	 *
	 * @param subCountry String
	 */
	public void enterSubCountryTaxName(final String subCountry) {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SUB_COUNTRY_CSS_TEMPLATE));
		clearAndType(String.format(subCountryCSS, "Add"), subCountry);
	}

	/**
	 * Clicks the service cell and enters value.
	 */
	public void clickServiceCell() {
		clickTableCell(TAX_SERVICE_VALUE_CSS);
	}

	/**
	 * A generic table cell for the user to choose from.
	 *
	 * @param tableCellCss String
	 */
	public void clickTableCell(final String tableCellCss) {
		click(getDriver().findElement(By.cssSelector(TAX_RATE_VALUE_TABLE_CSS + tableCellCss)));
	}
}
