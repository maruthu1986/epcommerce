package com.elasticpath.cucumber.definitions.dst;

import static org.assertj.core.api.Assertions.assertThat;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.domainobjects.DST;
import com.elasticpath.selenium.navigations.CatalogManagement;
import com.elasticpath.selenium.navigations.PriceListManagement;
import com.elasticpath.selenium.resultspane.CatalogSearchResultPane;
import com.elasticpath.selenium.resultspane.PriceListsResultPane;
import com.elasticpath.selenium.setup.PublishEnvSetUp;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ActivityToolbar;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.sync.SyncConfig;

/**
 * DST steps.
 */
public class DSTDefinition {

	private static final Object LOCKOBJ = new Object();
	private final DST dst;
	private static WebDriver driver;
	private CatalogManagement catalogManagement;
	private CatalogSearchResultPane catalogSearchResultPane;

	/**
	 * Constructor.
	 *
	 * @param dst the DST class
	 */
	public DSTDefinition(final DST dst) {
		this.dst = dst;
	}

	/**
	 * Runs sync command.
	 */
	@And("^I run the data sync tool$")
	public void runSyncTool() {
		SyncConfig syncConfig = new SyncConfig();
		syncConfig.writeToSyncConfig();
		syncConfig.runDataSync(dst.getChangeSetGuid());

		assertThat(syncConfig.getExitValue())
				.as("Sync failed........")
				.isEqualTo(0);
	}

	/**
	 * Click the publish environment price list manager.
	 */
	@When("^I go to the publish environment Price List Manager$")
	public void clickPublishPriceListManager() {
		new ActivityToolbar(getDriver()).clickPriceListManagementButton();
	}

	/**
	 * Click the publish environment catalog management.
	 */
	@When("^I go to the publish environment Catalog Management$")
	public void clickPublishCatalogManagement() {
		new ActivityToolbar(getDriver()).clickCatalogManagementButton();
	}

	/**
	 * Verify price list in publish environment.
	 */
	@Then("^I should see the new price list in the publish environment")
	public void verifyNewlyCreatedPriceList() {
		new PriceListManagement(getDriver()).clickPriceListsSearch();
		new PriceListsResultPane(getDriver()).verifyPriceListExists(dst.getPriceListName());
	}

	/**
	 * Verify price list is deleted.
	 */
	@Then("^the deleted price list no longer exists in publish environment$")
	public void verifyPriceListIsDeleted() {
		PriceListsResultPane priceListsResultPane = new PriceListManagement(getDriver()).clickPriceListsSearch();
		priceListsResultPane.verifyPriceListDeleted(dst.getPriceListName());
	}

	/**
	 * Verifies newly created product exists in publish environment.
	 */
	@Then("^I should see the new (?:product|bundle) in publish environment$")
	public void verifyNewlyCreatedProductExists() {
		String productName = dst.getProductName();
		catalogManagement = new CatalogManagement(getDriver());
		catalogManagement.clickCatalogSearchTab();
		searchProductByName(productName);
		catalogSearchResultPane = new CatalogSearchResultPane(getDriver());

		int index = 0;
		while (!catalogSearchResultPane.isProductInList(productName) && index < Constants.UUID_END_INDEX) {
			catalogSearchResultPane.sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			searchProductByName(productName);
			index++;
		}
		catalogSearchResultPane.setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_THREE_SECONDS);
		catalogSearchResultPane.verifyProductNameExists(productName);
		catalogSearchResultPane.setWebDriverImplicitWaitToDefault();
	}

	private void searchProductByName(final String productName) {
		catalogManagement.enterProductName(productName);
		catalogManagement.clickCatalogSearch();
	}

	/**
	 * Verify product is deleted.
	 */
	@Then("^the deleted (?:product|bundle) no longer exists in publish environment$")
	public void verifyProductIsDeleted() {
		catalogSearchResultPane.closePane("Product Search Results");
		searchProductByName(dst.getProductName());
		catalogSearchResultPane.verifyProductIsDeleted(dst.getProductName());
	}

	/**
	 * Switches to Author window.
	 */
	@And("^I switch to author environment$")
	public static void switchToAuthorWindow() {
		SetUp.getDriver().switchTo().window(SetUp.getDriver().getWindowHandle());
	}

	/**
	 * Switches to Publish window.
	 */
	@And("^I switch to publish environment$")
	public static void switchToPublishWindow() {
		getDriver().switchTo().window(getDriver().getWindowHandle());
	}

	private static WebDriver getDriver() {
		synchronized (LOCKOBJ) {
			driver = PublishEnvSetUp.getDriver();
		}
		return driver;
	}

}
