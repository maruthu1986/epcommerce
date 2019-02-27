package com.elasticpath.cucumber.definitions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.PriceEditorDialog;
import com.elasticpath.selenium.dialogs.SelectAProductDialog;
import com.elasticpath.selenium.dialogs.SelectASkuDialog;
import com.elasticpath.selenium.domainobjects.DST;
import com.elasticpath.selenium.editor.PriceListEditor;
import com.elasticpath.selenium.editor.product.ProductEditor;
import com.elasticpath.selenium.navigations.PriceListManagement;
import com.elasticpath.selenium.resultspane.PriceListAssignmentsResultPane;
import com.elasticpath.selenium.resultspane.PriceListsResultPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ActivityToolbar;
import com.elasticpath.selenium.toolbars.PriceListActionToolbar;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.selenium.util.Utility;
import com.elasticpath.selenium.wizards.CreatePriceListAssignmentWizard;


/**
 * Price List definition steps.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
public class PriceListDefinition {
	private final PriceListManagement priceListManagement;
	private final PriceListActionToolbar priceListActionToolbar;
	private final ActivityToolbar activityToolbar;
	private CreatePriceListAssignmentWizard createPriceListAssignmentWizard;
	private PriceListAssignmentsResultPane priceListAssignmentsResultPane;
	private PriceListsResultPane priceListsResultPane;
	private PriceListEditor priceListEditor;
	private PriceEditorDialog priceEditorDialog;
	private SelectAProductDialog selectAProductDialog;
	private SelectASkuDialog selectASkuDialog;
	private ProductEditor productEditor;
	private static String uniquePriceListAssignmentName = "";
	private static String uniquePriceListName = "";
	private static final String PRICE_LIST_DESC = "test price list";
	private static final String CURRENCY = "CAD";
	private static final int SLEEP_TIME = 1000;
	private final DST dst;

	/**
	 * Constructor.
	 *
	 * @param dst the DST class
	 */
	public PriceListDefinition(final DST dst) {
		priceListActionToolbar = new PriceListActionToolbar(SetUp.getDriver());
		priceListManagement = new PriceListManagement(SetUp.getDriver());
		activityToolbar = new ActivityToolbar(SetUp.getDriver());
		this.dst = dst;
	}

	/**
	 * Select price list tab.
	 */
	@When("^I select Price List tab$")
	public void selectPriceListTab() {
		priceListManagement.clickPriceListTab();
	}

	/**
	 * Select price list assignments tab.
	 */
	@When("^I select Price List Assignments tab$")
	public void selectPriceListAssignmentsTab() {
		clickPriceListAssignmentsTab();
	}

	/**
	 * Click new price list button.
	 */
	@When("^I click Create Price List button$")
	public void clickNewPriceListButton() {
		priceListEditor = priceListActionToolbar.clickCreatePriceList();
	}

	/**
	 * Create Price list assignment.
	 *
	 * @param priceList the price list.
	 * @param catalog   the catalog to assign it to.
	 */
	@When("^I create Price List Assignment with existing price list (.+) for catalog (.+)$")
	public void createPriceListAssignment(final String priceList, final String catalog) {
		createPLA(priceList, catalog);
	}

	/**
	 * Create Price list assignment for new price list.
	 *
	 * @param catalog the catalog to assign it to.
	 */
	@When("^I create Price List Assignment with newly created price list for catalog (.+)$")
	public void createPriceListAssignmentForNewPriceList(final String catalog) {
		createPLA(uniquePriceListName, catalog);
	}

	/**
	 * Set up price list assignment.
	 *
	 * @param catalog the catalog.
	 */
	@Given("^I have a Price List Assignment for catalog (.+)$")
	public void setupPriceListAssignment(final String catalog) {
		createNewPriceList(PRICE_LIST_DESC, CURRENCY);
		createPriceListAssignmentForNewPriceList(catalog);
	}

	/**
	 * Search Created price list assignments.
	 *
	 * @param priceListName price list name to search for.
	 */
	@When("^I search for Price List Name (.*)$")
	public void searchCreatedPriceListAssignment(final String priceListName) {
		searchForPriceListAssignmentByName(priceListName);
	}

	private void searchForPriceListAssignmentByName(final String priceListName) {
		clickPriceListAssignmentsTab();
		priceListManagement.enterPriceListName(priceListName);
		priceListAssignmentsResultPane = priceListManagement.clickPriceListAssignmentSearch();

		int index = 0;
		while (!priceListAssignmentsResultPane.isPLAInList(priceListName) && index < Constants.UUID_END_INDEX) {
			priceListAssignmentsResultPane.sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			searchPriceListAssignment(priceListName);
			index++;
		}
		priceListAssignmentsResultPane.setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_THREE_SECONDS);
		priceListAssignmentsResultPane.verifyPriceListAssignmentExists(priceListName);
		priceListAssignmentsResultPane.setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verify newly created price list assignment exists.
	 */
	@Then("^I should see newly created Price List Assignment in search result$")
	public void verifyNewCreatedPriceListAssignmentExists() {
		searchPriceListAssignment(uniquePriceListName);
		priceListAssignmentsResultPane.verifyPriceListAssignmentExists(uniquePriceListAssignmentName);
	}

	/**
	 * Delete newly created price list assignment.
	 */
	@When("^I delete newly created price list assignment$")
	public void deleteNewCreatedPriceListAssignment() {
		deletePLA(uniquePriceListName, uniquePriceListAssignmentName);
	}

	/**
	 * Delete newly created price list assignment for existing price list.
	 *
	 * @param priceList the price list name
	 */
	@When("^I delete newly created price list assignment for price list (.+)$")
	public void deleteNewCreatedPLAForExistingPL(final String priceList) {
		deletePLA(priceList, uniquePriceListAssignmentName);
	}

	/**
	 * Verify price list assignment is deleted.
	 */
	@Then("^the deleted price list assignment no longer exists$")
	public void verifyPriceListAssignmentDeleted() {
		searchPriceListAssignment(uniquePriceListName);
		priceListAssignmentsResultPane.verifyPriceListAssignmentDeleted(uniquePriceListAssignmentName);
	}

	/**
	 * Click search for price lists.
	 */
	@When("^I search for price list")
	public void clickSearchForPriceLists() {
		priceListsResultPane = priceListManagement.clickPriceListsSearch();
	}

	/**
	 * Verify Price lists.
	 *
	 * @param expectedPriceList the expected price list.
	 */
	@Then("^I should see price list (.*) in the result$")
	public void verifyPriceLists(final String expectedPriceList) {
		priceListsResultPane.verifyPriceListExists(expectedPriceList);
	}

	/**
	 * Verify newly created price lists.
	 */
	@Then("^I should see the newly created price list$")
	public void verifyNewCreatedPriceList() {
		clickSearchForPriceLists();
		verifyPriceLists(uniquePriceListName);
	}

	/**
	 * Create new price list.
	 *
	 * @param description the new price list description.
	 * @param currency    the CURRENCY.
	 */
	@When("^I create a new price list with description (.+) and currency (.+)$")
	public void createNewPriceList(final String description, final String currency) {
		createPriceList(description, currency);
	}

	/**
	 * Helper method for creating a new price list.
	 *
	 * @param description price list description
	 * @param currency    currency
	 */
	public void createPriceList(final String description, final String currency) {
		uniquePriceListName = "A" + Utility.getRandomUUID();
		if (dst != null) {
			dst.setPriceListName(uniquePriceListName);
		}
		priceListEditor = priceListActionToolbar.clickCreatePriceList();
		priceListEditor.verifyPriceListSummaryEditorExists();
		priceListEditor.enterPriceListName(uniquePriceListName);
		if (description != null) {
			priceListEditor.enterPriceListDescription(description);
		}
		priceListEditor.enterPriceListCurrency(currency);
		priceListActionToolbar.saveAll();
		priceListEditor.closePriceListEditor(uniquePriceListName);
	}

	/**
	 * Create new price list.
	 *
	 * @param currency the CURRENCY.
	 */
	@When("^I create new price list with currency (.+) and without description$")
	public void createNewPriceList(final String currency) {
		createPriceList(null, currency);
	}

	/**
	 * Set up price list.
	 */
	@Given("^I have a new Price List$")
	public void setupPriceList() {
		createNewPriceList(PRICE_LIST_DESC, CURRENCY);
		verifyNewCreatedPriceList();
	}

	/**
	 * Delete new price list.
	 */
	@Then("^I delete the newly created price list$")
	public void deleteNewPriceList() {
		clickSearchForPriceLists();
		priceListsResultPane.deletePriceList(uniquePriceListName);
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("PriceListManagerMessages.ConfirmDeletePriceList");
	}

	/**
	 * Select new price list.
	 */
	@Then("^I select the newly created price list$")
	public void selectNewPriceList() {
		clickSearchForPriceLists();
		priceListsResultPane.selectPriceList(uniquePriceListName);
	}

	/**
	 * Opens newly created price list in editor.
	 */
	@Then("^I open newly created price list in editor$")
	public void openNewPriceListInEditor() {
		clickSearchForPriceLists();
		priceListsResultPane.selectPriceList(uniquePriceListName);
		priceListEditor = priceListsResultPane.openSelectedPriceListEditor();
	}

	/**
	 * Select new price list.
	 */
	@Then("^I go to Price List Manager and select the newly created price list$")
	public void navigateAndselectNewPriceList() {
		activityToolbar.clickPriceListManagementButton();
		clickSearchForPriceLists();
		priceListsResultPane.selectPriceList(uniquePriceListName);
	}

	/**
	 * Selects new price list assignment.
	 */
	@Then("^I select the newly created price list assignment$")
	public void selectNewPLA() {
		searchPriceListAssignment(uniquePriceListName);
		priceListAssignmentsResultPane.selectPriceListAssignment(uniquePriceListAssignmentName);
	}

	/**
	 * Selects new price list assignment.
	 *
	 * @param priceList the price list name
	 */
	@Then("^I select the newly created price list assignment for price list (.+)$")
	public void selectPLA(final String priceList) {
		searchPriceListAssignment(priceList);
		priceListAssignmentsResultPane.selectPriceListAssignment(uniquePriceListAssignmentName);
	}


	/**
	 * Open the price list assignment.
	 */
	@Then("^I open the pricelist assignment$")
	public void openPriceListAssigment() {
		searchPriceListAssignment(uniquePriceListName);
		priceListAssignmentsResultPane.openPriceListAssignment(uniquePriceListAssignmentName);
	}

	/**
	 * Edit pricelist assignment description.
	 *
	 * @param descriptionText the description text.
	 */
	@Then("^I edit the pricelist assignment description to \"(.+)\"")
	public void editPriceListAssignmentDescription(final String descriptionText) {
		searchPriceListAssignment(uniquePriceListName);
		priceListAssignmentsResultPane.openPriceListAssignment(uniquePriceListAssignmentName);
		createPriceListAssignmentWizard.enterPriceListAssignmentDescription(descriptionText);
		createPriceListAssignmentWizard.clickFinish();
	}

	/**
	 * Verify the pricelist assignment description.
	 *
	 * @param descriptionText the description text.
	 */
	@Then("^the pricelist assignment description is \"(.+)\"")
	public void confirmEditPriceListAssignmentDescription(final String descriptionText) {
		assertTrue(createPriceListAssignmentWizard.getPriceListAssignmentDescription().equals(descriptionText));
		assertThat(createPriceListAssignmentWizard.getPriceListAssignmentDescription().equals(descriptionText))
				.as("Pricelist description should be " + descriptionText)
				.isTrue();
		assertThat(createPriceListAssignmentWizard.getPriceListAssignmentDescription())
				.as("Pricelist description should be " + descriptionText)
				.isEqualTo(descriptionText);
		createPriceListAssignmentWizard.clickFinish();
	}

	/**
	 * Verify price list is deleted.
	 */
	@Then("^The deleted price list no longer exists$")
	public void verifyPriceListDeleted() {
		clickSearchForPriceLists();
		priceListsResultPane.verifyPriceListDeleted(uniquePriceListName);
	}

	/**
	 * Search Price List Assignements for catalog.
	 *
	 * @param catalog the catalog.
	 */
	@When("^I search Price List Assignments for catalog (.+)$")
	public void searchPLAforCatalog(final String catalog) {
		selectCatalog(catalog);
		clickPLASearchButton();
	}

	/**
	 * Search all Price List Assignments.
	 */
	@When("I search for all Price List Assignments$")
	public void searchAllPLA() {
		clickPLASearchButton();
	}

	/**
	 * Verify Price List Assignments search results.
	 *
	 * @param plaList the price list assignment list.
	 */
	@Then("^Search result should contain following Price List Assignments?$")
	public void verifyPLASearchResults(final List<String> plaList) {
		for (String pla : plaList) {
			priceListAssignmentsResultPane.verifyPLASearchResults(pla);
		}
	}

	/**
	 * Open New price list editor.
	 */
	@And("^I open the newly created price list editor$")
	public void openNewPriceListEditor() {
		priceListEditor = priceListsResultPane.openPriceListEditor(uniquePriceListName);
		priceListEditor.verifyBaseAmountEditorExists();
	}

	/**
	 * Open price list editor.
	 *
	 * @param priceListName the price list name.
	 */
	@And("^I open price list (.+) in editor$")
	public void openPriceListEditor(final String priceListName) {
		priceListEditor = priceListsResultPane.openPriceListEditor(priceListName);
	}

	/**
	 * Opens selected price list editor.
	 */
	@And("^I open selected price list in editor$")
	public void openSelectedPriceListEditor() {
		priceListEditor = priceListsResultPane.openSelectedPriceListEditor();
	}

	/**
	 * Add Product price list.
	 *
	 * @param listPrice   The list price.
	 * @param productName the product name.
	 */
	@And("^I add a list price (.+) for product (.+)$")
	public void addProductPrice(final String listPrice, final String productName) {
		addProductPrices(listPrice, "", productName, "1");
		clickOKAndSaveAll();
	}

	/**
	 * Add a new pricelist.
	 *
	 * @param listPrice   The list price.
	 * @param salePrice   The sale price.
	 * @param quantity    The quantity.
	 * @param productName The product.
	 */
	@And("^I (?:can add|add) list price (.+) and sale price (.+) for quantity (.+) for product (.+)$")
	public void addProductTierPrice(final String listPrice, final String salePrice, final String quantity, final String productName) {
		addProductPrices(listPrice, salePrice, productName, quantity);
		clickOKAndSaveAll();
	}

	/**
	 * Attempt to add a new pricelist.
	 *
	 * @param listPrice   The list price.
	 * @param salePrice   The sale price.
	 * @param quantity    The quantity.
	 * @param productName The product.
	 */
	@And("^I attempt to add list price (.+) and sale price (.+) for quantity (.+) for product (.+)$")
	public void attemptToAddProductTierPriceFromDialog(final String listPrice, final String salePrice, final String quantity, final String
			productName) {
		addProductPrices(listPrice, salePrice, productName, quantity);
	}

	/**
	 * Attempt to add a product list price.
	 *
	 * @param listPrice   the list price.
	 * @param productName the product name.
	 */
	@And("^I attempt to add a list price (.+) for product (.+)$")
	public void attemptToAddProductPrice(final String listPrice, final String productName) {
		addProductPrices(listPrice, "", productName, "1");
	}

	/**
	 * Add Sku price list.
	 *
	 * @param listPrice the list price.
	 * @param skuCode   the sku code.
	 */
	@And("^I add a list price (.+) for sku code (.+)$")
	public void addSkuPrice(final String listPrice, final String skuCode) {
		addSkuPrices(listPrice, "", skuCode);
		clickOKAndSaveAll();
	}

	/**
	 * Attempt to add a list price to a sku.
	 *
	 * @param listPrice the list price.
	 * @param skuCode   the sku price.
	 */
	@And("^I attempt to add a list price (.+) for sku code (.+)$")
	public void attemptToAddSkuPrice(final String listPrice, final String skuCode) {
		addSkuPrices(listPrice, "", skuCode);
	}

	/**
	 * Add product list and sale price.
	 *
	 * @param salePrice   The sale price.
	 * @param listPrice   the list price.
	 * @param productName the product name.
	 */
	@And("^I add a sale price (.+) and a list price (.+) for the product (.+)$")
	public void addProductListAndSalePrice(final String salePrice, final String listPrice, final String productName) {
		addProductPrices(listPrice, salePrice, productName, "1");
		clickOKAndSaveAll();
	}

	/**
	 * Verify Product Price is present.
	 *
	 * @param productCodeList the product code list
	 */
	@Then("^the price list should have prices? for the following product codes?$")
	public void verifyProductPriceIsPresent(final List<String> productCodeList) {
		for (String productCode : productCodeList) {
			priceListEditor.verifyProductCodeIsPresentInPriceList(productCode);
		}
	}

	/**
	 * Verify Product Price is present.
	 *
	 * @param skuCodeList the sku code list.
	 */
	@Then("^the price list should have prices? for the following sku codes?$")
	public void verifySkuPriceIsPresent(final List<String> skuCodeList) {
		for (String skuCode : skuCodeList) {
			priceListEditor.verifyProductCodeIsPresentInPriceList(skuCode);
		}
	}

	/**
	 * Verify Product list and sale price.
	 *
	 * @param listPrice   the list price.
	 * @param salePrice   the sale price.
	 * @param productName the product name.
	 */
	@Then("^the price list should have a list price (.+) and a sale price (.+) for the product (.+)$")
	public void verifyProductListAndSalePrice(final String listPrice, final String salePrice, final String productName) {
		priceListEditor.verifyListPriceInPriceList(productName, listPrice);
		priceListEditor.verifySalePriceInPriceList(productName, salePrice);
	}

	/**
	 * Verify product price is not present.
	 *
	 * @param productCode the product code.
	 */
	@Then("^product code (.+) should not be in price list editor$")
	public void verifyProductPriceIsNotPresent(final String productCode) {
		priceListEditor.verifyProductCodeIsNotPresentInPriceList(productCode);
	}


	/**
	 * Delete price.
	 *
	 * @param productCode the product code.
	 */
	@When("^I delete price for product code (.+)$")
	public void deletePriceByProductCode(final String productCode) {
		deletePriceByColumnName(productCode, PriceListEditor.COLUMN_PRODUCT_CODE);
	}

	/**
	 * Delete price.
	 *
	 * @param skuCode the sku code.
	 */
	@When("^I delete price for sku code (.+)$")
	public void deletePriceBySkuCode(final String skuCode) {
		deletePriceByColumnName(skuCode, PriceListEditor.COLUMN_SKU_CODE);
	}

	/**
	 * Delete price.
	 *
	 * @param productName the product code.
	 */
	@When("^I delete price for product name (.+)$")
	public void deletePriceByProductName(final String productName) {
		deletePriceByColumnName(productName, PriceListEditor.COLUMN_PRODUCT_NAME);
	}

	private void deletePriceByColumnName(final String code, final String columnName) {
		priceListActionToolbar.clickReloadActiveEditor();
		priceListEditor.selectPriceRowByColumnName(code, columnName);
		priceListEditor.clickDeletePriceButton();
		priceListActionToolbar.saveAll();
	}

	/**
	 * Edit price.
	 *
	 * @param productName the product name.
	 * @param listPrice   the list price.
	 * @param salePrice   the sale price.
	 */
	@When("^I edit price for product (.+) as list price (.+) and sale price (.+)$")
	public void editPrice(final String productName, final String listPrice, final String salePrice) {
		priceListEditor.selectPriceRowByColumnName(productName, PriceListEditor.COLUMN_PRODUCT_NAME);
		priceEditorDialog = priceListEditor.clickEditPriceButton();
		priceEditorDialog.enterListPrice(listPrice);
		priceEditorDialog.enterSalePrice(salePrice);
		priceEditorDialog.clickOKButton();
		priceListActionToolbar.saveAll();
		priceListActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * open product editor for the given product code.
	 *
	 * @param productCode the code.
	 */
	@When("^I open the product editor for product code (.+)$")
	public void openItem(final String productCode) {
		priceListEditor.selectPriceRowByColumnName(productCode, PriceListEditor.COLUMN_PRODUCT_CODE);
		productEditor = priceListEditor.clickOpenItemButton();
	}

	/**
	 * Verify product name.
	 *
	 * @param productName the product name.
	 */
	@Then("^the product name should be (.+)$")
	public void verifyProductName(final String productName) {
		productEditor.verifyProductName(productName);
	}

	/**
	 * Enter price list summary values.
	 *
	 * @param priceListSummaryMap The summary value map.
	 */
	@And("^I enter following price list summary values? and save it$")
	public void enterPirceListSummaryValues(final Map<String, String> priceListSummaryMap) {
		priceListEditor.enterPriceListName(priceListSummaryMap.get("Price List"));
		priceListEditor.enterPriceListDescription(priceListSummaryMap.get("Description"));
		priceListEditor.enterPriceListCurrency(priceListSummaryMap.get("Currency"));
		priceListActionToolbar.saveAll();
	}

	/**
	 * Search price for code.
	 *
	 * @param code the code.
	 */
	@When("^I search price for code (.+)$")
	public void searchPrice(final String code) {
		priceListEditor.enterCodeToSearch(code);
		priceListEditor.clickSearchButton();
	}

	/**
	 * Verify error message.
	 *
	 * @param errMsgList the list of messages.
	 */
	@Then("^I should see following validation alert?$")
	public void verifyErrorAlert(final List<String> errMsgList) {
		for (String errMsg : errMsgList) {
			if (errMsg.length() > 0) {
				priceEditorDialog.verifyValidationErrorIsPresent(errMsg);
			}
		}
		priceEditorDialog.clickCancel();
	}

	/**
	 * Search price list by price.
	 *
	 * @param searchFromPrice Search from price.
	 * @param searchToPrice   Search to price.
	 */
	@When("^I search the price list with prices from (.+) and to (.+)$")
	public void searchPriceListByPrice(final String searchFromPrice, final String searchToPrice) {
		priceListEditor.searchByPrice(searchFromPrice, searchToPrice);
		priceListEditor.sleep(SLEEP_TIME);
	}

	/**
	 * Verify number of prices returned.
	 *
	 * @param expResultsReturned Expected number of results to be returned
	 */
	@Then("^I should see (.+) prices returned$")
	public void verifyNumberOfPricesReturned(final int expResultsReturned) {
		priceListEditor.verifyResultsReturned(expResultsReturned);
	}

	/**
	 * Filter price list by price.
	 *
	 * @param filterFromPrice Filter from price.
	 * @param filterToPrice   Filter to price.
	 */
	@When("^I filter the price list with prices from (.+) and to (.+)$")
	public void filterPriceListByPrice(final String filterFromPrice, final String filterToPrice) {
		priceListEditor.filterByPrice(filterFromPrice, filterToPrice);
		priceListEditor.sleep(SLEEP_TIME);
	}

	/**
	 * Edit price list  description and close price list editor pane.
	 */
	@Then("^I edit the price list description")
	public void editPriceListDescription() {
		//retrieve list
		priceListEditor.selectPriceListSummaryTab();
		priceListEditor.enterPriceListDescription("Edited description");
		activityToolbar.saveAll();
		priceListEditor.closePriceListEditor(uniquePriceListName);
	}

	/**
	 * Edit pricelist assignment description.
	 *
	 * @param descriptionText the description text.
	 * @param priceList       The price list.
	 */
	@When("^I edit the newly created price list assignment for price list (.+) description to \"(.+)\"")
	public void editPriceListAssignmentDescriptionForPriceList(final String priceList, final String descriptionText) {
		searchPriceListAssignment(priceList);
		priceListAssignmentsResultPane.openPriceListAssignment(uniquePriceListAssignmentName);
		createPriceListAssignmentWizard.enterPriceListAssignmentDescription(descriptionText);
		createPriceListAssignmentWizard.clickFinish();
	}

	/**
	 * Clean up price list assignment.
	 */
	@After("@cleanupPriceListAssignment")
	public void cleanUpPriceListAssignment() {
		deleteNewCreatedPriceListAssignment();
		verifyPriceListAssignmentDeleted();
		cleanUpPriceList();
	}

	/**
	 * Clean up price list.
	 */
	@After("@cleanupPriceList")
	public void cleanUpPriceList() {
		clickPriceListTab();
		deleteNewPriceList();
		verifyPriceListDeleted();
	}

	private void searchPriceListAssignment(final String priceList) {
		clickPriceListAssignmentsTab();
		priceListManagement.enterPriceListName(priceList);
		priceListAssignmentsResultPane = priceListManagement.clickPriceListAssignmentSearch();
	}

	private void clickCreatePriceListAssignment() {
		createPriceListAssignmentWizard = priceListActionToolbar.clickCreatePriceListAssignment();
	}

	private void enterPriceListAssignmentName() {
		uniquePriceListAssignmentName = "PLA" + Utility.getRandomUUID();
		createPriceListAssignmentWizard.enterPriceListAssignmentName(uniquePriceListAssignmentName);
	}

	private void clickNextButton() {
		createPriceListAssignmentWizard.clickNextInDialog();
	}

	private void clickFinishButton() {
		createPriceListAssignmentWizard.clickFinish();
	}

	private void selectPriceListName(final String priceListName) {
		createPriceListAssignmentWizard.selectPriceList(priceListName);
	}

	private void selectCatalogNameForPriceListAssignment(final String catalogName) {
		createPriceListAssignmentWizard.selectCatalogName(catalogName);
	}

	private void clickPriceListAssignmentsTab() {
		priceListManagement.clickPriceListAssignmentsTab();
	}

	private void clickPriceListTab() {
		priceListManagement.clickPriceListTab();
	}

	private void selectCatalog(final String catalogName) {
		priceListManagement.selectCatalogFromComboBox(catalogName);
	}

	private void clickPLASearchButton() {
		priceListAssignmentsResultPane = priceListManagement.clickPriceListAssignmentSearch();
	}

	/**
	 * Verify create price list button is present.
	 */
	@And("^I can view Create Price List button")
	public void verifyCreatePriceListButtonIsPresent() {
		priceListActionToolbar.verifyCreatePriceListButtonIsPresent();
	}

	/**
	 * Adds product prices.
	 *
	 * @param listPrice   The list price.
	 * @param salePrice   the sale price.
	 * @param quantity    The quantity.
	 * @param productName The product name.
	 */
	public void addProductPrices(final String listPrice, final String salePrice, final String productName, final String quantity) {
		priceListEditor.verifyBaseAmountEditorExists();
		priceEditorDialog = priceListEditor.clickAddPriceButton();
		selectAProductDialog = priceEditorDialog.clickSelectProductImageLink();
		selectAProductDialog.enterProductName(productName);
		selectAProductDialog.clickSearchButton();
		selectAProductDialog.selectProductByName(productName);
		selectAProductDialog.clickOKButton();
		priceEditorDialog.enterQuantity(quantity);
		priceEditorDialog.enterListPrice(listPrice);
		if (!"".equals(salePrice)) {
			priceEditorDialog.enterSalePrice(salePrice);
		}
	}

	/**
	 * Click On on editor dialog and save all.
	 */
	public void clickOKAndSaveAll() {
		priceEditorDialog.clickOKButton();
		priceListActionToolbar.saveAll();
		priceListActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Adds sku prices.
	 *
	 * @param listPrice the list price.
	 * @param salePrice the sale price.
	 * @param skuCode   the sku code.
	 */
	public void addSkuPrices(final String listPrice, final String salePrice, final String skuCode) {
		priceListEditor.verifyBaseAmountEditorExists();
		priceEditorDialog = priceListEditor.clickAddPriceButton();
		priceEditorDialog.selectTypeSku();
		selectASkuDialog = priceEditorDialog.clickSelectSkuImageLink();
		selectASkuDialog.enterSkuCode(skuCode);
		selectASkuDialog.clickSearchButton();
		selectASkuDialog.selectSkuCodeInSearchResult(skuCode);
		selectASkuDialog.clickOKButton();
		priceEditorDialog.enterListPrice(listPrice);
		if (!"".equals(salePrice)) {
			priceEditorDialog.enterSalePrice(salePrice);
		}
	}

	/**
	 * Returns price list name.
	 *
	 * @return uniquePriceListName
	 */
	public static String getUniquePriceListName() {
		return uniquePriceListName;
	}

	/**
	 * Returns price list assignment name.
	 *
	 * @return uniquePriceListAssignmentName
	 */
	public static String getUniquePriceListAssignmentName() {
		return uniquePriceListAssignmentName;
	}

	/**
	 * Create Price list assignment.
	 *
	 * @param priceList the price list
	 * @param catalog   the catalog to assign it to
	 */
	public void createPLA(final String priceList, final String catalog) {
		clickCreatePriceListAssignment();
		enterPriceListAssignmentName();
		clickNextButton();
		selectPriceListName(priceList);
		clickNextButton();
		selectCatalogNameForPriceListAssignment(catalog);
		clickFinishButton();
	}

	/**
	 * Delete Price list assignment.
	 *
	 * @param priceList           the price list
	 * @param priceListAssignment the price list assignment
	 */
	public void deletePLA(final String priceList, final String priceListAssignment) {
		searchPriceListAssignment(priceList);
		priceListAssignmentsResultPane.deletePriceListAssignment(priceListAssignment);
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("PriceListManagerMessages.ConfirmDeletePriceListAssignment");
	}

	/**
	 * Clicks next until a specific step of the Price List Assignment creation wizard is reached.
	 *
	 * @param step the step in the wizard to skip to ("Priority", "Price List", "Catalog", "Shoppers", "Time", "Stores")
	 */
	@And("^I skip to \"(.+)\" selection$")
	public void skipToStep(final String step) {
		createPriceListAssignmentWizard.skipToStep(step);
	}

	/**
	 * Verifies that user only has access to a certain list of stores in the Price List Assignment Wizard.
	 *
	 * @param storeList the list of stores to verify
	 */
	@Then("^Available Stores should contain the following Stores?$")
	public void verifyAvailableStores(final List<String> storeList) {
		createPriceListAssignmentWizard.verifyAvailableStores(storeList);
	}

	/**
	 * Verifies that user has access to all stores in the Price List Assignment by retrieving from DB.
	 */
	@Then("^Available Stores should contain all Stores?$")
	public void verifyAllAvailableStores() {
		createPriceListAssignmentWizard.verifyAllAvailableStores();
	}
}