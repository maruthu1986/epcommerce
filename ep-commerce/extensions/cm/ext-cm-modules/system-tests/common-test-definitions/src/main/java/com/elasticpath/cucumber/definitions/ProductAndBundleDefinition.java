package com.elasticpath.cucumber.definitions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.List;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.AddEditMerchandisingAssociationsDialog;
import com.elasticpath.selenium.dialogs.AddItemDialog;
import com.elasticpath.selenium.dialogs.AddPriceTierDialog;
import com.elasticpath.selenium.dialogs.BasePriceEditorDialog;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.EditDecimalValueAttributeDialog;
import com.elasticpath.selenium.dialogs.SelectACategoryDialog;
import com.elasticpath.selenium.dialogs.SelectAProductDialog;
import com.elasticpath.selenium.dialogs.SelectASkuDialog;
import com.elasticpath.selenium.domainobjects.Catalog;
import com.elasticpath.selenium.domainobjects.Category;
import com.elasticpath.selenium.domainobjects.DST;
import com.elasticpath.selenium.domainobjects.Product;
import com.elasticpath.selenium.domainobjects.ProductType;
import com.elasticpath.selenium.editor.SkuDetailsEditor;
import com.elasticpath.selenium.editor.product.ProductEditor;
import com.elasticpath.selenium.editor.product.tabs.AttributesTab;
import com.elasticpath.selenium.editor.product.tabs.BundleItemsTab;
import com.elasticpath.selenium.editor.product.tabs.MerchandisingTab;
import com.elasticpath.selenium.editor.product.tabs.PricingTab;
import com.elasticpath.selenium.navigations.CatalogManagement;
import com.elasticpath.selenium.resultspane.CatalogProductListingPane;
import com.elasticpath.selenium.resultspane.CatalogSearchResultPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ActivityToolbar;
import com.elasticpath.selenium.toolbars.CatalogManagementActionToolbar;
import com.elasticpath.selenium.toolbars.ChangeSetActionToolbar;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.selenium.util.Utility;
import com.elasticpath.selenium.wizards.AddSkuWizard;
import com.elasticpath.selenium.wizards.CreateBundleWizard;
import com.elasticpath.selenium.wizards.CreateProductWizard;

/**
 * Product Definition.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods", "PMD.TooManyFields"})
public class ProductAndBundleDefinition {
	private final CatalogManagement catalogManagement;
	private CreateBundleWizard createBundleWizard;
	private AddItemDialog addItemDialog;
	private AddPriceTierDialog addPriceTierDialog;
	private AddEditMerchandisingAssociationsDialog addEditMerchandisingAssociationsDialog;
	private AddSkuWizard addSkuWizard;
	private SkuDetailsEditor skuDetailsEditor;
	private SelectAProductDialog selectAProductDialog;
	private SelectACategoryDialog selectACategoryDialog;
	private SelectASkuDialog selectASkuDialog;
	private CatalogSearchResultPane catalogSearchResultPane;
	private CreateProductWizard createProductWizard;
	private BasePriceEditorDialog basePriceEditorDialog;
	private EditDecimalValueAttributeDialog editDecimalValueAttributeDialog;
	private final ActivityToolbar activityToolbar;
	private final BundleItemsTab bundleItemsTab;
	private final PricingTab priceTierTab;
	private final MerchandisingTab merchandisingTab;
	private final AttributesTab attributesTab;
	private final Product product;
	private final Catalog catalog;
	private final Category category;
	private final ProductType productType;
	private final DST dst;
	private static String uniqueSkuCode = "";
	private final CatalogManagementActionToolbar catalogManagementActionToolbar;
	private CatalogProductListingPane catalogProductListingPane;
	private ProductEditor productEditor;
	private final ChangeSetActionToolbar changeSetActionToolbar;

	/**
	 * Constructor.
	 *
	 * @param catalog     Catalog object.
	 * @param category    Category object.
	 * @param productType ProductType object.
	 * @param product     Product object.
	 * @param dst         DST object.
	 */
	public ProductAndBundleDefinition(final Catalog catalog, final Category category, final ProductType productType, final Product product,
									  final DST dst) {
		final WebDriver driver = SetUp.getDriver();
		catalogManagement = new CatalogManagement(driver);
		bundleItemsTab = new BundleItemsTab(driver);
		addPriceTierDialog = new AddPriceTierDialog(driver);
		priceTierTab = new PricingTab(driver);
		merchandisingTab = new MerchandisingTab(driver);
		addEditMerchandisingAssociationsDialog = new AddEditMerchandisingAssociationsDialog(driver);
		attributesTab = new AttributesTab(driver);
		activityToolbar = new ActivityToolbar(driver);
		editDecimalValueAttributeDialog = new EditDecimalValueAttributeDialog(driver);
		changeSetActionToolbar = new ChangeSetActionToolbar(driver);
		this.catalog = catalog;
		this.category = category;
		this.productType = productType;
		this.product = product;
		this.catalogManagementActionToolbar = new CatalogManagementActionToolbar(driver);
		this.dst = dst;
	}

	/**
	 * Create new product for existing category.
	 *
	 * @param productInfoList the product info list.
	 */
	@When("^(?:I create|a) new product with following attributes$")
	public void createNewProduct(final List<Product> productInfoList) {
		Product product = productInfoList.get(0);
		if (product.getCatalog() == null || product.getCategory() == null) {
			catalogManagement.expandCatalogAndVerifyCategory(this.catalog.getCatalogName(), this.category.getCategoryName());
			catalogManagement.doubleClickCategory(this.category.getCategoryName());
		} else {
			catalogManagement.expandCatalogAndVerifyCategory(product.getCatalog(), product.getCategory());
			catalogManagement.doubleClickCategory(product.getCategory());
		}
		createProductWizard = catalogManagement.clickCreateProductButton();
		String prodCode = Utility.getRandomUUID();
		this.product.setProductCode(prodCode);
		String prodName = product.getProductName() + "-" + prodCode;
		this.product.setProductName(prodName);
		if (dst != null) {
			dst.setProductCode(prodCode);
			dst.setProductName(prodName);
		}
		createProductWizard.enterProductCode(prodCode);
		createProductWizard.enterProductName(this.product.getProductName());

		selectProductType(product);

		createProductWizard.selectTaxCode(product.getTaxCode());

		selectStoreVisiblity(product);

		createProductWizard.selectAvailabilityRule(product.getAvailability());
		createProductWizard.clickNextInDialog();

		enterProductAttribute(product);

		createProductWizard.clickNextInDialog();

		if (createProductWizard.isAddSKUButtonPresent()) {
			addMultiSkuValues(product);

		} else {
			createProductWizard.enterSkuCode(getUniqueSkuCode());
			createProductWizard.selectShippableType(product.getShippableType());
		}

		addPrice(product);
		createProductWizard.clickFinish();
	}

	/**
	 * Verifies newly created product exists.
	 */
	@Then("^the newly created product is in the list$")
	public void verifyNewlyCreatedProductExists() {
		searchForProductByName(this.product.getProductName());
	}

	/**
	 * Deletes newly created product.
	 */
	@When("^I delete the newly created (?:product|bundle)$")
	public void deleteNewlyCreatedProduct() {
		verifyProductByName(this.product.getProductName());
		if (activityToolbar.isChangeSetEnabled()) {
			changeSetActionToolbar.clickAddItemToChangeSet();
			catalogSearchResultPane.isProductInList(this.product.getProductName());
		}
		catalogSearchResultPane.clickDeleteProductButton();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("CatalogMessages.DeleteProduct");
	}

	/**
	 * Verifies product exists with the name productName.
	 *
	 * @param productName product name.
	 */
	public void verifyProductByName(final String productName) {
		searchForProductByName(productName);
	}


	/**
	 * Verify product is deleted.
	 */
	@Then("^the (?:product|bundle) is deleted$")
	public void verifyProductIsDeleted() {
		searchNewlyCreatedProductByName(this.product.getProductName());
		catalogSearchResultPane.verifyProductIsDeleted(this.product.getProductName());
	}

	/**
	 * Delete Newly Created product.
	 */
	@Then("^the newly created bundle exists and contains the added items$")
	public void verifyNewlyCreatedBundle() {
		verifyProductByName(this.product.getProductName());
		catalogSearchResultPane.doubleClick(catalogSearchResultPane.getSelectedElement(), String.format(AbstractPageObject
				.EDITOR_PANE_TAB_CSS_TEMPLATE, this.product.getProductCode()));
		bundleItemsTab.selectTab("Constituents");

		verifyBundleProductCodesListExistInTestData();
		for (String productCode : this.product.getBundleProductSKUList()) {
			bundleItemsTab.verifyBundleItemExists(productCode);

		}
	}

	/**
	 * Delete Newly Created product.
	 *
	 * @param productCode product code
	 */
	@When("^I delete item with product code (.+) from the bundle$")
	public void deleteBundleItem(final String productCode) {
		bundleItemsTab.removeBundleItem(productCode);
	}

	/**
	 * verifies bundle item deleted.
	 *
	 * @param productCode product code
	 */
	@Then("^the bundle item with product code (.+) is deleted$")
	public void verifyBundleItemDeleted(final String productCode) {
		bundleItemsTab.verifyBundleItemDeleted(productCode);
	}

	/**
	 * Delete Newly Created product.
	 *
	 * @param productCode product code
	 * @param quantity    quantity
	 */
	@And("^I update bundle item with product code (.+) quantity to (.+)$")
	public void editBundleItemQuantity(final String productCode, final String quantity) {
		bundleItemsTab.editBundleItemQuantityAndVerify(productCode, quantity);
	}

	/**
	 * Add bundle item.
	 *
	 * @param productCode product code
	 */
	@And("^I add new Item with product code (.+) to the bundle$")
	public void addBundleItemFromTab(final String productCode) {
		bundleItemsTab.clickAddBundleItemButton();
		addBundleItem(productCode);
	}

	/**
	 * Verifies bundle item exists.
	 *
	 * @param productCode prduct code
	 */
	@Then("^the bundle item with product code (.+) exists in the bundle$")
	public void verifyBundleItemExists(final String productCode) {
		bundleItemsTab.verifyBundleItemExists(productCode);
	}

	/**
	 * Verifies bundle item quantity.
	 *
	 * @param productCode product code
	 * @param quantity    quantity
	 */
	@Then("^the bundle item with product code (.+) has quantity of (.+)$")
	public void verifyBundleItemQuantity(final String productCode, final String quantity) {
		bundleItemsTab.verifyBundleItemQuantity(productCode, quantity);
	}

	/**
	 * Verify bundle selection rule parameter.
	 *
	 * @param expSelectionParameter Expected selection rule parameter.
	 */
	@Then("^the bundle selection rule parameter is where n is (.+)$")
	public void verifyBundleSelectionRule(final String expSelectionParameter) {
		bundleItemsTab.verifyBundleSelectionParameter(expSelectionParameter);
	}

	/**
	 * Verify bundle selection rule.
	 *
	 * @param expSelectionRule expected selection rule.
	 */
	@Then("the bundle selection rule is (.+)$")
	public void verifyBundleSelectionRuleOnly(final String expSelectionRule) {
		bundleItemsTab.verifyBundleSelectionRule(expSelectionRule);
	}

	/**
	 * Edit  bundle Selection rule and parameter.
	 *
	 * @param selectionRule      New selection rule.
	 * @param selectionParameter New selection parameter
	 */
	@When("^I edit the selection rule to be (.+) where n is (.+)$")
	public void editBundleSelectionRule(final String selectionRule, final String selectionParameter) {
		bundleItemsTab.editSelectionRule(selectionRule);
		bundleItemsTab.editSelectionParameter(selectionParameter);
		activityToolbar.saveAll();
	}

	/**
	 * Create new bundle with selection rule for existing category.
	 *
	 * @param productInfoList the product info list.
	 */
	@When("^I create a new (?:dynamic bundle|bundle) with following attributes$")
	public void createNewBundleForExistingCategory(final List<Product> productInfoList) {
		Product product = productInfoList.get(0);
		catalogManagement.expandCatalogAndVerifyCategory(product.getCatalog(), product.getCategory());
		catalogManagement.doubleClickCategory(product.getCategory());
		createBundleWizard = catalogManagement.clickCreateBundleButton();
		String productCode = Utility.getRandomUUID();
		this.product.setProductCode(productCode);
		String productName = product.getProductName() + "-" + productCode;
		this.product.setProductName(productName);
		if (dst != null) {
			dst.setProductCode(productCode);
			dst.setProductName(productName);
		}
		createBundleWizard.enterProductCode(productCode);
		createBundleWizard.enterProductName(this.product.getProductName());
		createBundleWizard.selectBundlePricing(product.getBundlePricing());
		createBundleWizard.selectProductType(product.getProductType());
		createBundleWizard.selectBrand(product.getBrand());

		if (product.getStoreVisible().equalsIgnoreCase("true")) {
			createBundleWizard.checkStoreVisibleBox();
		} else {
			assertThat(product.getStoreVisible())
					.as("Store visible value is invalid - " + product.getStoreVisible())
					.isEqualToIgnoringCase("false");
		}

		createBundleWizard.clickNextInDialog();

		this.product.setBundleProductSKUCodes(product.getBundleProductSKUCodes());
		verifyBundleProductCodesListExistInTestData();
		for (String bundleProductCode : this.product.getBundleProductSKUList()) {
			if (bundleProductCode.contains("sku")) {
				addItemDialog = createBundleWizard.clickAddItemButton();
				addItemDialog.selectType("SKU");
				addBundleItemSku(bundleProductCode);

			} else {

				addItemDialog = createBundleWizard.clickAddItemButton();
				addBundleItem(bundleProductCode);
			}

		}
		if (product.getBundleSelectionRule() != null) {
			createBundleWizard.selectRule(product.getBundleSelectionRule());
			createBundleWizard.selectRuleParameter(product.getBundleSelectionRuleValue());
		}

		createBundleWizard.clickNextInDialog();
		createBundleWizard.enterAttributeShortTextMultiValue(product.getAttrShortTextMultiValue(), product.getAttrShortTextMulti());
		createBundleWizard.enterAttributeIntegerValue(product.getAttrIntegerValue(), product.getAttrInteger());
		createBundleWizard.enterAttributeDecimalValue(product.getAttrDecimalValue(), product.getAttrDecimal());
		createBundleWizard.clickNextInDialog();
		createBundleWizard.enterSkuCode(getUniqueSkuCode());
		addBundlePrice(product);
		createBundleWizard.clickFinish();
	}


	/**
	 * Adds a bundle Item with the given product code.
	 *
	 * @param bundleProductCode product code.
	 */
	public void addBundleItem(final String bundleProductCode) {
		selectAProductDialog = addItemDialog.clickSelectProductImageLink();
		selectAProductDialog.enterProductCode(bundleProductCode);
		selectAProductDialog.clickSearchButton();
		selectAProductDialog.selectProductByCode(bundleProductCode);
		selectAProductDialog.clickOKButton();
		addItemDialog.clickOKButton();
	}

	/**
	 * Adds a bundle Item with the given sku code.
	 *
	 * @param bundleSkuCode product code.
	 */
	public void addBundleItemSku(final String bundleSkuCode) {
		selectASkuDialog = addItemDialog.clickSelectSkuImageLink();
		selectASkuDialog.enterSkuCode(bundleSkuCode);
		selectASkuDialog.clickSearchButton();
		selectASkuDialog.selectSkuCodeInSearchResult(bundleSkuCode);
		selectASkuDialog.clickOKButton();
		addItemDialog.clickOKButton();
	}

	/**
	 * Checks if test data has the sku codes.
	 */
	private void verifySKUCodesListExistInTestData() {
		assertThat(this.product.getSKUCodeList())
				.as("No SKU codes list provided in test data.")
				.isNotNull();
	}

	/**
	 * Checks if test data has the product codes.
	 */
	private void verifyBundleProductCodesListExistInTestData() {
		assertThat(this.product.getBundleProductSKUList())
				.as("No Product codes list provided in test data.")
				.isNotNull();
	}


	/**
	 * Searches for a product by name.
	 *
	 * @param productName product name.
	 */
	public void searchForProductByName(final String productName) {
		catalogManagement.clickCatalogSearchTab();
		catalogManagement.enterProductName(productName);
		catalogSearchResultPane = catalogManagement.clickCatalogSearch();

		int index = 0;
		while (!catalogSearchResultPane.isProductInList(productName) && index < Constants.UUID_END_INDEX) {
			catalogSearchResultPane.sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			searchNewlyCreatedProductByName(productName);
			index++;
		}
		catalogSearchResultPane.setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_THREE_SECONDS);
		catalogSearchResultPane.verifyProductNameExists(productName);
		catalogSearchResultPane.setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Searches for a product by code.
	 *
	 * @param productCode product code.
	 */
	private void searchForProductByCode(final String productCode) {
		catalogManagement.clickCatalogSearchTab();
		catalogManagement.enterProductCode(productCode);
		catalogSearchResultPane = catalogManagement.clickCatalogSearch();

		int index = 0;
		while (!catalogSearchResultPane.isProductCodeInList(productCode) && index < Constants.UUID_END_INDEX) {
			catalogSearchResultPane.sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			searchNewlyCreatedProductByCode(productCode);
			index++;
		}
		catalogSearchResultPane.setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_THREE_SECONDS);
		catalogSearchResultPane.verifyProductCodeExists(productCode);
		catalogSearchResultPane.setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Searches for a newly created product by name.
	 *
	 * @param productName product name.
	 */
	private void searchNewlyCreatedProductByName(final String productName) {
		catalogManagement.enterProductName(productName);
		catalogSearchResultPane = catalogManagement.clickCatalogSearch();
	}

	/**
	 * Searches for a newly created product by code.
	 *
	 * @param productCode product code.
	 */
	private void searchNewlyCreatedProductByCode(final String productCode) {
		catalogManagement.enterProductCode(productCode);
		catalogSearchResultPane = catalogManagement.clickCatalogSearch();
	}

	private String getUniqueSkuCode() {
		uniqueSkuCode = this.product.getProductName() + Utility.getRandomUUID() + "_sku";
		this.product.setSkuCode(uniqueSkuCode);
		return uniqueSkuCode;
	}

	/**
	 * Selects Product Type.
	 *
	 * @param product Product.
	 */
	private void selectProductType(final Product product) {
		if (product.getProductType() == null || product.getBrand() == null) {
			createProductWizard.selectProductType(this.productType.getProductTypeName());
			createProductWizard.selectBrand(this.catalog.getBrand());
		} else {
			createProductWizard.selectProductType(product.getProductType());
			createProductWizard.selectBrand(product.getBrand());
		}
	}

	/**
	 * Selects store visiblity.
	 *
	 * @param product Product.
	 */
	private void selectStoreVisiblity(final Product product) {
		if (product.getStoreVisible().equalsIgnoreCase("true")) {
			createProductWizard.checkStoreVisibleBox();
		} else {
			assertThat(product.getStoreVisible())
					.as("Store visible value is invalid - " + product.getStoreVisible())
					.isEqualToIgnoringCase("false");
		}
	}

	/**
	 * Enters product attributes.
	 *
	 * @param product Product.
	 */
	private void enterProductAttribute(final Product product) {
		if (product.getAttrShortTextMultiValue() != null) {
			createProductWizard.enterAttributeShortTextMultiValue(product.getAttrShortTextMultiValue(), product.getAttrShortTextMulti());
			createProductWizard.enterAttributeIntegerValue(product.getAttrIntegerValue(), product.getAttrInteger());
			createProductWizard.enterAttributeDecimalValue(product.getAttrDecimalValue(), product.getAttrDecimal());
		}
	}

	/**
	 * Adds multi sku values.
	 *
	 * @param product Product.
	 */
	private void addMultiSkuValues(final Product product) {
		this.product.setProductSKUCodes(product.getProductSKUCodes());
		verifySKUCodesListExistInTestData();
		for (String productSkuCode : this.product.getSKUCodeList()) {
			addSkuWizard = createProductWizard.clickAddSkuButton();
			addSkuWizard.enterSkuCode(getUniqueSkuCode());
			addSkuWizard.selectSkuOptions("GC_Denominations", productSkuCode);
			addSkuWizard.selectSkuOptions("GC_Themes", product.getSKUOption());
			addSkuWizard.selectShippableType(product.getShippableType());
			addSkuWizard.clickNextButton();
			addSkuWizard.clickFinish();
		}
	}

	/**
	 * Adds product price.
	 *
	 * @param product Product.
	 */
	private void addPrice(final Product product) {
		if (product.getPriceList() != null && !activityToolbar.isChangeSetEnabled()) {
			createProductWizard.clickNextInDialog();
			createProductWizard.selectPriceList(product.getPriceList());
			basePriceEditorDialog = createProductWizard.clickAddBasePriceButton();
			basePriceEditorDialog.enterListPrice(product.getListPrice());
			basePriceEditorDialog.clickOKButton();
		}
	}

	/**
	 * Editing the newly created Product name.
	 *
	 * @param productName editing the product name to another
	 */
	@When("^I edit newly created product name to (.+)$")
	public void editProductName(final String productName) {
		productEditor = catalogSearchResultPane.openProductEditor(this.product.getProductName());
		this.product.setProductName(productName);
		productEditor.enterProductName(this.product.getProductName());
		catalogManagementActionToolbar.saveAll();
		productEditor.closeProductEditor(this.product.getProductCode());
	}

	/**
	 * Attempts to edit the product name and verifies that it is unable to edit because
	 * product is locked in changeset.
	 *
	 * @param productCode    the product code of the product to edit
	 * @param newProductName the new name
	 */
	@When("^I should not be able to edit (.+) object's name to (.+)$")
	public void testEditProductName(final String productCode, final String newProductName) {
		productEditor.selectproductEditor(productCode);
		productEditor.tryProductNameChange();
	}

	/**
	 * Editing the newly created Product name.
	 *
	 * @param productCode    product code of the product to edit
	 * @param newProductName the new name
	 */
	@When("^I should be able to edit (.+) object's name to (.+)$")
	public void editProductName(final String productCode, final String newProductName) {
		productEditor.selectproductEditor(productCode);
		try {
			productEditor.enterProductName(newProductName);
			productEditor.closeProductEditorWithoutSave(productCode);
		} catch (InvalidElementStateException exception) {
			fail("Unable to edit object name even though we unlocked changeset that contains this object");
		}
	}

	/**
	 * Search and open an existing Product to go specific Tab.
	 *
	 * @param existingProductName the product name
	 * @param tabName             for product
	 */
	@When("^I am viewing the (.+) tab of an existing product with name (.+)$")
	public void searchOpenProductEditorTab(final String tabName, final String existingProductName) {
		searchOpenProductEditor(existingProductName);
		productEditor.selectTab(tabName);
	}

	/**
	 * Search and open an existing Product.
	 *
	 * @param existingProductName the product name
	 */
	@When("^I search and open an existing product with name (.+)$")
	public void searchOpenProductEditor(final String existingProductName) {
		searchForProductByName(existingProductName);
		catalogSearchResultPane.verifyProductNameExists(existingProductName);
		productEditor = catalogSearchResultPane.openProductEditor(existingProductName);
	}

	/**
	 * Search and open an existing Product to go specific Tab.
	 *
	 * @param existingProductcode the product code
	 * @param tabName             for product
	 */
	@When("^I am viewing the (.+) tab of an existing product with product code (.+)$")
	public void searchOpenProductEditorTabWithCode(final String tabName, final String existingProductcode) {
		searchOpenProductWithCode(existingProductcode);
		productEditor.selectTab(tabName);
	}

	/**
	 * Search and open an existing Product with the given code.
	 *
	 * @param existingProductcode the product code
	 */
	@When("^I search and open an existing product with product code (.+)$")
	public void searchOpenProductWithCode(final String existingProductcode) {
		this.product.setProductCode(existingProductcode);
		searchForProductByCode(existingProductcode);
		catalogSearchResultPane.verifyProductCodeExists(existingProductcode);
		productEditor = catalogSearchResultPane.openProductEditorWithCode(existingProductcode);
	}

	/**
	 * Add Price tier for an existing Product.
	 *
	 * @param listPrice the list price.
	 * @param quantity  quantity
	 */
	@When("^I add a new price tier with List Price of (.+) for quantity of (.+)$")
	public void addPriceTierData(final String listPrice, final String quantity) {
		priceTierTab.selectPriceList("Mobile Price List (CAD)");
		addPriceTierDialog = priceTierTab.clickAddPriceTierButton();
		addPriceTierDialog.addPriceTierData(listPrice, quantity);
		addPriceTierDialog.clickOKButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verifies added Price tier exists.
	 *
	 * @param listPrice the list price.
	 */
	@Then("^the new price tier with List Price of (.+) exists in the pricing table$")
	public void verifyAddedPriceTierExists(final String listPrice) {
		priceTierTab.verifyAddedPriceTierExists(listPrice);
	}

	/**
	 * Edit created Price tier List Price.
	 *
	 * @param oldListPrice the list price.
	 * @param newListPrice the list price.
	 */
	@And("^I edit price tier List Price from (.+) to (.+)$")
	public void editPriceTierListPrice(final String oldListPrice, final String newListPrice) {
		priceTierTab.clickEditPriceTierButton(oldListPrice, newListPrice);
		catalogManagementActionToolbar.saveAll();
	}

	/**
	 * Delete newly created Price tier.
	 *
	 * @param listPrice the list price.
	 */
	@When("^I delete the price tier with List Price of (.+)$")
	public void deletePriceTierListPrice(final String listPrice) {
		priceTierTab.deletePriceTierListPrice(listPrice);
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify newly created Price tier is deleted.
	 *
	 * @param listPrice the list price.
	 */
	@Then("^the price tier with List Price of (.+) is deleted from the pricing table$")
	public void verifyPriceTierDelete(final String listPrice) {
		priceTierTab.verifyPriceTierDelete(listPrice);
	}

	/**
	 * Clears and Edit attribute value.
	 *
	 * @param attributeName String
	 * @param value         String
	 */
	@Then("^I clear and edit (.+) attribute value to (.+)$")
	public void clearAttributeValue(final String attributeName, final String value) {
		attributesTab.clickClearAttribute(attributeName);
		editDecimalValueAttributeDialog = attributesTab.clickEditAttributeButtonDecimalValue();
		editDecimalValueAttributeDialog.enterDecimalValue(value);
		editDecimalValueAttributeDialog.clickOKButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify updated attribute value.
	 *
	 * @param attributeName String
	 * @param value         String
	 */
	@Then("^the (.+) attribute value is updated to (.+)$")
	public void verifyAttributeValue(final String attributeName, final String value) {
		attributesTab.verifyProductAttributeValue(attributeName, value);
		productEditor.closeProductEditor(this.product.getProductCode());
	}

	/**
	 * Adding SKU to newly created Product.
	 *
	 * @param productSkuCode   for SKU
	 * @param skuShippableType for SKU
	 */
	@When("^I add SKU (.+) with (.+) shippable type$")
	public void addSkuToProduct(final String productSkuCode, final String skuShippableType) {
		productEditor = catalogSearchResultPane.openProductEditor(this.product.getProductName());
		productEditor.selectTab("SingleSku");
		addSkuWizard = productEditor.clickAddSkuButton();
		addSkuWizard.enterSkuCode(getUniqueSkuCode());
		addSkuWizard.selectSkuOptions("GC_Denominations", productSkuCode);
		addSkuWizard.selectSkuOptions("GC_Themes", "berries_theme - Berries");
		addSkuWizard.selectShippableType(skuShippableType);
		addSkuWizard.clickNextButton();
		addSkuWizard.clickFinish();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify and Open newly created Product SKU.
	 *
	 * @param productSkuCode for SKU
	 */
	@Then("^the SKU (.+) is in the list and sku editor$")
	public void openSkuFromProduct(final String productSkuCode) {
		productEditor.selectSkuDetails(productSkuCode);
		skuDetailsEditor = productEditor.clickOpenSkuButton();
		skuDetailsEditor.verifySkuDetails(productSkuCode);
		skuDetailsEditor.closeSkuDetailsEditor();
	}

	/**
	 * Delete SKU from newly created Product.
	 *
	 * @param productSkuCode for SKU
	 */
	@When("^I delete the SKU (.+)$")
	public void deleteSkuFromProduct(final String productSkuCode) {
		productEditor.selectSkuDetails(productSkuCode);
		productEditor.clickRemoveSkuDetailsButton();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("CatalogMessages.ProductEditorMultiSkuSection_RemoveConfirmation");
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify SKU is deleted.
	 *
	 * @param productSkuCode for SKU
	 */
	@When("^the SKU (.+) is no longer in the list$")
	public void verifySkuIsDeleted(final String productSkuCode) {
		productEditor.verifyProductSkuIsDeleted(productSkuCode);
		productEditor.closeProductEditor(this.product.getProductCode());
	}

	/**
	 * Adds a category to category assignment list.
	 *
	 * @param categoryAssignmentCode category name.
	 */
	@When("^I add category (.+) to category assignment list$")
	public void addCategoryAssignment(final String categoryAssignmentCode) {
		selectACategoryDialog = productEditor.clickAddCategoryButton();
		selectACategoryDialog.enterCategoryCode(categoryAssignmentCode);
		selectACategoryDialog.clickSearchButton();
		selectACategoryDialog.selectCategoryByCode(categoryAssignmentCode);
		selectACategoryDialog.clickOKButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Delete a category from category assignment list.
	 *
	 * @param categoryAssignmentName category name.
	 */
	@When("^I delete category (.+) from category assignment list$")
	public void deleteCategoryAssignment(final String categoryAssignmentName) {
		productEditor.selectCategoryDetails(categoryAssignmentName);
		productEditor.clickRemoveCategoryButton();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("CatalogMessages.CategoryAssignmentPage_RemoveConfirmTitle");
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
		productEditor.closeProductEditor(this.product.getProductCode());
	}

	/**
	 * Verify product under assigned category for a catalog.
	 *
	 * @param productName  Product Name.
	 * @param categoryName Category Name.
	 * @param catalogName  Catalog Name.
	 */
	@When("^product (.+) present under category (.+) for (.+)$")
	public void verifyProductLinkedWithCategory(final String productName, final String categoryName, final String catalogName) {
		catalogManagement.clickCatalogBrowseTab();
		catalogManagement.expandCatalogAndVerifyCategory(catalogName, categoryName);
		catalogProductListingPane = catalogManagement.doubleClickCategory(categoryName);

		int index = 0;
		while (!catalogProductListingPane.isProductNameInList(productName) && index < Constants.UUID_END_INDEX) {
			catalogProductListingPane.sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			catalogManagement.doubleClickCategory(categoryName);
			index++;
		}

		catalogProductListingPane.verifyProductNameExists(productName);
	}

	/**
	 * Verify product is not linked with specified category for a catalog.
	 *
	 * @param productName  Product Name.
	 * @param categoryName Category Name.
	 */
	@When("^product (.+) is not linked with (.+) Category$")
	public void verifyProductNotLinkedWithCategory(final String productName, final String categoryName) {
		catalogProductListingPane = catalogManagement.doubleClickCategory(categoryName);
		catalogProductListingPane.verifyProductNameNotExists(productName, categoryName);
	}

	/**
	 * Add product to Merchandising Associations Tab.
	 *
	 * @param productCode Product Code.
	 * @param tabName     Merchandising Associations Tab.
	 */
	@When("^I add product code (.+) to merchandising association (.+)$")
	public void addProductToMerchandisingTab(final String productCode, final String tabName) {
		merchandisingTab.clickMerchandisingTab(tabName);
		addEditMerchandisingAssociationsDialog = merchandisingTab.clickAddMerchandisingAssociationsButton();
		addEditMerchandisingAssociationsDialog.enterProductCode(productCode);
		addEditMerchandisingAssociationsDialog.clickAddButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify Added Merchandising Associations product.
	 *
	 * @param productCode Product Code.
	 * @param tabName     Merchandising Associations Tab.
	 */
	@When("^the product code (.+) exists under merchandising association (.+)$")
	public void verifyProductFromMerchandisingTab(final String productCode, final String tabName) {
		merchandisingTab.clickMerchandisingTab(tabName);
		merchandisingTab.verifySelectProductCode(productCode);
	}

	/**
	 * Edit product from Merchandising Associations Tab.
	 *
	 * @param oldProductCode Old Product Code.
	 * @param newProductCode New Product Code.
	 */
	@When("^I edit product code (.+) to (.+)")
	public void editMerchandisingTabProduct(final String oldProductCode, final String newProductCode) {
		merchandisingTab.verifySelectProductCode(oldProductCode);
		addEditMerchandisingAssociationsDialog = merchandisingTab.clickEditMerchandisingAssociationsButton();
		addEditMerchandisingAssociationsDialog.enterProductCode(newProductCode);
		addEditMerchandisingAssociationsDialog.clickSetButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Delete product from Merchandising Associations Tab.
	 *
	 * @param productCode Product Code.
	 */
	@When("^I delete product code (.+)$")
	public void deleteMerchandisingTabProduct(final String productCode) {
		merchandisingTab.verifySelectProductCode(productCode);
		merchandisingTab.clickRemoveMerchandisingAssociationsButton();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("CatalogMessages.ProductMerchandisingAssociationDialog_RemoveTitle");
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify Merchandising Associations product is deleted.
	 *
	 * @param productCode Product Code.
	 * @param tabName     Merchandising Associations Tab.
	 */
	@When("^the product code (.+) is no longer in merchandising association (.+)$")
	public void verifyProductIsDeletedFromMerchandisingTab(final String productCode, final String tabName) {
		merchandisingTab.clickMerchandisingTab(tabName);
		merchandisingTab.verifyProductCodeIsDeleted(productCode);
		productEditor.closeProductEditor(this.product.getProductCode());
	}

	/**
	 * Save bundle editor.
	 */
	@Then("^I save bundle changes$")
	public void clickSaveAllIcon() {
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}


	/**
	 * Adds bundle product price.
	 *
	 * @param product Product.
	 */
	private void addBundlePrice(final Product product) {
		if (product.getPriceList() != null && !activityToolbar.isChangeSetEnabled()) {
			createBundleWizard.clickNextInDialog();
			createBundleWizard.selectPriceList(product.getPriceList());
			basePriceEditorDialog = createBundleWizard.clickAddBasePriceButton();
			basePriceEditorDialog.enterListPrice(product.getListPrice());
			basePriceEditorDialog.clickOKButton();
		}
	}

}
