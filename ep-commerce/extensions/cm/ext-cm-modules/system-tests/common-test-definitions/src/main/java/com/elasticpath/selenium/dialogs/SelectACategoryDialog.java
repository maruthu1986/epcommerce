package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Select a Category Dialog.
 */
public class SelectACategoryDialog extends AbstractDialog {

	private static final String SELECT_A_CATEGORY_PARENT_CSS = "div[widget-id='Select a Category'][widget-type='Shell'] ";
	private static final String CATEGORY_CODE_INPUT_CSS = SELECT_A_CATEGORY_PARENT_CSS + "div[widget-id='Category Code'] > input";
	private static final String SEARCH_BUTTON_CSS = SELECT_A_CATEGORY_PARENT_CSS + "div[widget-id='Search'][widget-type='Button']";
	private static final String OK_BUTTON_CSS = SELECT_A_CATEGORY_PARENT_CSS + "div[widget-id='OK']";
	private static final String CATEGORY_RESULT_PARENT_CSS = "div[widget-id='Category Finder'][widget-type='Table'] ";
	private static final String CATEGORY_RESULT_COLUMN_CSS = CATEGORY_RESULT_PARENT_CSS + "div[column-id='%s']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public SelectACategoryDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs category code.
	 *
	 * @param categoryCode the product name.
	 */
	public void enterCategoryCode(final String categoryCode) {
		clearAndType(CATEGORY_CODE_INPUT_CSS, categoryCode);
	}

	/**
	 * Clicks search button.
	 */
	public void clickSearchButton() {
		clickButton(SEARCH_BUTTON_CSS, "Search");
	}

	/**
	 * Selects the item in search result list.
	 *
	 * @param categoryCode the category code.
	 */
	public void selectCategoryByCode(final String categoryCode) {
		assertThat(selectItemInDialog(CATEGORY_RESULT_PARENT_CSS, CATEGORY_RESULT_COLUMN_CSS, categoryCode, "Category Code"))
				.as("Unable to find category code - " + categoryCode)
				.isTrue();
	}

	/**
	 * Clicks OK.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(SELECT_A_CATEGORY_PARENT_CSS));
	}

}
