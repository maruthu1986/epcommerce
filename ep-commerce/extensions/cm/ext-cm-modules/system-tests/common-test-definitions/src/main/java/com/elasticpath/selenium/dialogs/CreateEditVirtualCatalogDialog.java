package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create Virtual Catalog Dialog.
 */
public class CreateEditVirtualCatalogDialog extends AbstractDialog {

	private final String createEditVirtualCatalogParentCss;
	private final String catalogCodeInputCss;
	private final String catalogNameInputCss;
	private final String defaultLanguageComboCss;
	private final String saveButtonCss;
	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String CREATE_EDIT_VIRTUAL_CATALOG_PARENT_CSS_TEMPLATE = "div[widget-id*='%s Virtual Catalog'][widget-type='Shell'] ";
	private static final String CATALOG_CODE_INPUT_CSS_TEMPLATE = "div[widget-id='Catalog Code'] > input";
	private static final String CATALOG_NAME_INPUT_CSS_TEMPLATE = "div[widget-id='Catalog Name'] > input";
	private static final String DEFAULT_LANGUAGE_COMBO_CSS_TEMPLATE = "div[widget-id='Default Language'][widget-type='CCombo']";
	private static final String SAVE_BUTTON_CSS_TEMPLATE = "div[widget-id='Save'][style*='opacity: 1']";

	/**
	 * Constructor.
	 *
	 * @param driver     WebDriver which drives this page.
	 * @param dialogName String for wild card dialog name.
	 */
	public CreateEditVirtualCatalogDialog(final WebDriver driver, final String dialogName) {
		super(driver);

		createEditVirtualCatalogParentCss = String.format(CREATE_EDIT_VIRTUAL_CATALOG_PARENT_CSS_TEMPLATE, dialogName);
		catalogCodeInputCss = createEditVirtualCatalogParentCss + CATALOG_CODE_INPUT_CSS_TEMPLATE;
		catalogNameInputCss = createEditVirtualCatalogParentCss + CATALOG_NAME_INPUT_CSS_TEMPLATE;
		defaultLanguageComboCss = createEditVirtualCatalogParentCss + DEFAULT_LANGUAGE_COMBO_CSS_TEMPLATE;
		saveButtonCss = createEditVirtualCatalogParentCss + SAVE_BUTTON_CSS_TEMPLATE;
	}

	/**
	 * Inputs catalog code.
	 *
	 * @param catalogCode the code.
	 */
	public void enterCatalogCode(final String catalogCode) {
		clearAndType(catalogCodeInputCss, catalogCode);
	}

	/**
	 * Inputs catalog name.
	 *
	 * @param catalogName the catalog name.
	 */
	public void enterCatalogName(final String catalogName) {
		clearAndType(catalogNameInputCss, catalogName);
	}

	/**
	 * Selects a default language in combo box.
	 *
	 * @param defaultLanguage the default language.
	 */
	public void selectDefaultLanguage(final String defaultLanguage) {
		assertThat(selectComboBoxItem(defaultLanguageComboCss, defaultLanguage))
				.as("Unable to find defaultLanguage - " + defaultLanguage)
				.isTrue();
	}

	/**
	 * Clicks save button.
	 */
	public void clickSaveButton() {
		clickButton(saveButtonCss, "Save");
		waitTillElementDisappears(By.cssSelector(createEditVirtualCatalogParentCss));
	}
}
