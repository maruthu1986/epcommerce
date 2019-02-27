package com.elasticpath.cucumber.definitions;

import static org.assertj.core.api.Assertions.assertThat;

import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.AddEditBrandDialog;
import com.elasticpath.selenium.dialogs.CreateChangeSetDialog;
import com.elasticpath.selenium.dialogs.MoveSelectedObjectsDialog;
import com.elasticpath.selenium.domainobjects.CartItemModifierGroup;
import com.elasticpath.selenium.domainobjects.Catalog;
import com.elasticpath.selenium.domainobjects.Category;
import com.elasticpath.selenium.domainobjects.CategoryType;
import com.elasticpath.selenium.domainobjects.DST;
import com.elasticpath.selenium.domainobjects.LinkedCategory;
import com.elasticpath.selenium.domainobjects.Product;
import com.elasticpath.selenium.domainobjects.ProductType;
import com.elasticpath.selenium.editor.ChangeSetEditor;
import com.elasticpath.selenium.navigations.ChangeSet;
import com.elasticpath.selenium.resultspane.CatalogProductListingPane;
import com.elasticpath.selenium.resultspane.ChangeSetSearchResultPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ActivityToolbar;
import com.elasticpath.selenium.toolbars.ChangeSetActionToolbar;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.selenium.util.Utility;

/**
 * Change set steps.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
public class ChangeSetDefinition {
	private final ChangeSetActionToolbar changeSetActionToolbar;
	private final ChangeSet changeSet;
	private final ActivityToolbar activityToolbar;
	private ChangeSetSearchResultPane changeSetSearchResultPane;
	private CreateChangeSetDialog createChangeSetDialog;
	private ChangeSetEditor changeSetEditor;
	private String changeSetName = "";
	private String secondChangeSetName = "";
	private static final String OBJECT_NAME = "Object Name";
	private final ProductType productType;
	private final CartItemModifierGroup cartItemModifierGroup;
	private final Catalog catalog;
	private final Category category;
	private final LinkedCategory linkedCategory;
	private final CategoryType categoryType;
	private String changeSetEditedName;
	private final Product product;
	private static final int RETRY_COUNTER = 3;
	private final DST dst;
	private AddEditBrandDialog addEditBrandDialog;
	private final ProductAndBundleDefinition productAndBundleDefinition;

	/**
	 * Constructor.
	 *
	 * @param productType           Product Type object.
	 * @param cartItemModifierGroup Cart Item Modifier Group object.
	 * @param catalog               Catalog.
	 * @param category              Category.
	 * @param linkedCategory        linkedCategory.
	 * @param categoryType          CategoryType.
	 * @param product               Product.
	 */
	//	CHECKSTYLE:OFF: checkstyle:too many parameters
	public ChangeSetDefinition(final ProductType productType, final CartItemModifierGroup cartItemModifierGroup, final Catalog catalog,
							   final Category category, final LinkedCategory linkedCategory, final CategoryType categoryType, final Product product,
							   final ProductAndBundleDefinition productAndBundleDefinition, final DST dst) {
		changeSetActionToolbar = new ChangeSetActionToolbar(SetUp.getDriver());
		changeSet = new ChangeSet(SetUp.getDriver());
		activityToolbar = new ActivityToolbar(SetUp.getDriver());
		this.productType = productType;
		this.cartItemModifierGroup = cartItemModifierGroup;
		this.catalog = catalog;
		this.category = category;
		this.linkedCategory = linkedCategory;
		this.categoryType = categoryType;
		this.product = product;
		this.productAndBundleDefinition = productAndBundleDefinition;
		this.dst = dst;
	}

	/**
	 * Select change set.
	 *
	 * @param changeSetName the change set name.
	 */
	@And("^I select change set (.+)$")
	public void selectChangeSet(final String changeSetName) {
		changeSetActionToolbar.selectChangeSet(changeSetName);
	}

	/**
	 * Clicks the Search button.
	 **/
	@And("^I click change set search button$")
	public void clickSearchButton() {
		changeSetSearchResultPane = changeSet.clickSearchButton();
	}

	/**
	 * Clicks the Create button.
	 **/
	@And("^I click create change set button$")
	public void clickCreateButton() {
		createChangeSetDialog = changeSetSearchResultPane.clickCreateButton();
	}

	/**
	 * Creates a new change set.
	 *
	 * @param changeSetName the change set name
	 **/
	@And("^I create a new change set (.+)$")
	public void createChangeSetAndOpenEditor(final String changeSetName) {
		createChangeSet(changeSetName);
		searchChangeSetByName(this.changeSetName);
		changeSetEditor = changeSetSearchResultPane.openChangeSetEditor(this.changeSetName);
		changeSetEditor.selectObjectsTab();
	}


	/**
	 * Creates a second change set.
	 *
	 * @param changeSetName the change set name
	 **/
	@And("^I create a second change set (.+)$")
	public void createSecondChangeSetAndOpenEditor(final String changeSetName) {
		createSecondChangeSet(changeSetName);
		searchChangeSetByName(this.secondChangeSetName);
		changeSetEditor = changeSetSearchResultPane.openChangeSetEditor(this.secondChangeSetName);
		changeSetEditor.selectObjectsTab();
	}

	/**
	 * Creates a new change set.
	 *
	 * @param newName the change set name
	 **/
	@And("^I change the changeset name to (.+)$")
	public void editChangeSetName(final String newName) {
		changeSetEditor.selectSummaryTab();
		this.changeSetEditedName = newName + "_" + Utility.getRandomUUID();
		changeSetEditor.changeName(this.changeSetEditedName);
		changeSetActionToolbar.saveAll();

	}

	/**
	 * Verifies changeset name was successfully edited.
	 **/
	@Then("^the changeset should have the edited name$")
	public void verifyChangeSetEditedName() {
		changeSetEditor.closePane(this.changeSetEditedName);
		searchChangeSetByName(this.changeSetEditedName);
		changeSetSearchResultPane.selectChangeSet(this.changeSetEditedName);
	}

	/**
	 * Creates a new change set.
	 *
	 * @param changeSetName the change set name
	 **/
	@And("^I create and select the newly created change set (.+)$")
	public void createAndSelectChangeSet(final String changeSetName) {
		createChangeSetAndOpenEditor(changeSetName);
		if (dst != null) {
			changeSetEditor.setChangeSetGuid(dst);
			changeSetEditor.selectObjectsTab();
		}
		selectChangeSet(this.changeSetName);
	}

	/**
	 * Selects newly created change set.
	 **/
	@And("^I select (?:newly created|latest) change set$")
	public void selectNewChangeSet() {
		selectChangeSet(this.changeSetName);
	}

	/**
	 * Selects second change set.
	 **/
	@And("^I select second change set$")
	public void selectSecondChangeSet() {
		changeSetActionToolbar.selectChangeSet(changeSetName);
	}

	/**
	 * Selects object with the given name to the second changeset.
	 **/
	@And("^I select and move (.+) object to the second changeset$")
	public void selectAndMoveObject(final String objectName) {
		changeSetEditor.selectObjectInChangeSet(objectName);
		MoveSelectedObjectsDialog moveSelectedObjectsDialog = changeSetEditor.clickMoveSelectedObjectsButton();
		moveSelectedObjectsDialog.selectChangeSetfromTable(this.secondChangeSetName);
		moveSelectedObjectsDialog.clickMoveButton();
	}

	/**
	 * Selects object with the given name to the second changeset.
	 **/
	@Then("^the second changeSet should contain object (.+)$")
	public void verifyObjectExistsInSecondChangeset(final String objectName) {
		selectSecondChangeSetEditor();
		changeSetEditor.selectObjectsTab();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.verifyObjectExists(objectName, OBJECT_NAME);
	}

	/**
	 * Closes change set editor.
	 **/
	@And("^I close newly created change set editor$")
	public void closeNewChangeSetEditor() {
		changeSetEditor.closePane(this.changeSetName);
	}


	/**
	 * Verifies catalog in change set.
	 **/
	@Then("^I should see (?:newly created|deleted) virtual catalog in the change set$")
	public void verifyVirtualCatalogInChangeSet() {
		changeSetEditor.selectObjectsTab();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.verifyObjectExists(this.catalog.getCatalogName(), OBJECT_NAME);
	}

	/**
	 * Verifies price list in change set.
	 **/
	@Then("^I should see (?:newly created|deleted|edited) price list in the change set$")
	public void verifyNewPriceListInChangeSet() {
		changeSetEditor.selectObjectsTab();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.verifyObjectExists(PriceListDefinition.getUniquePriceListName(), OBJECT_NAME);
	}

	/**
	 * Verifies price list in change set.
	 *
	 * @param objectName the object name
	 **/
	@Then("^I should see (?:newly created|deleted) price for (.+) in the change set$")
	public void verifyNewPriceInChangeSet(final String objectName) {
		selectNewChangeSetEditor();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.selectObjectsTab();
		changeSetEditor.verifyObjectExists(objectName + " - " + PriceListDefinition.getUniquePriceListName(), OBJECT_NAME);
	}

	/**
	 * Verifies price list assignment in change set.
	 **/
	@Then("^I should see (?:newly created|deleted|edited) price list assignment in the change set$")
	public void verifyNewPriceListAssignmentInChangeSet() {
		changeSetEditor.selectObjectsTab();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.verifyObjectExists(PriceListDefinition.getUniquePriceListAssignmentName(), OBJECT_NAME);
	}

	/**
	 * Verifies category in change set.
	 **/
	@Then("^I should see (?:newly created|deleted) category in the change set$")
	public void verifyCategoryInChangeSet() {
		changeSetEditor.selectObjectsTab();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.verifyObjectExists(this.category.getCategoryName(), OBJECT_NAME);
	}

	/**
	 * Verifies linked category in change set.
	 **/
	@Then("^I should see (?:newly created|deleted) linked category in the change set$")
	public void verifyLinkedCategoryInChangeSet() {
		changeSetEditor.selectObjectsTab();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.verifyObjectExists(this.linkedCategory.getLinkedCategoryName(), OBJECT_NAME);
	}

	/**
	 * Verifies group in change set.
	 **/
	@Then("^I should see (?:newly created|deleted) group in the change set$")
	public void verifyGroupInChangeSet() {
		selectNewChangeSetEditor();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.selectObjectsTab();
		changeSetEditor.verifyObjectExists(this.cartItemModifierGroup.getGroupCode(), OBJECT_NAME);
	}

	/**
	 * Verifies category type in change set.
	 **/
	@Then("^I should see (?:newly created|deleted) category type in the change set$")
	public void verifyCategoryTypeInChangeSet() {
		selectNewChangeSetEditor();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.selectObjectsTab();
		changeSetEditor.verifyObjectExists(this.categoryType.getCategoryTypeName(), OBJECT_NAME);
	}

	/**
	 * Verifies product type in change set.
	 **/
	@Then("^I should see (?:newly created|deleted) product type in the change set$")
	public void verifyProductTypeInChangeSet() {
		selectNewChangeSetEditor();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.selectObjectsTab();
		changeSetEditor.verifyObjectExists(this.productType.getProductTypeName(), OBJECT_NAME);
	}

	/**
	 * Verifies product in change set.
	 **/
	@Then("^I should see (?:newly created|deleted|edited) (?:product|bundle) in the change set$")
	public void verifyProductInChangeSet() {
		selectNewChangeSetEditor();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.selectObjectsTab();
		changeSetEditor.verifyObjectExists(this.product.getProductName(), OBJECT_NAME);
	}

	/**
	 * Verifies existing product in change set object.
	 *
	 * @param existingProductName the product name
	 **/
	@Then("^I should see product (.+) in the change set$")
	public void verifyExistingProductInChangeSet(final String existingProductName) {
		selectNewChangeSetEditor();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.selectObjectsTab();
		changeSetEditor.verifyObjectExists(existingProductName, OBJECT_NAME);
	}

	/**
	 * Verifies existing product in change set with the given change set name.
	 *
	 * @param existingProductName the product name
	 **/
	@Then("^I should see product (.+) in the second changeset$")
	public void verifyExistingProductInSecondChangeSet(final String existingProductName) {
		selectSecondChangeSetEditor();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.selectObjectsTab();
		changeSetEditor.verifyObjectExists(existingProductName, OBJECT_NAME);
	}

	/**
	 * Verifies catalog attribute in change set.
	 **/
	@Then("^I should see the (?:newly created|edited|deleted) catalog attribute in the change set$")
	public void verifyCatalogAttributeInChangeSet() {
		selectNewChangeSetEditor();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.selectObjectsTab();
		changeSetEditor.verifyObjectExists(this.catalog.getAttributeName(), OBJECT_NAME);
	}

	/**
	 * Verifies promotion in change set.
	 **/
	@Then("^I should see (?:newly created|disabled) cart promotion in the change set$")
	public void verifyNewPromotionInChangeSet() {
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.selectObjectsTab();
		changeSetEditor.verifyObjectExists(PromotionsDefinition.getCartPromoName(), OBJECT_NAME);
	}

	/**
	 * Verifies sku options in change set.
	 **/
	@Then("^I should see the (?:newly created|edited|deleted) sku option in the change set$")
	public void verifySkuOptionInChangeSet() {
		selectNewChangeSetEditor();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.selectObjectsTab();
		changeSetEditor.verifyObjectExists(this.product.getSKUOption(), OBJECT_NAME);
	}

	/**
	 * Search, select and product to the change set.
	 *
	 * @param productName the product name
	 **/
	@When("^I add product (.+) to the change set$")
	public void addProductToChangeset(final String productName) {
		productAndBundleDefinition.searchForProductByName(productName);
		clickAddItemToChangeSetButton();
	}


	/**
	 * Locks and Finalizes change set.
	 */
	@After(value = "@lockAndFinalize", order = Constants.CLEANUP_ORDER_FOURTH)
	public void lockAndFinalizeNewChangeSet() {
		searchAndLockNewChangeSet();
		searchChangeSetByName(this.changeSetName);
		changeSetSearchResultPane.clickFinalizedButton();
		verifyChangeSetStatus("Finalized");
	}

	/**
	 * Locks and Finalizes change set.
	 */
	@After(value = "@lockAndFinalizeSecond", order = Constants.CLEANUP_ORDER_FOURTH)
	public void lockAndFinalizeSecondChangeSet() {
		searchAndLockSecondChangeSet();
		searchChangeSetByName(this.secondChangeSetName);
		changeSetSearchResultPane.clickFinalizedButton();
		verifyChangeSetStatusSecond("Finalized");
	}

	/**
	 * Locks and Publishes change set.
	 */
	public void lockAndPublishNewChangeSet() {
		searchAndLockNewChangeSet();
		searchChangeSetByName(this.changeSetName);
		changeSetSearchResultPane.clickPublishButton();
	}

	/**
	 * Locks change set.
	 */
	public void searchAndLockNewChangeSet() {
		activityToolbar.clickChangeSetButton();
		searchChangeSetByName(this.changeSetName);
		changeSetSearchResultPane.clickLockButton();
	}

	/**
	 * Locks change set.
	 */
	public void searchAndLockSecondChangeSet() {
		activityToolbar.clickChangeSetButton();
		searchChangeSetByName(this.secondChangeSetName);
		changeSetSearchResultPane.clickLockButton();
	}

	/**
	 * Locks and Publishes latest change set.
	 **/
	@And("^I lock and publish latest change set")
	public void lockAndPublishChangeSet() {
		activityToolbar.clickChangeSetButton();
		lockAndPublishNewChangeSet();
	}

	/**
	 * Locks and Finalizes latest change set.
	 **/
	@And("^I lock and finalize latest change set")
	public void lockAndFinalizeChangeSet() {
		lockAndFinalizeNewChangeSet();
	}

	/**
	 * Lock, finalize and close latest change set editor.
	 **/
	@And("^I lock, finalize and close latest change set editor")
	public void lockFinalizeCloseEditor() {
		lockAndFinalizeNewChangeSet();
		closeNewChangeSetEditor();
	}

	/**
	 * Clicks add item to change set button.
	 **/
	@And("^I click add item to change set button")
	public void clickAddItemToChangeSetButton() {
		changeSetActionToolbar.clickAddItemToChangeSet();
	}

	/**
	 * Locks change set.
	 **/
	@When("^I lock the latest change set")
	public void lockNewChangeSet() {
		activityToolbar.clickChangeSetButton();
		changeSetSearchResultPane.selectChangeSet(this.changeSetName);
		changeSetSearchResultPane.clickLockButton();
	}

	/**
	 * Unlocks change set.
	 **/
	@When("^I unlock the latest change set")
	public void unlockNewChangeSet() {
		activityToolbar.clickChangeSetButton();
		changeSetSearchResultPane.selectChangeSet(this.changeSetName);
		changeSetSearchResultPane.clickUnlockButton();
	}

	/**
	 * Finalizes change set.
	 **/
	@When("^I finalize the latest change set")
	public void finalizeNewChangeSet() {
		activityToolbar.clickChangeSetButton();
		changeSetSearchResultPane.selectChangeSet(this.changeSetName);
		changeSetSearchResultPane.clickFinalizedButton();
	}

	/**
	 * Verifies change set status.
	 *
	 * @param changeSetStatus the change set status
	 **/
	@Then("^the change set status should be (.+)")
	public void verifyChangeSetStatus(final String changeSetStatus) {
		changeSetSearchResultPane.verifyChangeSetStatus(this.changeSetName, changeSetStatus);
	}

	/**
	 * Verifies change set status of second change set
	 *
	 * @param changeSetStatus the change set status
	 **/
	@Then("^the second change set status should be (.+)")
	public void verifyChangeSetStatusSecond(final String changeSetStatus) {
		changeSetSearchResultPane.verifyChangeSetStatus(this.secondChangeSetName, changeSetStatus);
	}

	/**
	 * Selects newly created change set's editor.
	 **/
	@And("^I select the newly created change set's editor")
	public void selectNewChangeSetEditor() {
		changeSetEditor.selectChangeSetEditor(this.changeSetName);
	}

	/**
	 * Selects first change set's editor.
	 **/
	@And("^I select the first change set's editor")
	public void selectFirstChangeSetEditor() {
		changeSetEditor.selectChangeSetEditor(this.changeSetName);
	}

	/**
	 * Selects second change set's editor.
	 **/
	@And("^I select the second change set's editor")
	public void selectSecondChangeSetEditor() {
		changeSetEditor.selectChangeSetEditor(this.secondChangeSetName);
	}

	/**
	 * Adds price list to change set.
	 **/
	@And("^I add newly created price list to the change set")
	public void addPriceListToChangeSet() {
		activityToolbar.clickPriceListManagementButton();
		selectChangeSet(this.changeSetName);
	}

	/**
	 * Descriptive step.
	 */
	@And("^the user (?:admin|csruser) (?:does not |does )have changeset permission$")
	public void doNothing() {
		//Descriptive step
	}

	/**
	 * Verifies brand in change set.
	 *
	 * @param brandName brand name
	 */
	@Then("^I should see (?:deleted|edited) brand (.+) in the change set$")
	public void verifyBrandInChangeSet(final String brandName) {
		verifyBrandExistsInChangeSetEditor(brandName);
	}

	/**
	 * click on Open Change Set to verify updated brand name.
	 *
	 * @param brandName brand name
	 */
	@When("^I open object in the changeset for (.+)$")
	public void clickOpenObjectInChangeSet(final String brandName) {
		verifyBrandExistsInChangeSetEditor(brandName);
		addEditBrandDialog = changeSetEditor.clickOpenObjectButton();
	}

	/**
	 * Edit Brand name from change set.
	 *
	 * @param newBrandName New brand name.
	 */
	@When("^I change brand name to (.+)$")
	public void editBrandNameInChangeSet(final String newBrandName) {
		addEditBrandDialog.enterBrandName(newBrandName);
		this.catalog.setBrand(newBrandName);
		addEditBrandDialog.clickAddButton();
	}

	/**
	 * Verifies brand in change set.
	 */
	@Then("^I should see (?:newly created|edited|deleted) brand in the change set$")
	public void verifyNewBrandInChangeSet() {
		verifyBrandExistsInChangeSetEditor(this.catalog.getBrand());
	}

	private void verifyBrandExistsInChangeSetEditor(final String brandname) {
		selectNewChangeSetEditor();
		changeSetEditor.selectObjectsTab();
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.verifyObjectExists(brandname, OBJECT_NAME);
	}

	/**
	 * Descriptive step.
	 */
	@Then("^total number of change set objects should match the number of category items added to the change set$")
	public void verifyNumberOfCategoryItems() {
		changeSetEditor.verifyNumberOfChangeSetObjects(CatalogProductListingPane.getNumberOfCategoryItems());
	}

	/**
	 * Create change set.
	 *
	 * @param changeSetName change set name
	 */
	private void createChangeSet(final String changeSetName) {
		activityToolbar.clickChangeSetButton();
		clickSearchButton();
		clickCreateButton();
		this.changeSetName = changeSetName + "_" + Utility.getRandomUUID();
		createChangeSetDialog.enterChangeSetName(this.changeSetName);
		createChangeSetDialog.clickFinishButton();
	}

	/**
	 * Create change set.
	 *
	 * @param changeSetName change set name
	 */
	private void createSecondChangeSet(final String changeSetName) {
		clickCreateButton();
		this.secondChangeSetName = changeSetName + "_" + Utility.getRandomUUID();
		createChangeSetDialog.enterChangeSetName(this.secondChangeSetName);
		createChangeSetDialog.clickFinishButton();
	}

	/**
	 * Searches change set by name.
	 *
	 * @param changeSetName string variable for change set name.
	 */
	private void searchChangeSetByName(final String changeSetName) {
		int counter = 0;
		changeSet.searchChangeSetByName(changeSetName);

		while (!changeSetSearchResultPane.isChangeSetInList(changeSetName) && counter < RETRY_COUNTER) {
			changeSetSearchResultPane.sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			changeSet.searchChangeSetByName(changeSetName);
			counter++;
		}

		assertThat(changeSetSearchResultPane.isChangeSetInList(changeSetName))
				.as("Changeset '" + changeSetName + "' is not in search result pane as expected")
				.isTrue();
	}

}
