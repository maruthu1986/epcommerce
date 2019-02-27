package com.elasticpath.selenium.toolbars;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ChangePaginationSettingsDialog;
import com.elasticpath.selenium.util.Constants;

/**
 * Abstract Toolbar class. Common toolbar actions should be defined here.
 * Toolbar classes should extend this class for common methods.
 */
public abstract class AbstractToolbar extends AbstractPageObject {

	/**
	 * CSS String of Save All button.
	 */
	/**
	 * CSS String of Save All button.
	 */
	protected static final String SAVE_ALL_BUTTON_CSS = "div[widget-id='Save All (Ctrl+Shift+S)']";
	/**
	 * CSS String of Reload button.
	 */
	protected static final String RELOAD_ACTIVE_EDITOR_BUTTON_CSS
			= "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.RefreshAction_Tooltip']";
	/**
	 * CSS String of Save button.
	 */
	protected static final String SAVE_BUTTON_CSS = "div[widget-id='Save (Ctrl+S)']";
	private static final String CHANGE_PAGINATION_SETTINGS_CSS = "div[widget-id='Change Pagination Settings']";


	/**
	 * Constructor.
	 *
	 * @param driver the driver.
	 */
	public AbstractToolbar(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Save button.
	 */
	public void clickSaveButton() {
		clickButton(SAVE_BUTTON_CSS, "Save");
	}


	/**
	 * Clicks on Save All button.
	 */
	public void saveAll() {
		clickButton(SAVE_ALL_BUTTON_CSS, "Save All");
		sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
	}

	/**
	 * Go to Change Pagination Setting dialog.
	 *
	 * @return ChangePaginationSettingsDialog
	 */
	public ChangePaginationSettingsDialog changePaginationSetting() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(CHANGE_PAGINATION_SETTINGS_CSS)));
		return new ChangePaginationSettingsDialog(getDriver());
	}

	/**
	 * Clicks Reloads Active Editor button.
	 */
	public void clickReloadActiveEditor() {
		clickButton(RELOAD_ACTIVE_EDITOR_BUTTON_CSS, "Reload Active Editor");
		sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
	}
}
