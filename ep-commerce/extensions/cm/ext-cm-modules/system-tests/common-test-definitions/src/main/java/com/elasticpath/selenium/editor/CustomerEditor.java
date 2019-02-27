package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.AddEditCustomerAddressDialog;
import com.elasticpath.selenium.dialogs.AddCustomerSegmentMembershipDialog;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.util.Constants;


/**
 * Customer Editor.
 */
public class CustomerEditor extends AbstractPageObject {

	private static final String EDITOR_PANE_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String TAB_CSS = "div[widget-id='%s'][seeable='true']";
	private static final String COLUMN_ID = "div[column-id='%s']";
	private static final String EDITOR_BUTTON_CSS = EDITOR_PANE_PARENT_CSS + TAB_CSS;
	private static final String CUSTOMER_SEGMENT_PARENT_CSS = "div[widget-id='Customer Segments Table'][widget-type='Table'][seeable='true'] ";
	private static final String CUSTOMER_SEGMENT_COLUMN_CSS = CUSTOMER_SEGMENT_PARENT_CSS + COLUMN_ID;
	private static final String CUSTOMER_EDITOR_CLOSE_ICON_CSS = "[appearance-id='ctab-item'][active-tab='true'] > "
			+ "div[style*='.gif']";
	private static final String ORDER_TABLE_PARENT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Customer Details Order Table'] ";
	private static final String ORDER_ID_COLUMN_CSS = ORDER_TABLE_PARENT_CSS + COLUMN_ID;
	private static final String FIRST_NAME_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='First Name'] input";
	private static final String LAST_NAME_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Last Name'] input";
	private static final String PHONE_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[automation-id='com.elasticpath.cmclient.fulfillment"
			+ ".FulfillmentMessages.ProfileBasicSection_PhoneNum'] input";
	private static final String ADDRESS_LIST_PARENT_CSS = "div[widget-id='Customer Details Address Table'][widget-type='Table'] ";
	private static final String ADDRESS_LIST_CSS = ADDRESS_LIST_PARENT_CSS + COLUMN_ID;
	private static final int SLEEP_TIME = 500;
	private static final String EDITOR_ELEMENT_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.fulfillment.FulfillmentMessages";
	private static final String REMOVE_SEGMENT_CONF_DIALOG_ID = EDITOR_ELEMENT_PARENT_CSS + ".CustomerSegmentsPageDialog_RemoveConfirm']";
	private static final String VIEW_DATA_POINTS_CSS = EDITOR_ELEMENT_PARENT_CSS + ".ViewDataPoints_Label']";
	private static final String DELETE_POLICY_DATA_CSS = EDITOR_ELEMENT_PARENT_CSS + ".DeletePolicyData_Title']";
	private static final String SHOW_DISABLED_DATA_POLICIES = EDITOR_ELEMENT_PARENT_CSS + ".ShowDisabledPolicies_Label'][seeable='true']";
	private static final String DATA_POLICY_PARENT_CSS = "div[widget-id='Customer Data Policies Table'][widget-type='Table'][seeable='true'] ";
	private static final String DATA_POLICY_COLUMN_CSS = DATA_POLICY_PARENT_CSS + COLUMN_ID;
	private static final String DATA_POINT_PARENT_CSS = "div[widget-id='Customer Data Points Table'][widget-type='Table'][seeable='true'] ";
	private static final String DATA_POINT_COLUMN_CSS = DATA_POINT_PARENT_CSS + COLUMN_ID;
	private static final String DATA_POINT_LIST_ROW_COLUMN_CSS = DATA_POINT_PARENT_CSS + "div[row-id='%s']";
	private static final String DATA_POINT_VALUE_COLUMN_CSS = " div[column-num='2']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CustomerEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks to select tab.
	 *
	 * @param tabName the tab name.
	 */
	public void clickTab(final String tabName) {
		String cssSelector = String.format(TAB_CSS, tabName);
		resizeWindow(cssSelector);
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(cssSelector)));
	}

	/**
	 * Clicks Add Segment... button.
	 *
	 * @return AddSegmentDialog
	 */
	public AddCustomerSegmentMembershipDialog clickAddSegmentButton() {
		clickEditorButton("Add Segment...", AddCustomerSegmentMembershipDialog.CUSTOMER_SEGMENT_MEMBERSHIP_PARENT_CSS);
		return new AddCustomerSegmentMembershipDialog(getDriver());
	}

	/**
	 * Clicks Remove Segment... button.
	 */
	public void clickRemoveSegmentButton() {
		clickEditorButton("Remove Segment...", REMOVE_SEGMENT_CONF_DIALOG_ID);
	}

	/**
	 * Clicks button.
	 *
	 * @param buttonWidgetId String the widget id
	 * @param pageObjectId   the page object id
	 */
	public void clickEditorButton(final String buttonWidgetId, final String pageObjectId) {
		scrollWidgetIntoView(String.format(EDITOR_BUTTON_CSS, buttonWidgetId));
		clickButton(String.format(EDITOR_BUTTON_CSS, buttonWidgetId), buttonWidgetId, pageObjectId);
	}

	/**
	 * Select Customer Segment.
	 *
	 * @param customerSegmentName String
	 */
	public void selectCustomerSegment(final String customerSegmentName) {
		assertThat(selectItemInEditorPaneWithScrollBar(CUSTOMER_SEGMENT_PARENT_CSS, CUSTOMER_SEGMENT_COLUMN_CSS, customerSegmentName))
				.as("Unable to find customer segment - " + customerSegmentName)
				.isTrue();
	}

	/**
	 * Checks if given customer segment exists.
	 *
	 * @param customerSegmentName Customer Segment Name.
	 * @return boolean
	 */
	public boolean isCustomerSegmentExists(final String customerSegmentName) {
		boolean isExists = false;
		try {
			setWebDriverImplicitWait(1);
			isExists = verifyItemIsNotInEditorPaneWithScrollBar(CUSTOMER_SEGMENT_PARENT_CSS, CUSTOMER_SEGMENT_COLUMN_CSS, customerSegmentName);
			setWebDriverImplicitWaitToDefault();
		} catch (Exception e) {
			setWebDriverImplicitWaitToDefault();
		}
		return isExists;
	}

	/**
	 * Close Customer Editor.
	 */
	public void closeCustomerEditor() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(CUSTOMER_EDITOR_CLOSE_ICON_CSS)));
	}

	/**
	 * Verifies Order ID column value.
	 *
	 * @param orderIdNumber the column value.
	 * @param columnName    the column number.
	 */
	public void verifyCustomerOrderIdColumnValue(final String orderIdNumber, final String columnName) {
		assertThat(selectItemInEditorPane(ORDER_TABLE_PARENT_CSS, ORDER_ID_COLUMN_CSS, orderIdNumber,
				columnName))
				.as("Unable to find Order Number in Customer profile - " + orderIdNumber)
				.isTrue();
	}

	/**
	 * Inputs first name.
	 *
	 * @param firstName String
	 */
	public void enterFirstName(final String firstName) {
		clearAndType(FIRST_NAME_INPUT_CSS, firstName);
	}

	/**
	 * Inputs last name.
	 *
	 * @param lastName String
	 */
	public void enterLastName(final String lastName) {
		clearAndType(LAST_NAME_INPUT_CSS, lastName);
	}

	/**
	 * Inputs phone number.
	 *
	 * @param phoneNumber String
	 */
	public void enterPhoneNumber(final String phoneNumber) {
		clearAndType(PHONE_INPUT_CSS, phoneNumber);
	}

	/**
	 * Verifies first name.
	 *
	 * @param expFirstName Expected first name
	 */
	public void verifyFirstName(final String expFirstName) {
		assertThat(getDriver().findElement(By.cssSelector(FIRST_NAME_INPUT_CSS)).getAttribute("value").equalsIgnoreCase(expFirstName)).isTrue();
	}

	/**
	 * Verifies last name.
	 *
	 * @param expLastName Expected last name
	 */

	public void verifyLastName(final String expLastName) {
		assertThat(getDriver().findElement(By.cssSelector(LAST_NAME_INPUT_CSS)).getAttribute("value").equalsIgnoreCase(expLastName)).isTrue();
	}

	/**
	 * Clicks Add Address... button.
	 *
	 * @return AddAddressDialog
	 */
	public AddEditCustomerAddressDialog clickAddAddressButton() {
		clickEditorButton("Add Address...", AddEditCustomerAddressDialog.CUSTOMER_ADDRESS_PARENT_CSS);
		return new AddEditCustomerAddressDialog(getDriver());
	}

	/**
	 * Verifies address value.
	 *
	 * @param addressValueToCheck String
	 * @param columnName          String
	 */
	public void verifyAddressLineExists(final String addressValueToCheck, final String columnName) {
		verifyCustomerAddressTableColumnValue(addressValueToCheck, columnName);
	}

	/**
	 * Verifies Customer Address Table column value.
	 *
	 * @param columnValue the column value.
	 * @param columnName  the column number.
	 */
	public void verifyCustomerAddressTableColumnValue(final String columnValue, final String columnName) {
		sleep(SLEEP_TIME);
		assertThat(selectItemInEditorPane(ADDRESS_LIST_PARENT_CSS, ADDRESS_LIST_CSS, columnValue, columnName))
				.as("Unable to find address column value - " + columnValue)
				.isTrue();
	}

	/**
	 * Select and Verify Data Policy for Customer.
	 *
	 * @param dataPolicyName String
	 */
	public void verifyDataPolicyExists(final String dataPolicyName) {
		assertThat(selectItemInEditorPaneWithScrollBar(DATA_POLICY_PARENT_CSS, DATA_POLICY_COLUMN_CSS, dataPolicyName))
				.as("Unable to find Data Policy - " + dataPolicyName)
				.isTrue();
	}

	/**
	 * Select Data Point Value for Data Policy.
	 *
	 * @param dataPointValue String
	 */
	public void selectDataPoint(final String dataPointValue) {
		assertThat(selectItemInEditorPaneWithScrollBar(DATA_POINT_PARENT_CSS, DATA_POINT_COLUMN_CSS, dataPointValue))
				.as("Unable to find Data Point Value - " + dataPointValue)
				.isTrue();
	}

	/**
	 * View Data Point values for Customer Data Policy.
	 *
	 * @param dataPointName String
	 */
	public void verifyDataPointValue(final String dataPointName, final String dataPointValue) {
		selectDataPoint(dataPointName);
		assertThat(getDriver().findElement(By.cssSelector(String.format(DATA_POINT_LIST_ROW_COLUMN_CSS, dataPointName)
				+ DATA_POINT_VALUE_COLUMN_CSS)).getText())
				.as("Data Point Value not match - " + dataPointName)
				.isEqualTo(dataPointValue);
	}

	/**
	 * Clicks View Data Points button.
	 */
	public void clickViewDataPointsButton() {
		clickEditorButton("View Data Points", VIEW_DATA_POINTS_CSS);
	}

	/**
	 * Clicks Delete Policy Data button.
	 */
	public void clickDeletePolicyDataButton() {
		clickEditorButton("Delete Policy Data", DELETE_POLICY_DATA_CSS);
		new ConfirmDialog(SetUp.getDriver()).clickDeleteButton("FulfillmentMessages.DeletePolicyData_Confirm");
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("FulfillmentMessages.DeletePolicyData_Title");
	}

	/**
	 * Verify Data Points is deleted for Customer Data Policy.
	 *
	 * @param dataPointName String
	 */
	public void verifyDataPointValueIsEmpty(final String dataPointName) {
		selectDataPoint(dataPointName);
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(isElementPresent(By.cssSelector(String.format(DATA_POINT_LIST_ROW_COLUMN_CSS, dataPointName)
				+ DATA_POINT_VALUE_COLUMN_CSS)))
				.as("Data is not deleted for Data Point - " + dataPointName)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verify Data Points is deleted for Customer Data Policy.
	 *
	 * @param dataPointName String
	 */
	public void verifyDataPointValueSetToHyphen(final String dataPointName) {
		selectDataPoint(dataPointName);
		assertThat(getDriver().findElement(By.cssSelector(String.format(DATA_POINT_LIST_ROW_COLUMN_CSS, dataPointName)
				+ DATA_POINT_VALUE_COLUMN_CSS)).getText())
				.as("Data is not deleted for Data Point - " + dataPointName)
				.isEqualTo("‐");
	}

	/**
	 * Select Show disabled data policies.
	 */
	public void selectShowDisabledDataPolicy() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(SHOW_DISABLED_DATA_POLICIES)));
	}

	/**
	 * Verify Data Policy for Customer is not exists.
	 *
	 * @param dataPolicyName String
	 */
	public void verifyDataPolicyIsNotExists(final String dataPolicyName) {
		setWebDriverImplicitWait(1);
		assertThat(verifyItemIsNotInEditorPaneWithScrollBar(DATA_POLICY_PARENT_CSS, DATA_POLICY_COLUMN_CSS, dataPolicyName))
				.as("Data Policy should not be present - " + dataPolicyName)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

}
