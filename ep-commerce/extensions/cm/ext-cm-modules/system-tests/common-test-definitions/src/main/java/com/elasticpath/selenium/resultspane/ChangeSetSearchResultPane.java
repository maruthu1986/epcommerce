package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateChangeSetDialog;
import com.elasticpath.selenium.editor.ChangeSetEditor;
import com.elasticpath.selenium.util.Constants;

/**
 * Change Set Search Results Pane.
 */
public class ChangeSetSearchResultPane extends AbstractPageObject {

	private static final String CREATE_BUTTON_CSS
			= "div[automation-id='com.elasticpath.cmclient.changeset.ChangeSetMessages.ChangeSetsView_CreateChangeSetTooltip']";
	private static final String CHANGE_SET_SEARCH_RESULT_PARENT = "div[widget-id='Changeset View Table'][widget-type='Table'] ";
	private static final String CHANGE_SET_SEARCH_RESULT_COLUMN_CSS = CHANGE_SET_SEARCH_RESULT_PARENT + "div[column-id='%s']";
	private static final String CHANGE_SET_SEARCH_RESULT_ROW_CSS = CHANGE_SET_SEARCH_RESULT_PARENT + "div[row-id='%s'] ";
	private static final String LOCK_BUTTON_CSS = "div[widget-id='Lock']";
	private static final String UNLOCK_BUTTON_CSS = "div[widget-id='Unlock']";
	private static final String PUBLISH_BUTTON_CSS = "div[widget-id='Publish']";
	private static final String FINALIZE_BUTTON_CSS = "div[widget-id='Finalize']";
	private static final String NAME_COLUMN_NAME = "Name";
	private static final int CREATE_CHANGESET_TOOLBAR_WAIT = 3;


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ChangeSetSearchResultPane(final WebDriver driver) {
		super(driver);
	}


	/**
	 * Clicks Create button.
	 *
	 * @return CreateChangeSetDialog
	 */
	public CreateChangeSetDialog clickCreateButton() {
		/**
		 * This wait is intentional due to the issue that changeset toolbar is initially shifting to the right when CM first launched causing button
		 * not clickable intermittently.
		 */
		getWaitDriver().waitFor(CREATE_CHANGESET_TOOLBAR_WAIT);
		clickButton(CREATE_BUTTON_CSS, "Create", CreateChangeSetDialog.CREATE_CHANGE_SET_PARENT_CSS);
		return new CreateChangeSetDialog(getDriver());
	}

	/**
	 * Clicks Lock button.
	 */
	public void clickLockButton() {
		sleep(Constants.SLEEP_ONE_SECOND_IN_MILLIS);
		if (isButtonEnabled(LOCK_BUTTON_CSS)) {
			clickButton(LOCK_BUTTON_CSS, "Lock");
		}
	}

	/**
	 * Clicks Unlock button.
	 */
	public void clickUnlockButton() {
		sleep(Constants.SLEEP_ONE_SECOND_IN_MILLIS);
		if (isButtonEnabled(UNLOCK_BUTTON_CSS)) {
			clickButton(UNLOCK_BUTTON_CSS, "Unlock");
		}
	}

	/**
	 * Clicks Publish button.
	 */
	public void clickPublishButton() {
		clickButton(PUBLISH_BUTTON_CSS, "Publish");
		new ConfirmDialog(getDriver()).clickOK();
		waitTillElementDisappears(By.cssSelector("div[widget-id='OK'][seeable='true']"));
	}

	/**
	 * Clicks Finalized button.
	 */
	public void clickFinalizedButton() {
		getWaitDriver().waitForButtonToBeEnabled(FINALIZE_BUTTON_CSS);
		clickButton(FINALIZE_BUTTON_CSS, "Finalize");
		new ConfirmDialog(getDriver()).clickOK();
		waitTillElementDisappears(By.cssSelector("div[widget-id='OK'][seeable='true']"));
	}

	/**
	 * Verifies change set in list.
	 *
	 * @param changeSetName String
	 */
	public void verifyChangeSetExists(final String changeSetName) {
		assertThat(selectItemInCenterPane(CHANGE_SET_SEARCH_RESULT_PARENT, CHANGE_SET_SEARCH_RESULT_COLUMN_CSS, changeSetName, NAME_COLUMN_NAME))
				.as("Expected Change Set does not exist in search result - " + changeSetName)
				.isTrue();
	}

	/**
	 * Selects change set in list.
	 *
	 * @param changeSetName String
	 */
	public void selectChangeSet(final String changeSetName) {
		verifyChangeSetExists(changeSetName);
	}

	/**
	 * Opens Change Set editor.
	 *
	 * @param changeSetName The change set name
	 * @return ChangeSetEditor
	 */
	public ChangeSetEditor openChangeSetEditor(final String changeSetName) {
		selectChangeSet(changeSetName);
		doubleClick(getSelectedElement(), ChangeSetEditor.getChangeSetTabId(changeSetName));
		return new ChangeSetEditor(getDriver());
	}

	/**
	 * Verifies change set status.
	 *
	 * @param changeSetName the change set name
	 * @param status        the change set status
	 */
	public void verifyChangeSetStatus(final String changeSetName, final String status) {
		selectChangeSet(changeSetName);
		assertThat(getDriver().findElement(By.cssSelector(String.format(CHANGE_SET_SEARCH_RESULT_ROW_CSS, changeSetName)
				+ String.format("div[column-id='%s']", status))).getText())
				.as("Change set status validation failed")
				.isEqualTo(status);

	}

	/**
	 * Verifies if change set exists.
	 *
	 * @param changeSetName String
	 * @return boolean true if change set is in the list, else returns false
	 */
	public boolean isChangeSetInList(final String changeSetName) {
		setWebDriverImplicitWait(1);
		boolean isChangeSetInList = selectItemInCenterPane(CHANGE_SET_SEARCH_RESULT_PARENT, CHANGE_SET_SEARCH_RESULT_COLUMN_CSS, changeSetName,
				NAME_COLUMN_NAME);
		setWebDriverImplicitWaitToDefault();
		return isChangeSetInList;
	}

}
