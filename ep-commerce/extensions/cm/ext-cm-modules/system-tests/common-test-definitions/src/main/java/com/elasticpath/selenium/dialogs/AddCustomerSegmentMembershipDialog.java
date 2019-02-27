package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Add Customer Segment Membership Dialog.
 */
public class AddCustomerSegmentMembershipDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String CUSTOMER_SEGMENT_MEMBERSHIP_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.fulfillment"
			+ ".FulfillmentMessages.CustomerSegmentsPageDialog_AddWindowTitle'] ";
	private static final String CUSTOMER_SEGMENT_NAME_COMBO_CSS = CUSTOMER_SEGMENT_MEMBERSHIP_PARENT_CSS + "div[widget-id='Segment "
			+ "Name'][widget-type='CCombo']";
	private static final String SAVE_BUTTON_CSS = CUSTOMER_SEGMENT_MEMBERSHIP_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonSave']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddCustomerSegmentMembershipDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verify created Enabled Customer Segment in combo box list.
	 *
	 * @param customerSegmentName String
	 */
	public void verifyCustomerSegmentExists(final String customerSegmentName) {
		assertThat(selectComboBoxItem(CUSTOMER_SEGMENT_NAME_COMBO_CSS, customerSegmentName))
				.as("Unable to find customer segment - " + customerSegmentName)
				.isTrue();
	}

	/**
	 * Select Customer Segment.
	 *
	 * @param segmentName Segment Name.
	 */
	public void selectCustomerSegment(final String segmentName) {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(CUSTOMER_SEGMENT_MEMBERSHIP_PARENT_CSS));
		assertThat(selectComboBoxItem(CUSTOMER_SEGMENT_NAME_COMBO_CSS, segmentName))
				.as("Unable to find Segment Name - " + segmentName)
				.isTrue();
	}

	/**
	 * Clicks save.
	 */
	@Override
	public void clickSave() {
		clickButton(SAVE_BUTTON_CSS, "Save");
		waitTillElementDisappears(By.cssSelector(CUSTOMER_SEGMENT_MEMBERSHIP_PARENT_CSS));
	}
}