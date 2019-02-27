package com.elasticpath.selenium.toolbars;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.dialogs.UserMenuDialog;
import com.elasticpath.selenium.navigations.CatalogManagement;
import com.elasticpath.selenium.navigations.ChangeSet;
import com.elasticpath.selenium.navigations.Configuration;
import com.elasticpath.selenium.navigations.CustomerService;
import com.elasticpath.selenium.navigations.PriceListManagement;
import com.elasticpath.selenium.navigations.PromotionsShipping;
import com.elasticpath.selenium.navigations.Reporting;
import com.elasticpath.selenium.navigations.ShippingReceiving;

/**
 * Top Navigation class.
 */
public class ActivityToolbar extends AbstractToolbar {

	private static final String TOOLBAR_APPEARANCE_ID_CSS = "div[appearance-id='toolbar-button']";
	private static final String TOOLBAR_CATALOG_MANAGEMENT_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Catalog Management']";
	private static final String TOOLBAR_PRICE_LIST_MANAGER_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Price List Manager']";
	private static final String TOOLBAR_PROMOTIONS_SHIPPING_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Promotions/Shipping']";
	private static final String TOOLBAR_CUSTOMER_SERVICE_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Customer Service']";
	private static final String TOOLBAR_SHIPPING_RECEIVING_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Shipping/Receiving']";
	private static final String TOOLBAR_REPORTING_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Reporting']";
	private static final String TOOLBAR_CONFIGURATION_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Configuration']";
	private static final String TOOLBAR_CHANGE_SET_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Change Set']";
	private static final String CHANGESET_TOOL_ITEM
			= "div[widget-id*='Change Set'][widget-type='ToolItem']";
	private static final String USER_MENU_BUTTON_CSS = "div[widget-id='User Menu']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ActivityToolbar(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Wait for the page to be interactable. After it has loaded, and after the Busy indicator is gone.
	 */
	public void waitForPage() {
		getWaitDriver().waitForElementToBeInteractable("[widget-id='User Menu']");

	}

	/**
	 * Clicks on Configuration button.
	 *
	 * @return Configuration
	 */
	public Configuration clickConfigurationButton() {
		clickToolbarButton(TOOLBAR_CONFIGURATION_BUTTON_CSS, "Configuration");
		return new Configuration(getDriver());
	}

	/**
	 * Clicks on Customer Service button.
	 *
	 * @return CustomerService
	 */
	public CustomerService clickCustomerServiceButton() {
		clickToolbarButton(TOOLBAR_CUSTOMER_SERVICE_BUTTON_CSS, "Customer Service");
		return new CustomerService(getDriver());
	}


	/**
	 * Clicks on Catalog Management button.
	 *
	 * @return CatalogManagement
	 */
	public CatalogManagement clickCatalogManagementButton() {
		clickToolbarButton(TOOLBAR_CATALOG_MANAGEMENT_BUTTON_CSS, "Catalog Management");
		return new CatalogManagement(getDriver());
	}

	/**
	 * Clicks on Price List Manager button.
	 *
	 * @return PriceListManagement
	 */
	public PriceListManagement clickPriceListManagementButton() {
		clickToolbarButton(TOOLBAR_PRICE_LIST_MANAGER_BUTTON_CSS, "Price List Manager");
		return new PriceListManagement(getDriver());
	}

	/**
	 * Clicks on Promotions Shipping button.
	 *
	 * @return StoreMarketing
	 */
	public PromotionsShipping clickPromotionsShippingButton() {
		clickToolbarButton(TOOLBAR_PROMOTIONS_SHIPPING_BUTTON_CSS, "Promotions/Shipping");
		return new PromotionsShipping(getDriver());
	}

	/**
	 * Clicks on Shipping/Receiving button.
	 *
	 * @return ShippingReceiving
	 */
	public ShippingReceiving clickShippingReceivingButton() {
		clickToolbarButton(TOOLBAR_SHIPPING_RECEIVING_BUTTON_CSS, "Shipping Receiving");
		return new ShippingReceiving(getDriver());
	}

	/**
	 * Clicks on Reporting button.
	 *
	 * @return Reporting
	 */
	public Reporting clickReportingButton() {
		clickToolbarButton(TOOLBAR_REPORTING_BUTTON_CSS, "Reporting");
		return new Reporting(getDriver());
	}

	/**
	 * Clicks on Change Set button.
	 *
	 * @return ChangeSet
	 */
	public ChangeSet clickChangeSetButton() {
		clickToolbarButton(TOOLBAR_CHANGE_SET_BUTTON_CSS, "Change Set");
		return new ChangeSet(getDriver());
	}

	/**
	 * Verifies Catalog Management button is not present.
	 */
	public void verifyCatalogManagementButtonIsNotPresent() {
		setWebDriverImplicitWait(1);
		assertThat(isElementPresent(By.cssSelector(TOOLBAR_CATALOG_MANAGEMENT_BUTTON_CSS)))
				.as("Catalog Management access is still enabled for restricted user role.")
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verifies Price List Manager button is not present.
	 */
	public void verifyPriceListManagerButtonIsNotPresent() {
		setWebDriverImplicitWait(1);
		assertThat(isElementPresent(By.cssSelector(TOOLBAR_PRICE_LIST_MANAGER_BUTTON_CSS)))
				.as("Price List Manager access is still enabled for restricted user role.")
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verifies Configuration button is not present.
	 */
	public void verifyConfigurationButtonIsNotPresent() {
		setWebDriverImplicitWait(1);
		assertThat(isElementPresent(By.cssSelector(TOOLBAR_CONFIGURATION_BUTTON_CSS)))
				.as("Configuration access is still enabled for restricted user role.")
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verifies Promotions Shipping button is not present.
	 */
	public void verifyPromotionsShippingButtonIsNotPresent() {
		setWebDriverImplicitWait(1);
		assertThat(isElementPresent(By.cssSelector(TOOLBAR_PROMOTIONS_SHIPPING_BUTTON_CSS)))
				.as("Promotions and Shipping access is still enabled for restricted user role.")
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verifies Shipping Receiving button is not present.
	 */
	public void verifyShippingReceivingButtonIsNotPresent() {
		setWebDriverImplicitWait(1);
		assertThat(isElementPresent(By.cssSelector(TOOLBAR_SHIPPING_RECEIVING_BUTTON_CSS)))
				.as("Shipping Receiving access is still enabled for restricted user role.")
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verifies Customer Service button is not present.
	 */
	public void verifyCustomerServiceButtonIsNotPresent() {
		setWebDriverImplicitWait(1);
		assertThat(isElementPresent(By.cssSelector(TOOLBAR_CUSTOMER_SERVICE_BUTTON_CSS)))
				.as("Customer Service access is still enabled for restricted user role.")
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verifies Changeset button is not present.
	 */
	public void verifyChangesetButtonNotPresent() {
		assertThat(isChangeSetEnabled())
				.as("Changeset button is present when it should not be.")
				.isFalse();
	}

	/**
	 * Verifies if change set is enabled.
	 *
	 * @return boolean
	 */
	public boolean isChangeSetEnabled() {
		setWebDriverImplicitWait(1);
		boolean isEnabled = isElementPresent(By.cssSelector(CHANGESET_TOOL_ITEM));
		setWebDriverImplicitWaitToDefault();
		return isEnabled;
	}

	/**
	 * Clicks on User Menu button.
	 *
	 * @return UserMenuDialog
	 */
	public UserMenuDialog clickUserMenu() {
		clickButton(USER_MENU_BUTTON_CSS, "User Menu", UserMenuDialog.LOGOUT_CSS);
		return new UserMenuDialog(getDriver());
	}

	/**
	 * Clicks toolbar button.
	 *
	 * @param cssSelector CSS selector
	 * @param buttonName  the button name
	 */
	private void clickToolbarButton(final String cssSelector, final String buttonName) {
		clickButton(cssSelector, buttonName);
		//to escape tooltip
		getDriver().findElement(By.cssSelector("body")).sendKeys(Keys.ESCAPE);
	}
}
