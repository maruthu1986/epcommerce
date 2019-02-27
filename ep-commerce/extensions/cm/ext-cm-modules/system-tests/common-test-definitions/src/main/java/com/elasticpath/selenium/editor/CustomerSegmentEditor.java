package com.elasticpath.selenium.editor;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Customer Segment Editor.
 */
public class CustomerSegmentEditor extends AbstractPageObject {

	/**
	 * Page Object Id.
	 */
	public static final String CUSTOMER_SEGMENT_EDITOR_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String CUSTOMER_SEGMENT_NAME_INPUT_CSS = CUSTOMER_SEGMENT_EDITOR_PARENT_CSS + "div[widget-id='Customer Segment Name'] > "
			+ "input";
	private static final String CUSTOMER_SEGMENT_DESCRIPTION_INPUT_CSS = CUSTOMER_SEGMENT_EDITOR_PARENT_CSS + "div[widget-id='Description'] > input";
	private static final String CUSTOMER_SEGMENT_SUMMARY_EDITOR_CSS = "div[automation-id='CustomerSegmentSummaryPage']";
	private static final String CUSTOMER_SEGMENT_EDITOR_CLOSE_ICON_CSS = "div[widget-id='%s'][appearance-id='ctab-item'][active-tab='true'] > "
			+ "div[style*='.gif']";
	private static final String CUSTOMER_SEGMENT_ENABLED_CHECKBOX_CSS = CUSTOMER_SEGMENT_EDITOR_PARENT_CSS + "div[widget-type='Button'] > "
			+ "div[style*='e53cf03a.png']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CustomerSegmentEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies if Customer Segment editor is visible.
	 */
	public void verifyCustomerSegmentEditor() {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(CUSTOMER_SEGMENT_SUMMARY_EDITOR_CSS));
	}

	/**
	 * Enters Customer Segment name.
	 *
	 * @param customerSegmentName the Customer Segment name.
	 */
	public void enterCustomerSegmentName(final String customerSegmentName) {
		clearAndType(CUSTOMER_SEGMENT_NAME_INPUT_CSS, customerSegmentName);
	}

	/**
	 * Enters Customer Segment description.
	 *
	 * @param customerSegmentDescription the Customer Segment description.
	 */
	public void enterCustomerSegmentDescription(final String customerSegmentDescription) {
		clearAndType(CUSTOMER_SEGMENT_DESCRIPTION_INPUT_CSS, customerSegmentDescription);
	}

	/**
	 * Close Customer Segment Editor.
	 *
	 * @param customerSegmentName String
	 */
	public void closeCustomerSegmentEditor(final String customerSegmentName) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(CUSTOMER_SEGMENT_EDITOR_CLOSE_ICON_CSS, customerSegmentName)
		)));
	}

	/**
	 * Click enabled checkbox.
	 */
	public void clickCustomerSegmentEnabledCheckBox() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(CUSTOMER_SEGMENT_ENABLED_CHECKBOX_CSS)));
	}

}
