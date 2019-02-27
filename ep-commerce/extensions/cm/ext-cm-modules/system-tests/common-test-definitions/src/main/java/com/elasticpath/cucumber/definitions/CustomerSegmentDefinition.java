package com.elasticpath.cucumber.definitions;

import static org.assertj.core.api.Assertions.assertThat;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.cucumber.macros.AuthenticationMacro;
import com.elasticpath.cucumber.macros.ItemMacro;
import com.elasticpath.selenium.dialogs.AddCustomerSegmentMembershipDialog;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.editor.CustomerEditor;
import com.elasticpath.selenium.editor.CustomerSegmentEditor;
import com.elasticpath.selenium.resultspane.CustomerSegmentResultPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ActivityToolbar;
import com.elasticpath.selenium.toolbars.ConfigurationActionToolbar;
import com.elasticpath.selenium.toolbars.CustomerServiceActionToolbar;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.selenium.util.Utility;

/**
 * Customer Segment step definitions.
 */
public class CustomerSegmentDefinition {
	private CustomerSegmentResultPane customerSegmentResultPane;
	private final ConfigurationActionToolbar configurationActionToolbar;
	private CustomerSegmentEditor customerSegmentEditor;
	private final ActivityToolbar activityToolbar;
	private String uniqueCustomerSegmentName;
	private final CustomerEditor customerEditor;
	private AddCustomerSegmentMembershipDialog addCustomerSegmentMembershipDialog;
	private String customerSegmentName;
	private final CustomerServiceActionToolbar customerServiceActionToolbar;
	private String customerID = "";
	private final NavigationDefinition navigationDefinition;
	private final CustomerDefinition customerDefinition;

	/**
	 * Constructor.
	 */
	public CustomerSegmentDefinition() {
		configurationActionToolbar = new ConfigurationActionToolbar(SetUp.getDriver());
		customerEditor = new CustomerEditor(SetUp.getDriver());
		addCustomerSegmentMembershipDialog = new AddCustomerSegmentMembershipDialog(SetUp.getDriver());
		activityToolbar = new ActivityToolbar(SetUp.getDriver());
		customerServiceActionToolbar = new CustomerServiceActionToolbar(SetUp.getDriver());
		navigationDefinition = new NavigationDefinition();
		customerDefinition = new CustomerDefinition();
	}

	/**
	 * Clicks on Customer Segments.
	 */
	@When("^I go to Customer Segments$")
	public void clickCustomerSegments() {
		activityToolbar.clickConfigurationButton();
		customerSegmentResultPane = configurationActionToolbar.clickCustomerSegments();
	}

	/**
	 * Create new Customer Segment.
	 *
	 * @param description the new Customer Segments description.
	 */
	@When("^I create a customer segment with description (.+)$")
	public void createCustomerSegment(final String description) {
		uniqueCustomerSegmentName = "CS" + Utility.getRandomUUID();
		customerSegmentEditor = customerSegmentResultPane.clickCreateCustomerSegmentButton();
		customerSegmentEditor.verifyCustomerSegmentEditor();
		customerSegmentEditor.enterCustomerSegmentName(this.uniqueCustomerSegmentName);
		if (description != null) {
			customerSegmentEditor.enterCustomerSegmentDescription(description);
		}
		configurationActionToolbar.saveAll();
		this.customerSegmentName = this.uniqueCustomerSegmentName + " - " + description;
		customerSegmentEditor.closeCustomerSegmentEditor(this.uniqueCustomerSegmentName);
	}

	/**
	 * Verify newly created Customer Segment.
	 */
	@Then("^I should see the newly created customer segment$")
	public void verifyNewCustomerSegmentExists() {
		customerSegmentResultPane.verifyCustomerSegmentsExists(this.uniqueCustomerSegmentName);
	}

	/**
	 * Verify newly created Customer Segment present in the segment list.
	 */
	@Then("^the newly created Customer Segment is available in the segment list")
	public void verifyCustomerSegmentInList() {
		addCustomerSegmentMembershipDialog = customerEditor.clickAddSegmentButton();
		addCustomerSegmentMembershipDialog.verifyCustomerSegmentExists(this.customerSegmentName);
		addCustomerSegmentMembershipDialog.clickSave();
		customerEditor.selectCustomerSegment(this.uniqueCustomerSegmentName);
		customerEditor.clickRemoveSegmentButton();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("FulfillmentMessages.CustomerSegmentsPageDialog_RemoveConfirm");
		customerEditor.closeCustomerEditor();
		new ConfirmDialog(SetUp.getDriver()).clickNoButton("CoreMessages.AbstractCmClientFormEditor_OkTitle_save");
	}

	/**
	 * Delete newly created Customer Segment.
	 */
	@When("^I delete newly created customer segment$")
	public void deleteNewCustomerSegment() {
		activityToolbar.clickConfigurationButton();
		customerSegmentResultPane.deleteCustomerSegment(this.uniqueCustomerSegmentName);
	}

	/**
	 * Verify new Customer Segment no longer exists.
	 */
	@Then("^newly created customer segment no longer exists$")
	public void verifyNewCustomerSegmentIsDeleted() {
		customerSegmentResultPane.verifyCustomerSegmentIsNotInList(this.uniqueCustomerSegmentName);
	}

	/**
	 * Editing the new Customer Segment Enabled checkbox.
	 */
	@When("^I enable newly created customer segment$")
	public void editCustomerSegment() {
		customerSegmentEditor = customerSegmentResultPane.openCustomerSegmentEditor(this.uniqueCustomerSegmentName);
		customerSegmentEditor.verifyCustomerSegmentEditor();
		customerSegmentEditor.clickCustomerSegmentEnabledCheckBox();
		configurationActionToolbar.saveAll();
		customerSegmentEditor.closeCustomerSegmentEditor(this.uniqueCustomerSegmentName);
	}

	/**
	 * Verifies item price that is depending on customer segment.
	 *
	 * @param customerID          Customer ID.
	 * @param customerSegmentName Customer segment.
	 * @param price               item price.
	 * @param sku                 item sku code.
	 * @param store               store.
	 */
	@Given("^a customer (.+) who is member of segment (.+) that has item price (.+) for sku (.+) in store (.+)$")
	public void verifyItemPricePerCustomerSegment(final String customerID, final String customerSegmentName, final String price, final String sku,
												  final String store) {
		verifyItemPriceFromCortex(customerID, price, sku, store);
		this.customerID = customerID;
	}

	/**
	 * Verifies item price for registered customer.
	 *
	 * @param sku        item sku code.
	 * @param price      item price.
	 * @param customerID Customer ID.
	 * @param store      store.
	 */
	@Then("^the item price for sku (.+) is (.+) when customer (.+) retrieve the item price in store (.+)$")
	public void verifyItemPrice(final String sku, final String price, final String customerID, final String store) {
		verifyItemPriceFromCortex(customerID, price, sku, store);
	}

	private void verifyItemPriceFromCortex(final String customerID, final String price, final String sku,
										   final String store) {
		ItemMacro itemMacro = new ItemMacro();
		AuthenticationMacro authenticationMacro = new AuthenticationMacro();
		authenticationMacro.authenticateRegisteredUser(store, customerID);
		assertThat(itemMacro.retrieveItemPurchasePrice(sku))
				.as("Expected item price not match.")
				.isEqualTo(price);
	}

	/**
	 * Remove Customer Segment.
	 *
	 * @param segmentName Segment Name.
	 * @param customerID  Customer ID.
	 */
	@When("^I remove customer segment (.+) for the customer (.+)$")
	public void removeCustomerSegment(final String segmentName, final String customerID) {
		navigationDefinition.clickCustomerService();
		customerDefinition.openCustomerEditor(customerID);
		customerDefinition.selectCustomerEditorTab("Customer Segments");

		this.customerSegmentName = segmentName;
		customerEditor.selectCustomerSegment(segmentName);
		customerEditor.clickRemoveSegmentButton();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("FulfillmentMessages.CustomerSegmentsPageDialog_RemoveConfirm");
		customerServiceActionToolbar.clickSaveButton();
		configurationActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Add Customer Segment.
	 *
	 * @param segmentName Segment Name.
	 */
	@When("^I add segment (.+)$")
	public void addCustomerSegment(final String segmentName) {
		addCustomerSegmentMembershipDialog = customerEditor.clickAddSegmentButton();
		addCustomerSegmentMembershipDialog.selectCustomerSegment(segmentName);
		addCustomerSegmentMembershipDialog.clickSave();
		customerServiceActionToolbar.clickSaveButton();
		configurationActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Resets customer segment back to the default state in case of wrong state caused by test failures.
	 */
	@After(value = "@resetCustomerSegment", order = Constants.CLEANUP_ORDER_FIRST)
	public void resetCustomerSegment() {
		customerEditor.closeCustomerEditor();
		customerDefinition.openCustomerEditor(this.customerID);
		customerDefinition.selectCustomerEditorTab("Customer Segments");
		if (!customerEditor.isCustomerSegmentExists(this.customerSegmentName)) {
			//This is hardcoded because the add segment is expecting the segment name + segment description.
			addCustomerSegment("ITEST_SEGMENT - ITEST_SEGMENT");
		}
		customerEditor.selectCustomerSegment(this.customerSegmentName);
	}

}
