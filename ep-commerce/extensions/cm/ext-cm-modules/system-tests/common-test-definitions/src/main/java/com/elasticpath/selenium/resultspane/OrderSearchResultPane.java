package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.editor.OrderEditor;
import com.elasticpath.selenium.navigations.CustomerService;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.util.Constants;

/**
 * Order search results pane.
 */
public class OrderSearchResultPane extends AbstractPageObject {
	private static final String RESULTS_PANE_PARENT_CSS = "div[pane-location='center-pane-inner'][seeable='true'] ";
	private static final String RESULTS_LIST_TABLE_PARENT_CSS = RESULTS_PANE_PARENT_CSS + "div[widget-id='Order Search Result Table'] ";
	private static final String RESULTS_COLUMN_CSS = RESULTS_LIST_TABLE_PARENT_CSS + "div[column-id='%s']";
	private final CustomerService customerService;

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public OrderSearchResultPane(final WebDriver driver) {
		super(driver);
		customerService = new CustomerService(SetUp.getDriver());
	}

	/**
	 * Verifies the column text in Order Search Results tab and selects the row.
	 *
	 * @param columnValue the column value.
	 * @param columnName  the column number.
	 */
	public void verifyOrderColumnValueAndSelectRow(final String columnValue, final String columnName) {
		boolean isOrderInList = isOrderInList(columnValue, columnName);

		int index = 0;
		while (!isOrderInList && index < Constants.UUID_END_INDEX) {
			sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			customerService.clickOrderSearch();
			isOrderInList = isOrderInList(columnValue, columnName);
			index++;
		}

		assertThat(isOrderInList)
				.as("Order " + columnValue + "does not exist in search result - " + columnName)
				.isTrue();
	}

	/**
	 * Verifies if order exists.
	 *
	 * @param columnValue the column value.
	 * @param columnName  the column number.
	 * @return true if order is in the last, false otherwise
	 */
	public boolean isOrderInList(final String columnValue, final String columnName) {
		setWebDriverImplicitWait(1);
		boolean isOrderInList = selectItemInCenterPane(RESULTS_LIST_TABLE_PARENT_CSS, String.format(RESULTS_COLUMN_CSS, columnValue), columnValue,
				columnName);

		setWebDriverImplicitWaitToDefault();
		return isOrderInList;
	}

	/**
	 * Select order and open editor.
	 *
	 * @param columnValue the column value.
	 * @param columnName  the column number.
	 * @return the order editor.
	 */
	public OrderEditor selectOrderAndOpenOrderEditor(final String columnValue, final String columnName) {
		verifyOrderColumnValueAndSelectRow(columnValue, columnName);
		doubleClick(getSelectedElement(), OrderEditor.EDITOR_PANE_PARENT_CSS);
		return new OrderEditor(getDriver());
	}

	/**
	 * Close the order search result pane.
	 *
	 * @param tabName the tab name.
	 */
	public void close(final String tabName) {
		getWaitDriver().waitForElementToBeInteractable("[widget-id='" + tabName + "'][active-tab='true'][appearance-id='ctab-item']");
		String closeCSS = "[widget-id='" + tabName + "'][active-tab='true'][appearance-id='ctab-item'] :nth-toolTipTextChild(3)";
		WebElement element = getWaitDriver().waitForElementToBeVisible(By.cssSelector(closeCSS));
		click(element);
	}

	/**
	 * Returns parent table css.
	 *
	 * @return RESULTS_LIST_TABLE_PARENT_CSS the parent table css
	 */
	public static String getResultsListTableParentCssParentCss() {
		return RESULTS_LIST_TABLE_PARENT_CSS.trim();
	}

}
