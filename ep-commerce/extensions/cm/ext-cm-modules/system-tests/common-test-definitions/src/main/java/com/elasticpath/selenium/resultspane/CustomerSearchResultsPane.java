package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.editor.CustomerEditor;

/**
 * Customer Search Results Pane.
 */
public class CustomerSearchResultsPane extends AbstractPageObject {

	private static final String CUSTOMER_SEARCH_RESULT_PARENT_CSS = "div[widget-id='Customer Search Result Table'][widget-type='Table'] ";
	private static final String CUSTOMER_SEARCH_RESULT_LIST_CSS = CUSTOMER_SEARCH_RESULT_PARENT_CSS + "div[parent-widget-id='Customer Search Result"
			+ " " + "Table'] div[column-id='%s']";
	private static final String CUSTOMER_EDITOR_TOOLTIP_CSS = "div[widget-id*='Customer #']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CustomerSearchResultsPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies Customer exists.
	 *
	 * @param expectedEmailID the expected email id.
	 */
	public void verifyCustomerExists(final String expectedEmailID) {
		assertThat(selectItemInCenterPane(CUSTOMER_SEARCH_RESULT_PARENT_CSS,
				CUSTOMER_SEARCH_RESULT_LIST_CSS, expectedEmailID, "Email Address"))
				.as("Unable to find customer with email - " + expectedEmailID)
				.isTrue();
	}

	/**
	 * Verifies if customer email is in the list.
	 *
	 * @param expectedEmailID String
	 * @param columnName      the column name
	 * @return boolean
	 */
	public boolean isCustomerInList(final String expectedEmailID, final String columnName) {
		setWebDriverImplicitWait(1);
		boolean customerExists = selectItemInCenterPane(CUSTOMER_SEARCH_RESULT_PARENT_CSS, CUSTOMER_SEARCH_RESULT_LIST_CSS,
				expectedEmailID, columnName);
		setWebDriverImplicitWaitToDefault();
		return customerExists;
	}

	/**
	 * Select Customer and open editor.
	 *
	 * @param columnValue the column value.
	 * @return the customer editor.
	 */
	public CustomerEditor selectAndOpenCustomerEditor(final String columnValue) {
		verifyCustomerExists(columnValue);
		doubleClick(getSelectedElement(), CUSTOMER_EDITOR_TOOLTIP_CSS);
		return new CustomerEditor(getDriver());
	}

	/**
	 * Returns parent table css.
	 *
	 * @return CUSTOMER_SEARCH_RESULT_PARENT_CSS the parent table css
	 */
	public static String getCustomerSearchResultParentCss() {
		return CUSTOMER_SEARCH_RESULT_PARENT_CSS.trim();
	}
}
