package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create Catalog Dialog.
 */
public class CreateCatalogDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String CREATE_CATALOG_PARENT_CSS = "div[widget-id='Create Catalog'][widget-type='Shell'] ";
	private static final String CATALOG_CODE_INPUT_CSS = CREATE_CATALOG_PARENT_CSS + "div[widget-id='Catalog Code'] input";
	private static final String CATALOG_NAME_INPUT_CSS = CREATE_CATALOG_PARENT_CSS + "div[widget-id='Catalog Name'] input";
	private static final String MOVE_RIGHT_BUTTON_CSS = CREATE_CATALOG_PARENT_CSS + "div[widget-id='>']";
	private static final String DEFAULT_LANGUAGE_COMBO_LIST_PARENT_CSS = "div[widget-id='Default Language'][widget-type='CCombo']";
	private static final String SAVE_BUTTON_CSS = CREATE_CATALOG_PARENT_CSS + "div[widget-id='Save'][seeable='true']";
	private static final String AVAILABLE_LANGUAGES_PARENT_CSS = "div[widget-id='Available Languages'][widget-type='Table'] ";
	private static final String AVAILABLE_LANGUAGES_COLUMN_CSS = AVAILABLE_LANGUAGES_PARENT_CSS + "div[column-id='%s']";
	private static final String SELECTED_LANGUAGES_PARENT_CSS = "div[widget-id='Selected Languages'][widget-type='Table'] ";
	private static final String SELECTED_LANGUAGES_COLUMN_CSS = SELECTED_LANGUAGES_PARENT_CSS + "div[column-id='%s']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreateCatalogDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs catalog code.
	 *
	 * @param catalogCode the code.
	 */
	public void enterCatalogCode(final String catalogCode) {
		clearAndType(CATALOG_CODE_INPUT_CSS, catalogCode);
	}

	/**
	 * Inputs catalog name.
	 *
	 * @param catalogName the catalog name.
	 */
	public void enterCatalogName(final String catalogName) {
		clearAndType(CATALOG_NAME_INPUT_CSS, catalogName);
	}

	/**
	 * Clicks '>' button.
	 */
	public void clickMoveRightButton() {
		clickButton(MOVE_RIGHT_BUTTON_CSS, "> (Move Right)");
	}

	/**
	 * Selects language from available language list.
	 *
	 * @param language the language.
	 */
	public void selectAvailableLanguage(final String language) {
		assertThat(selectItemInDialog(AVAILABLE_LANGUAGES_PARENT_CSS, AVAILABLE_LANGUAGES_COLUMN_CSS, language, ""))
				.as("Unable to find language - " + language)
				.isTrue();
	}

	/**
	 * Verifies selected language in list.
	 *
	 * @param language the language.
	 */
	public void verifySelectedLanguage(final String language) {
		assertThat(selectItemInDialog(SELECTED_LANGUAGES_PARENT_CSS, SELECTED_LANGUAGES_COLUMN_CSS, language, ""))
				.as("Unable to find language - " + language)
				.isTrue();
	}

	/**
	 * Selects a default language in combo box.
	 *
	 * @param defaultLanguage the default language.
	 */
	public void selectDefaultLanguage(final String defaultLanguage) {
		assertThat(selectComboBoxItem(DEFAULT_LANGUAGE_COMBO_LIST_PARENT_CSS, defaultLanguage))
				.as("Unable to find default language - " + defaultLanguage)
				.isTrue();
	}

	/**
	 * Clicks save button.
	 */
	public void clickSaveButton() {
		clickButton(SAVE_BUTTON_CSS, "Save");
		waitTillElementDisappears(By.cssSelector(CREATE_CATALOG_PARENT_CSS));
	}

}
