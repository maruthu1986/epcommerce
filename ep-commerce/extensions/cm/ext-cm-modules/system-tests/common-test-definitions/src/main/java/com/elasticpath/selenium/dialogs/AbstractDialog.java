package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Abstract dialog class for common dialog actions. Dialog is a single prompt requesting user's confirmation.
 * Dialog classes should extend this class for common methods.
 */
public abstract class AbstractDialog extends AbstractPageObject {

	/**
	 * Cancel button css selector.
	 */
	protected static final String CANCEL_BUTTON_CSS = "div[widget-id='Cancel'][seeable='true']";
	/**
	 * OK button css selector.
	 */
	protected static final String OK_BUTTON_CSS = "div[widget-id='OK'][seeable='true']";
	private static final String SAVE_BUTTON_CSS = "div[widget-id='Save']";
	private static final String MOVERIGHT_BUTTON_CSS = "div[automation-id*='%s'] div[widget-id='>']";
	private static final String MOVEALLLEFT_BUTTON_CSS = "div[automation-id*='%s'] div[widget-id='<<']";

	/**
	 * constructor.
	 *
	 * @param driver the driver.
	 */
	public AbstractDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks cancel.
	 */
	public void clickCancel() {
		clickButton(CANCEL_BUTTON_CSS, "Cancel");
	}

	/**
	 * Clicks save.
	 */
	public void clickSave() {
		clickButton(SAVE_BUTTON_CSS, "Save");
	}

	/**
	 * Clicks OK.
	 */
	public void clickOK() {
		clickButton(OK_BUTTON_CSS, "OK");
	}

	/**
	 * Clicks '>' (move right).
	 *
	 * @param dialogAutomationId the css
	 */
	public void clickMoveRight(final String dialogAutomationId) {
		clickButton(String.format(MOVERIGHT_BUTTON_CSS, dialogAutomationId), dialogAutomationId);
	}

	/**
	 * Clicks '<<' (move all left).
	 *
	 * @param dialogAutomationId the css
	 */
	public void clickMoveAllLeft(final String dialogAutomationId) {
		clickButton(String.format(MOVEALLLEFT_BUTTON_CSS, dialogAutomationId), dialogAutomationId);
	}
}
