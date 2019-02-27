package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.util.Constants;

/**
 * Edit Global Attributes Dialog.
 */
public class EditGlobalAttributesDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AddEditGlobalAttributesDialog_WindowTitle'] ";
	private static final String ADD_ATTRIBUTE_BUTTON_CSS
			= EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS + "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.Button_Add']";
	private static final String EDIT_ATTRIBUTE_BUTTON_CSS
			= EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS + "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.Button_Edit']";
	private static final String REMOVE_ATTRIBUTE_BUTTON_CSS
			= EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS + "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.Button_Remove']";
	private static final String SAVE_BUTTON_CSS = EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS + "div[widget-id='Save']";
	private static final String GLOBAL_ATTRIBUTES_TABLE_CSS = EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS + "div[widget-id='Global "
			+ "Attributes'][widget-type='Table'] ";
	private static final String GLOBAL_ATTRIBUTES_LIST_CSS = GLOBAL_ATTRIBUTES_TABLE_CSS + "div[column-id='%s']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public EditGlobalAttributesDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Click add attribute button.
	 *
	 * @return the Add Edit attribute dialog.
	 */
	public AddAttributeDialog clickAddAttributeButton() {
		clickButton(ADD_ATTRIBUTE_BUTTON_CSS, "Add Attribute", AddAttributeDialog.ADD_ATTRUBUTE_PARENT_CSS);
		return new AddAttributeDialog(getDriver());
	}
	/**
	 * Clicks edit attribute button.
	 *
	 * @return EditAttributeDialog
	 */
	public EditAttributeDialog clickEditAttributeButton() {
		clickButton(EDIT_ATTRIBUTE_BUTTON_CSS, "Edit Attribute", EditAttributeDialog.EDIT_ATTRUBUTE_PARENT_CSS);
		return new EditAttributeDialog(getDriver());
	}
	/**
	 * Clicks Remove Attribute button.
	 */
	public void clickRemoveAttributeButton() {
		clickButton(REMOVE_ATTRIBUTE_BUTTON_CSS, "Remove Attribute");
	}

	/**
	 * Clicks Save button.
	 */
	public void clickSaveButton() {
		clickButton(SAVE_BUTTON_CSS, "Save");
		waitTillElementDisappears(By.cssSelector(EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS));
	}

	/**
	 * Selects global attribute row.
	 *
	 * @param attributeValue the attribute value.
	 */
	public void selectGlobalAttributeRow(final String attributeValue) {
		assertThat(selectItemInDialog(GLOBAL_ATTRIBUTES_TABLE_CSS, GLOBAL_ATTRIBUTES_LIST_CSS, attributeValue, "Name"))
				.as("Unable to find global attribute value - " + attributeValue)
				.isTrue();
	}

	/**
	 * Verifies global attribute value.
	 *
	 * @param attributeValue the attribute value.
	 */
	public void verifyGlobalAttributeValue(final String attributeValue) {
		assertThat(selectItemInDialog(GLOBAL_ATTRIBUTES_TABLE_CSS, GLOBAL_ATTRIBUTES_LIST_CSS, attributeValue, "Name"))
				.as("Unable to find global attribute value - " + attributeValue)
				.isTrue();
	}

	/**
	 * Verifies global attribute value is not present.
	 *
	 * @param attributeValue the attribute value.
	 */
	public void verifyGlobalAttributeValueIsNotInList(final String attributeValue) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(verifyItemIsNotInDialog(GLOBAL_ATTRIBUTES_TABLE_CSS, GLOBAL_ATTRIBUTES_LIST_CSS, attributeValue, "Name"))
				.as("Global attribute is still in list - " + attributeValue)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Delete global attribute.
	 */
	public void deleteGlobalAttribute() {
		clickRemoveAttributeButton();
		new ConfirmDialog(getDriver()).clickOKButton("CatalogMessages.CatalogAttributesSection_RemoveDialog");
	}
}
