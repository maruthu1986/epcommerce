package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Problem Occured Dialog.
 */
public class ProblemOccurredDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String PROBLEM_OCCURED_DIALOG_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".SystemErrorTitle'] ";
	private static final String DETAILS_BUTTON_CSS = PROBLEM_OCCURED_DIALOG_PARENT_CSS + "div[widget-id*='Details'][appearance-id='push-button']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ProblemOccurredDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Details button.
	 */
	public void clickDetailsButton() {
		clickButton(DETAILS_BUTTON_CSS, "Details");
	}

	/**
	 * Checks if the dialog is present.
	 *
	 * @return boolean
	 */
	public boolean isDialogPresent() {
		return isElementPresent(By.cssSelector(PROBLEM_OCCURED_DIALOG_PARENT_CSS));
	}

}
