package com.elasticpath.cucumber.definitions.catalog;

import java.util.List;

import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;

import com.elasticpath.cucumber.definitions.NavigationDefinition;
import com.elasticpath.cucumber.definitions.ProductAndBundleDefinition;
import com.elasticpath.cucumber.definitions.catalog.editor.tabs.CartItemModifierGroupTabDefinition;
import com.elasticpath.cucumber.definitions.catalog.editor.tabs.CategoryTypesTabDefinition;
import com.elasticpath.cucumber.definitions.catalog.editor.tabs.ProductTypesTabDefinition;
import com.elasticpath.selenium.dialogs.AddAttributeDialog;
import com.elasticpath.selenium.dialogs.AddEditBrandDialog;
import com.elasticpath.selenium.dialogs.CategoryFinderDialog;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateCatalogDialog;
import com.elasticpath.selenium.dialogs.CreateEditVirtualCatalogDialog;
import com.elasticpath.selenium.dialogs.EditAttributeDialog;
import com.elasticpath.selenium.dialogs.EditGlobalAttributesDialog;
import com.elasticpath.selenium.domainobjects.CartItemModifierGroup;
import com.elasticpath.selenium.domainobjects.Catalog;
import com.elasticpath.selenium.domainobjects.Category;
import com.elasticpath.selenium.domainobjects.LinkedCategory;
import com.elasticpath.selenium.domainobjects.ProductType;
import com.elasticpath.selenium.editor.catalog.CatalogEditor;
import com.elasticpath.selenium.editor.catalog.tabs.BrandsTab;
import com.elasticpath.selenium.editor.product.ProductEditor;
import com.elasticpath.selenium.navigations.CatalogManagement;
import com.elasticpath.selenium.resultspane.CatalogProductListingPane;
import com.elasticpath.selenium.resultspane.CatalogSearchResultPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ActivityToolbar;
import com.elasticpath.selenium.toolbars.CatalogManagementActionToolbar;
import com.elasticpath.selenium.toolbars.ChangeSetActionToolbar;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.selenium.util.Utility;
import com.elasticpath.selenium.wizards.CreateCategoryWizard;

/**
 * Catalog Search step definitions.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods", "PMD.TooManyFields"})
public class CatalogDefinition {
	private final CatalogManagement catalogManagement;
	private final ActivityToolbar activityToolbar;
	private final ChangeSetActionToolbar changeSetActionToolbar;
	private CatalogSearchResultPane catalogSearchResultPane;
	private CatalogProductListingPane catalogProductListingPane;
	private final CatalogManagementActionToolbar catalogManagementActionToolbar;
	private CreateCatalogDialog createCatalogDialog;
	private CreateEditVirtualCatalogDialog createEditVirtualCatalogDialog;
	private CreateCategoryWizard createCategoryWizard;
	private CategoryFinderDialog categoryFinderDialog;
	private EditGlobalAttributesDialog editGlobalAttributesDialog;
	private AddAttributeDialog addAttributeDialog;
	private EditAttributeDialog editAttributeDialog;
	private CatalogEditor catalogEditor;
	private ProductEditor productEditor;
	private AddEditBrandDialog addEditBrandDialog;
	private final BrandsTab brandsTab;
	private final Catalog catalog;
	private final Category category;
	private final LinkedCategory linkedCategory;
	private final ProductType productType;
	private String globalAttributeName = "";
	private String virtualProductName = "";
	private String virtualCategoryName = "";
	private String virtualCatalogName = "";
	private final WebDriver driver;
	private final ProductTypesTabDefinition productTypesTabDefinition;
	private final CartItemModifierGroup cartItemModifierGroup;
	private final CartItemModifierGroupTabDefinition cartItemModifierGroupTabDefinition;
	private final CategoryTypesTabDefinition categoryTypesTabDefinition;
	private final ProductAndBundleDefinition productAndBundleDefinition;

	/**
	 * Constructor.
	 *
	 * @param catalog                            catalog shared object between CategoryTypesTabDefinition and CatalogDefinition
	 * @param category                           Category object.
	 * @param productType                        ProductType object.
	 * @param productTypesTabDefinition          ProductTypesTabDefinition object.
	 * @param cartItemModifierGroup              Cart Item Modifier Group object.
	 * @param cartItemModifierGroupTabDefinition Cart Item Modifier Group Tab Definition object.
	 * @param categoryTypesTabDefinition         CategoryTypesTabDefinition object.
	 * @param productAndBundleDefinition         ProductAndBundleDefinition object.
	 */
//	CHECKSTYLE:OFF: checkstyle:too many parameters
	public CatalogDefinition(final Catalog catalog, final Category category, final LinkedCategory linkedCategory, final ProductType productType,
							 final ProductTypesTabDefinition
									 productTypesTabDefinition, final CartItemModifierGroup cartItemModifierGroup, final
							 CartItemModifierGroupTabDefinition
									 cartItemModifierGroupTabDefinition, final CategoryTypesTabDefinition
									 categoryTypesTabDefinition, final ProductAndBundleDefinition productAndBundleDefinition) {
		driver = SetUp.getDriver();
		catalogManagement = new CatalogManagement(driver);
		catalogManagementActionToolbar = new CatalogManagementActionToolbar(driver);
		activityToolbar = new ActivityToolbar(driver);
		brandsTab = new BrandsTab(driver);
		changeSetActionToolbar = new ChangeSetActionToolbar(driver);
		this.catalog = catalog;
		this.category = category;
		this.linkedCategory = linkedCategory;
		this.productType = productType;
		this.cartItemModifierGroup = cartItemModifierGroup;
		this.productTypesTabDefinition = productTypesTabDefinition;
		this.cartItemModifierGroupTabDefinition = cartItemModifierGroupTabDefinition;
		this.categoryTypesTabDefinition = categoryTypesTabDefinition;
		this.productAndBundleDefinition = productAndBundleDefinition;
	}

	/**
	 * Search for product name.
	 *
	 * @param productName product name.
	 */
	@When("^I search for product name (.*)$")
	public void searchForProductName(final String productName) {
		searchForProductByName(productName);
	}

	/**
	 * Verify Product name.
	 *
	 * @param expectedProductName expected product name.
	 */
	@Then("^Product name (.*) should appear in result$")
	public void verifyProductSearchResult(final String expectedProductName) {
		catalogSearchResultPane.verifyProductNameExists(expectedProductName);
	}

	/**
	 * Expand Catalog.
	 *
	 * @param catalogName the catalog Name.
	 */
	@When("^I expand (.+) catalog$")
	public void expandCatalog(final String catalogName) {
		catalogManagement.expandCatalog(catalogName);
	}

	/**
	 * Expand catalog that was created in the same scenario.
	 */
	@When("^I expand catalog created in this scenario$")
	public void expandNewlyCreatedCatalog() {
		catalogManagement.expandCatalog(this.catalog.getCatalogName());
	}

	/**
	 * Double click category.
	 *
	 * @param categoryName the category name.
	 */
	@When("^I open category (.+) to view products list$")
	public void doubleClickCategory(final String categoryName) {
		catalogProductListingPane = catalogManagement.doubleClickCategory(categoryName);
	}

	/**
	 * Verify product in product listing.
	 *
	 * @param productNameList the product name list.
	 */
	@Then("^Product Listing should contain following products$")
	public void verifyProductInProductListing(final List<String> productNameList) {
		for (String productName : productNameList) {
			catalogProductListingPane.verifyProductNameExists(productName);
		}
	}

	/**
	 * Create a new catalog with the given details.
	 *
	 * @param catalogInfoList the catalog details list.
	 */
	@When("^(?:I create a|a) new catalog with following details$")
	public void createNewCatalog(final List<Catalog> catalogInfoList) {
		for (Catalog catalog : catalogInfoList) {
			createCatalogDialog = catalogManagementActionToolbar.clickCreateCatalogButton();
			String catalogCode = Utility.getRandomUUID();
			createCatalogDialog.enterCatalogCode(catalogCode);
			this.catalog.setCatalogName(catalog.getCatalogName() + "-" + catalogCode);
			createCatalogDialog.enterCatalogName(this.catalog.getCatalogName());
			createCatalogDialog.selectAvailableLanguage(catalog.getLanguage());
			createCatalogDialog.clickMoveRightButton();
			createCatalogDialog.verifySelectedLanguage(catalog.getLanguage());
			createCatalogDialog.selectDefaultLanguage(catalog.getLanguage());
			createCatalogDialog.clickSaveButton();
			if (catalog.getBrand() != null) {
				addCatalogBrand(catalog.getBrand());
			}
		}
	}

	/**
	 * Add catalog brand.
	 *
	 * @param brandName brand name.
	 */
	private void addCatalogBrand(final String brandName) {
		selectCatalogEditorTab("Brands");
		addEditBrandDialog = brandsTab.clickAddBrandButton();
		String brandCode = Utility.getRandomUUID();
		addEditBrandDialog.enterBrandCode(brandCode);
		this.catalog.setBrand(brandName + brandCode);
		addEditBrandDialog.enterBrandName(this.catalog.getBrand());
		addEditBrandDialog.clickAddButton();
		brandsTab.verifyAndSelectBrand(addEditBrandDialog.getBrandCode());
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Delete new brand.
	 */
	@When("^I delete the newly created brand")
	public void deleteNewBrand() {
		selectCatalogEditorTab("Brands");
		brandsTab.verifyAndSelectBrand(this.catalog.getBrand());
		brandsTab.clickRemoveBrandButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Delete given brand.
	 *
	 * @param brandCode brand code to be deleted.
	 */
	@When("^I delete the brand (.+)$")
	public void deleteNewBrandCode(final String brandCode) {
		deleteBrandCode(brandCode);
	}

	/**
	 * Delete newly created brand.
	 */
	@When("^I delete the newly create brand$")
	public void deleteNewBrandCode() {
		deleteBrandCode(this.catalog.getBrand());
		this.catalog.setBrand(null);
	}

	private void deleteBrandCode(final String brandCode) {
		brandsTab.verifyAndSelectBrand(brandCode);
		brandsTab.clickRemoveBrandButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}


	/**
	 * Verify brand deleted.
	 */
	@Then("^The brand is deleted$")
	public void verifyBrandDeleted() {
		brandsTab.verifyBrandDelete(this.catalog.getBrand());
	}

	/**
	 * Verify catalog exists.
	 */
	@Then("^newly (?:created|edited) (?:virtual catalog|catalog) is in the list$")
	public void verifyCatalogExists() {
		catalogManagement.clickCatalogRefreshButton();
		catalogManagement.verifyCatalogExists(this.catalog.getCatalogName());
	}

	/**
	 * Create virtual catalog with language.
	 *
	 * @param catalogInfoList catalog details.
	 */
	@And("^(?:I create a|a) new virtual catalog with following details$")
	public void createVirtualCatalogWithLanguage(final List<Catalog> catalogInfoList) {
		activityToolbar.clickCatalogManagementButton();
		for (Catalog catalog : catalogInfoList) {
			String virtualCatalogCode = Utility.getRandomUUID();
			this.catalog.setCatalogName(catalog.getCatalogName() + "-" + virtualCatalogCode);
			this.catalog.setLanguage(catalog.getLanguage());
			createEditVirtualCatalogDialog = catalogManagementActionToolbar.clickCreateVirtualCatalogButton();
			createEditVirtualCatalogDialog.enterCatalogCode(virtualCatalogCode);
			createEditVirtualCatalogDialog.enterCatalogName(this.catalog.getCatalogName());
			createEditVirtualCatalogDialog.selectDefaultLanguage(this.catalog.getLanguage());
			createEditVirtualCatalogDialog.clickSaveButton();
		}
	}

	/**
	 * select new catalog.
	 */
	@And("^I select newly created (?:virtual catalog|catalog) in the list$")
	public void selectNewCatalog() {
		catalogManagement.selectCatalog(this.catalog.getCatalogName());
	}

	/**
	 * select existing catalog.
	 *
	 * @param catalogName the catalog name.
	 */
	@And("^I select catalog (.+) in the list$")
	public void selectExistingCatalog(final String catalogName) {
		catalogManagement.selectCatalog(catalogName);
	}

	/**
	 * select and delete existing catalog.
	 *
	 * @param catalogName the catalog name.
	 */
	@And("^I delete selected catalog (.+) in the list$")
	public void selectDeleteExistingCatalog(final String catalogName) {
		activityToolbar.clickCatalogManagementButton();
		catalogManagement.selectCatalog(catalogName);
		catalogManagement.rightClickDelete();
	}

	/**
	 * Delete newly created catalog.
	 */
	@And("^I delete newly created (?:virtual catalog|catalog)$")
	public void deleteNewlyCreatedCatalog() {
		selectNewCatalog();
		catalogManagement.rightClickDelete().clickOK();
		catalogManagement.clickCatalogRefreshButton();
	}

	/**
	 * Verify newly created catalog is deleted.
	 */
	@And("^newly created (?:virtual catalog|catalog) is deleted$")
	public void verifyNewlyCreatedCatalogIsDeleted() {
		catalogManagement.verifyCatalogIsDeleted(this.catalog.getCatalogName());
	}

	/**
	 * Clicks next button in create category wizard.
	 */
	private void clickNextButtonCreateCategory() {
		createCategoryWizard.clickNextInDialog();
	}

	/**
	 * Select category.
	 *
	 * @param catalogName  the catalog name.
	 * @param categoryName the category name.
	 */
	@And("^I select (.+) category in (.+) catalog$")
	public void selectCategory(final String catalogName, final String categoryName) {
		catalogManagement.selectCategoryInCatalog(catalogName, categoryName);
	}

	/**
	 * Verify Category exists.
	 */
	@Then("^the newly created category exists$")
	public void verifyCategoryExists() {
		catalogManagement.verifyCategoryExists(this.category.getCategoryName());
	}

	/**
	 * Verify Linked Category exists.
	 *
	 * @param linkedCategory the linked category
	 * @param catalog        the catalog
	 */
	@And("^the linked category (.+) (?:should be|is) added to catalog (.+)$")
	public void verifyLinkedCategoryExists(final String linkedCategory, final String catalog) {
		catalogManagement.expandCatalogAndVerifyCategory(catalog, linkedCategory);
		catalogManagement.verifyCategoryExists(linkedCategory);
		//cleanup category so the test can pass the next time we run it
		deleteLinkedCategory();
	}

	/**
	 * Select new category.
	 */
	@And("^I select newly created category$")
	public void selectNewCategory() {
		catalogManagement.selectCategory(this.category.getCategoryName());
	}

	/**
	 * Select new linked category.
	 */
	@And("^I select newly created linkedcategory$")
	public void selectNewLinkedcategory() {
		catalogManagement.selectCategory(this.linkedCategory.getLinkedCategoryName());
	}

	/**
	 * Delete new category.
	 */
	@And("^I delete newly created category")
	public void deleteNewCategory() {
//		Need to select category again before right click delete.
		selectNewCategory();
		catalogManagement.clickDeleteCategoryIcon();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("CatalogMessages.CatalogBrowseView_Action_DeleteCategoryDialog");
	}

	/**
	 * Delete new category.
	 */
	@And("^I delete newly created linked category")
	public void deleteNewLinkedCategory() {
		selectNewLinkedcategory();
		deleteLinkedCategory();
	}

	/**
	 * Helper method for deleting already selected linked category.
	 */
	public void deleteLinkedCategory() {
		catalogManagement.clickRemoveLinkedCategoryIcon();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("CatalogMessages.CatalogBrowseView_Action_RemoveLinkedCatDialogTitle");
	}

	/**
	 * Verify newly created category is deleted.
	 */
	@And("^newly created category is deleted$")
	public void verifyNewlyCreatedCategoryIsDeleted() {
		catalogManagement.verifyCategoryIsNotInList(this.category.getCategoryName());
	}


	/**
	 * Create new category for existing catelog.
	 *
	 * @param catalog          the catelog.
	 * @param categoryInfoList the category info list.
	 */
	@When("^I create new category for (.+) with following data")
	public void createNewCategoryforExistingCatalog(final String catalog, final List<Category> categoryInfoList) {
		for (Category category : categoryInfoList) {
			selectExistingCatalog(catalog);
			createCategoryWizard = catalogManagement.clickCreateCategoryIcon();
			this.category.setCategoryCode(Utility.getRandomUUID());
			createCategoryWizard.enterCategoryCode(this.category.getCategoryCode());
			this.category.setCategoryName(category.getCategoryName() + " - " + this.category.getCategoryCode());
			createCategoryWizard.enterCategoryName(this.category.getCategoryName());
			this.category.setCategoryType(category.getCategoryType());
			createCategoryWizard.selectCategoryType(this.category.getCategoryType());
			createCategoryWizard.enterCurrentEnableDateTime();
			if (category.getStoreVisible().equalsIgnoreCase("true")) {
				createCategoryWizard.checkStoreVisibleBox();
			}
			clickNextButtonCreateCategory();
			createCategoryWizard.enterAttributeLongText(category.getAttrLongTextValue(), category.getAttrLongTextName());
			createCategoryWizard.enterAttributeDecimalValue(category.getAttrDecimalValue(), category.getAttrDecimalName());
			createCategoryWizard.enterAttributeShortText(category.getAttrShortTextValue(), category.getAttrShortTextName());
			createCategoryWizard.clickFinish();
		}
	}

	/**
	 * Create new linked category for existing catelog.
	 *
	 * @param catalog                the catelog.
	 * @param linkedCategoryInfoList the linked category info list.
	 */
	@When("^I add new linked category to (.+) with following data")
	public void createNewLinkedCategoryforExistingCatalog(final String catalog, final List<String> linkedCategoryInfoList) {
		selectExistingCatalog(catalog);
		categoryFinderDialog = catalogManagement.clickAddLinkedCategoryIcon();
		categoryFinderDialog.selectCatalog(linkedCategoryInfoList.get(0));
		categoryFinderDialog.enterCategoryName(linkedCategoryInfoList.get(1));
		categoryFinderDialog.clickSearchButton();
		categoryFinderDialog.selectCategory(linkedCategoryInfoList.get(1));
		categoryFinderDialog.clickOK();
	}

	/**
	 * Create new linked category for existing catelog.
	 *
	 * @param catalog                the catelog.
	 * @param linkedCategoryInfoList the linked category info list.
	 */
	@When("^I add new linked category with changeset to (.+) with following data")
	public void createNewLinkedCategoryChangeset(final String catalog, final List<String> linkedCategoryInfoList) {
		selectExistingCatalog(catalog);
		changeSetActionToolbar.clickAddItemToChangeSet();
		this.linkedCategory.setCatalog(catalog);
		selectExistingCatalog(catalog);
		categoryFinderDialog = catalogManagement.clickAddLinkedCategoryIcon();
		this.linkedCategory.setMasterCatalog(linkedCategoryInfoList.get(0));
		categoryFinderDialog.selectCatalog(this.linkedCategory.getMasterCatalog());
		this.linkedCategory.setLinkedCategoryName(linkedCategoryInfoList.get(1));
		categoryFinderDialog.enterCategoryName(this.linkedCategory.getLinkedCategoryName());
		categoryFinderDialog.clickSearchButton();
		categoryFinderDialog.selectCategory(this.linkedCategory.getLinkedCategoryName());
		categoryFinderDialog.clickOK();
		selectCategory(catalog, this.linkedCategory.getLinkedCategoryName());
		changeSetActionToolbar.clickAddItemToChangeSet();
	}

	/**
	 * Create enw global attribute.
	 *
	 * @param globalAttributeName the global attribute name.
	 * @param usage               the usage.
	 * @param type                the type.
	 * @param required            required flag.
	 */
	@When("^I create a new global attribute with name (.+) for (.+) of type (.+) with required (.+)$")
	public void createNewGlobalAttribute(final String globalAttributeName, final String usage, final String type, final boolean required) {
		this.globalAttributeName = globalAttributeName + "_" + Utility.getRandomUUID();
		editGlobalAttributesDialog = catalogManagementActionToolbar.clickEditGlobalAttributesButton();
		addAttributeDialog = editGlobalAttributesDialog.clickAddAttributeButton();
		addAttributeDialog.enterAttributeKey("GA_" + Utility.getRandomUUID());
		addAttributeDialog.enterAttributeName(this.globalAttributeName);
		addAttributeDialog.selectAttributeUsage(usage);
		addAttributeDialog.selectAttributeType(type);

		if (required) {
			addAttributeDialog.clickCheckBox("Required Attribute");
		}

		addAttributeDialog.clickAddButton();
		editGlobalAttributesDialog.clickSaveButton();
	}

	/**
	 * verify newly created global attribute exists.
	 */
	@Then("^newly (?:created|edited) global attribute is in the list$")
	public void verifyNewlyCreatedGlobalAttributeExists() {
		editGlobalAttributesDialog = catalogManagementActionToolbar.clickEditGlobalAttributesButton();
		editGlobalAttributesDialog.verifyGlobalAttributeValue(this.globalAttributeName);
	}

	/**
	 * verify newly created global attribute exists.
	 */
	@When("^I edit newly created global attribute name to (.+)$")
	public void editCreatedGlobalAttribute(final String newGlobalAttributeName) {
		editGlobalAttributesDialog.selectGlobalAttributeRow(this.globalAttributeName);
		editAttributeDialog = editGlobalAttributesDialog.clickEditAttributeButton();
		this.globalAttributeName = newGlobalAttributeName + " " + Utility.getRandomUUID();
		editAttributeDialog.enterAttributeName(this.globalAttributeName);
		editAttributeDialog.clickOKButton();
		editGlobalAttributesDialog.clickSaveButton();
	}

	/**
	 * Select new global attribute.
	 */
	@And("^I select newly (?:created|edited) global attribute in the list$")
	public void selectNewGlobalAttribute() {
		editGlobalAttributesDialog.selectGlobalAttributeRow(this.globalAttributeName);
	}

	/**
	 * Delete new global attribute.
	 */
	@And("^I delete newly created global attribute$")
	public void deleteNewGlobalAttribute() {
		editGlobalAttributesDialog.deleteGlobalAttribute();
		editGlobalAttributesDialog.clickSaveButton();
	}

	/**
	 * Verify newly created global attribute is deleted.
	 */
	@And("^I verify newly created global attribute is deleted$")
	public void verifyNewlyCreatedGlobalAttributeIsDeleted() {
		editGlobalAttributesDialog = catalogManagementActionToolbar.clickEditGlobalAttributesButton();
		editGlobalAttributesDialog.verifyGlobalAttributeValueIsNotInList(this.globalAttributeName);
	}

	/**
	 * Searches for a product by name.
	 *
	 * @param productName product name.
	 */
	private void searchForProductByName(final String productName) {
		catalogManagement.clickCatalogSearchTab();
		catalogManagement.enterProductName(productName);
		catalogSearchResultPane = catalogManagement.clickCatalogSearch();
	}

	/**
	 * Verify create catalog button is present.
	 */
	@And("^I can view Create Catalog button")
	public void verifyCreateCatalogButtonIsPresent() {
		catalogManagementActionToolbar.verifyCreateCatalogButtonIsPresent();
	}

	/**
	 * Open newly created catalog.
	 */
	@And("^I open the newly created catalog editor")
	public void openNewCatalogEditor() {
		selectNewCatalog();
		catalogEditor = catalogManagement.clickOpenCatalogCategoryButton();
	}


	/**
	 * Open selected catalog.
	 */
	@And("^I open the selected catalog")
	public void openSelectedCatalog() {
		catalogEditor = catalogManagement.clickOpenCatalogCategoryButton();
	}

	/**
	 * Select order editor tab.
	 *
	 * @param tabName the tab name.
	 */
	@When("^I select (.+) tab in the Catalog Editor$")
	public void selectCatalogEditorTab(final String tabName) {
		openNewCatalogEditor();
		if (catalogEditor == null) {
			catalogEditor = new CatalogEditor(driver);
		}
		catalogEditor.selectTab(tabName);
	}

	/**
	 * Verify catalog attribute.
	 *
	 * @param attributeValueList the attribute value list.
	 */
	@And("^it should have following catalog attributes?$")
	public void verifyCatalogAttribute(final List<String> attributeValueList) {
		for (String attributeValue : attributeValueList) {
			catalogEditor.verifyCatalogAttributeValue(attributeValue);
		}
	}

	/**
	 * verify newly created catalog attribute exists.
	 */
	@Then("^(?:newly created|edited) catalog attribute is in the list$")
	public void verifyNewlyCreatedCatalogAttributeExists() {
		catalogEditor.verifyCatalogAttributeValue(this.catalog.getAttributeName());
	}

	/**
	 * Create new catalog attribute.
	 *
	 * @param catalogInfoList Catalog attribute details.
	 */
	@When("^I create a new catalog attribute with following details$")
	public void createNewCatalogAttribute(final List<Catalog> catalogInfoList) {
		for (Catalog catalog : catalogInfoList) {
			this.catalog.setAttributeName(catalog.getAttributeName() + "_" + Utility.getRandomUUID());
			this.catalog.setAttributeUsage(catalog.getAttributeUsage());
			this.catalog.setAttributeType(catalog.getAttributeType());
			this.catalog.setAttributeRequired(catalog.isAttributeRequired());

			addAttributeDialog = catalogEditor.clickAddAttributeButton();

			addAttributeDialog.enterAttributeKey("CA_" + Utility.getRandomUUID());
			addAttributeDialog.enterAttributeName(this.catalog.getAttributeName());
			addAttributeDialog.selectAttributeUsage(this.catalog.getAttributeUsage());
			addAttributeDialog.selectAttributeType(this.catalog.getAttributeType());

			if (this.catalog.isAttributeRequired()) {
				addAttributeDialog.clickCheckBox("Required Attribute");
			}

			addAttributeDialog.clickAddButton();
			catalogManagementActionToolbar.saveAll();
		}
	}

	/**
	 * Delete new catalog.
	 */
	@When("^I close the editor and try to delete the newly created catalog$")
	public void deleteNewCatalogError() {
		catalogManagement.closePane(this.catalog.getCatalogName());
		selectNewCatalog();
		catalogManagement.rightClickDelete();
	}

	/**
	 * Delete new catalog.
	 */
	@When("^I close the editor and delete the newly created catalog$")
	public void deleteNewCatalog() {
		catalogManagement.closePane(this.catalog.getCatalogName());
		selectNewCatalog();
		catalogManagement.rightClickDelete().clickOK();
	}

	/**
	 * Delete new catalog attribute.
	 */
	@When("^I delete the newly created catalog attribute$")
	public void deleteNewCatalogAttribute() {
		catalogEditor.selectTab("Attributes");
		catalogEditor.selectCatalogAttributeValue(this.catalog.getAttributeName());
		catalogEditor.clickRemoveAttributeButton();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("CatalogMessages.CatalogAttributesSection_RemoveDialog");
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify newly created catalog attribute is deleted.
	 */
	@Then("^the newly created catalog attribute is deleted$")
	public void verifyNewAttributeDelete() {
		catalogEditor.verifyCatalogAttributeDelete(this.catalog.getAttributeName());
	}

	/**
	 * Edit catalog attribute name.
	 */
	@When("^I edit the catalog attribute name$")
	public void editCatalogAttributeName() {
		catalogEditor.selectCatalogAttributeValue(this.catalog.getAttributeName());
		this.catalog.setAttributeName("Edit Prod Desc" + "_" + Utility.getRandomUUID());
		editAttributeDialog = catalogEditor.clickEditAttributeButton();
		editAttributeDialog.enterAttributeName(this.catalog.getAttributeName());
		editAttributeDialog.clickOKButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Edit virtual catalog name.
	 */
	@When("^I edit the virtual catalog name to a different name$")
	public void editVirtualCatalogName() {
		createEditVirtualCatalogDialog = catalogManagement.clickOpenVirtualCatalogButton();
		String virtualCatalogCode = Utility.getRandomUUID();
		this.catalog.setCatalogName(catalog.getCatalogName() + "-" + virtualCatalogCode);
		createEditVirtualCatalogDialog.enterCatalogName(this.catalog.getCatalogName());
		createEditVirtualCatalogDialog.clickSaveButton();
	}

	/**
	 * Verify virtual catalog tab present in product tabs
	 *
	 * @param product  Product Name.
	 * @param category Category Name.
	 * @param catalog  Catalog Name.
	 */
	@And("^there is an existing product (.+) in category (.+) in virtual catalog (.+)$")
	public void verifyProductExistsInVirtualCatalog(final String product, final String category, final String catalog) {
		virtualProductName = product;
		virtualCategoryName = category;
		virtualCatalogName = catalog;
		activityToolbar.clickCatalogManagementButton();
		catalogManagement.expandCatalogAndVerifyCategory(virtualCatalogName, virtualCategoryName);
		catalogProductListingPane = catalogManagement.doubleClickCategory(virtualCategoryName);
		catalogProductListingPane.verifyProductNameExists(virtualProductName);
	}

	/**
	 * Clicks Exclude Product button.
	 */
	@When("^I exclude the product from the virtual catalog$")
	public void clickExcludeProduct() {
		catalogProductListingPane.clickExcludeProductButton();
	}

	/**
	 * @param catalogName the catalog name.
	 */
	@Then("^the product does not have (.+) in category assignment and merchandising associations$")
	public void verifyVirtualCatalogNotInProductTabs(final String catalogName) {
		productEditor = catalogProductListingPane.selectProductAndOpenProductEditor();
		productEditor.selectTab("CategoryAssignment");
		productEditor.verifyCatalogTabIsNotPresent(catalogName);
		productEditor.selectTab("MerchandisingAssociation");
		productEditor.verifyCatalogTabIsNotPresent(catalogName);
	}

	/**
	 * Clicks Include Product button.
	 */
	@When("^I include the product in the virtual catalog$")
	public void clickProductState() {
		catalogProductListingPane.clickIncludeProductButton();
	}

	/**
	 * Verify virtual catalog tab is not present in product tabs
	 *
	 * @param catalogName the catalog name.
	 */
	@Then("^the product has (.+) in category assignment and merchandising associations$")
	public void verifyVirtualCatalogInProductTabs(final String catalogName) {
		productEditor.selectTab("CategoryAssignment");
		catalogManagementActionToolbar.clickReloadActiveEditor();
		productEditor.verifyCatalogTabIsPresent(catalogName);
		productEditor.selectTab("MerchandisingAssociation");
		productEditor.verifyCatalogTabIsPresent(catalogName);
	}

	/**
	 * Deletes new catalog.
	 */
	@After(value = "@cleanupCatalog", order = Constants.CLEANUP_ORDER_THIRD)
	public void cleanupCatalog() {
		NavigationDefinition navigationDefinition = new NavigationDefinition();
		navigationDefinition.clickCatalogManagement();
		catalogManagement.clickCatalogBrowseTab();
		if (catalogEditor == null) {
			selectNewCatalog();
			deleteNewlyCreatedCatalog();
		} else {

			if (this.catalog.getBrand() != null) {
				selectNewCatalog();
				openSelectedCatalog();
				deleteNewBrand();
				verifyBrandDeleted();
			}
			catalogEditor.selectCatalogEditor(this.catalog.getCatalogName());
			catalogManagement.closePane(this.catalog.getCatalogName());
			activityToolbar.clickCatalogManagementButton();
			catalogManagement.clickCatalogRefreshButton();
			deleteNewlyCreatedCatalog();
		}
	}

	/**
	 * Deletes new product.
	 */
	@After(value = "@cleanupProduct", order = Constants.CLEANUP_ORDER_FIRST)
	public void cleanupProduct() {
		this.productAndBundleDefinition.deleteNewlyCreatedProduct();
		this.productAndBundleDefinition.verifyProductIsDeleted();
		catalogManagement.clickCatalogBrowseTab();
		if (this.productType.getProductTypeName() != null) {
			selectNewCatalog();
			openSelectedCatalog();
			this.productTypesTabDefinition.deleteNewProductType();
			this.productTypesTabDefinition.verifyNewProductTypeDelete();
		}
		if (this.cartItemModifierGroup.getGrouopName() != null) {
			selectNewCatalog();
			openSelectedCatalog();
			this.cartItemModifierGroupTabDefinition.deleteNewGroup();
			this.cartItemModifierGroupTabDefinition.verifyNewGroupDelete();
		}
	}

	/**
	 * Delete new category and category type.
	 */
	@After(value = "@cleanUpCategory", order = Constants.CLEANUP_ORDER_SECOND)
	public void deleteNewlyCreatedCategory() {
//		Need to select category again before right click delete.
		catalogManagement.clickCatalogBrowseTab();
		catalogManagement.selectCategory(this.category.getCategoryName());
		catalogManagement.clickDeleteCategoryIcon();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("CatalogMessages.CatalogBrowseView_Action_DeleteCategoryDialog");
		if (this.catalog.getCatalogName() != null) {
			this.categoryTypesTabDefinition.deleteNewCategoryType();
			this.categoryTypesTabDefinition.verifyNewCategoryTypeDelete();
		}
	}

	/**
	 * Resets the virtual catalog product to its default state.
	 */
	@After(value = "@resetVirtualCatalogProduct", order = Constants.CLEANUP_ORDER_FIRST)
	public void resetVirtualCatalogProduct() {
		catalogProductListingPane.verifyProductNameExists(this.virtualProductName);
		catalogProductListingPane.clickIncludeProductButton();
	}

	@After(value = "@cleanupVirtualCatalog", order = Constants.CLEANUP_ORDER_FIRST)
	public void cleanupVritualCatalog() {
		deleteNewlyCreatedCatalog();
		verifyNewlyCreatedCatalogIsDeleted();
	}

	/**
	 * Verifies brand name exists.
	 *
	 * @param brandName String
	 */
	@Then("^the brand (.+) is displayed in the brands table$")
	public void verifyBrandNameExists(final String brandName) {
		catalogEditor.selectBrandsTab();
		brandsTab.verifyAndSelectBrandByName(this.catalog.getBrand());
	}

	/**
	 * Edit the brand name for a given brand code.
	 *
	 * @param newBrandName New brand name.
	 */
	@And("^I edit brand name to (.+) for the newly added brand$")
	public void editBrandNameForCode(final String newBrandName) {
		brandsTab.verifyAndSelectBrand(this.catalog.getBrand());
		addEditBrandDialog = brandsTab.clickEditBrandButton();
		addEditBrandDialog.enterBrandName(newBrandName);
		this.catalog.setBrand(newBrandName);
		addEditBrandDialog.clickAddButton();
		saveChanges();
	}


	/**
	 * Deletes a given brand.
	 *
	 * @param brandName String
	 */
	@And("^I delete brand (.+)$")
	public void deleteBrand(final String brandName) {
		deleteBrandHelper(brandName);
		this.catalog.setBrand(null);
		saveChanges();
	}

	/**
	 * Attempt to delete an existing brand.
	 *
	 * @param brandName String
	 */
	@And("^I attempt to delete an existing brand (.+) used by product$")
	public void deleteExistingBrand(final String brandName) {
		deleteBrandHelper(brandName);
	}

	/**
	 * Verifies error message is displayed.
	 *
	 * @param errorMessage String
	 */
	@Then("^an error message of (.+) is displayed$")
	public void verifyErrorMessageDisplayed(final String errorMessage) {
		brandsTab.verifyErrorMessageDisplayed(errorMessage);
	}

	/**
	 * Adds a brand with supplied name.
	 *
	 * @param brandName String
	 */
	@When("^I add a brand (.+)$")
	public void addBrand(final String brandName) {
		catalogEditor.selectBrandsTab();
		addEditBrandDialog = brandsTab.clickAddBrandButton();

		String brandUuid = Utility.getRandomUUID();
		this.catalog.setBrand(brandName);
		addEditBrandDialog.enterBrandName(this.catalog.getBrand());
		addEditBrandDialog.enterBrandCode(this.catalog.getBrand() + brandUuid);
		addEditBrandDialog.clickAddButtonNoWait();
		saveChanges();
	}

	/**
	 * Adds an existing brand.
	 *
	 * @param brandName String
	 */
	@When("^I add an existing brand (.+)$")
	public void addExistingBrand(final String brandName) {
		catalogEditor.selectBrandsTab();
		addEditBrandDialog = brandsTab.clickAddBrandButton();
		addEditBrandDialog.enterBrandName(brandName);
		addEditBrandDialog.enterBrandCode(brandName);
		addEditBrandDialog.clickAddButtonNoWait();
	}

	/**
	 * Verifies error message is displayed in the add/edit dialog.
	 *
	 * @param errorMessage String
	 */
	@Then("^an error message of (.+) is displayed in the add dialog$")
	public void verifyErrorMessageDisplayedInAddDialog(final String errorMessage) {
		addEditBrandDialog.verifyErrorMessageDisplayedInAddEditDialog(errorMessage);
	}

	/**
	 * Saves changes in catalog editor.
	 */
	@When("^I save my changes$")
	public void saveChanges() {
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify named brand was deleted.
	 *
	 * @param brandName String
	 */
	@Then("^The brand (.+) is deleted$")
	public void verifyBrandNameDeleted(final String brandName) {
		brandsTab.verifyBrandDelete(brandName);
	}

	/**
	 * Double clicks the category and adds all category items to a change set.
	 *
	 * @param categoryList list of category names
	 */
	@And("^I add all products from the following (?:category|categories) to a changeset$")
	public void addAllCategoryItemsToAChangeSet(final List<String> categoryList) {
		CatalogProductListingPane.setNumberOfCategoryItems(0);
		for (String category : categoryList) {
			catalogProductListingPane = catalogManagement.doubleClickCategory(category);
			catalogProductListingPane.addAllCategoryItemsToAChangeSet();
		}
	}

	/**
	 * Delete brand method.
	 *
	 * @param brandName String
	 */
	private void deleteBrandHelper(final String brandName) {
		catalogEditor.selectBrandsTab();
		brandsTab.verifyAndSelectBrandByName(brandName);
		brandsTab.clickRemoveBrandButton();
	}
}