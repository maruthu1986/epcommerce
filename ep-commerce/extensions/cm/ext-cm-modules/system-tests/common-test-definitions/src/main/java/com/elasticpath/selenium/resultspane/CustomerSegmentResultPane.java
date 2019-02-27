package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.editor.CustomerSegmentEditor;
import com.elasticpath.selenium.util.Constants;

/**
 * Customer Segment Result Pane.
 */
public class CustomerSegmentResultPane extends AbstractPageObject {
	private static final String CUSTOMER_SEGMENT_LIST_PARENT_CSS = "div[widget-id='Customer Segment List'][widget-type='Table'] ";
	private static final String CUSTOMER_SEGMENT_LIST_CSS = CUSTOMER_SEGMENT_LIST_PARENT_CSS + "div[column-id='%s']";
	private static final String CREATE_CUSTOMER_SEGMENT_BUTTON_CSS = "div[widget-id='Create Customer Segment'][seeable='true']";
	private static final String DELETE_CUSTOMER_SEGMENT_BUTTON_CSS = "div[widget-id='Delete Customer Segment'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CustomerSegmentResultPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Create Customer Segments.
	 *
	 * @return CustomerSegmentEditor
	 */
	public CustomerSegmentEditor clickCreateCustomerSegmentButton() {
		clickButton(CREATE_CUSTOMER_SEGMENT_BUTTON_CSS, "Create Customer Segment");
		return new CustomerSegmentEditor(getDriver());
	}

	/**
	 * Opens Customer Segment editor.
	 *
	 * @param customerSegmentName String.
	 * @return the Customer Segment editor.
	 */
	public CustomerSegmentEditor openCustomerSegmentEditor(final String customerSegmentName) {
		verifyCustomerSegmentsExists(customerSegmentName);
		doubleClick(getSelectedElement(), CustomerSegmentEditor.CUSTOMER_SEGMENT_EDITOR_PARENT_CSS);
		return new CustomerSegmentEditor(getDriver());
	}

	/**
	 * Verifies if Customer Segment exists.
	 *
	 * @param customerSegmentName String
	 */
	public void verifyCustomerSegmentsExists(final String customerSegmentName) {
		assertThat(selectItemInCenterPaneWithoutPagination(CUSTOMER_SEGMENT_LIST_PARENT_CSS, CUSTOMER_SEGMENT_LIST_CSS, customerSegmentName,
				"Customer Segment Name"))
				.as("Customer Segment Name does not exist in the list - " + customerSegmentName)
				.isTrue();
	}

	/**
	 * Selects and deletes the Customer Segment.
	 *
	 * @param customerSegmentName String
	 */
	public void deleteCustomerSegment(final String customerSegmentName) {
		verifyCustomerSegmentsExists(customerSegmentName);
		clickDeleteCustomerSegmentButton();
		new ConfirmDialog(getDriver()).clickOKButton("AdminCustomersMessages.DeleteCustomerSegmentTitle");
	}

	/**
	 * Clicks Delete Customer Segment button.
	 */
	public void clickDeleteCustomerSegmentButton() {
		clickButton(DELETE_CUSTOMER_SEGMENT_BUTTON_CSS, "Delete Customer Segment");
	}

	/**
	 * Verifies Customer Segment is not in the list.
	 *
	 * @param customerSegmentName String
	 */
	public void verifyCustomerSegmentIsNotInList(final String customerSegmentName) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(verifyItemIsNotInCenterPaneWithoutPagination(CUSTOMER_SEGMENT_LIST_PARENT_CSS, CUSTOMER_SEGMENT_LIST_CSS, customerSegmentName,
				"Customer Segment Name"))
				.as("Delete failed, Customer Segment is still in the list - " + customerSegmentName)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

}
