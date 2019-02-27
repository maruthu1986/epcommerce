package com.elasticpath.cucumber.definitions.catalog.editor.tabs;

import java.util.List;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.domainobjects.CartItemModifierGroup;
import com.elasticpath.selenium.domainobjects.ProductType;
import com.elasticpath.selenium.editor.catalog.CatalogEditor;
import com.elasticpath.selenium.editor.catalog.tabs.ProductTypeTab;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.CatalogManagementActionToolbar;
import com.elasticpath.selenium.util.Utility;
import com.elasticpath.selenium.wizards.AddEditProductTypeWizard;

/**
 * Product Types Tab Definitions.
 */
public class ProductTypesTabDefinition {

	private final CatalogEditor catalogEditor;
	private AddEditProductTypeWizard addEditProductTypeWizard;
	private final CatalogManagementActionToolbar catalogManagementActionToolbar;
	private final ProductTypeTab productTypeTab;
	private final ProductType productType;
	private final CartItemModifierGroup cartItemModifierGroup;

	/**
	 * Constructor.
	 *
	 * @param cartItemModifierGroup Cart Item Modifier Group.
	 * @param productType           ProductType to be used in current session.
	 */
	public ProductTypesTabDefinition(final CartItemModifierGroup cartItemModifierGroup, final ProductType productType) {
		final WebDriver driver = SetUp.getDriver();
		this.productTypeTab = new ProductTypeTab(driver);
		this.catalogManagementActionToolbar = new CatalogManagementActionToolbar(driver);
		this.catalogEditor = new CatalogEditor(driver);
		this.cartItemModifierGroup = cartItemModifierGroup;
		this.productType = productType;
	}

	/**
	 * Create product type.
	 *
	 * @param productTypeName the category type name
	 * @param attributeList   the list of attributes
	 */
	@When("^I create a new product type (.*) with following attributes$")
	public void createProductType(final String productTypeName, final List<String> attributeList) {
		addEditProductTypeWizard = productTypeTab.clickAddProductTypeButton();
		this.productType.setProductTypeName(productTypeName + Utility.getRandomUUID());
		addEditProductTypeWizard.enterProductTypeName(this.productType.getProductTypeName());
		for (String attribute : attributeList) {
			addEditProductTypeWizard.selectAvailableAttribute(attribute);
			addEditProductTypeWizard.clickMoveRightButton();
			addEditProductTypeWizard.verifyAssignedAttribute(attribute);
		}
		addEditProductTypeWizard.clickFinish();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}


	/**
	 * verify newly created product type exists.
	 */
	@Then("^(?:newly created|updated) product type is in the list$")
	public void verifyNewlyCreatedProductTypeExists() {
		productTypeTab.verifyProductType(this.productType.getProductTypeName());
	}

	/**
	 * Edit product type name.
	 */
	@When("^I edit the product type name$")
	public void editProductTypeName() {
		productTypeTab.selectProductType(this.productType.getProductTypeName());
		this.productType.setProductTypeName("Edit Prod Type" + "_" + Utility.getRandomUUID());
		productTypeTab.clickEditProductTypeButton();
		addEditProductTypeWizard.enterProductTypeName(this.productType.getProductTypeName());
		addEditProductTypeWizard.clickFinish();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Delete new product type.
	 */
	@When("^I delete the newly created product type$")
	public void deleteNewProductType() {
		productTypeTab.selectTab("ProductTypes");
		productTypeTab.selectProductType(this.productType.getProductTypeName());
		productTypeTab.clickRemoveProductTypeButton();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("CatalogMessages.CatalogProductTypesSection_RemoveDialog");
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify newly created product type is deleted.
	 */
	@Then("^the newly created product type is deleted$")
	public void verifyNewProductTypeDelete() {
		productTypeTab.verifyProductTypeDelete(this.productType.getProductTypeName());
	}

	/**
	 * Create new product type with newly created cart item modifier group.
	 *
	 * @param productTypeName the product type name.
	 */
	@When("^I create a new product type (.*) with newly created cart item modifier group$")
	public void createProductTypeWithCartItemModiferGroup(final String productTypeName) {
		catalogEditor.selectTab("ProductTypes");
		addEditProductTypeWizard = productTypeTab.clickAddProductTypeButton();
		this.productType.setProductTypeName(productTypeName + Utility.getRandomUUID());
		addEditProductTypeWizard.enterProductTypeName(this.productType.getProductTypeName());
		addEditProductTypeWizard.selectAvailableGroup(this.cartItemModifierGroup.getGroupCode());
		addEditProductTypeWizard.clickMoveRightForCartItemModiferGroup();
		addEditProductTypeWizard.verifyAssignedGroup(this.cartItemModifierGroup.getGroupCode());
		addEditProductTypeWizard.clickFinish();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

}
