package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateEditTaxCodeDialog;
import com.elasticpath.selenium.util.Constants;

/**
 * Tax Codes Pane.
 */
public class TaxCodesPane extends AbstractPageObject {

	private static final String TAXCODE_LIST_PARENT_CSS = "div[widget-id='Tax Code'][widget-type='Table'] ";
	private static final String TAXCODE_LIST_CSS = TAXCODE_LIST_PARENT_CSS + "div[column-id='%s']";
	private static final String CREATE_TAXCODE_BUTTON_CSS = "div[widget-id='Create Tax Code'][seeable='true']";
	private static final String DELETE_TAXCODE_BUTTON_CSS = "div[widget-id='Delete Tax Code'][seeable='true']";
	private static final String EDIT_TAXCODE_BUTTON_CSS = "div[widget-id='Edit Tax Code'][seeable='true']";


	/**
	 * Constructor for the Tax Code Pane.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public TaxCodesPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies if Tax Code exists.
	 *
	 * @param taxCodeName String
	 */
	public void verifyTaxCodeIsInList(final String taxCodeName) {
		assertThat(selectItemInCenterPaneWithoutPagination(TAXCODE_LIST_PARENT_CSS, TAXCODE_LIST_CSS, taxCodeName,
				"Tax Code Name"))
				.as("Tax Code does not exist in the list - " + taxCodeName)
				.isTrue();
	}

	/**
	 * Verifies Tax Code is not in the list.
	 *
	 * @param taxCodeName String
	 */
	public void verifyTaxCodeIsNotInList(final String taxCodeName) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(verifyItemIsNotInCenterPaneWithoutPagination(TAXCODE_LIST_PARENT_CSS, TAXCODE_LIST_CSS, taxCodeName,
				"Tax Code Name"))
				.as("Delete failed, Tax Code is still in the list - " + taxCodeName)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Clicks Create Tax Code button.
	 *
	 * @return CreateEditTaxCodeDialog
	 */
	public CreateEditTaxCodeDialog clickCreateTaxCodeButton() {
		final String dialogName = "Create";
		clickButton(CREATE_TAXCODE_BUTTON_CSS, "Create Tax Code", String.format(CreateEditTaxCodeDialog.TAXCODE_PARENT_CSS_TEMPLATE, dialogName));
		return new CreateEditTaxCodeDialog(getDriver(), dialogName);
	}

	/**
	 * Selects and Deletes the Tax Code.
	 *
	 * @param taxCodeName String
	 */
	public void deleteTaxCode(final String taxCodeName) {
		verifyTaxCodeIsInList(taxCodeName);
		clickDeleteTaxCodeButton();
		new ConfirmDialog(getDriver()).clickOKButton("DeleteTaxCodeTitle");
	}

	/**
	 * Clicks Delete Tax Code button.
	 */
	public void clickDeleteTaxCodeButton() {
		clickButton(DELETE_TAXCODE_BUTTON_CSS, "Delete Tax Code");
	}

	/**
	 * Selects and Edits the Tax Code.
	 *
	 * @param taxCodeName String
	 * @return CreateEditTaxCodeDialog
	 */
	public CreateEditTaxCodeDialog clickEditTaxCodeButton(final String taxCodeName) {
		final String dialogName = "Edit";
		verifyTaxCodeIsInList(taxCodeName);
		clickButton(EDIT_TAXCODE_BUTTON_CSS, "Edit Tax Code", String.format(CreateEditTaxCodeDialog.TAXCODE_PARENT_CSS_TEMPLATE, dialogName));
		return new CreateEditTaxCodeDialog(getDriver(), dialogName);
	}

}
