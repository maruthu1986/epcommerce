package com.elasticpath.cucumber.definitions;

import java.util.List;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.domainobjects.Report;
import com.elasticpath.selenium.navigations.Reporting;
import com.elasticpath.selenium.resultspane.ReportPane;
import com.elasticpath.selenium.setup.SetUp;

/**
 * Reporting steps.
 */
public class ReportingDefinition {

	private final Reporting reporting;
	private final NavigationDefinition navigationDefinition;
	private ReportPane reportPane;
	private int initialNumberOfOrders;

	/**
	 * Constructor.
	 */
	public ReportingDefinition() {
		reporting = new Reporting(SetUp.getDriver());
		navigationDefinition = new NavigationDefinition();
	}

	/**
	 * Selects report type.
	 *
	 * @param reportType the report type
	 */
	@When("^I select report type (.+)$")
	public void selectReportType(final String reportType) {
		reporting.selectReportType(reportType);
	}

	/**
	 * Selects store.
	 *
	 * @param store the store
	 */
	@And("^I select store (.+) for report type$")
	public void selectStore(final String store) {
		reporting.selectStore(store);
	}

	/**
	 * Selects report type and store.
	 *
	 * @param reportType the report type
	 * @param store      the store
	 */
	@And("^in reporting activity I select the report type (.+) for store (.+)$")
	public void selectReportTypeAndStore(final String reportType, final String store) {
		navigationDefinition.clickReporting();
		selectReportType(reportType);
		selectStore(store);
	}

	/**
	 * Selects currency.
	 *
	 * @param currency the currency
	 */
	@And("^I select currency (.+)$")
	public void selectCurrency(final String currency) {
		reporting.selectCurrency(currency);
	}

	/**
	 * Selects reporting activity, report type, store, currency and order status.
	 *
	 * @param reportOptionList list of report options
	 */
	@And("^I select following report options$")
	public void selectReportOptions(final List<Report> reportOptionList) {
		Report reportOptions = reportOptionList.get(0);
		navigationDefinition.clickReporting();
		selectReportTypeAndStore(reportOptions.getReportType(), reportOptions.getStore());
		if (reportOptions.getCurrency() != null) {
			selectCurrency(reportOptions.getCurrency());
		}
		if (reportOptions.getOrderStatus() != null) {
			for (String orderStatus : reportOptions.getOrderStatusList()) {
				checkOrderStatusBox(orderStatus);
			}
		}
	}

	/**
	 * Enters 'from' date.
	 */
	@And("^I enter from date for the report$")
	public void enterFromDate() {
		reporting.enterFromDate();
	}

	/**
	 * Enters 'to' date.
	 */
	@And("^I enter to date for the report$")
	public void enterToDate() {
		reporting.enterToDate();
	}

	/**
	 * Enters 'from' and 'to' date.
	 */
	@And("^I enter from and to date for the report$")
	public void enterFromAndToDate() {
		reporting.enterFromDate();
		reporting.enterToDate();
	}

	/**
	 * Click on run report button.
	 */
	@And("^I run the report")
	public void clickRunReportButton() {
		reportPane = reporting.clickRunReportButton();
	}

	/**
	 * Enters from/to dates and clicks on run report button.
	 */
	@And("^I enter dates and run the report$")
	public void enterDatesAndRunReport() {
		enterFromAndToDate();
		clickRunReportButton();
	}


	/**
	 * Sets the initialNumberOfOrders for promotion report.
	 *
	 * @param value column value
	 */
	@And("^I view the number of orders for promotion (.+)$")
	public void getPromoReportInitialOrderNumber(final String value) {
		initialNumberOfOrders = reportPane.getPromoReportNumberOfOrders(value);
	}

	/**
	 * Sets the initialNumberOfOrders for order summary report.
	 */
	@And("^I view the number of orders for order summary$")
	public void getOrderSummaryInitialOrderNumber() {
		initialNumberOfOrders = reportPane.getOrderSummaryReportNumberOfOrders();
	}

	/**
	 * Click on run report button.
	 *
	 * @param reportName the report name
	 */
	@And("^I view the (.+) report$")
	public void viewReport(final String reportName) {
		reportPane = reporting.clickRunReportButton();
	}

	/**
	 * Verifies number of orders for Shopping Cart Promotion Usage report.
	 *
	 * @param promoName the promotion name
	 */
	@Then("^the number of orders for promotion (.+) should have increased$")
	public void verifyOrderNumber(final String promoName) {
		reportPane.verifyPromoReportNumberOfOrders(promoName, initialNumberOfOrders);
	}

	/**
	 * Verifies number of orders for Order Summary report.
	 */
	@Then("^the number of orders for Order Summary report should have increased$")
	public void verifyOrderSummaryOrderNumber() {
		reportPane.verifyNumberOfOrdersInOrderSummaryReport(initialNumberOfOrders);
	}

	/**
	 * Checks the order status box.
	 *
	 * @param orderStatus the order status
	 */
	@And("^I check the order status '(.+)' checkbox$")
	public void checkOrderStatusBox(final String orderStatus) {
		reporting.checkOrderStatusBox(orderStatus);
	}

	/**
	 * Unchecks the order status box.
	 *
	 * @param orderStatus the order status
	 */
	@And("^I uncheck the order status '(.+)' checkbox$")
	public void uncheckOrderStatusBox(final String orderStatus) {
		reporting.uncheckOrderStatusBox(orderStatus);
	}

	/**
	 * Verifies order number in Returns and Exchanges report.
	 */
	@Then("^Returns And Exchanges report should contain the returned order number$")
	public void verifyOrderNumber() {
		reportPane.verifyOrderNumberInReturnAndExchangeReport();
	}

	/**
	 * Verifies order total number in Order Status report.
	 *
	 * @param orderTotal the order total
	 */
	@Then("^the latest order total should be (.+) in Order Status report$")
	public void verifyOrderTotal(final String orderTotal) {
		reportPane.verifyOrderTotalInOrderStatusReport(orderTotal);
	}

	/**
	 * Enters 'user id' Customer data report.
	 *
	 * @param userId customer data report.
	 */
	@And("^I run Customer Personal Data report for customer (.+)$")
	public void enterUserId(final String userId) {
		reporting.enterUserId(userId);
		clickRunReportButton();
	}

	/**
	 * Enters empty value in user id Customer data report.
	 */
	@And("^I enter empty value in User ID$")
	public void enterEmptyUserId() {
		reporting.enterUserId(" ");
	}

	/**
	 * Verify Validations displayed for User ID.
	 */
	@And("^I can see validation error messages for User ID$")
	public void verifyUserIdValidation() {
		reporting.verifyUserIdValidation();
	}

	/**
	 * Verify 'user id' Customer data report.
	 *
	 * @param userId customer data report.
	 */
	@And("^the User ID (.+) is in the Customer Personal Data report$")
	public void verifyReportUserId(final String userId) {
		reportPane.verifyReportUserId(userId);

	}

	/**
	 * Verify Customer data report content is empty.
	 */
	@And("^the Report is empty$")
	public void verifyReportContentIsEmpty() {
		reportPane.verifyReportContentIsEmpty();
	}

	/**
	 * Verify Customer data report content.
	 *
	 * @param dataPointListValues in customer data report.
	 */
	@And("^the Report Content shows following Data Points$")
	public void verifyReportContent(final List<String> dataPointListValues) {
		for (String nameField : dataPointListValues) {
			reportPane.verifyReportContent(nameField);
		}
	}
}
