package com.elasticpath.cucumber.definitions;

import java.util.List;
import java.util.Map;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.editor.CustomerEditor;
import com.elasticpath.selenium.navigations.CustomerService;
import com.elasticpath.selenium.resultspane.CustomerSearchResultsPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ActivityToolbar;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.selenium.util.DBConnector;

/**
 * Customer step definition.
 */
public class CustomerDefinition {
	private final CustomerService customerService;
	private CustomerSearchResultsPane customerSearchResultsPane;
	private CustomerEditor customerEditor;
	private final ActivityToolbar activityToolbar;

	/**
	 * Constructor.
	 */
	public CustomerDefinition() {
		customerService = new CustomerService(SetUp.getDriver());
		activityToolbar = new ActivityToolbar(SetUp.getDriver());
	}

	/**
	 * Click customer tab.
	 */
	@When("^I select Customers tab")
	public void clicksCustomersTab() {
		customerService.clickCustomersTab();
	}

	/**
	 * Search for customer.
	 *
	 * @param customerEmailID the customer email id.
	 */
	@When("^I search for customer with email ID (.+)$")
	public void searchCustomer(final String customerEmailID) {
		clicksCustomersTab();
		enterCustomerEmailID(customerEmailID);
		clickCustomerSearch();

		int index = 0;

		while (!customerSearchResultsPane.isCustomerInList(customerEmailID, "Email Address") && index < Constants.UUID_END_INDEX) {
			customerSearchResultsPane.sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			clickCustomerSearch();
			index++;
		}
	}

	/**
	 * Verify Customer exists.
	 *
	 * @param expectedCustomerID the expected customer Id.
	 */
	@Then("^I should see customer with email ID (.+) in result list$")
	public void verifyCustomerExists(final String expectedCustomerID) {
		customerSearchResultsPane.verifyCustomerExists(expectedCustomerID);
	}

	/**
	 * Search and open Customer editor.
	 *
	 * @param customerEmailID the customer email id.
	 */
	@When("^I search and open customer editor for email ID (.+)$")
	public void openCustomerEditor(final String customerEmailID) {
		activityToolbar.clickCustomerServiceButton();
		customerService.clickCustomersTab();
		enterCustomerEmailID(customerEmailID);
		clickCustomerSearch();
		customerEditor = customerSearchResultsPane.selectAndOpenCustomerEditor(customerEmailID);
	}

	/**
	 * Search for customer by phone number.
	 *
	 * @param phoneNumber phone number.
	 */
	@When("^I search for customer by phone number (.+)$")
	public void searchCustomerByPhone(final String phoneNumber) {
		activityToolbar.clickCustomerServiceButton();
		customerService.clickCustomersTab();
		customerService.clearInputFieldsInCustomersTab();
		enterPhoneNumber(phoneNumber);
		clickCustomerSearch();

		int index = 0;
		while (!customerSearchResultsPane.isCustomerInList(phoneNumber, "Telephone #") && index < Constants.UUID_END_INDEX) {
			customerSearchResultsPane.sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			clickCustomerSearch();
			index++;
		}
	}

	/**
	 * Select Customer editor tab.
	 *
	 * @param tabName the tab name.
	 */
	@When("^I select (.+) tab in the Customer Editor$")
	public void selectCustomerEditorTab(final String tabName) {
		customerEditor.clickTab(tabName);
	}

	/**
	 * Updates customer profile phone number.
	 *
	 * @param phoneNumber phone number.
	 */
	@When("^I update the phone number to (.+)$")
	public void updatePhoneNumber(final String phoneNumber) {
		customerEditor.enterPhoneNumber(phoneNumber);
		activityToolbar.saveAll();
		activityToolbar.clickReloadActiveEditor();
		customerEditor.closeCustomerEditor();
		customerSearchResultsPane.closePane("Customer Search Results");
	}


	private void enterCustomerEmailID(final String customerEmailID) {
		customerService.enterEmailUserID(customerEmailID);
	}

	private void enterPhoneNumber(final String phoneNumber) {
		customerService.enterPhoneNumber(phoneNumber);
	}

	private void clickCustomerSearch() {
		customerSearchResultsPane = customerService.clickCustomerSearch();
	}

	/**
	 * Select Data Policy to view.
	 *
	 * @param dataPolicyName the Data Policy name.
	 */
	@When("^I view Data Points for Data Policy (.+)$")
	public void viewDataPoints(final String dataPolicyName) {
		customerEditor.verifyDataPolicyExists(dataPolicyName);
		customerEditor.clickViewDataPointsButton();
	}

	/**
	 * Verifies data point value per data point.
	 * @param customerDataMap map
	 */
	@Then("^the following data points (?:captured correct customer data|remain captured)$")
	public void verifyDataPointsValue(final Map<String, String> customerDataMap) {
		for (String key : customerDataMap.keySet()) {
			customerEditor.verifyDataPointValue(key, customerDataMap.get(key));
		}
	}

	/**
	 * Select Data Policy to Delete Data points.
	 *
	 * @param dataPolicyName the Data Policy name.
	 */
	@When("^I Delete Data Points for Data Policy (.+)$")
	public void deleteDataPoints(final String dataPolicyName) {
		customerEditor.verifyDataPolicyExists(dataPolicyName);
		customerEditor.clickDeletePolicyDataButton();
	}

	/**
	 * Ensure Data Point Values in the Table are empty.
	 *
	 * @param dataPointNameList List of Data Point Names.
	 */
	@Then("^Data Point Values are empty for following Removable Data Points$")
	public void verifyDataPointValueIsEmpty(final List<String> dataPointNameList) {
		for (String nameField : dataPointNameList) {
			customerEditor.verifyDataPointValueIsEmpty(nameField);
		}
	}

	/**
	 * Ensure Data Point Values are set to Hyphen.
	 *
	 * @param dataPointNameList List of Data Point Names.
	 */
	@Then("^Data Point Values are set to Hyphen for following Removable Data Points$")
	public void verifyDataPointValueSetToHyphen(final List<String> dataPointNameList) {
		for (String nameField : dataPointNameList) {
			customerEditor.verifyDataPointValueSetToHyphen(nameField);
		}
	}

	/**
	 * Select Show disabled data policies.
	 */
	@When("^I click on Show Disabled Data Policies$")
	public void selectShowDisabledDataPolicy() {
		customerEditor.selectShowDisabledDataPolicy();
	}

	/**
	 * Data Policy should not be visible in the Table.
	 *
	 * @param dataPolicyName the Data Policy name.
	 */
	@When("^Disabled Data Policy (.+) should not be visible$")
	public void verifyDataPolicyIsNotExists(final String dataPolicyName) {
		customerEditor.verifyDataPolicyIsNotExists(dataPolicyName);

	}

	/**
	 * Verify Data Policy is present in the table.
	 *
	 * @param dataPolicyName the Data Policy name.
	 */
	@When("^I should see Disabled Data Policy (.+)$")
	public void verifyDataPolicyExists(final String dataPolicyName) {
		customerEditor.verifyDataPolicyExists(dataPolicyName);
		resetDataPolicyState(dataPolicyName);
	}

	/**
	 * Resets Data Policy State.
	 * @param dataPolicyName String.
	 */
	private void resetDataPolicyState(final String dataPolicyName) {
		DBConnector dbConnector = new DBConnector();
		dbConnector.executeUpdateQuery("UPDATE TDATAPOLICY SET STATE='1', END_DATE=NULL WHERE POLICY_NAME='" + dataPolicyName + "';");
		dbConnector.closeAll();
	}

}
