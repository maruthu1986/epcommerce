package com.elasticpath.cucumber.definitions;

import java.util.concurrent.ThreadLocalRandom;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import org.apache.log4j.Logger;

import com.elasticpath.selenium.framework.util.PropertyManager;
import com.elasticpath.selenium.navigations.CatalogManagement;
import com.elasticpath.selenium.navigations.CustomerService;
import com.elasticpath.selenium.resultspane.CatalogProductListingPane;
import com.elasticpath.selenium.resultspane.OrderSearchResultPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ActivityToolbar;

/**
 * Performance Definitions.
 */
public class PerformanceDefinitions {

	private static final Logger LOGGER = Logger.getLogger(PerformanceDefinitions.class);
	private static final int STARTTIME_MINIMUM = 100;
	private static final int START_TIME_MAXIMUM = 1000;
	private final ActivityToolbar activityToolbar;
	private final CustomerService customerService;
	private OrderSearchResultPane orderSearchResultPane;
	private final CatalogManagement catalogManagement;
	private CatalogProductListingPane catalogProductListingPane;

	/**
	 * Constructor.
	 */
	public PerformanceDefinitions() {
		activityToolbar = new ActivityToolbar(SetUp.getDriver());
		customerService = new CustomerService(SetUp.getDriver());
		catalogManagement = new CatalogManagement(SetUp.getDriver());
	}

	/**
	 * Pause for a random amount of time (less than a second), to spread out load test.
	 */
	@And("^I Pause to get a coffee$")
	public void iPauseToGetACoffee() {
		int randomTime = ThreadLocalRandom.current().nextInt(STARTTIME_MINIMUM, START_TIME_MAXIMUM);
		try {
			Thread.sleep(randomTime);
		} catch (InterruptedException e) {
			LOGGER.warn(e);
		}
	}


	/**
	 * Loop test for a given amount of iterations.
	 */
	@Then("^Loop$")
	public void loopNTimes() {

		String orderNumber = "20000";
		String email = "itest.default.user@elasticpath.com";
		int lengthOfTest = 1;
		try {
			lengthOfTest = Integer.parseInt(PropertyManager.getInstance().getProperty("lengthOfTest"));
		} catch (NumberFormatException e) {
			LOGGER.warn("Could not parse `lengthOfTest` property, defaulting to 1");
		}
		LOGGER.info("Thread will run for " + lengthOfTest + " iterations");
		for (int i = 0; i < lengthOfTest; i++) {

			iPauseToGetACoffee();
			activityToolbar.waitForPage();
			activityToolbar.clickCustomerServiceButton();
			activityToolbar.waitForPage();
			customerService.enterOrderNumber(orderNumber);
			activityToolbar.waitForPage();
			orderSearchResultPane = customerService.clickOrderSearch();
			activityToolbar.waitForPage();
			orderSearchResultPane.verifyOrderColumnValueAndSelectRow(orderNumber, "Order #");
			activityToolbar.waitForPage();
			customerService.clearInputFieldsInOrdersTab();
			activityToolbar.waitForPage();
			orderSearchResultPane.close("Order Search Results");
			activityToolbar.waitForPage();
			customerService.enterEmailUserID(email);
			activityToolbar.waitForPage();
			orderSearchResultPane = customerService.clickOrderSearch();
			activityToolbar.waitForPage();
			customerService.clearInputFieldsInOrdersTab();
			activityToolbar.waitForPage();
			orderSearchResultPane.close("Order Search Results");
			activityToolbar.waitForPage();
			activityToolbar.clickCatalogManagementButton();
			activityToolbar.waitForPage();
			catalogManagement.expandCatalog("Mobile Catalog");
			activityToolbar.waitForPage();
			catalogManagement.verifyCatalogExists("Accessories");
			catalogProductListingPane = catalogManagement.doubleClickCategory("Accessories");
			activityToolbar.waitForPage();
			catalogProductListingPane.close("Product Listing");
			activityToolbar.waitForPage();
			catalogManagement.expandCatalog("Mobile Catalog");
			activityToolbar.waitForPage();

		}

	}

}
