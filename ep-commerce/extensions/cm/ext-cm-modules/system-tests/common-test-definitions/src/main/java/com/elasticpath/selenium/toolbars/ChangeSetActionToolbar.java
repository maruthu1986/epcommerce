package com.elasticpath.selenium.toolbars;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Change Set Toolbar.
 */
public class ChangeSetActionToolbar extends AbstractToolbar {

	private static final String CHANGE_SET_LIST_CSS = "div[widget-id='Select Change Set']";
	private static final String CHANGE_SET_MENU_ITEM_CSS = "div[appearance-id='menu'] div[widget-id='%s']";
	private static final String ADD_ITEM_TO_CHANGE_SET_BUTTON_CSS
			= "div[automation-id*='com.elasticpath.cmclient.changeset.ChangeSetMessages.Add']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ChangeSetActionToolbar(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Selects change set.
	 *
	 * @param changeSetName the change set name.
	 */
	public void selectChangeSet(final String changeSetName) {
		click(getDriver().findElement(By.cssSelector(CHANGE_SET_LIST_CSS)));
		hoverMouseOverElement(getDriver().findElement(By.cssSelector(SAVE_ALL_BUTTON_CSS)));
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(CHANGE_SET_MENU_ITEM_CSS, changeSetName))));
	}

	/**
	 * Clicks add item to change set button.
	 */
	public void clickAddItemToChangeSet() {
		getWaitDriver().waitForButtonToBeEnabled(ADD_ITEM_TO_CHANGE_SET_BUTTON_CSS);
		clickButton(ADD_ITEM_TO_CHANGE_SET_BUTTON_CSS, "Add to change set");
	}

	/**
	 * Returns Add Item to Change Set button css.
	 *
	 * @return button css
	 */
	public String getAddItemToChangeSetButtonCss() {
		return ADD_ITEM_TO_CHANGE_SET_BUTTON_CSS;
	}

}
