package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.editor.product.ProductEditor;
import com.elasticpath.selenium.navigations.CatalogManagement;
import com.elasticpath.selenium.toolbars.ChangeSetActionToolbar;
import com.elasticpath.selenium.util.Constants;

/**
 * Catalog product listing pane.
 */
public class CatalogProductListingPane extends AbstractPageObject {
	/**
	 * Parent table of Product Listing Pane.
	 */
	public static final String PRODUCT_TABLE_PARENT_CSS = "div[widget-id='Browse Product List'][widget-type='Table'] ";
	private static final String PRODUCT_ROW_COLUMN_CSS = PRODUCT_TABLE_PARENT_CSS + "div[column-id='%s']";
	private static final String EXCLUDE_PRODUCT_BUTTON_CSS = "div[widget-id='Exclude Product'][seeable='true']";
	private static final String INCLUDE_PRODUCT_BUTTON_CSS = "div[widget-id='Include Product'][seeable='true']";
	private final CatalogManagement catalogManagement;
	private final ChangeSetActionToolbar changeSetActionToolbar;
	private static int numberOfCategoryItems;
	private static final String PRODUCT_NAME = "Product Name";

	public static int getNumberOfCategoryItems() {
		return numberOfCategoryItems;
	}

	public static void setNumberOfCategoryItems(final int numOfCatItems) {
		numberOfCategoryItems = numOfCatItems;
	}

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CatalogProductListingPane(final WebDriver driver) {
		super(driver);
		catalogManagement = new CatalogManagement(driver);
		changeSetActionToolbar = new ChangeSetActionToolbar(getDriver());
	}

	/**
	 * Verifies if product exists.
	 *
	 * @param expectedProductName the expected product name.
	 */
	public void verifyProductNameExists(final String expectedProductName) {
		assertThat(selectItemInCenterPane(PRODUCT_TABLE_PARENT_CSS, PRODUCT_ROW_COLUMN_CSS, expectedProductName, PRODUCT_NAME))
				.as("Expected Product does not exist in product listing - " + expectedProductName)
				.isTrue();
	}

	/**
	 * Verifies if product exists.
	 *
	 * @param expectedProductName the expected product name.
	 * @return true if product in the list, else returns false
	 */
	public boolean isProductNameInList(final String expectedProductName) {
		return selectItemInCenterPane(PRODUCT_TABLE_PARENT_CSS, PRODUCT_ROW_COLUMN_CSS, expectedProductName, PRODUCT_NAME);
	}

	/**
	 * Verifies product does not exists.
	 *
	 * @param expectedProductName the expected product name.
	 * @param categoryName        the category name to click and retry if product still exists in category
	 */
	public void verifyProductNameNotExists(final String expectedProductName, final String categoryName) {
		setWebDriverImplicitWait(1);

		int counter = 0;
		while (selectItemInCenterPane(PRODUCT_TABLE_PARENT_CSS, PRODUCT_ROW_COLUMN_CSS, expectedProductName, PRODUCT_NAME) && counter < Constants
				.RETRY_COUNTER_3) {
			sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			closePane("Product Listing");
			catalogManagement.doubleClickCategory(categoryName);
			counter++;
		}

		assertThat(selectItemInCenterPane(PRODUCT_TABLE_PARENT_CSS, PRODUCT_ROW_COLUMN_CSS, expectedProductName, PRODUCT_NAME))
				.as("Expected Product does not exist in product listing - " + expectedProductName)
				.isFalse();

		setWebDriverImplicitWaitToDefault();
	}


	/**
	 * Close the Catalog product list ipane.
	 *
	 * @param tabName The tab name to close.
	 */
	public void close(final String tabName) {
		getWaitDriver().waitForElementToBeInteractable("[widget-id='" + tabName + "'][active-tab='true'][appearance-id='ctab-item']");
		String closeCSS = "[widget-id='" + tabName + "'][active-tab='true'][appearance-id='ctab-item'] :nth-toolTipTextChild(3)";
		WebElement element = getWaitDriver().waitForElementToBeVisible(By.cssSelector(closeCSS));
		click(element);
	}

	/**
	 * Select order and open editor.
	 *
	 * @return the order editor.
	 */
	public ProductEditor selectProductAndOpenProductEditor() {
		doubleClick(getSelectedElement(), ProductEditor.PRODUCT_EDITOR_PARENT_CSS);
		return new ProductEditor(getDriver());
	}

	/**
	 * Clicks Exclude Product.
	 */
	public void clickExcludeProductButton() {
		clickButton(EXCLUDE_PRODUCT_BUTTON_CSS, "Exclude Product");
	}

	/**
	 * Clicks Include Product.
	 */
	public void clickIncludeProductButton() {
		int retry = 0;
		while (isButtonEnabled(INCLUDE_PRODUCT_BUTTON_CSS) && retry < Constants.RETRY_COUNTER_3) {
			getDriver().findElement(By.cssSelector(INCLUDE_PRODUCT_BUTTON_CSS)).click();

			if (!isButtonEnabled(INCLUDE_PRODUCT_BUTTON_CSS)) {
				sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
				retry++;
			}
		}
	}

	/**
	 * Checks if Exclude Product button enabled.
	 *
	 * @return boolean.
	 */
	public boolean isExcludeProductButtonEnabled() {
		return isIncludeExcludeProductButtonEnabled(EXCLUDE_PRODUCT_BUTTON_CSS);
	}

	/**
	 * Checks if Include Product button enabled.
	 *
	 * @return boolean.
	 */
	public boolean isIncludeProductButtonEnabled() {
		return isIncludeExcludeProductButtonEnabled(INCLUDE_PRODUCT_BUTTON_CSS);
	}

	private boolean isIncludeExcludeProductButtonEnabled(final String cssSelector) {
		boolean isEnabled = false;
		setWebDriverImplicitWait(1);
		try {
			getDriver().findElement(By.cssSelector(cssSelector));
			isEnabled = true;
			setWebDriverImplicitWaitToDefault();
		} catch (Exception e) {
			isEnabled = false;
			setWebDriverImplicitWaitToDefault();
		}
		return isEnabled;
	}

	/**
	 * Adds all category items to a change set.
	 */
	public void addAllCategoryItemsToAChangeSet() {
		String addToChangeSetButtonCss = changeSetActionToolbar.getAddItemToChangeSetButtonCss();
		while (true) {
			setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_THREE_SECONDS);
			List<WebElement> elementList = getDriver().findElements(By.cssSelector(PRODUCT_TABLE_PARENT_CSS + "div[widget-type*='row'] > "
					+ "div[style*='.png']"));
			setWebDriverImplicitWaitToDefault();

			numberOfCategoryItems = elementList.size() + numberOfCategoryItems;

			for (int i = 0; i < elementList.size(); i++) {

				getWaitDriver().waitForButtonToBeDisabled(addToChangeSetButtonCss);
				assertThat(isButtonEnabled(addToChangeSetButtonCss))
						.as("Add item to change set button is not disabled as expected")
						.isFalse();
				elementList.get(i).click();

				int counter = 0;
				while (!isButtonEnabled(addToChangeSetButtonCss) && counter < Constants.RETRY_COUNTER_5) {
					sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
					elementList.get(i).click();
					counter++;
				}

				changeSetActionToolbar.clickAddItemToChangeSet();

				sleep(Constants.SLEEP_ONE_SECOND_IN_MILLIS);
			}
			if (isButtonEnabled(CENTER_PANE_NEXT_BUTTON_CSS)) {
				clickButton(CENTER_PANE_NEXT_BUTTON_CSS, "Next Page");
				sleep(Constants.SLEEP_ONE_SECOND_IN_MILLIS);
			} else {
				if (!elementList.isEmpty()) {
					click(elementList.get(0));
				}
				closePane("Product Listing");
				break;
			}
		}
	}

}
