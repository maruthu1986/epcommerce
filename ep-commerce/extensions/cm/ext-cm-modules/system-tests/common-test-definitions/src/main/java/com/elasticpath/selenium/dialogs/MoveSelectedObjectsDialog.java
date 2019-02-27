package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

/**
 * Move Selected Objects Dialog.
 */
public class MoveSelectedObjectsDialog extends AbstractDialog {

	private static final String MOVE_ITEM_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.changeset.ChangeSetMessages"
			+ ".ChangeSetDialog_MoveObjects_WindowTitle'][widget-type='Shell'] ";
	private static final String MOVE_ITEM_TABLE_CSS = MOVE_ITEM_PARENT_CSS + "div[widget-id='ChangeSet Table'][widget-type='Table'] ";
	private static final String CHANGE_SET_NAME_COLUMN_CSS = MOVE_ITEM_TABLE_CSS + "div[column-id='%s']";
	private static final String MOVE_BUTTON_CSS = MOVE_ITEM_PARENT_CSS + "div[widget-id='Move']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public MoveSelectedObjectsDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Selects a row with the given changeset name from the table.
	 *
	 * @param changeSetName change set name
	 */
	public void selectChangeSetfromTable(final String changeSetName) {
		assertThat(selectItemInEditorPaneWithScrollBar(MOVE_ITEM_TABLE_CSS, CHANGE_SET_NAME_COLUMN_CSS, changeSetName, "Change Set Name"))
				.as("Unable to find object - " + changeSetName)
				.isTrue();
	}

	/**
	 * Clicks Move button.
	 */
	public void clickMoveButton() {
		clickButton(String.format(MOVE_BUTTON_CSS, "Move"), "Move");
	}

}
