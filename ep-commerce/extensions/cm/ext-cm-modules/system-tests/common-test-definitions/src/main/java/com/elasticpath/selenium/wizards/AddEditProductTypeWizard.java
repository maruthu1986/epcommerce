package com.elasticpath.selenium.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.util.Constants;

/**
 * Add and Edit Product Type Wizard.
 */
public class AddEditProductTypeWizard extends AbstractWizard {

	private static final String ADD_PRODUCT_TYPE_PARENT_CSS
			= "div[automation-id*='com.elasticpath.cmclient.catalog.CatalogMessages.ProductTypeAddEditWizard'][widget-type='Shell'] ";
	private static final String PRODUCT_TYPE_NAME_INPUT_CSS = ADD_PRODUCT_TYPE_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductTypeAddEditWizard_Name'] input";
	private static final String MOVE_RIGHT_BUTTON_CSS = ADD_PRODUCT_TYPE_PARENT_CSS + "div[widget-id='>']";
	private static final String AVAILABLE_ATTRIBUTES_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductTypeAddEditWizard_AvailableAttributes']"
			+ "[widget-type='Table'] ";
	private static final String AVAILABLE_GROUPS_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductTypeAddEditWizard_AvailableCartItemModifierGroups']"
			+ "[widget-type='Table'] ";
	private static final String DIV_COLUMN_ID_S = "div[column-id='%s']";
	private static final String AVAILABLE_ATTRIBUTES_COLUMN_CSS = AVAILABLE_ATTRIBUTES_PARENT_CSS + DIV_COLUMN_ID_S;
	private static final String AVAILABLE_GROUPS_COLUMN_CSS = AVAILABLE_GROUPS_PARENT_CSS + DIV_COLUMN_ID_S;
	private static final String ASSIGNED_ATTRIBUTES_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductTypeAddEditWizard_AssignedAttributes']"
			+ "[widget-type='Table'] ";
	private static final String ASSIGNED_ATTRIBUTES_COLUMN_CSS = ASSIGNED_ATTRIBUTES_PARENT_CSS + DIV_COLUMN_ID_S;
	private static final String ASSIGNED_CART_ITEM_MODIFIER_GROUP_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.catalog"
			+ ".CatalogMessages.ProductTypeAddEditWizard_AssignedCartItemModifierGroups'][widget-type='Table'] ";
	private static final String ASSIGNED_CART_ITEM_MODIFIER_GROUP_COLUMN_CSS = ASSIGNED_CART_ITEM_MODIFIER_GROUP_PARENT_CSS + DIV_COLUMN_ID_S;
	private static final String ASSIGN_GROUP_MOVE_RIGHT_CSS = " div[widget-id='>']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddEditProductTypeWizard(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs product type name.
	 *
	 * @param productTypeName the product type name
	 */
	public void enterProductTypeName(final String productTypeName) {
		clearAndType(PRODUCT_TYPE_NAME_INPUT_CSS, productTypeName);
	}

	/**
	 * Clicks '>' button.
	 */
	public void clickMoveRightButton() {
		click(getDriver().findElement(By.cssSelector(MOVE_RIGHT_BUTTON_CSS)));
	}

	/**
	 * Clicks move right to assign group.
	 */
	public void clickMoveRightForCartItemModiferGroup() {
		getDriver().findElement(By.cssSelector(AVAILABLE_GROUPS_PARENT_CSS)).findElement(By.xpath("..")).findElement(By.xpath(".."))
				.findElement(By.cssSelector(ASSIGN_GROUP_MOVE_RIGHT_CSS)).click();
	}

	/**
	 * Selects attribute from available attributes list.
	 *
	 * @param attribute the attribute
	 */
	public void selectAvailableAttribute(final String attribute) {
		assertThat(selectItemInDialog(AVAILABLE_ATTRIBUTES_PARENT_CSS, AVAILABLE_ATTRIBUTES_COLUMN_CSS, attribute, ""))
				.as("Unable to find available attribute - " + attribute)
				.isTrue();
	}

	/**
	 * Selects group from available groups list.
	 *
	 * @param group the group
	 */
	public void selectAvailableGroup(final String group) {
		assertThat(selectItemInDialog(AVAILABLE_GROUPS_PARENT_CSS, AVAILABLE_GROUPS_COLUMN_CSS, group, ""))
				.as("Unable to find available group - " + group)
				.isTrue();
	}

	/**
	 * Verifies that the group does not exist in this dialog box by trying to find it for a period of
	 * IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS.
	 *
	 * @param group the group
	 */
	public void verifyGroupAbsence(final String group) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(verifyItemIsNotInDialog(AVAILABLE_GROUPS_PARENT_CSS, AVAILABLE_GROUPS_COLUMN_CSS, group, ""))
				.as("Deleted cart item modifier group still present - " + group)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verifies if assigned in list.
	 *
	 * @param group the cart item modifier group.
	 */
	public void verifyAssignedGroup(final String group) {
		getWaitDriver().adjustWaitInterval(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(selectItemInDialog(ASSIGNED_CART_ITEM_MODIFIER_GROUP_PARENT_CSS, ASSIGNED_CART_ITEM_MODIFIER_GROUP_COLUMN_CSS, group, ""))
				.as("Unable to find cart item modifier group - " + group)
				.isTrue();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verifies assigned attributes in list.
	 *
	 * @param attribute the language.
	 */
	public void verifyAssignedAttribute(final String attribute) {
		assertThat(selectItemInDialog(ASSIGNED_ATTRIBUTES_PARENT_CSS, ASSIGNED_ATTRIBUTES_COLUMN_CSS, attribute, ""))
				.as("Unable to find assigned attribute - " + attribute)
				.isTrue();
		click(getSelectedElement());
	}
}
