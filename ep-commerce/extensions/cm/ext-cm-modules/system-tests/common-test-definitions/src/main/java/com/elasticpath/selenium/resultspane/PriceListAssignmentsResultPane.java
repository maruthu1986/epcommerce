package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Price List Assignment Results Pane.
 */
public class PriceListAssignmentsResultPane extends AbstractPageObject {

	private static final String OPEN_PRICE_LIST_ASSIGNMENT_CSS = "div[widget-id='Open Price List Assignment']";
	private static final String DELETE_PRICE_LIST_ASSIGNMENT_CSS = "div[widget-id='Delete Price List Assignment']";
	private static final String PRICE_LIST_ASSIGNMENT_TABLE_CSS = "div[widget-id='Price List Assignment Search "
			+ "Result'][widget-type='Table'] ";
	private static final String PRICE_LIST_ASSIGNMENT_COLUMN_CSS = PRICE_LIST_ASSIGNMENT_TABLE_CSS + "div[parent-widget-id='Price List Assignment "
			+ "Search Result'] div[column-id='%s']";
	private static final String PRICE_LIST_ASSIGNMENT_COLUMN_NAME = "Price List Assignment";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PriceListAssignmentsResultPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies expected Price List Assignment exists.
	 *
	 * @param priceListAssignment the price list assignment.
	 */
	public void verifyPriceListAssignmentExists(final String priceListAssignment) {
		assertThat(selectItemInCenterPane(PRICE_LIST_ASSIGNMENT_TABLE_CSS, PRICE_LIST_ASSIGNMENT_COLUMN_CSS, priceListAssignment,
				PRICE_LIST_ASSIGNMENT_COLUMN_NAME))
				.as("Expected Price List Assignment does not exist - " + priceListAssignment)
				.isTrue();
	}

	/**
	 * Open the pricelist assignment.
	 *
	 * @param priceListAssignment the pricelist assignment
	 */
	public void openPriceListAssignment(final String priceListAssignment) {
		selectItemInCenterPane(PRICE_LIST_ASSIGNMENT_TABLE_CSS, PRICE_LIST_ASSIGNMENT_COLUMN_CSS, priceListAssignment,
				PRICE_LIST_ASSIGNMENT_COLUMN_NAME);
		clickButton(OPEN_PRICE_LIST_ASSIGNMENT_CSS, "Open Price List Assignment");
	}

	/**
	 * Verifies if Price List Assignment is deleted.
	 *
	 * @param expectedPriceListAssignment the expected price list assignment.
	 */
	public void verifyPriceListAssignmentDeleted(final String expectedPriceListAssignment) {
		setWebDriverImplicitWait(1);
		assertThat(selectItemInCenterPane(PRICE_LIST_ASSIGNMENT_TABLE_CSS, PRICE_LIST_ASSIGNMENT_COLUMN_CSS, expectedPriceListAssignment,
				PRICE_LIST_ASSIGNMENT_COLUMN_NAME))
				.as("Price List Assignment is not deleted - " + expectedPriceListAssignment)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Deletes given price list assignment.
	 *
	 * @param priceListAssignment the price list assignment.
	 */
	public void deletePriceListAssignment(final String priceListAssignment) {
		assertThat(selectItemInCenterPane(PRICE_LIST_ASSIGNMENT_TABLE_CSS, PRICE_LIST_ASSIGNMENT_COLUMN_CSS, priceListAssignment,
				PRICE_LIST_ASSIGNMENT_COLUMN_NAME))
				.as("Unable to find Price List Assignment - " + priceListAssignment)
				.isTrue();
		clickButton(DELETE_PRICE_LIST_ASSIGNMENT_CSS, "Delete Price List Assignment");
	}

	/**
	 * Verifies Price List Assignments result.
	 *
	 * @param priceListAssignment the price list assignment.
	 */
	public void verifyPLASearchResults(final String priceListAssignment) {
		assertThat(selectItemInCenterPane(PRICE_LIST_ASSIGNMENT_TABLE_CSS, PRICE_LIST_ASSIGNMENT_COLUMN_CSS, priceListAssignment,
				PRICE_LIST_ASSIGNMENT_COLUMN_NAME))
				.as("Unable to find Price List Assignment - " + priceListAssignment)
				.isTrue();
	}


	/**
	 * Verifies if price list assignment exists.
	 *
	 * @param priceListAssignment String
	 * @return boolean
	 */
	public boolean isPLAInList(final String priceListAssignment) {
		setWebDriverImplicitWait(1);
		boolean isPLAInList = selectItemInCenterPane(PRICE_LIST_ASSIGNMENT_TABLE_CSS, PRICE_LIST_ASSIGNMENT_COLUMN_CSS, priceListAssignment,
				PRICE_LIST_ASSIGNMENT_COLUMN_NAME);
		setWebDriverImplicitWaitToDefault();
		return isPLAInList;
	}

	/**
	 * Selects Price List Assignments.
	 *
	 * @param priceListAssignment the price list assignment.
	 */
	public void selectPriceListAssignment(final String priceListAssignment) {
		verifyPLASearchResults(priceListAssignment);
	}
}
