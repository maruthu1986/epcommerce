package com.elasticpath.selenium.editor;

import static com.elasticpath.selenium.util.Constants.IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.AddViewDataPointDialog;

/**
 * Data Policy Editor.
 */
public class DataPolicyEditor extends AbstractPageObject {

	/**
	 * Page Object Id.
	 */
	private static final String DATA_POLICY_EDITOR_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String DATA_POLICY_EDITOR_FIELD_PARENT_CSS = "div[automation-id*='com.elasticpath.cmclient.admin.datapolicies"
			+ ".AdminDataPoliciesMessages";
	private static final String DATA_POLICY_EDITOR_SUMMARY_PAGE_CSS = "div[automation-id='com.elasticpath.cmclient.admin.datapolicies"
			+ ".AdminDataPoliciesMessages.DataPolicyEditor_SummaryPage";
	private static final String DATA_POLICY_NAME_INPUT_CSS = DATA_POLICY_EDITOR_SUMMARY_PAGE_CSS + "_NameField'][widget-type='Text'] input";
	private static final String DATA_POLICY_REFERENCE_KEY_INPUT_CSS = DATA_POLICY_EDITOR_SUMMARY_PAGE_CSS
			+ "_ReferenceKeyField'][widget-type='Text'] input";
	private static final String DATA_POLICY_RETENTION_TYPE_INPUT_CSS = DATA_POLICY_EDITOR_SUMMARY_PAGE_CSS
			+ "_RetentionType'][widget-type='CCombo']";
	private static final String DATA_POLICY_RETENTION_PERIOD_INPUT_CSS = DATA_POLICY_EDITOR_SUMMARY_PAGE_CSS
			+ "_RetentionPeriod'][widget-type='Text'] input";
	private static final String DATA_POLICY_STATE_INPUT_CSS = DATA_POLICY_EDITOR_SUMMARY_PAGE_CSS + "_DataPolicyState'][widget-type='CCombo']";
	private static final String DATA_POLICY_DATE_CSS = DATA_POLICY_EDITOR_SUMMARY_PAGE_CSS + "_%s']" + "[widget-type='LayoutComposite'] ";
	private static final String DATA_POLICY_DATE_INPUT_CSS = DATA_POLICY_DATE_CSS + "input";
	private static final String DATA_POLICY_ACTIVITY_INPUT_CSS = DATA_POLICY_EDITOR_SUMMARY_PAGE_CSS + "_Activities'][widget-type='Text'] input";
	private static final String FIELD_DISABLE_CSS = "[disabled]";
	private static final String DATA_POLICY_SUMMARY_EDITOR_CSS = "div[automation-id='DataPolicySummaryPage']";
	private static final String AVAILABLE_DATA_POINTS_PARENT_CSS = "div[widget-id='Available Data Points'][widget-type='Table'] ";
	private static final String AVAILABLE_DATA_POINTS_COLUMN_CSS = AVAILABLE_DATA_POINTS_PARENT_CSS + "div[column-id='%s']";
	private static final String ASSIGNED_DATA_POINTS_PARENT_CSS = "div[widget-id='Assigned Data Points'][widget-type='Table'] ";
	private static final String ASSIGNED_DATA_POINTS_COLUMN_CSS = ASSIGNED_DATA_POINTS_PARENT_CSS + "div[column-id='%s']";
	private static final String MOVE_RIGHT_BUTTON_CSS = DATA_POLICY_EDITOR_PARENT_CSS + "div[widget-id='>']";
	private static final String DATA_POLICY_SEGMENT_INPUT_CSS = DATA_POLICY_EDITOR_FIELD_PARENT_CSS + ".DataPolicyEditor_SegmentsPage_Dialog_Title']"
			+ " input";
	private static final String ADD_SEGMENT_BUTTON_CSS = DATA_POLICY_EDITOR_PARENT_CSS + "div[widget-id='Add Segment']";
	private static final String CREATE_DATA_POINT_BUTTON_CSS = DATA_POLICY_EDITOR_PARENT_CSS + "div[automation-id='com.elasticpath.cmclient.admin"
			+ ".datapolicies.AdminDataPoliciesMessages.DataPolicyEditor_DataPoints_Button_CreateDataPoint'][seeable='true']";
	private static final String DATA_POLICY_EDITOR_CLOSE_ICON_CSS = "div[widget-id='%s'][appearance-id='ctab-item'][active-tab='true'] > "
			+ "div[style*='.gif']";
	private static final String DATA_POLICY_RETENTION_PERIOD_VALIDATION_CSS = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".EpValidatorFactory_Integer'][widget-id='%s']";
	private static final String DATA_POLICY_END_DATE_VALIDATION_CSS = "div[automation-id='com.elasticpath.cmclient.admin.datapolicies"
			+ ".AdminDataPoliciesMessages.DataPolicyEditor_SummaryPage_EndDate'] [widget-id='%s']";
	private static final String DATA_POLICY_FIELD_VALIDATIONS_CSS = DATA_POLICY_EDITOR_FIELD_PARENT_CSS + ".DataPolicyEditor_SummaryPage_%s'] "
			+ "+ div[automation-id='com.elasticpath.cmclient.core.CoreMessages.EpValidatorFactory_ValueRequired']";
	private static final String DATA_POLICY_DATE_FIELD_VALIDATIONS_CSS = DATA_POLICY_EDITOR_FIELD_PARENT_CSS + ".DataPolicyEditor_SummaryPage_%s'] "
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.EpValidatorFactory_ValueRequired']";
	private static final String TAB_CSS = "div[widget-id='%s'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public DataPolicyEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies if Data Policy editor is visible.
	 */
	public void verifyDataPolicyEditor() {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(DATA_POLICY_SUMMARY_EDITOR_CSS));
	}

	/**
	 * Enters Data Policy Name.
	 *
	 * @param dataPolicyName Data Policy Name field.
	 */
	public void enterDataPolicyName(final String dataPolicyName) {
		clearAndType(DATA_POLICY_NAME_INPUT_CSS, dataPolicyName);
	}

	/**
	 * Enters Data Policy Reference Key.
	 *
	 * @param dataPolicyReferenceKey Data Policy Reference Key field.
	 */
	public void enterDataPolicyReferenceKey(final String dataPolicyReferenceKey) {
		clearAndType(DATA_POLICY_REFERENCE_KEY_INPUT_CSS, dataPolicyReferenceKey);
	}

	/**
	 * Enters Data Policy Retention Period Days.
	 *
	 * @param dataPolicyRetentionPeriod in days.
	 */
	public void enterDataPolicyRetentionPeriod(final String dataPolicyRetentionPeriod) {
		clearAndType(DATA_POLICY_RETENTION_PERIOD_INPUT_CSS, dataPolicyRetentionPeriod);
	}

	/**
	 * Select Data Policy state.
	 *
	 * @param dataPolicyState Data Policy State.
	 */
	public void enterDataPolicyState(final String dataPolicyState) {
		assertThat(selectComboBoxItem(DATA_POLICY_STATE_INPUT_CSS, dataPolicyState))
				.as("Unable to find state - " + dataPolicyState)
				.isTrue();
	}

	/**
	 * Enters Data Policy Activity.
	 *
	 * @param dataPolicyActivity Data Policy Activity field.
	 */
	public void enterDataPolicyActivity(final String dataPolicyActivity) {
		clearAndType(DATA_POLICY_ACTIVITY_INPUT_CSS, dataPolicyActivity);
	}

	/**
	 * Select Data points.
	 *
	 * @param dataPolicyPoint Data Policy point.
	 */
	public void enterAvailableDataPoint(final String dataPolicyPoint) {
		clickTab("Data Points");
		assertThat(selectItemInDialog(AVAILABLE_DATA_POINTS_PARENT_CSS, AVAILABLE_DATA_POINTS_COLUMN_CSS, dataPolicyPoint, ""))
				.as("Unable to find available data point - " + dataPolicyPoint)
				.isTrue();
		clickMoveRightButton();
		verifyAssignedDataPoint(dataPolicyPoint);
	}

	/**
	 * Verifies assigned Data Policy point.
	 *
	 * @param dataPolicyPoint Data Policy point.
	 */
	public void verifyAssignedDataPoint(final String dataPolicyPoint) {
		assertThat(selectItemInDialog(ASSIGNED_DATA_POINTS_PARENT_CSS, ASSIGNED_DATA_POINTS_COLUMN_CSS, dataPolicyPoint, ""))
				.as("Unable to find assigned data point - " + dataPolicyPoint)
				.isTrue();
	}

	/**
	 * Clicks to select tab.
	 *
	 * @param tabName the tab name.
	 */
	public void clickTab(final String tabName) {
		String cssSelector = String.format(TAB_CSS, tabName);
		resizeWindow(cssSelector);
		click(getDriver().findElement(By.cssSelector(cssSelector)));
	}

	/**
	 * Enters Data Policy segment.
	 *
	 * @param dataPolicySegment Data Policy Segment.
	 */
	public void enterDataPolicySegment(final String dataPolicySegment) {
		clickTab("Data Policy Segments");
		clearAndType(DATA_POLICY_SEGMENT_INPUT_CSS, dataPolicySegment);
		clickAddSegmentButton();
	}

	/**
	 * Clicks '>' button.
	 */
	public void clickMoveRightButton() {
		clickButton(MOVE_RIGHT_BUTTON_CSS, "> (Move Right)");
	}

	/**
	 * Clicks Add  Segment button.
	 */
	public void clickAddSegmentButton() {
		clickButton(ADD_SEGMENT_BUTTON_CSS, "Add Segment");
	}

	/**
	 * Clicks Create Data Point button.
	 *
	 * @return AddViewDataPointDialog.
	 */
	public AddViewDataPointDialog clickCreateDataPointButton() {
		final String dialogName = "Create";
		clickButton(CREATE_DATA_POINT_BUTTON_CSS, "Create Data Point", String.format(AddViewDataPointDialog.ADD_VIEW_DATA_POINT_DIALOG_CSS_TEMPLATE,
				dialogName));
		return new AddViewDataPointDialog(getDriver(), dialogName);
	}

	/**
	 * Close Data Policy Editor.
	 *
	 * @param dataPolicyName String
	 */
	public void closeDataPolicyEditor(final String dataPolicyName) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(DATA_POLICY_EDITOR_CLOSE_ICON_CSS, dataPolicyName)
		)));
	}

	/**
	 * Verifies Data Policy Retention Period Validation.
	 *
	 * @param expValidationText String
	 */
	public void verifyRetentionPeriodValidation(final String expValidationText) {
		setWebDriverImplicitWait(IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS);
		assertThat(getDriver().findElements(By.cssSelector(String.format(DATA_POLICY_RETENTION_PERIOD_VALIDATION_CSS,
				expValidationText))).isEmpty())
				.as("Expected Retention Period validation not displayed - " + expValidationText)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Enters Date and Time based on given number of days from today.
	 * @param dateFieldName String
	 * @param numberOfDaysFromToday int
	 */
	public void enterDateTime(final String dateFieldName, final int numberOfDaysFromToday) {
		String dateFieldCSS = String.format(DATA_POLICY_DATE_INPUT_CSS, dateFieldName);
		clearAndType(dateFieldCSS, getFormattedDateTime(numberOfDaysFromToday));
	}

	/**
	 * Verifies Data Policy End Date Validation.
	 *
	 * @param expValidationText String
	 */
	public void verifyEndDateValidation(final String expValidationText) {
		setWebDriverImplicitWait(IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS);
		assertThat(getDriver().findElements(By.cssSelector(String.format(DATA_POLICY_END_DATE_VALIDATION_CSS,
				expValidationText))).isEmpty())
				.as("Expected end date validation not displayed - " + expValidationText)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verifies Data Point button is not present for Active Data Policy.
	 */
	public void verifyDataPointButtonIsNotPresent() {
		setWebDriverImplicitWait(IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS);
		assertThat(isElementPresent(By.cssSelector(CREATE_DATA_POINT_BUTTON_CSS)))
				.as("Data Point Button is present for Active Data Policy")
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verify validation displayed for field.
	 *
	 * @param expFieldValidation message to be returned
	 */
	public void verifyValidationsReturned(final String expFieldValidation) {
		setWebDriverImplicitWait(IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS);
		switch (expFieldValidation) {
			case "NameField":
				assertThat(getDriver().findElements(By.cssSelector(String.format(DATA_POLICY_FIELD_VALIDATIONS_CSS,
						expFieldValidation))).isEmpty())
						.as("Data Policy Name field required validations not displayed")
						.isFalse();
				break;
			case "ReferenceKeyField":
				assertThat(getDriver().findElements(By.cssSelector(String.format(DATA_POLICY_FIELD_VALIDATIONS_CSS,
						expFieldValidation))).isEmpty())
						.as("Data Policy ReferenceKey field required validations not displayed")
						.isFalse();
				break;
			case "RetentionPeriod":
				assertThat(getDriver().findElements(By.cssSelector(String.format(DATA_POLICY_FIELD_VALIDATIONS_CSS,
						expFieldValidation))).isEmpty())
						.as("Data Policy RetentionPeriod field required validations not displayed")
						.isFalse();
				break;
			case "StartDate":
				assertThat(getDriver().findElements(By.cssSelector(String.format(DATA_POLICY_DATE_FIELD_VALIDATIONS_CSS,
						expFieldValidation))).isEmpty())
						.as("Data Policy Start Date field required validations not displayed")
						.isFalse();
				break;

			default:
				fail("No field name given.");
				return;
		}
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verifies the given fields are disabled.
	 *
	 * @param field String
	 */
	public void verifyDataPolicyFieldsDisabled(final String field) {
		setWebDriverImplicitWait(IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS);
		switch (field) {
			case "NameField":
				assertThat(getDriver().findElements(By.cssSelector(DATA_POLICY_NAME_INPUT_CSS + FIELD_DISABLE_CSS)).isEmpty())
						.as("Data Policy Name field is not disabled")
						.isFalse();
				break;
			case "ReferenceKeyField":
				assertThat(getDriver().findElements(By.cssSelector(DATA_POLICY_REFERENCE_KEY_INPUT_CSS + FIELD_DISABLE_CSS)).isEmpty())
						.as("Data Policy Reference field is not disabled")
						.isFalse();
				break;
			case "RetentionType":
				assertThat(getDriver().findElements(By.cssSelector(DATA_POLICY_RETENTION_TYPE_INPUT_CSS + " input" + FIELD_DISABLE_CSS))
						.isEmpty())
						.as("Data Policy Retention Type field is not disabled")
						.isFalse();
				break;
			case "RetentionPeriod":
				assertThat(getDriver().findElements(By.cssSelector(DATA_POLICY_RETENTION_PERIOD_INPUT_CSS + FIELD_DISABLE_CSS)).isEmpty())
						.as("Data Policy Retention Period field is not disabled")
						.isFalse();
				break;
			case "State":
				assertThat(getDriver().findElements(By.cssSelector(DATA_POLICY_STATE_INPUT_CSS + " input" + FIELD_DISABLE_CSS)).isEmpty())
						.as("Data Policy Retention Period field is not disabled")
						.isFalse();
				break;
			case "StartDate":
				assertThat(getDriver().findElements(By.cssSelector(String.format(DATA_POLICY_DATE_INPUT_CSS, field) + FIELD_DISABLE_CSS)).isEmpty())
						.as("Data Policy Start Date field is not disabled")
						.isFalse();
				break;
			case "EndDate":
				assertThat(getDriver().findElements(By.cssSelector(String.format(DATA_POLICY_DATE_INPUT_CSS, field) + FIELD_DISABLE_CSS)).isEmpty())
						.as("Data Policy End Date field is not disabled")
						.isFalse();
				break;
			case "Activities":
				assertThat(getDriver().findElements(By.cssSelector(DATA_POLICY_ACTIVITY_INPUT_CSS + FIELD_DISABLE_CSS)).isEmpty())
						.as("Data Policy Activities field is not disabled")
						.isFalse();
				break;

			default:
				return;
		}
		setWebDriverImplicitWaitToDefault();
	}
}
