package com.elasticpath.selenium.resultspane;

import static com.elasticpath.selenium.util.Constants.IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.editor.DataPolicyEditor;
import com.elasticpath.selenium.util.Utility;

/**
 * Data Policy Result Pane.
 */
public class DataPolicyResultPane extends AbstractPageObject {
	private static final String DATA_POLICY_LIST_PARENT_CSS = "div[widget-id='Data policy list'][widget-type='Table'] ";
	private static final String DATA_POLICY_LIST_CSS = DATA_POLICY_LIST_PARENT_CSS + "div[column-id='%s']";
	private static final String DATA_POLICY_LIST_ROW_COLUMN_CSS = DATA_POLICY_LIST_PARENT_CSS + "div[row-id='%s']";
	private static final String DATA_POLICY_STATE_COLUMN_CSS = " div[automation-id*='com.elasticpath.cmclient.admin.datapolicies"
			+ ".AdminDataPoliciesMessages.DataPolicyEditor_SummaryPage_State']";
	private static final String DATA_POLICY_END_DATE_COLUMN_CSS = " div[column-num='3']";
	private static final String CREATE_DATA_POLICY_BUTTON_CSS = "div[widget-id='Create Data Policy'][seeable='true']";
	private static final String EDIT_DATA_POLICY_BUTTON_CSS = "div[widget-id='Edit Data Policy'][seeable='true']";
	private static final String VIEW_DATA_POLICY_BUTTON_CSS = "div[widget-id='View Data Policy'][seeable='true']";
	private static final String DISABLE_DATA_POLICY_BUTTON_CSS = "div[widget-id='Disable Data Policy'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public DataPolicyResultPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Create Data Policy button.
	 *
	 * @return DataPolicyEditor
	 */
	public DataPolicyEditor clickCreateDataPolicyButton() {
		clickButton(CREATE_DATA_POLICY_BUTTON_CSS, "Create Data Policy");
		return new DataPolicyEditor(getDriver());
	}

	/**
	 * View Data Policy editor.
	 *
	 * @param dataPolicyName String.
	 * @return the Data Policy editor.
	 */
	public DataPolicyEditor viewDataPolicyEditor(final String dataPolicyName) {
		verifyDataPolicyExists(dataPolicyName);
		clickButton(VIEW_DATA_POLICY_BUTTON_CSS, "View Data Policy");
		return new DataPolicyEditor(getDriver());
	}

	/**
	 * Edit Data Policy.
	 *
	 * @param dataPolicyName String.
	 * @return the Data Policy editor.
	 */
	public DataPolicyEditor editDataPolicyEditor(final String dataPolicyName) {
		verifyDataPolicyExists(dataPolicyName);
		clickButton(EDIT_DATA_POLICY_BUTTON_CSS, "Edit Data Policy");
		return new DataPolicyEditor(getDriver());
	}

	/**
	 * Clicks Disable Data Policy button.
	 *
	 * @param dataPolicyName String.
	 */
	public void clickDisableDataPolicyButton(final String dataPolicyName) {
		verifyDataPolicyExists(dataPolicyName);
		clickButton(DISABLE_DATA_POLICY_BUTTON_CSS, "Disable Data Policy");
		new ConfirmDialog(getDriver()).clickOKButton("AdminDataPoliciesMessages.DataPolicyEditor_DisableDialogTitle");
	}

	/**
	 * Verifies if Data Policy exists.
	 *
	 * @param dataPolicyName String
	 */
	public void verifyDataPolicyExists(final String dataPolicyName) {
		assertThat(selectItemInCenterPaneWithoutPagination(DATA_POLICY_LIST_PARENT_CSS, DATA_POLICY_LIST_CSS, dataPolicyName,
				"Data Policy Name"))
				.as("Data Policy Name does not exist in the list - " + dataPolicyName)
				.isTrue();
	}

	/**
	 * Verifies Data Policy state.
	 *
	 * @param dataPolicyName  String
	 * @param dataPolicyState for data policy
	 */
	public void verifyDataPolicyState(final String dataPolicyName, final String dataPolicyState) {
		verifyDataPolicyExists(dataPolicyName);
		assertThat(getDriver().findElement(By.cssSelector(String.format(DATA_POLICY_LIST_ROW_COLUMN_CSS, dataPolicyName)
				+ DATA_POLICY_STATE_COLUMN_CSS)).getText())
				.as("Unexpected Data Policy State")
				.isEqualTo(dataPolicyState);
	}

	/**
	 * Verifies Data Policy End Date is set.
	 *
	 * @param dataPolicyName String
	 */
	public void verifyDataPolicyEndDateIsToday(final String dataPolicyName) {
		verifyDataPolicyExists(dataPolicyName);
		String actualDateString = getDriver().findElement(By.cssSelector(String.format(DATA_POLICY_LIST_ROW_COLUMN_CSS, dataPolicyName)
				+ DATA_POLICY_END_DATE_COLUMN_CSS)).getText();

		assertThat(Utility.convertToDate(actualDateString))
				.as("Data Policy End Date not set to current time.")
				.isToday();
	}

	/**
	 * Verifies Data Policy End Date is in future.
	 * @param dataPolicyName String
	 */
	public void verifyDataPolicyEndDateIsFuture(final String dataPolicyName) {
		verifyDataPolicyExists(dataPolicyName);
		String actualDateString = getDriver().findElement(By.cssSelector(String.format(DATA_POLICY_LIST_ROW_COLUMN_CSS, dataPolicyName)
				+ DATA_POLICY_END_DATE_COLUMN_CSS)).getText();

		assertThat(Utility.convertToDate(actualDateString))
				.as("Data Policy End Date is not future date.")
				.isInTheFuture();
	}

	/**
	 * Verifies Create Data Policy button is present.
	 */
	public void verifyCreateDataPolicyButtonIsPresent() {
		setWebDriverImplicitWait(IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS);
		assertThat(getDriver().findElement(By.cssSelector(CREATE_DATA_POLICY_BUTTON_CSS)).isDisplayed())
				.as("Unable to find Create Data Policy button")
				.isTrue();

		setWebDriverImplicitWaitToDefault();
	}
}
