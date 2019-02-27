package com.elasticpath.selenium.resultspane;


import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateEditTaxJurisdictionDialog;
import com.elasticpath.selenium.util.Constants;

/**
 * Tax Jurisdictions Pane class.
 */
public class TaxJurisdictionsPane extends AbstractPageObject {

	private static final String TAX_JURISDICTION_LIST_PARENT_CSS = "div[widget-id='Tax Jurisdiction'][widget-type='Table'] ";
	private static final String TAX_JURISDICTION_LIST_CSS = TAX_JURISDICTION_LIST_PARENT_CSS + "div[column-id='%s']";
	private static final String CREATE_TAXJURISDICTION_BUTTON_CSS = "div[widget-id='Create Tax Jurisdiction'][seeable='true']";
	private static final String DELETE_TAXJURISDICTION_BUTTON_CSS = "div[widget-id='Delete Tax Jurisdiction'][seeable='true']";
	private static final String EDIT_TAXJURISDICTION_BUTTON_CSS = "div[widget-id='Edit Tax Jurisdiction'][seeable='true']";

	/**
	 * Constructor for the Tax Jurisdiction Pane.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public TaxJurisdictionsPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies if Tax Jurisdiction Country exists.
	 *
	 * @param taxJurisdictionCountry String
	 */
	public void verifyTaxJurisdictionIsInList(final String taxJurisdictionCountry) {
		assertThat(selectItemInCenterPaneWithoutPagination(TAX_JURISDICTION_LIST_PARENT_CSS, TAX_JURISDICTION_LIST_CSS, taxJurisdictionCountry,
				"Tax Jurisdiction Country"))
				.as("Tax Jurisdiction Country does not exist in the list - " + taxJurisdictionCountry)
				.isTrue();
	}

	/**
	 * Verifies Tax Jurisdiction Country is not in the list.
	 *
	 * @param taxJurisdictionCountry String
	 */
	public void verifyTaxCodeIsNotInList(final String taxJurisdictionCountry) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(verifyItemIsNotInCenterPaneWithoutPagination(TAX_JURISDICTION_LIST_PARENT_CSS, TAX_JURISDICTION_LIST_CSS, taxJurisdictionCountry,
				"Tax Jurisdiction Country"))
				.as("Delete failed, Tax Jurisdiction Country is still in the list - " + taxJurisdictionCountry)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Clicks Create Tax Jurisdiction button.
	 *
	 * @return CreateEditTaxJurisdictionDialog
	 */
	public CreateEditTaxJurisdictionDialog clickCreateTaxJurisdictionButton() {
		final String dialogName = "Create";
		clickButton(CREATE_TAXJURISDICTION_BUTTON_CSS, "Create Tax Jurisdiction", String.format(CreateEditTaxJurisdictionDialog
				.TAXJURISDICTION_PARENT_CSS_TEMPLATE, dialogName));
		return new CreateEditTaxJurisdictionDialog(getDriver(), dialogName);
	}

	/**
	 * Selects and Deletes the Tax Jurisdiction.
	 *
	 * @param taxJurisdictionCountry String
	 */
	public void deleteTaxJusisdictionCountry(final String taxJurisdictionCountry) {
		verifyTaxJurisdictionIsInList(taxJurisdictionCountry);
		clickDeleteTaxJurisdictionButton();
		new ConfirmDialog(getDriver()).clickOKButton("Delete");
	}

	/**
	 * Clicks Delete Tax Jurisdiction button.
	 */
	public void clickDeleteTaxJurisdictionButton() {
		clickButton(DELETE_TAXJURISDICTION_BUTTON_CSS, "Delete Tax Jurisdiction");
	}

	/**
	 * Selects and Edits the Tax Jurisdiction.
	 *
	 * @param taxJurisdictionCountry String
	 * @return CreateEditTaxJurisdictionDialog
	 */
	public CreateEditTaxJurisdictionDialog clickEditTaxJurisdictionButton(final String taxJurisdictionCountry) {
		final String dialogName = "Edit";
		verifyTaxJurisdictionIsInList(taxJurisdictionCountry);
		clickButton(EDIT_TAXJURISDICTION_BUTTON_CSS, "Edit Tax Jurisdiction", String.format(CreateEditTaxJurisdictionDialog
				.TAXJURISDICTION_PARENT_CSS_TEMPLATE, dialogName));
		return new CreateEditTaxJurisdictionDialog(getDriver(), dialogName);
	}

}
