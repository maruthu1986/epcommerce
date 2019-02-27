package com.elasticpath.cucumber.definitions.catalog.editor.tabs;

import static org.assertj.core.api.Assertions.fail;

import java.util.List;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.dialogs.AddEditCartItemModifierGroupDialog;
import com.elasticpath.selenium.dialogs.AddEditCartItemModifierGroupFieldDialog;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.domainobjects.CartItemModiferGroupField;
import com.elasticpath.selenium.domainobjects.CartItemModifierGroup;
import com.elasticpath.selenium.editor.catalog.CatalogEditor;
import com.elasticpath.selenium.editor.catalog.tabs.CartItemModifierGroupsTab;
import com.elasticpath.selenium.editor.catalog.tabs.ProductTypeTab;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.CatalogManagementActionToolbar;
import com.elasticpath.selenium.util.Utility;
import com.elasticpath.selenium.wizards.AddEditProductTypeWizard;

/**
 * Cart Item Modifier Group Tab Definition.
 */
public class CartItemModifierGroupTabDefinition {

	private final CatalogManagementActionToolbar catalogManagementActionToolbar;
	private final CartItemModifierGroupsTab cartItemModifierGroupsTab;
	private final CatalogEditor catalogEditor;
	private final ProductTypeTab productTypeTab;
	private final CartItemModifierGroup cartItemModifierGroup;
	private final CartItemModiferGroupField cartItemModiferGroupField;
	private AddEditCartItemModifierGroupDialog addEditModifierGroupDialog;
	private AddEditCartItemModifierGroupFieldDialog fieldDialg;

	/**
	 * Constructor.
	 *
	 * @param cartItemModifierGroup     Cart Item Modifier Group object to be used in this session.
	 * @param cartItemModiferGroupField Cart Item Modifier Group Field to be used in this session.
	 */
	public CartItemModifierGroupTabDefinition(final CartItemModifierGroup cartItemModifierGroup, final CartItemModiferGroupField
			cartItemModiferGroupField) {
		final WebDriver driver = SetUp.getDriver();
		this.catalogManagementActionToolbar = new CatalogManagementActionToolbar(driver);
		this.cartItemModifierGroupsTab = new CartItemModifierGroupsTab(driver);
		this.catalogEditor = new CatalogEditor(driver);
		this.productTypeTab = new ProductTypeTab(driver);
		this.cartItemModifierGroup = cartItemModifierGroup;
		this.cartItemModiferGroupField = cartItemModiferGroupField;
	}

	/**
	 * Create Cart Item Modifier Group.
	 *
	 * @param code      the group code
	 * @param groupName the group name
	 */
	@When("^I create a new modifier group with group code (.*) and group name (.*)$")
	public void createCartItemModifierGroup(final String code, final String groupName) {
		addEditModifierGroupDialog = cartItemModifierGroupsTab.clickAddGroupButton();
		this.cartItemModifierGroup.setGroupCode(code + Utility.getRandomUUID());
		this.cartItemModifierGroup.setGrouopName(groupName);
		addEditModifierGroupDialog.enterModifierGroupCode(this.cartItemModifierGroup.getGroupCode());
		addEditModifierGroupDialog.enterModifierGroupName(this.cartItemModifierGroup.getGrouopName());
		addEditModifierGroupDialog.clickAddButton();
		catalogManagementActionToolbar.saveAll();
	}

	/**
	 * Create field with short text.
	 *
	 * @param fieldInfoList the field details.
	 */
	@And("^I add a new field to this group with following details$")
	public void createFieldWithShortText(final List<CartItemModiferGroupField> fieldInfoList) {
		for (CartItemModiferGroupField fieldInfo : fieldInfoList) {
			cartItemModifierGroupsTab.selectGroup(this.cartItemModifierGroup.getGroupCode());
			cartItemModifierGroupsTab.clickEditGroupButton();
			fieldDialg = addEditModifierGroupDialog.clickAddFieldButton();
			this.cartItemModiferGroupField.setFieldCode(fieldInfo.getFieldCode() + Utility.getRandomUUID());
			this.cartItemModiferGroupField.setFieldName(fieldInfo.getFieldName());
			fieldDialg.enterModifierFieldCode(this.cartItemModiferGroupField.getFieldCode());
			fieldDialg.enterDisplayName(this.cartItemModiferGroupField.getFieldName());
			fieldDialg.selectFieldType(fieldInfo.getFieldType());
			this.cartItemModiferGroupField.setFieldType(fieldInfo.getFieldType());
			switch (fieldInfo.getFieldType()) {
				case "Short Text":
					if (fieldInfo.getShortTextSize() != null) {

						createShortTextField(fieldInfo.getShortTextSize());
					}
					break;

				case "Multi Select Option":
					if (fieldInfo.getOptionValue() != null && fieldInfo.getOptionName() != null) {

						createMultiSelectOptionField(fieldInfo.getOptionValue(), fieldInfo.getOptionName());
					}
					break;

				default:
					fail("No field type given.");
					return;
			}
			addEditModifierGroupDialog.clickOkButton();
			catalogManagementActionToolbar.saveAll();
			catalogManagementActionToolbar.clickReloadActiveEditor();
		}
	}

	/**
	 * Create short text field.
	 *
	 * @param fieldSize the max field size.
	 */
	private void createShortTextField(final String fieldSize) {
		fieldDialg.enterMaxSize(fieldSize);
		fieldDialg.clickAddButton();
	}

	/**
	 * Create multi select option field.
	 *
	 * @param optionlValue option value.
	 * @param optionName   option name.
	 */
	private void createMultiSelectOptionField(final String optionlValue, final String optionName) {
		String optionalValueRandom = optionlValue + Utility.getRandomUUID();
		String optionNameRandom = optionName + Utility.getRandomUUID();
		fieldDialg.createOption(optionalValueRandom, optionNameRandom);
		this.cartItemModiferGroupField.setOptionValue(optionalValueRandom);
		this.cartItemModiferGroupField.setOptionName(optionNameRandom);
		fieldDialg.clickAddButton();

	}

	/**
	 * Edit option name.
	 */
	@When("^I edit and verify the edited option name$")
	public void editOptionName() {
		addEditModifierGroupDialog.selectField(this.cartItemModiferGroupField.getFieldCode());
		addEditModifierGroupDialog.clickEditFieldButton();
		fieldDialg.editAndVerifyOptionName(this.cartItemModiferGroupField.getOptionName());
		fieldDialg.clickOkButton();
		addEditModifierGroupDialog.clickOkButton();
	}

	/**
	 * verify newly created group exists.
	 */
	@Then("^newly created group is in the list$")
	public void verifyNewlyCreatedGroupExists() {
		cartItemModifierGroupsTab.verifyGroup(this.cartItemModifierGroup.getGroupCode());
	}

	/**
	 * verify group with updated name exists.
	 */
	@Then("^updated group name is in the list$")
	public void verifyUpdatedGroupNameExists() {
		cartItemModifierGroupsTab.verifyGroup(this.addEditModifierGroupDialog.getGroupName());
	}

	/**
	 * Edit group name.
	 */
	@When("^I edit the group name$")
	public void editGroupName() {
		cartItemModifierGroupsTab.selectGroup(this.cartItemModifierGroup.getGroupCode());
		this.addEditModifierGroupDialog.setGroupName("EditGroupName" + "_" + Utility.getRandomUUID());
		cartItemModifierGroupsTab.clickEditGroupButton();
		addEditModifierGroupDialog.enterModifierGroupName(this.addEditModifierGroupDialog.getGroupName());
		addEditModifierGroupDialog.clickOkButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Edit field name.
	 */
	@When("^I edit and verify the edited field name$")
	public void editFieldName() {
		cartItemModifierGroupsTab.selectGroup(this.cartItemModifierGroup.getGroupCode());
		cartItemModifierGroupsTab.clickEditGroupButton();
		addEditModifierGroupDialog.selectField(this.cartItemModiferGroupField.getFieldName());
		addEditModifierGroupDialog.clickEditFieldButton();
		this.cartItemModiferGroupField.setFieldName("EditFieldName" + "_" + Utility.getRandomUUID());
		fieldDialg.enterDisplayName(this.cartItemModiferGroupField.getFieldName());
		fieldDialg.clickOkButton();
		addEditModifierGroupDialog.verifyField(this.cartItemModiferGroupField.getFieldName());
	}

	/**
	 * Delete new field.
	 */
	@When("^I delete the newly created field$")
	public void deleteNewField() {
		cartItemModifierGroupsTab.selectGroup(this.cartItemModifierGroup.getGroupCode());
		cartItemModifierGroupsTab.clickEditGroupButton();
		addEditModifierGroupDialog.selectField(this.cartItemModiferGroupField.getFieldName());
		addEditModifierGroupDialog.clickRemoveFieldButton();

	}

	/**
	 * Verify field deleted.
	 */
	@Then("^the field is deleted$")
	public void verifyFieldDeleted() {
		addEditModifierGroupDialog.verfiyFieldDelete(this.cartItemModiferGroupField.getFieldName());
		addEditModifierGroupDialog.clickOkButton();
	}

	/**
	 * Delete new group.
	 */
	@When("^I delete the newly created group$")
	public void deleteNewGroup() {
		cartItemModifierGroupsTab.selectTab("CartItemModifierGroups");
		cartItemModifierGroupsTab.selectGroup(this.cartItemModifierGroup.getGroupCode());
		cartItemModifierGroupsTab.clickRemoveGroupButton();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("CatalogCartItemModifierGroupsSection_RemoveDialog");
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify newly created group is deleted.
	 */
	@Then("^the newly created group should be deleted$")
	public void verifyNewGroupDelete() {
		cartItemModifierGroupsTab.verifyGroupDelete(this.addEditModifierGroupDialog.getGroupName());
	}

	/**
	 * Verify cart item modifier group exists when creating a new product type.
	 */
	@Then("^this new cart item modifier group should be available as a selection when creating Product Type$")
	public void verifyGroupInProductTypeWizard() {
		catalogEditor.selectTab("ProductTypes");
		AddEditProductTypeWizard productTypeWizard = productTypeTab.clickAddProductTypeButton();
		productTypeWizard.selectAvailableGroup(this.cartItemModifierGroup.getGroupCode());
		productTypeWizard.clickCancel();
		catalogEditor.selectTab("CartItemModifierGroups");
	}

	/**
	 * Verify cart item modifier group does not exist when creating a new product type.
	 */
	@Then("^the deleted group should not appear when creating a new Product Type$")
	public void verifyGroupDeletedInProductType() {
		catalogEditor.selectTab("ProductTypes");
		AddEditProductTypeWizard productTypeWizard = productTypeTab.clickAddProductTypeButton();
		productTypeWizard.verifyGroupAbsence(this.cartItemModifierGroup.getGroupCode());
		productTypeWizard.clickCancel();
		catalogEditor.selectTab("CartItemModifierGroups");
	}
}
