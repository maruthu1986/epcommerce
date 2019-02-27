package com.elasticpath.selenium.dialogs;


import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

/**
 * Taxes Dialog in Tax Jurisdiction.
 */

public class AddEditTaxDialog extends AbstractDialog {

	private final String taxParentCSS;
	private final String taxNameInputCSS;
	private final String taxAddressFieldCSS;
	private final String saveTaxButtonCSS;
	private final String taxDisplayNameInputCSS;
	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String TAX_PARENT_CSS_TEMPLATE =
			"div[automation-id*='com.elasticpath.cmclient.admin.taxes.TaxesMessages.%sTaxCategory'][widget-type='Shell'] ";
	private static final String TAX_NAME_INPUT_CSS_TEMPLATE =
			"div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages.TaxCategory'][widget-type='Text'] input";
	private static final String TAX_ADDRESS_FIELD_CSS_TEMPLATE =
			"div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages.TaxAddressField'][widget-type='CCombo']";
	private static final String SAVE_TAX_JURISDICTION =
			"div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonSave']";
	private static final String TAX_DISPLAY_NAME_INPUT_CSS_TEMPLATE = "div[widget-type='Text'][tabindex='10'] input";

	/**
	 * Constructor for the Add and editing in Tax dialog.
	 *
	 * @param driver     WebDriver which drives this page
	 * @param dialogName String for the wild card dialog name
	 */
	public AddEditTaxDialog(final WebDriver driver, final String dialogName) {
		super(driver);

		taxParentCSS = String.format(TAX_PARENT_CSS_TEMPLATE, dialogName);
		taxAddressFieldCSS = taxParentCSS + TAX_ADDRESS_FIELD_CSS_TEMPLATE;
		taxNameInputCSS = taxParentCSS + TAX_NAME_INPUT_CSS_TEMPLATE;
		saveTaxButtonCSS = taxParentCSS + SAVE_TAX_JURISDICTION;
		taxDisplayNameInputCSS = taxParentCSS + TAX_DISPLAY_NAME_INPUT_CSS_TEMPLATE;
	}

	/**
	 * Inputs Tax Name.
	 *
	 * @param taxName String
	 */
	public void enterTaxCodeName(final String taxName) {
		clearAndType(taxNameInputCSS, taxName);
	}


	/**
	 * Inputs Tax Display Name.
	 *
	 * @param taxDisplayName String
	 */
	public void enterTaxDisplayName(final String taxDisplayName) {
		clearAndType(taxDisplayNameInputCSS, taxDisplayName);
	}

	/**
	 * Selects Address Field in combo box.
	 *
	 * @param field String
	 */
	public void selectAddressField(final String field) {
		assertThat(selectComboBoxItem(taxAddressFieldCSS, field))
				.as("Unable to find address field - " + field)
				.isTrue();
	}

	/**
	 * Clicks the Create/Edit save button.
	 */
	@Override
	public void clickSave() {
		clickButton(saveTaxButtonCSS, "Save");
	}

}
