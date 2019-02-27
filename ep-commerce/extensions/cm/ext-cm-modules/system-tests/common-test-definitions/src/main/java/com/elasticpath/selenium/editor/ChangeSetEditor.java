package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.AddEditBrandDialog;
import com.elasticpath.selenium.dialogs.MoveSelectedObjectsDialog;
import com.elasticpath.selenium.domainobjects.DST;
import com.elasticpath.selenium.setup.SetUp;

/**
 * Change Set Editor.
 */
public class ChangeSetEditor extends AbstractPageObject {

	/**
	 * Page Object Id.
	 */
	private static final String SUMMARY_TAB_PARENT_CSS = "div[automation-id='SummaryPage'][active-editor='true'][seeable='true'] ";
	private static final String SUMMARY_GUID_INPUT_CSS = SUMMARY_TAB_PARENT_CSS + " div[automation-id='com.elasticpath.cmclient.changeset"
			+ ".ChangeSetMessages.ChangeSetEditor_ChangeSet_Guid'] input";
	private static final String OBJECT_TABLE_PARENT_CSS = "div[widget-id='Object Table'][widget-type='Table'][seeable='true'] ";
	private static final String OBJECT_COLUMN_CSS = OBJECT_TABLE_PARENT_CSS + "div[column-id='%s']";
	private static final String CHANGE_SET_TAB_CSS =
			"div[automation-id='com.elasticpath.cmclient.changeset.ChangeSetMessages.ChangeSetEditor_%s'][seeable='true']";
	private static final String CHANGE_SET_EDITOR = "div[widget-id='%s'][appearance-id='ctab-item']";
	private static final String CHANGE_SET_SUMMARY_PAGE = "div[pane-location='editor-pane'] div[automation-id='SummaryPage'][seeable='true'] ";
	private static final String CHANGE_SET_OBJECTS_TITLE = "div[automation-id='com.elasticpath.cmclient.changeset.ChangeSetMessages"
			+ ".ChangeSetEditor_%s'][seeable='true'][appearance-id='label-wrapper']";
	private static final String CHANGE_SET_NAME_INPUT_CSS = CHANGE_SET_SUMMARY_PAGE + "div[automation-id='com.elasticpath.cmclient.changeset"
			+ ".ChangeSetMessages.ChangeSetEditor_ChangeSet_Name'] input";
	private static final String OPEN_OBJECT_BUTTON_CSS = "div[widget-id='Open Object'][seeable='true']";
	private static final String OBJECT_SELECT_CHECKBOX_CSS = "div[widget-id='Object Table'][widget-type='Table'] "
			+ "div[row-id='alien'][widget-id='alien'] div[style*='themes']";
	private static final String MOVE_SELECTED_OBJECTS_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.changeset."
			+ "ChangeSetMessages.ChangeSetEditor_Objects_MoveObjects";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ChangeSetEditor(final WebDriver driver) {
		super(driver);
	}


	/**
	 * Verifies object exists.
	 *
	 * @param objectColumnValue the column value
	 * @param columnName        the column name
	 */
	public void verifyObjectExists(final String objectColumnValue, final String columnName) {
		assertThat(selectItemInEditorPaneWithScrollBar(OBJECT_TABLE_PARENT_CSS, OBJECT_COLUMN_CSS, objectColumnValue, columnName))
				.as("Unable to find object - " + objectColumnValue)
				.isTrue();
	}

	/**
	 * Selects Objects tab.
	 */
	public void selectObjectsTab() {
		String cssSelector = String.format(CHANGE_SET_TAB_CSS, "ObjectsPageTitle");
		resizeWindow(cssSelector);
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(cssSelector)));
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(String.format(CHANGE_SET_OBJECTS_TITLE, "ObjectsFormTitle")));
	}

	/**
	 * Selects Summary tab.
	 */
	public void selectSummaryTab() {
		String cssSelector = String.format(CHANGE_SET_TAB_CSS, "Summary_Page_Title");
		resizeWindow(cssSelector);
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(cssSelector)));
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(String.format(CHANGE_SET_OBJECTS_TITLE, "Summary_Form_Title")));
	}

	/**
	 * Selects change set's editor.
	 *
	 * @param changeSetName the change set name
	 */
	public void selectChangeSetEditor(final String changeSetName) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(CHANGE_SET_EDITOR, changeSetName))));
	}

	/**
	 * Changes changeset Name.
	 *
	 * @param newName the new name to change to
	 */
	public void changeName(final String newName) {
		clearAndType(CHANGE_SET_NAME_INPUT_CSS, newName);
	}

	/**
	 * Returns changeset tab css.
	 *
	 * @param changeSetName change set name.
	 * @return changeset tab css.
	 */
	public static String getChangeSetTabId(final String changeSetName) {
		return String.format(CHANGE_SET_EDITOR, changeSetName);
	}

	/**
	 * Compares number of items in Objects tab with the number of items added to the change set.
	 *
	 * @param numOfItems items added to the change set
	 */
	public void verifyNumberOfChangeSetObjects(final int numOfItems) {
		String results = getDriver().findElement(By.cssSelector("div[automation-id='ObjectsPage'] div[automation-id='com.elasticpath.cmclient.core"
				+ ".CoreMessages.navigation_Search_Results']")).getText();
		String[] resultsArray = results.split("of");
		int resultTotal = Integer.parseInt(resultsArray[1].trim());

		assertThat(resultTotal)
				.as("Number of objects are not as expected")
				.isEqualTo(numOfItems);
	}

	/**
	 * Returns change set guid.
	 *
	 * @return change set guid
	 */
	public String getChangeSetGuid() {
		selectSummaryTab();
		return getDriver().findElement(By.cssSelector(SUMMARY_GUID_INPUT_CSS)).getAttribute("value");
	}

	/**
	 * Click open object button.
	 *
	 * @return AddEditBrandDialog
	 */
	public AddEditBrandDialog clickOpenObjectButton() {
		clickButton(OPEN_OBJECT_BUTTON_CSS, "Open Object");
		final String dialogName = "Edit";
		return new AddEditBrandDialog(getDriver(), dialogName);
	}

	/**
	 * sets change set guid.
	 *
	 * @param dst the DST class
	 */
	public void setChangeSetGuid(final DST dst) {
		dst.setChangeSetGuid(getChangeSetGuid());
	}


	/**
	 * Clicks the checkbox which is the first column of the object in the change set editor's Objects tab.
	 *
	 * @param objectName object name
	 */
	public void selectObjectInChangeSet(final String objectName) {
		verifyObjectExists(objectName, "Object Name");
		clickCheckBox(OBJECT_SELECT_CHECKBOX_CSS);
	}

	/**
	 * Clicks move selected objects button.
	 *
	 * @return MoveSelectedObjectsDialog object
	 */
	public MoveSelectedObjectsDialog clickMoveSelectedObjectsButton() {
		click(MOVE_SELECTED_OBJECTS_BUTTON_CSS);
		return new MoveSelectedObjectsDialog(SetUp.getDriver());
	}

}
