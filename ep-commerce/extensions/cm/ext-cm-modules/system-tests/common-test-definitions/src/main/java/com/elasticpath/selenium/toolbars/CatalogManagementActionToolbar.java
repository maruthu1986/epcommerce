package com.elasticpath.selenium.toolbars;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.dialogs.CreateCatalogDialog;
import com.elasticpath.selenium.dialogs.CreateEditVirtualCatalogDialog;
import com.elasticpath.selenium.dialogs.EditGlobalAttributesDialog;

/**
 * Catalog Management Toolbar.
 */
public class CatalogManagementActionToolbar extends AbstractToolbar {

	private static final String APPEARANCE_ID_CSS = "[appearance-id='toolbar-button']";
	private static final String CREATE_CATALOG_BUTTON_CSS = APPEARANCE_ID_CSS
			+ "[widget-id='Create Catalog']";
	private static final String CREATE_VIRTUAL_CATALOG_BUTTON_CSS = APPEARANCE_ID_CSS
			+ "[widget-id='Create Virtual Catalog']";
	private static final String EDIT_GLOBAL_ATTRIBUTE_BUTTON_CSS = APPEARANCE_ID_CSS
			+ "[widget-id='Edit Global Attributes']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CatalogManagementActionToolbar(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Create Catalog button.
	 *
	 * @return CreateCatalogDialog
	 */
	public CreateCatalogDialog clickCreateCatalogButton() {
		clickButton(CREATE_CATALOG_BUTTON_CSS, "Create Catalog", CreateCatalogDialog.CREATE_CATALOG_PARENT_CSS);
		return new CreateCatalogDialog(getDriver());
	}

	/**
	 * Clicks Create Virtual Catalog button.
	 *
	 * @return CreateEditVirtualCatalogDialog
	 */
	public CreateEditVirtualCatalogDialog clickCreateVirtualCatalogButton() {
		final String dialogName = "Create";
		clickButton(CREATE_VIRTUAL_CATALOG_BUTTON_CSS, "Create Virtual Catalog", String.format(CreateEditVirtualCatalogDialog
				.CREATE_EDIT_VIRTUAL_CATALOG_PARENT_CSS_TEMPLATE, dialogName));
		return new CreateEditVirtualCatalogDialog(getDriver(), dialogName);
	}

	/**
	 * Clicks Edit Global Attribute button.
	 *
	 * @return EditGlobalAttributesDialog
	 */
	public EditGlobalAttributesDialog clickEditGlobalAttributesButton() {
		clickButton(EDIT_GLOBAL_ATTRIBUTE_BUTTON_CSS, "Edit Global Attribute", EditGlobalAttributesDialog.EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS);
		return new EditGlobalAttributesDialog(getDriver());
	}

	/**
	 * Verifies Create Catalog button is present.
	 */
	public void verifyCreateCatalogButtonIsPresent() {
		assertThat(isElementPresent(By.cssSelector(CREATE_CATALOG_BUTTON_CSS)))
				.as("Unable to find Crate Catalog button")
				.isTrue();
	}

}
