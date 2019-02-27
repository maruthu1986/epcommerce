package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create Change Set Dialog.
 */
public class CreateChangeSetDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String CREATE_CHANGE_SET_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.changeset.ChangeSetMessages"
			+ ".CreateChangeSetWizard_Title'] ";
	private static final String CHANGE_SET_NAME_INPUT_CSS = CREATE_CHANGE_SET_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.changeset.ChangeSetMessages.ChangeSetEditor_ChangeSet_Name'] input";

	private static final String FINISH_BUTTON_CSS = CREATE_CHANGE_SET_PARENT_CSS + "div[widget-id='Finish']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreateChangeSetDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs change set name.
	 *
	 * @param changeSetName the code.
	 */
	public void enterChangeSetName(final String changeSetName) {
		clearAndType(CHANGE_SET_NAME_INPUT_CSS, changeSetName);
	}

	/**
	 * Clicks Finish button.
	 */
	public void clickFinishButton() {
		clickButton(FINISH_BUTTON_CSS, "Finish");
		waitTillElementDisappears(By.cssSelector(CREATE_CHANGE_SET_PARENT_CSS));
	}

}
