package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateShippingServiceLevelDialog;
import com.elasticpath.selenium.dialogs.EditShippingServiceLevelDialog;
import com.elasticpath.selenium.util.Constants;

/**
 * Shipping Service Level search results pane.
 */
public class ShippingServiceLevelSearchResultPane extends AbstractPageObject {

	private static final String SHIPPING_SERVICE_LEVEL_TAB_CSS = "div[widget-id='Shipping Service Level Search "
			+ "Results'][appearance-id='ctab-item'][seeable='true']";
	private static final String OPEN_SHIPPING_SERVICE_LEVEL_BUTTON_CSS = "div[widget-id='Open Service "
			+ "Level'][widget-type='ToolItem'][seeable='true']";
	private static final String CREATE_SHIPPING_SERVICE_LEVEL_BUTTON_CSS = "div[widget-id='Create Service "
			+ "Level'][widget-type='ToolItem'][seeable='true']";
	private static final String DELETE_SHIPPING_SERVICE_LEVEL_BUTTON_CSS = "div[widget-id='Delete Service "
			+ "Level'][widget-type='ToolItem'][seeable='true']";
	private static final String SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_PARENT_CSS = "div[widget-id='Shipping Levels Search "
			+ "Results'][widget-type='Table'] ";
	private static final String SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_LIST = "div[column-id='%s']";
	private static final String SHIPPING_SERVICE_LEVEL_CODE_COLUMN_NAME = "Service Level Code";
	private static final String SHIPPING_SERVICE_LEVEL_NAME_COLUMN_NAME = "Service Level Name";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ShippingServiceLevelSearchResultPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Selects Create Service Level Search Results tab.
	 */
	public void clickCreateServiceLevelResultsTab() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(SHIPPING_SERVICE_LEVEL_TAB_CSS)));
	}

	/**
	 * Clicks Open Service Level button.
	 *
	 * @return the CreateShippingServiceLevelDialog.
	 */
	public EditShippingServiceLevelDialog clickOpenServiceLevelResultsTab() {
		clickButton(OPEN_SHIPPING_SERVICE_LEVEL_BUTTON_CSS, "Open Service Level", EditShippingServiceLevelDialog
				.EDIT_SHIPPING_SERVICE_LEVEL_PARENT_CSS);
		return new EditShippingServiceLevelDialog(getDriver());
	}

	/**
	 * Clicks Create Service Level button.
	 *
	 * @return the CreateShippingServiceLevelDialog.
	 */
	public CreateShippingServiceLevelDialog clickCreateServiceLevelButton() {
		clickButton(CREATE_SHIPPING_SERVICE_LEVEL_BUTTON_CSS, "Create Service Level", CreateShippingServiceLevelDialog
				.CREATE_SHIPPING_SERVICE_LEVEL_PARENT_CSS);
		return new CreateShippingServiceLevelDialog(getDriver());
	}

	/**
	 * Verifies if shipping service level exists.
	 *
	 * @param shippingServiceLevelCode the shipping service level code.
	 */
	public void verifyShippingServiceLevelExists(final String shippingServiceLevelCode) {
		assertThat(selectItemInCenterPane(SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_PARENT_CSS, SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_LIST,
				shippingServiceLevelCode, SHIPPING_SERVICE_LEVEL_CODE_COLUMN_NAME))
				.as("Shipping Service Level does not exist in search result - " + shippingServiceLevelCode)
				.isTrue();
	}

	/**
	 * Verifies if shipping service level exists.
	 *
	 * @param shippingServiceLevelName String
	 */
	public void verifyShippingServiceLevelExistsByName(final String shippingServiceLevelName) {
		assertThat(selectItemInCenterPane(SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_PARENT_CSS, SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_LIST,
				shippingServiceLevelName, SHIPPING_SERVICE_LEVEL_NAME_COLUMN_NAME))
				.as("Shipping Service Level does not exist in search result - " + shippingServiceLevelName)
				.isTrue();
	}

	/**
	 * Verifies if shipping service level exists.
	 *
	 * @param shippingServiceLevelCode the shipping service level code.
	 * @return true if in list.
	 */
	public boolean isShippingServiceLevelInList(final String shippingServiceLevelCode) {
		return selectItemInCenterPane(SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_PARENT_CSS, SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_LIST,
				shippingServiceLevelCode, SHIPPING_SERVICE_LEVEL_CODE_COLUMN_NAME);
	}

	/**
	 * Verifies if service level is deleted.
	 *
	 * @param shippingServiceLevelCode the shipping service level code.
	 */
	public void verifyShippingServiceLevelIsDeleted(final String shippingServiceLevelCode) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(selectItemInCenterPane(SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_PARENT_CSS, SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_LIST,
				shippingServiceLevelCode, SHIPPING_SERVICE_LEVEL_CODE_COLUMN_NAME))
				.as("Delete failed, Shipping Service Level is still in the list - " + shippingServiceLevelCode)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Clicks Delete Service Level button.
	 *
	 * @return the DeleteConfirmDialog.
	 */
	public ConfirmDialog clickDeleteServiceLevelButton() {
		clickButton(DELETE_SHIPPING_SERVICE_LEVEL_BUTTON_CSS, "Delete Service Level");
		return new ConfirmDialog(getDriver());
	}

	/**
	 * Select Service Level by code.
	 *
	 * @param shippingServiceLevelCode the shipping service level code.
	 */
	public void selectServiceLevelByCode(final String shippingServiceLevelCode) {
		selectItemInCenterPane(SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_PARENT_CSS, SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_LIST,
				shippingServiceLevelCode, SHIPPING_SERVICE_LEVEL_CODE_COLUMN_NAME);
	}

	/**
	 * Select Service Level by name.
	 *
	 * @param shippingServiceLevelName the shipping service level name.
	 */
	public void selectServiceLevelByName(final String shippingServiceLevelName) {
		selectItemInCenterPane(SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_PARENT_CSS, SHIPPING_SERVICE_LEVEL_SEARCH_RESULT_LIST,
				shippingServiceLevelName, SHIPPING_SERVICE_LEVEL_NAME_COLUMN_NAME);
	}
}
