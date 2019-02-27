package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Validation Errors dialog.
 */
public class ErrorDialog extends AbstractDialog {

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ErrorDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verify Error message.
	 *
	 * @param errMsg the error message.
	 */
	public void verifyErrorMessage(final String errMsg) {
		setWebDriverImplicitWait(2);
		try {
			getDriver().findElement(By.cssSelector("div[widget-id*='" + errMsg + "']"));
			clickOK();
		} catch (Exception e) {
			assertThat(false)
					.as("Expected error message validation failed - '" + errMsg + "'").isEqualTo(true);
		}
		setWebDriverImplicitWaitToDefault();
	}

}
