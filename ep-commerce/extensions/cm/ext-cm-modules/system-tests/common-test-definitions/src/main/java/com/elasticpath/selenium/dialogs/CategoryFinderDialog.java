package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

/**
 * Category Finder Dialog class for adding a linked category to a virtual catalog.
 */
public class CategoryFinderDialog extends AbstractDialog {


	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String CATEGORY_FINDER_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.core."
			+ "CoreMessages.CategoryFinderDialog_WindowTitle'] ";
	private static final String CATALOG_COMBO_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages."
			+ "CategoryFinderDialog_Catalog'][widget-type='CCombo'] ";
	private static final String SEARCH_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".CategoryFinderDialog_Search'][widget-type='Button']";
	private static final String CATEGORY_FINDER_TABLE_CSS = CATEGORY_FINDER_PARENT_CSS + "div[widget-id='Category Finder'][widget-type='Table'] ";
	private static final String CATEGORY_COLUMN_CSS = CATEGORY_FINDER_TABLE_CSS + "div[column-id='%s']";
	private static final String CATEGORY_NAME_INPUT_CSS = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".CategoryFinderDialog_CategoryName'] input";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CategoryFinderDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Search button.
	 */
	public void clickSearchButton() {
		clickButton(SEARCH_BUTTON_CSS, "Search");
	}

	/**
	 * Selects a category in the category table.
	 *
	 * @param category the category to select.
	 */
	public void selectCategory(final String category) {
		assertThat(selectItemInDialog(CATEGORY_FINDER_TABLE_CSS, CATEGORY_COLUMN_CSS, category, ""))
				.as("Unable to find category - " + category)
				.isTrue();
	}

	/**
	 * Selects a catalog from the combo box list.
	 *
	 * @param catalog the catalog.
	 */
	public void selectCatalog(final String catalog) {
		assertThat(selectComboBoxItem(CATALOG_COMBO_PARENT_CSS, catalog))
				.as("Unable to find catalog - " + catalog)
				.isTrue();
	}

	/**
	 * Enter category name.
	 *
	 * @param categoryName Category Name.
	 */
	public void enterCategoryName(final String categoryName) {
		clearAndType(CATEGORY_NAME_INPUT_CSS, categoryName);
	}
}