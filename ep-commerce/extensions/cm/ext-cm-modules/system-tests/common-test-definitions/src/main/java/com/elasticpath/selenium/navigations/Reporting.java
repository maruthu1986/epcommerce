package com.elasticpath.selenium.navigations;

import static org.assertj.core.api.Assertions.assertThat;

import static com.elasticpath.selenium.util.Constants.IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.elasticpath.selenium.resultspane.ReportPane;

/**
 * Reporting Page.
 */
public class Reporting extends AbstractNavigation {

	private static final String ACTIVE_LEFT_PANE = "div[pane-location='left-pane-inner'] div[active-editor='true'] ";
	private static final String REPORT_TYPE_COMBO_CSS =
			ACTIVE_LEFT_PANE + "div[automation-id='com.elasticpath.cmclient.reporting.ReportingMessages.reportType'][widget-type='CCombo']";
	private static final String STORE_COMBO_CSS =
			ACTIVE_LEFT_PANE + "div[automation-id*='com.elasticpath.cmclient.reporting'][automation-id*='store'][widget-type='CCombo']";
	private static String fromDateAutomationId = "com.elasticpath.cmclient.core.CoreMessages.SampleDateTime";
	private String toDateAutomationId = "com.elasticpath.cmclient.reporting.%s.to";
	private static final String FROM_DATE_INPUT_CSS =
			ACTIVE_LEFT_PANE + "div[automation-id='" + fromDateAutomationId + "'] > input";
	private String toDateInputCss =
			ACTIVE_LEFT_PANE + "div[automation-id*='" + toDateAutomationId + "'] > div[widget-type='Text'] > input";
	private static final String RUN_REPORT_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.reporting.ReportingMessages.runReport']";
	private static final String ORDER_STATUS_CHECKBOX_ROW_CSS = ACTIVE_LEFT_PANE + "div[widget-id='%s']";
	private static final String ORDER_STATUS_CHECKBOX_CSS = ORDER_STATUS_CHECKBOX_ROW_CSS + " > div";
	private static final String USER_ID_CSS = "div[automation-id*='userId'][widget-type='Text'] > input";
	private static final String CURRENCY_COMBO_CSS = "div[automation-id*='currency'][widget-type='CCombo']";
	private static final String USER_ID_FIELD_VALIDATIONS_CSS =  "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.EpValidatorFactory_ValueRequired']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public Reporting(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Selects report type.
	 *
	 * @param reportType the report type.
	 */
	public void selectReportType(final String reportType) {

		assertThat(selectComboBoxItem(REPORT_TYPE_COMBO_CSS, reportType))
				.as("Unable to find report type - " + reportType)
				.isTrue();

		String automationId = "";
		if ("Shopping Cart Promotion Usage".equals(reportType)) {
			automationId = "promotionusage.PromotionUsageMessages";
			toDateAutomationId = String.format(toDateAutomationId, automationId);
			toDateInputCss = String.format(toDateInputCss, automationId);
		} else if ("Order Summary".equals(reportType)) {
			automationId = "ordersummary.OrderSummaryReportMessages";
			toDateAutomationId = String.format(toDateAutomationId, automationId);
			toDateInputCss = String.format(toDateInputCss, automationId);
		} else if ("Returns And Exchanges".equals(reportType)) {
			automationId = "returnsandexchanges.ReturnsAndExchangesReportMessages";
			toDateAutomationId = String.format(toDateAutomationId, automationId);
			toDateInputCss = String.format(toDateInputCss, automationId);
		} else if ("Order Status".equals(reportType)) {
			automationId = "ordersbystatus.OrdersByStatusReportMessages";
			toDateAutomationId = String.format(toDateAutomationId, automationId);
			toDateInputCss = String.format(toDateInputCss, automationId);
		}
		else if ("Customer Personal Data".equals(reportType)) {
			automationId = "customerpersonaldata.CustomerPersonalDataMessages";
		}

	}

	/**
	 * Selects currency.
	 *
	 * @param currency the report type.
	 */
	public void selectCurrency(final String currency) {
		assertThat(selectComboBoxItem(CURRENCY_COMBO_CSS, currency))
				.as("Unable to find currency - " + currency)
				.isTrue();
	}

	/**
	 * Selects store.
	 *
	 * @param store the store.
	 */
	public void selectStore(final String store) {
		assertThat(selectComboBoxItem(STORE_COMBO_CSS, store))
				.as("Unable to find store - " + store)
				.isTrue();
	}

	/**
	 * Inputs 'from' date.
	 */
	public void enterFromDate() {
		getWaitDriver().waitForElementToBeInteractable(FROM_DATE_INPUT_CSS);
		WebElement fromDateElement = getDriver().findElement(By.cssSelector(FROM_DATE_INPUT_CSS));
		fromDateElement.clear();
		enterDateWithJavaScript(FROM_DATE_INPUT_CSS, getFormattedDateTime(-1));
		updateDateField(fromDateElement);
	}

	/**
	 * Inputs 'to' date.
	 */
	public void enterToDate() {
		getWaitDriver().waitForElementToBeInteractable(toDateInputCss);
		WebElement toDateElement = getDriver().findElement(By.cssSelector(toDateInputCss));
		toDateElement.clear();
		enterDateWithJavaScript(toDateInputCss, getFormattedDateTime(1));
		updateDateField(toDateElement);
	}

	private void updateDateField(final WebElement element) {
		element.click();
		element.sendKeys(Keys.SPACE);
		element.sendKeys(Keys.BACK_SPACE);
	}

	/**
	 * Clicks on run report button.
	 *
	 * @return ReportPane the report pane
	 */
	public ReportPane clickRunReportButton() {
		scrollWidgetIntoView(RUN_REPORT_BUTTON_CSS);
		clickButton(RUN_REPORT_BUTTON_CSS, "Run Report");
		return new ReportPane(getDriver());
	}

	/**
	 * Checks the order status box.
	 *
	 * @param orderStatus the order status
	 */
	public void checkOrderStatusBox(final String orderStatus) {
		if (!isChecked(String.format(ORDER_STATUS_CHECKBOX_ROW_CSS, orderStatus))) {
			click(getDriver().findElement(By.cssSelector(String.format(ORDER_STATUS_CHECKBOX_CSS, orderStatus))));
		}
	}

	/**
	 * Unchecks the order status box.
	 *
	 * @param orderStatus the order status
	 */
	public void uncheckOrderStatusBox(final String orderStatus) {
		if (isChecked(String.format(ORDER_STATUS_CHECKBOX_ROW_CSS, orderStatus))) {
			click(getDriver().findElement(By.cssSelector(String.format(ORDER_STATUS_CHECKBOX_CSS, orderStatus))));
		}
	}

	/**
	 * Input User id.
	 *
	 * @param userId customer data report.
	 */
	public void enterUserId(final String userId) {
		clearAndType(USER_ID_CSS, userId);
	}

	/**
	 * Verify Validations displayed for User ID.
	 */
	public void verifyUserIdValidation() {
		setWebDriverImplicitWait(IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS);
		assertThat(isElementPresent(By.cssSelector(USER_ID_FIELD_VALIDATIONS_CSS)))
				.as("Unable to find Use ID validation message")
				.isTrue();
		setWebDriverImplicitWaitToDefault();
	}

}
