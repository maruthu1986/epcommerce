package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.editor.RmaEditor;


/**
 * RMA Search Results pane.
 */
public class RmaSearchResultPane extends AbstractPageObject {

	private static final String RESULTS_PANE_PARENT_CSS = "div[pane-location='center-pane-inner'][seeable='true'] ";
	private static final String RESULTS_LIST_TABLE_PARENT_CSS = RESULTS_PANE_PARENT_CSS + "div[widget-id='Order return Search Result'] ";
	private static final String RESULTS_COLUMN_CSS = RESULTS_LIST_TABLE_PARENT_CSS + "div[column-id='%s']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public RmaSearchResultPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies the column text in Order Search Results tab and selects the row.
	 *
	 * @param columnValue the column value.
	 * @param columnName  the column number.
	 */
	public void verifyOrderColumnValueAndSelectRow(final String columnValue, final String columnName) {
		assertThat(selectItemInCenterPane(RESULTS_LIST_TABLE_PARENT_CSS, String.format(RESULTS_COLUMN_CSS, columnValue), columnValue, columnName))
				.as("Unable to find column value - " + columnValue)
				.isTrue();
	}

	/**
	 * Select order and open editor.
	 *
	 * @param columnValue the column value.
	 * @param columnName  the column number.
	 * @return the order editor.
	 */
	public RmaEditor selectOrderAndOpenRmaEditor(final String columnValue, final String columnName) {
		verifyOrderColumnValueAndSelectRow(columnValue, columnName);
		doubleClick(getSelectedElement());
		return new RmaEditor(getDriver());
	}
}
