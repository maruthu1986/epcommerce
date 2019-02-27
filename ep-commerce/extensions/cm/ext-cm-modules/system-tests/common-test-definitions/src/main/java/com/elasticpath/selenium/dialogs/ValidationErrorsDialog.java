package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Validation Errors dialog.
 */
public class ValidationErrorsDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String ERROR_PARENT_CSS = "div[widget-id*='Error'] ";
	private static final String ERROR_DIV_CSS = ERROR_PARENT_CSS + "div[widget-id*='%s'] > div";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ValidationErrorsDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verify Error message.
	 *
	 * @param errMsg the error message.
	 */
	public void verifyErrorMessage(final String errMsg) {
		WebElement element = getWaitDriver().waitForElementToBeVisible(By.cssSelector(String.format(ERROR_DIV_CSS, errMsg)));
		assertThat(element.getText())
				.as("Error message validation failed")
				.contains(errMsg);

		clickOK();
	}

	/**
	 * Verify all error messages.
	 *
	 * @param errMsgList the error message list.
	 */
	public void verifyAllErrorMessages(final List<String> errMsgList) {
		for (String errMsg : errMsgList) {
			if (errMsg.length() > 0) {
				WebElement element = getWaitDriver().waitForElementToBeVisible(By.cssSelector(String.format(ERROR_DIV_CSS, errMsg)));
				assertThat(element.getText())
						.as("Error message validation failed")
						.contains(errMsg);
			}
		}

		clickOK();
	}
}
