package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create and Edit Shipping Region dialogs.
 */
public class CreateEditTaxCodeDialog extends AbstractDialog {

	private final String taxCodeParentCSS;
	private final String taxCodeNameInputCSS;
	private final String saveButtonCSS;
	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String TAXCODE_PARENT_CSS_TEMPLATE =
			"div[automation-id*='com.elasticpath.cmclient.admin.taxes.TaxesMessages.%sTaxCode'][widget-type='Shell'] ";
	private static final String TAXCODES_NAME_INPUT_CSS_TEMPLATE =
			"div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages.TaxCode'] input";
	private static final String SAVE_BUTTON_CSS_TEMPLATE =
			"div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonSave']";

	/**
	 * Constructor for CreateEditTaxCodeDialog.
	 *
	 * @param driver     WebDriver which drives this page
	 * @param dialogName String for wild card dialog name
	 */
	public CreateEditTaxCodeDialog(final WebDriver driver, final String dialogName) {
		super(driver);

		taxCodeParentCSS = String.format(TAXCODE_PARENT_CSS_TEMPLATE, dialogName);
		taxCodeNameInputCSS = taxCodeParentCSS + TAXCODES_NAME_INPUT_CSS_TEMPLATE;
		saveButtonCSS = taxCodeParentCSS + SAVE_BUTTON_CSS_TEMPLATE;
	}

	/**
	 * Inputs Tax Code Name.
	 *
	 * @param taxCodeName String
	 */
	public void enterTaxCodeName(final String taxCodeName) {
		clearAndType(taxCodeNameInputCSS, taxCodeName);
	}

	/**
	 * Clicks save button.
	 */
	@Override
	public void clickSave() {
		clickButton(saveButtonCSS, "Save");
		waitTillElementDisappears(By.cssSelector(taxCodeParentCSS));
	}
}
