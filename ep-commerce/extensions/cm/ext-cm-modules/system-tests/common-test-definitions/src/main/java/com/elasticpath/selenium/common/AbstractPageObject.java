package com.elasticpath.selenium.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.elasticpath.selenium.framework.pages.AbstractPage;
import com.elasticpath.selenium.framework.util.PropertyManager;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.selenium.util.FluentWaitDriver;

/**
 * Page super class. All page objects in this test framework extends the page super class.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods", "PMD.ExcessiveClassLength"})
public abstract class AbstractPageObject extends AbstractPage {

	private static final Logger LOGGER = Logger.getLogger(AbstractPageObject.class);
	private static final int POLLING_INTERVAL = 500;
	private static final int COMBO_SCROLL_REPETITIONS = 10;
	private static final int WINDOW_SIZE = 800;
	private static final int MAX_COUNT = 100;
	private final String siteURL;
	private final String publishEnvURL;
	private final FluentWaitDriver fluentWaitDriver;
	private WebElement selectedElement;
	private static final String CENTER_PANE_FIRST_BUTTON_CSS = "div[widget-id='First Page'][appearance-id='toolbar-button'][seeable='true']";
	/**
	 * Center Pane Next button.
	 */
	public static final String CENTER_PANE_NEXT_BUTTON_CSS = "div[widget-id='Next Page'][appearance-id='toolbar-button'][seeable='true']";
	private static final String CATALOG_TREE_PARENT_CSS = "div[pane-location='left-pane-inner'] div[widget-id='Catalog Browse "
			+ "Tree'][widget-type='Tree'] ";
	private static final String CATALOG_TREE_ITEM_CSS = CATALOG_TREE_PARENT_CSS + "div[row-id='%1$s'] div[column-id='%1$s']";
	private static final String CLOSE_PANE_ICON_CSS = "div[widget-id*='%s'][active-tab='true'] > div[style*='close.gif']";
	private static final long WEBDRIVER_DEFAULT_TIMEOUT = Long.parseLong(PropertyManager.getInstance().getProperty("selenium.waitdriver.timeout"));
	/**
	 * Editor Tab CSS template.
	 */
	public static final String EDITOR_PANE_TAB_CSS_TEMPLATE = "div[pane-location='editor-pane'] "
			+ "div[appearance-id='ctab-item'][widget-id='%s']";
	/**
	 * The Tab CSS.
	 */
	protected static final String TAB_CSS = "div[widget-id='%s'][widget-type='CTabItem']";
	private String lastSearchedElementInCenterPane;
	private final JavascriptExecutor jsDriver;

	/**
	 * constructor.
	 *
	 * @param driver WebDriver which drives this webpage.
	 */
	public AbstractPageObject(final WebDriver driver) {
		super(driver);

		fluentWaitDriver = new FluentWaitDriver(driver);
		super.setWaitDriver(fluentWaitDriver);

		siteURL = getPropertyManager().getProperty("selenium.session.baseurl");
		publishEnvURL = getPropertyManager().getProperty("selenium.session.publish.baseurl");
		jsDriver = (JavascriptExecutor) driver;
	}

	@Override
	public void afterInit() {
		// Do nothing
	}

	/**
	 * Get site url.
	 *
	 * @return the url.
	 */
	public String getSiteURL() {
		return siteURL;
	}

	/**
	 * Get publish site url.
	 *
	 * @return the publish url.
	 */
	public String getPublishEnvURL() {
		return publishEnvURL;
	}

	@Override
	public FluentWaitDriver getWaitDriver() {
		return fluentWaitDriver;
	}

	@Override
	public void clearAndType(final WebElement element, final String text) {
		List<WebElement> elementList = new ArrayList<>();
		elementList.add(element);
		getWaitDriver().waitForElementsToBeNotStale(elementList);

		element.clear();

		if (text != null && !text.isEmpty()) {
			element.sendKeys(text);
		}
	}

	/**
	 * Short cut for both clear the field and type in new text.
	 *
	 * @param cssString the css string
	 * @param text      the text
	 */
	public void clearAndType(final String cssString, final String text) {
		getWaitDriver().waitForElementToBeNotStale(cssString);
		getWaitDriver().waitForElementToBeInteractable(cssString);

		if (text != null && !text.isEmpty()) {
			WebElement element = getDriver().findElement(By.cssSelector(cssString));
			waitForElementToLoad(element);

			retryFunction((WebDriver driver) -> {
				WebElement webElement = driver.findElement(By.cssSelector(cssString));
				webElement.clear();
				webElement.click();
				sleep(Constants.SLEEP_HUNDRED_MILLI_SECONDS);
				webElement.sendKeys(text);
				return true;
			}, POLLING_INTERVAL, WEBDRIVER_DEFAULT_TIMEOUT);

			sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
		}
	}

	/**
	 * Sleep for a number of milliseconds.
	 *
	 * @param mills number of milliseconds.
	 */
	public void sleep(final long mills) {
		try {
			Thread.sleep(mills);
		} catch (InterruptedException e) {
			LOGGER.error(e);
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Scroll up with arrow key.
	 *
	 * @param element        The element.
	 * @param numberOfUpKeys Number of keys.
	 */
	public void scrollUpWithUpArrowKey(final WebElement element, final int numberOfUpKeys) {
		for (int i = 0; i < numberOfUpKeys; i++) {
			element.sendKeys(Keys.ARROW_UP);
		}
	}

	/**
	 * Scroll down the table.
	 *
	 * @param element          Webelement.
	 * @param numberOfDownKeys the number of keys.
	 */
	protected void scrollDownWithDownArrowKey(final WebElement element, final int numberOfDownKeys) {
		for (int i = 0; i < numberOfDownKeys; i++) {
			element.sendKeys(Keys.ARROW_DOWN);
		}
	}

	/**
	 * Close the Pane.
	 *
	 * @param textId The text id if the close pane icon.
	 */
	public void closePane(final String textId) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(CLOSE_PANE_ICON_CSS, textId))));
		waitTillElementDisappears(By.cssSelector(String.format(CLOSE_PANE_ICON_CSS, textId)));
	}

	/**
	 * Checks if button is enabled.
	 *
	 * @param buttonCss button css
	 * @return boolean
	 */
	public boolean isButtonEnabled(final String buttonCss) {
		getWaitDriver().waitForElementToBePresent(By.cssSelector(buttonCss));
		return (boolean) ((JavascriptExecutor) getDriver()).executeScript(String.format("return EPTest.isButtonEnabled(\"%s\");", buttonCss));
	}

	/**
	 * Scrolls to Table List Item.
	 *
	 * @param parentCss  the css of the table
	 * @param value      the value to search in the column
	 * @param columnName the title of the column in which to search for the value
	 * @return true if found, false if not found
	 */
	private boolean scrollToTableListItem(final String parentCss, final String value, final String columnName) {
		getWaitDriver().waitForElementToBePresent(By.cssSelector(parentCss));
		return (boolean) ((JavascriptExecutor) getDriver())
				.executeScript(String.format("return EPTest.scrollToTableItemWithText(\"%s\",\"%s\",\"%s\");", parentCss, value, columnName));
	}

	/**
	 * Scrolls to an item in the combo box.
	 *
	 * @param comboParentCss the css of the combo box.
	 * @param value          the value of the item to scroll to.
	 */
	private void scrollToComboItem(final String comboParentCss, final String value) {
		getWaitDriver().waitForElementToBePresent(By.cssSelector(comboParentCss));
		((JavascriptExecutor) getDriver()).executeScript(String.format("EPTest.scrollToComboItemWithText(\"%s\",\"%s\");", comboParentCss, value));
	}

	/**
	 * Scrolls widget into view.
	 *
	 * @param cssString The css string.
	 */
	protected void scrollWidgetIntoView(final String cssString) {
		getWaitDriver().waitForElementToBePresent(By.cssSelector(cssString));
		((JavascriptExecutor) getDriver()).executeScript(String.format("EPTest.scrollWidgetIntoView(\"%s\");", cssString));
		moveFocusToElement(getDriver().findElement(By.cssSelector(cssString)));
	}

	/**
	 * Scrolls widget into view.
	 *
	 * @param element The WebElement.
	 */
	protected void scrollWidgetIntoView(final WebElement element) {
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		jse.executeScript("arguments[0].scrollIntoView();", element);
		moveFocusToElement(element);
	}

	/**
	 * Scrolls widget into view.
	 *
	 * @param cssSelector The css selector.
	 */
	protected void scrollElementIntoView(final String cssSelector) {
		getWaitDriver().waitForElementToBeNotStale(cssSelector);
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		jse.executeScript("arguments[0].scrollIntoView();", getDriver().findElement(By.cssSelector(cssSelector)));
	}

	/**
	 * Scrolls widget into view.
	 *
	 * @param bySelector The By selector
	 * @param counter    the retry counter
	 */
	protected void scrollWidgetIntoView(final By bySelector, final int counter) {
		try {
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView();", getDriver().findElement(bySelector));
		} catch (Exception ex) {
			if (counter > 0) {
				LOGGER.warn("Exception encountered when attempting to scrollWidgetIntoView, retrying");
				LOGGER.warn(ex.getMessage());
				scrollWidgetIntoView(bySelector, counter - 1);
			}
		}
	}

	/**
	 * Verifies if element is in viewport.
	 *
	 * @param cssString the css string
	 * @return true if element is in viewport, false if it is not
	 */
	public boolean isElementInViewport(final String cssString) {
		WebElement element = getWaitDriver().waitForElementToBePresent(By.cssSelector(cssString));
		return (Boolean) ((JavascriptExecutor) getDriver()).executeScript(
				"var elem = arguments[0],                      "
						+ "  box = elem.getBoundingClientRect(),    "
						+ "  cx = box.left + box.width / 2,         "
						+ "  cy = box.top + box.height / 2,         "
						+ "  e = document.elementFromPoint(cx, cy); "
						+ "for (; e; e = e.parentElement) {         "
						+ "  if (e === elem)                        "
						+ "    return true;                         "
						+ "}                                        "
						+ "return false;", element);
	}

	/**
	 * Selects item in center pane with pagination.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the column number.
	 * @return true if value exists.
	 */
	public boolean selectItemInCenterPane(final String tableParentCss, final String tableColumnCss, final String value, final String columnName) {
		boolean valueExists = false;
		boolean isNextButtonEnabled = true;
		assertThat(getWaitDriver().waitForElementToBeInteractable(tableParentCss))
				.as("Center pane is not interactable")
				.isTrue();

		getWaitDriver().waitForElementToBeInteractable(CENTER_PANE_FIRST_BUTTON_CSS);

		if (isButtonEnabled(CENTER_PANE_FIRST_BUTTON_CSS)) {
			clickButton(CENTER_PANE_FIRST_BUTTON_CSS, "First Page");
			waitTillElementDisappears(By.cssSelector(String.format(tableColumnCss, lastSearchedElementInCenterPane)));
			getWaitDriver().waitForElementToBeInteractable(tableParentCss);
		}
		lastSearchedElementInCenterPane = value;

		while (isNextButtonEnabled) {
			getWaitDriver().waitForElementToBeInteractable(tableParentCss);

			setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_THREE_SECONDS);
			if (!isElementPresent(By.cssSelector(String.format(tableColumnCss, value)))) {
				sleep(Constants.SLEEP_FIVE_SECONDS_IN_MILLIS);
			}
			setWebDriverImplicitWaitToDefault();

			if (scrollToTableListItem(tableParentCss, value, columnName)) {
				setWebDriverImplicitWait(1);
				if (isElementPresent(By.cssSelector(String.format(tableColumnCss, value)))) {
					WebElement element = getDriver().findElement(By.cssSelector(String.format(tableColumnCss, value)));
					if (value.equals(element.getText())) {
						valueExists = true;
						LOGGER.debug("Element found - " + value);
						click(String.format(tableColumnCss, value));
						this.selectedElement = element;
						setWebDriverImplicitWaitToDefault();
						break;
					}
				}
				setWebDriverImplicitWaitToDefault();

			} else {
				isNextButtonEnabled = isButtonEnabled(CENTER_PANE_NEXT_BUTTON_CSS);
				if (isNextButtonEnabled) {
					LOGGER.debug("Navigating to next page.");
					clickButton(CENTER_PANE_NEXT_BUTTON_CSS, "Next Page");
				}
			}
			setWebDriverImplicitWaitToDefault();
		}

		return valueExists;
	}

	/**
	 * Selects item in center pane without pagination .
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the column number.
	 * @return true if selected item.
	 */
	public boolean selectItemInCenterPaneWithoutPagination(final String tableParentCss, final String tableColumnCss, final String value,
														   final String columnName) {
		return selectItem(tableParentCss, tableColumnCss, value, columnName, true, true);
	}

	/**
	 * Verifies item is not in center pane without pagination .
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the column number.
	 * @return false if item is not present.
	 */
	public boolean verifyItemIsNotInCenterPaneWithoutPagination(final String tableParentCss, final String tableColumnCss, final String value,
																final String columnName) {
		return selectItem(tableParentCss, tableColumnCss, value, columnName, true, false);
	}

	/**
	 * Selects item in editor pane without scrollbar.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the column name.
	 * @return true if selected item.
	 */
	public boolean selectItemInEditorPane(final String tableParentCss, final String tableColumnCss, final String value, final String columnName) {
		return selectItem(tableParentCss, tableColumnCss, value, columnName, false, true);
	}

	/**
	 * Verify item is not in editor pane without scrollbar.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the column name.
	 * @return false if item is not present.
	 */
	public boolean verifyItemIsNotInEditorPane(final String tableParentCss, final String tableColumnCss, final String value, final String
			columnName) {
		return selectItem(tableParentCss, tableColumnCss, value, columnName, false, false);
	}

	/**
	 * Selects item in editor pane with scrollbar for a specific column.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @return true if selected item.
	 */
	public boolean selectItemInEditorPaneWithScrollBar(final String tableParentCss, final String tableColumnCss, final String value) {
		return selectItem(tableParentCss, tableColumnCss, value, "Name", true, true);
	}

	/**
	 * Selects item in editor pane with scrollbar.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the column name.
	 * @return true if selected item.
	 */
	public boolean selectItemInEditorPaneWithScrollBar(final String tableParentCss, final String tableColumnCss, final String value, final String
			columnName) {
		return selectItem(tableParentCss, tableColumnCss, value, columnName, true, true);
	}

	/**
	 * Verifies item is not in editor pane with scrollbar for specific column name.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @return false if item is not present.
	 */
	public boolean verifyItemIsNotInEditorPaneWithScrollBar(final String tableParentCss, final String tableColumnCss, final String value) {
		return selectItem(tableParentCss, tableColumnCss, value, "Name", true, false);
	}

	/**
	 * Selects item in dialog.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the column name.
	 * @return true if selected item.
	 */
	public boolean selectItemInDialog(final String tableParentCss, final String tableColumnCss, final String value, final String columnName) {
		return selectItem(tableParentCss, tableColumnCss, value, columnName, true, true);
	}

	/**
	 * Verifies item is not in dialog.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the column number.
	 * @return false if item is not present.
	 */
	public boolean verifyItemIsNotInDialog(final String tableParentCss, final String tableColumnCss, final String value, final String columnName) {
		return selectItem(tableParentCss, tableColumnCss, value, columnName, true, false);
	}

	/**
	 * Selects item in dialog and pane.
	 *
	 * @param tableParentCss      the table parent css.
	 * @param tableColumnCss      the table column css.
	 * @param value               the value.
	 * @param columnName          the column name.
	 * @param isScrollBarPresent  is scrollbar present.
	 * @param expectedReturnValue the expected return value, true if the item is expected in the list, false if it is not.
	 * @return true if selected item.
	 */
	private boolean selectItem(final String tableParentCss, final String tableColumnCss, final String value,
							   final String columnName, final boolean isScrollBarPresent, final boolean expectedReturnValue) {
		String cleanedValue = value.replace("'", "\\'");
		boolean valueExists = false;
		if (isScrollBarPresent) {
			scrollToTableListItem(tableParentCss, cleanedValue, columnName);
		}

		if (expectedReturnValue) {
			getWaitDriver().waitForElementToBeInteractable(String.format(tableColumnCss, cleanedValue));
		}

		if (isElementPresent(By.cssSelector(String.format(tableColumnCss, cleanedValue)))) {
			if (expectedReturnValue) {
				getWaitDriver().waitForTextInElement(String.format(tableColumnCss, cleanedValue), value);
			}

			assertThat(getWaitDriver().waitForElementToBeNotStale(String.format(tableColumnCss, cleanedValue)))
					.as("Failed to get text from element - " + String.format(tableColumnCss, cleanedValue))
					.isTrue();

			if (cleanedValue.equals(getDriver().findElement(By.cssSelector(String.format(tableColumnCss, cleanedValue)))
					.getText().replace("'", "\\'"))) {
				valueExists = true;
				sleep(Constants.SLEEP_ONE_SECOND_IN_MILLIS);
				this.selectedElement = click(By.cssSelector(String.format(tableColumnCss, cleanedValue)));
			}
		}
		return valueExists;
	}

	/**
	 * Selects item from combo box.
	 *
	 * @param comboBoxParent the combo box parent css.
	 * @param value          the value.
	 * @return true if selected.
	 */
	public boolean selectComboBoxItem(final String comboBoxParent, final String value) {
		scrollToComboItem(comboBoxParent, value);
		for (int repeat = 0; repeat < COMBO_SCROLL_REPETITIONS; repeat++) {
			WebElement element = getDriver().findElement(By.cssSelector(comboBoxParent + " input"));
			if (value.equals(element.getAttribute("value").trim())) {
				this.selectedElement = element;
				return true;
			} else {
				scrollToComboItem(comboBoxParent, value);
			}
		}
		return false;
	}

	/**
	 * Selects item from catalog tree.
	 *
	 * @param catalogTreeItem the catalog tree item.
	 * @return true if selected.
	 */
	public boolean selectCatalogTreeItem(final String catalogTreeItem) {
		boolean itemExists = false;
		if (isElementPresent(By.cssSelector(String.format(CATALOG_TREE_ITEM_CSS, catalogTreeItem)))) {
			WebElement treeItem = getDriver().findElement(By.cssSelector(String.format(CATALOG_TREE_ITEM_CSS, catalogTreeItem)));
			if (catalogTreeItem.equals(treeItem.getText())) {
				click(getWaitDriver().waitForElementToBeClickable(treeItem));
				this.selectedElement = treeItem;
				itemExists = true;
			}
		}
		return itemExists;
	}

	/**
	 * Clicks button.
	 *
	 * @param cssSelector the element css selector
	 * @param buttonName  the button name
	 */
	public void clickButton(final String cssSelector, final String buttonName) {
		scrollWidgetIntoView(cssSelector);
		getWaitDriver().waitForElementToBeInteractable(cssSelector);
		verifyButtonIsEnabled(cssSelector, buttonName);
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		jse.executeScript("document.querySelector(\"" + cssSelector + "\").rwtWidget.execute();");
		sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
	}

	/**
	 * Verifies a button is enabled and then clicks it.
	 *
	 * @param cssSelector  the css Selector
	 * @param buttonName   the button name
	 * @param pageObjectId the page object id
	 */
	public void clickButton(final String cssSelector, final String buttonName, final String pageObjectId) {
		scrollWidgetIntoView(cssSelector);
		getWaitDriver().waitForElementToBeInteractable(cssSelector);
		verifyButtonIsEnabled(cssSelector, buttonName);
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		jse.executeScript("document.querySelector(\"" + cssSelector + "\").rwtWidget.execute();");
		assertThat(isElementPresent(By.cssSelector(pageObjectId)))
				.as("Clicking element " + getDriver().findElement(By.cssSelector(cssSelector)).getText() + "did not open dialog/wizard identified by"
						+ " css selector:" + pageObjectId)
				.isTrue();
	}

	/**
	 * Clicks an element and if exceptions occur, it will retry click every POLLING_INTERVAL and
	 * ignore the exceptions for a total of timeout seconds.
	 *
	 * @param element element to click
	 */
	public void click(final WebElement element) {
		scrollWidgetIntoView(element);
		retryFunction((WebDriver driver) -> {
			element.click();
			return true;
		}, POLLING_INTERVAL, WEBDRIVER_DEFAULT_TIMEOUT);
		sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
	}

	/**
	 * Retries the function func repeatedly until it returns true or an Object that is not null.
	 * one of the following occurs:
	 * <ol>
	 * <li>the function returns neither null nor false,</li>
	 * <li>the function throws an unignored exception,</li>
	 * <li>the timeout expires,
	 * <li>
	 * <li>the current thread is interrupted</li>
	 * </ol>
	 *
	 * @param func            the function to execute
	 * @param pollingInterval the polling interval
	 * @param timeout         timeout
	 */
	private void retryFunction(final Function<WebDriver, Object> func, final int pollingInterval, final long timeout) {
		setWebDriverImplicitWait(1);
		new WebDriverWait(getDriver(), timeout)
				.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS)
				.ignoring(StaleElementReferenceException.class, WebDriverException.class)
				.until(func);
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Clicks an element and if exceptions occur, it will retry click every POLLING_INTERVAL and
	 * ignore the exceptions for a total of timeout seconds.
	 * Before clicking, it will check to see if element is interactable, wait for element to load, scrolls the element into view if it's scrollable,
	 * and moves the focus to the element. Then eventually it performs the click.
	 * Takes cssSelector
	 *
	 * @param cssSelector the cssSelector.
	 */
	public void click(final String cssSelector) {
		getWaitDriver().waitForElementToBeInteractable(cssSelector);
		clickHelper(cssSelector);
	}

	/**
	 * Calls clickHelper method.
	 * <p>
	 * Takes cssSelector
	 *
	 * @param cssSelector the cssSelector.
	 */
	public void clickCheckBox(final String cssSelector) {
		clickHelper(cssSelector);
	}

	/**
	 * Calls clickHelper method to click on expand tree icon.
	 * <p>
	 * Takes cssSelector
	 *
	 * @param cssSelector the cssSelector.
	 */
	public void clickExpandTreeIcon(final String cssSelector) {
		clickHelper(cssSelector);
	}

	/**
	 * Clicks an element and if exceptions occur, it will retry click every POLLING_INTERVAL and
	 * ignore the exceptions for a total of timeout seconds.
	 * Before clicking, waits for element to load, scrolls the element into view if it's scrollable,
	 * and moves the focus to the element. Then eventually it performs the click.
	 * Takes cssSelector
	 *
	 * @param cssSelector the cssSelector.
	 */
	private void clickHelper(final String cssSelector) {
		waitForElementToLoad(getDriver().findElement(By.cssSelector(cssSelector)));
		scrollElementIntoView(cssSelector);
		moveFocusToElement(getDriver().findElement(By.cssSelector(cssSelector)));
		retryFunction((WebDriver driver) -> {
			driver.findElement(By.cssSelector(cssSelector)).click();
			return true;
		}, POLLING_INTERVAL, WEBDRIVER_DEFAULT_TIMEOUT);
		sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
	}


	/**
	 * Clicks an element and if exceptions occur, it will retry click every POLLING_INTERVAL and
	 * ignore the exceptions for a total of timeout seconds.
	 * Takes by selector
	 *
	 * @param bySelector By selector
	 * @return the webElement found
	 */
	public WebElement click(final By bySelector) {
		scrollWidgetIntoView(bySelector, Constants.RETRY_COUNTER_3);
		moveFocusToElement(getDriver().findElement(bySelector));
		retryFunction((WebDriver driver) -> {
			driver.findElement(bySelector).click();
			return true;
		}, POLLING_INTERVAL, WEBDRIVER_DEFAULT_TIMEOUT);
		sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
		return getDriver().findElement(bySelector);
	}

	/**
	 * Mouse double click.
	 *
	 * @param element the WebElement.
	 */
	public void doubleClick(final WebElement element) {
		element.click();
		Actions actions = new Actions(getDriver());
		actions.doubleClick(element).build().perform();
	}

	/**
	 * Double clicks an element and verifies that it opened the corresponding pageObject,
	 * otherwise will retry upto 3 times and fail after that.
	 *
	 * @param element      the WebElement.
	 * @param pageObjectId the page object id
	 */
	public void doubleClick(final WebElement element, final String pageObjectId) {
		doubleClick(element);

		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS);
		int count = 0;
		while (!isElementPresent(By.cssSelector(pageObjectId)) && count < Constants.RETRY_COUNTER_3) {
			doubleClick(element);
			count++;
		}
		setWebDriverImplicitWaitToDefault();

		assertThat(isElementPresent(By.cssSelector(pageObjectId)))
				.as("Clicking element " + element.toString() + "did not open dialog/wizard identified by css selector:" + pageObjectId)
				.isTrue();
	}

	/**
	 * Mouse right click.
	 */
	public void rightClick() {
		Actions actions = new Actions(getDriver());
		actions.contextClick().build().perform();
	}

	/**
	 * Get selected element.
	 *
	 * @return the web element.
	 */
	public WebElement getSelectedElement() {
		return this.selectedElement;
	}

	/**
	 * Click on Combo Box.
	 *
	 * @param by the element selector
	 * @return The element that has been clicked.
	 */
	@SuppressWarnings({"PMD.ShortVariable"})
	public WebElement clickOnComboBox(final By by) {
		WebElement comboBox = getWaitDriver().waitForElementToBeClickable(by);
		comboBox.click();
		return comboBox;
	}

	/**
	 * Clear field.
	 *
	 * @param element the field to clear.
	 */
	public void clearField(final WebElement element) {
		element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
	}

	/**
	 * Waits till element is not visible.
	 *
	 * @param by the element selector
	 */
	@SuppressWarnings({"PMD.ShortVariable"})
	public void waitTillElementDisappears(final By by) {
		getWaitDriver().waitForElementToBeInvisible(by);
	}

	/**
	 * Clicks on tab to select it.
	 *
	 * @param tabName the tab name.
	 */
	public void selectTab(final String tabName) {
		scrollWidgetIntoView(getDriver().findElement(By.cssSelector(String.format(TAB_CSS, tabName))));
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(TAB_CSS, tabName))).click();
	}

	/**
	 * Returns the checkbox state.
	 *
	 * @param checkboxRowCssSelector the checkbox row css
	 * @return true if checkbox is checked, false if it is not
	 */
	public boolean isChecked(final String checkboxRowCssSelector) {
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		return (Boolean) jse.executeScript("return new rwt.remote.WidgetManager().findWidgetById(document.querySelector(\"" + checkboxRowCssSelector
				+ "\").id).isChecked();");
	}

	/**
	 * Sets implicit wait when element not found.
	 *
	 * @param timeoutInSeconds the timeout in seconds.
	 */
	public void setWebDriverImplicitWait(final long timeoutInSeconds) {
		getDriver().manage().timeouts().implicitlyWait(timeoutInSeconds, TimeUnit.SECONDS);
	}

	/**
	 * Sets the default webdriver timeout back to default per selenium property selenium.waitdriver.timeout.
	 */
	public void setWebDriverImplicitWaitToDefault() {
		getDriver().manage().timeouts().implicitlyWait(WEBDRIVER_DEFAULT_TIMEOUT, TimeUnit.SECONDS);
	}

	/**
	 * Verifies button status.
	 *
	 * @param cssSelector the css selector
	 * @param buttonName  the button name
	 */
	public void verifyButtonIsEnabled(final String cssSelector, final String buttonName) {
		getWaitDriver().waitForButtonToBeEnabled(cssSelector);
		assertThat(isButtonEnabled(cssSelector))
				.as(buttonName + " button is not enabled as expected")
				.isTrue();
	}

	/**
	 * Verifies if the result pane is open or not.
	 *
	 * @param cssSelector parent table css
	 * @return true if pane is present else returns false
	 */
	public boolean isResultPanePresent(final String cssSelector) {
		setWebDriverImplicitWait(1);
		boolean isResultPanePresent = isElementPresent(By.cssSelector(cssSelector));
		if (!isResultPanePresent) {
			LOGGER.warn("Result pane '" + cssSelector + "' failed to open");
		}
		setWebDriverImplicitWaitToDefault();
		return isResultPanePresent;
	}

	/**
	 * Clicks button and waits for pane to open.
	 *
	 * @param buttonCssSelector     the button css selector
	 * @param buttonName            the button name
	 * @param paneParentCssSelector the pane css selector
	 */
	public void clickButtonAndWaitForPaneToOpen(final String buttonCssSelector, final String buttonName, final String paneParentCssSelector) {
		clickButton(buttonCssSelector, buttonName);
		sleep(Constants.SLEEP_ONE_HUNDRED_MILLISECOND);
		int counter = 0;
		while (!isResultPanePresent(paneParentCssSelector) && counter < Constants.RETRY_COUNTER_3) {
			clickButton(buttonCssSelector, buttonName);
			sleep(Constants.SLEEP_ONE_HUNDRED_MILLISECOND);
			counter++;
		}
	}

	/**
	 * Waits for element to load.
	 *
	 * @param element the element
	 */
	public void waitForElementToLoad(final WebElement element) {
		int curCount = 0;
		int initialX = 0;
		int initialY = 0;
		int finalX = 1;
		int finalY = 1;

		while (initialX != finalX && initialY != finalY && curCount < MAX_COUNT) {
			initialX = element.getLocation().getX();
			initialY = element.getLocation().getY();

			sleep(Constants.SLEEP_HUNDRED_MILLI_SECONDS);

			finalX = element.getLocation().getX();
			finalY = element.getLocation().getY();

			LOGGER.debug("element x and y locations before sleep - x: " + initialX + " y: " + initialY);
			LOGGER.debug("element x and y locations after sleep - x: " + finalX + " y: " + finalY);

			curCount++;
		}
	}

	/**
	 * Resizes the window if editor's tabs are not visible.
	 *
	 * @param cssSelector the css selector
	 */
	public void resizeWindow(final String cssSelector) {
		if (!getDriver().findElement(By.cssSelector(cssSelector)).isDisplayed()) {
			Dimension dimension = new Dimension(WINDOW_SIZE, WINDOW_SIZE);
			getDriver().manage().window().setSize(dimension);
			sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			getDriver().manage().window().fullscreen();
			sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
		}
	}

	/**
	 * Clicks an item and verifies that the itemToVerify exists.
	 *
	 * @param itemToClickCSS  the CSS for item to click
	 * @param itemToVerifyCSS the CSS for item to verify
	 */
	public void expandTreeAndVerifyItem(final String itemToClickCSS, final String itemToVerifyCSS) {
		clickExpandTreeIcon(itemToClickCSS);
		int count = 0;
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS);
		while (!isElementPresent(By.cssSelector(itemToVerifyCSS)) && count < Constants.RETRY_COUNTER_3) {
			click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(itemToClickCSS)));
			count++;
		}
		setWebDriverImplicitWaitToDefault();
		assertThat(isElementPresent(By.cssSelector(itemToVerifyCSS)))
				.as("Category " + itemToVerifyCSS + " was not found").isTrue();
	}

	/**
	 * Returns true if the element identified by cssString has focus.
	 *
	 * @param cssString css selector for the element
	 * @return true/false
	 */
	public boolean isElementFocused(final String cssString) {
		return getDriver().findElement(By.cssSelector(cssString)).equals(getDriver().switchTo().activeElement());
	}

	/**
	 * Moves focus to element.
	 *
	 * @param element element
	 */
	public void moveFocusToElement(final WebElement element) {
		//following line will move focus to element.
		new Actions(getDriver()).moveToElement(element).perform();
	}

	/**
	 * Mouse right click with Retry.
	 *
	 * @param bySelector the By selector
	 */
	public void rightClick(final By bySelector) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_THREE_SECONDS);
		retryFunction((WebDriver driver) -> {
			Actions actions = new Actions(getDriver());
			actions.contextClick().build().perform();
			return driver.findElement(bySelector);
		}, POLLING_INTERVAL, WEBDRIVER_DEFAULT_TIMEOUT);
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Sets value for date field.
	 *
	 * @param cssSelector date field css selector
	 * @param date        formatted date string
	 */
	public void enterDateWithJavaScript(final String cssSelector, final String date) {
		jsDriver.executeScript("document.querySelector(\"" + cssSelector + "\").value = '" + date + "';");
	}

	/**
	 * Formats the date and time (Sep 26, 2017 9:32 AM).
	 *
	 * @param numberOfDays number of days to add or subtract from current date
	 * @return formatted date String
	 */
	public String getFormattedDateTime(final int numberOfDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, numberOfDays);

		String dateTimeFormat = PropertyManager.getInstance().getProperty("ep.dateTimeFormat");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimeFormat, Locale.ENGLISH);

		return simpleDateFormat.format(calendar.getTime());
	}

	/**
	 * Formats the date (Sep 26, 2017).
	 *
	 * @param numberOfDays number of days to add or subtract from current date
	 * @return formatted date String
	 */
	public String getFormattedDate(final int numberOfDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, numberOfDays);

		String dateFormat = PropertyManager.getInstance().getProperty("ep.dateFormat");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.ENGLISH);

		return simpleDateFormat.format(calendar.getTime());
	}

	/**
	 * Returns the checkbox is selected.
	 *
	 * @param checkboxCssSelector the checkbox css.
	 * @return true if selected.
	 */
	public boolean isSelected(final String checkboxCssSelector) {
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		getWaitDriver().waitForElementToBeInteractable(checkboxCssSelector);
		return (Boolean) jse.executeScript("return document.querySelector(\"" + checkboxCssSelector + "\").rwtWidget._selected;");
	}

}
