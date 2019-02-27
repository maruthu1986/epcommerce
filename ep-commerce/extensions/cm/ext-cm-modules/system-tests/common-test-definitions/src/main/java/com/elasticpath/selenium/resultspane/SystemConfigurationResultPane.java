package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * System Configuration Result Pane.
 */
public class SystemConfigurationResultPane extends AbstractPageObject {
	private static final String SETTING_NAME_FILTER_INPUT_CSS = "div[automation-id='com.elasticpath.cmclient.admin.configuration"
			+ ".AdminConfigurationMessages.filterLabel'] + div > input";
	private static final String EXPECTED_SETTING_RESULT_CSS = "div[row-id='%s']";
	private static final String DEFINED_CONTEXT_NAME_ROW_VALUE_CSS = "div[row-id='%s'][widget-type='row'] ";
	private static final String DEFINED_CONTEXT_NAME_COLUMN_VALUE_CSS = "div[column-id='%s']+ div[column-num='1']";
	private static final String MAXIMIZE_WINDOW_CSS = "div[pane-location='center-pane-outer'] div[appearance-id='ctabfolder-button']"
			+ "[widget-id='Maximize'][seeable='true']";
	private static final String RESTORE_WINDOW_CSS = "div[pane-location='left-pane-outer'] div[appearance-id='ctabfolder-button']"
			+ "[widget-id='Restore'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public SystemConfigurationResultPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Enters setting name in filter input box.
	 *
	 * @param settingName String
	 */
	public void enterSettingNameFilterInput(final String settingName) {
		clearAndType(SETTING_NAME_FILTER_INPUT_CSS, settingName);
	}

	/**
	 * Maximize System Configuration window.
	 */
	public void maximizeSystemConfigurationWindow() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(MAXIMIZE_WINDOW_CSS)));
	}

	/**
	 * Restore System Configuration window.
	 */
	public void restoreSystemConfigurationWindow() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(RESTORE_WINDOW_CSS)));
	}

	/**
	 * Verifies and Select if setting name shows in filter.
	 *
	 * @param settingName String
	 */
	public void selectSettingName(final String settingName) {
		assertThat(getWaitDriver().waitForElementToBeInteractable(String.format(EXPECTED_SETTING_RESULT_CSS, settingName)))
				.as("Expected setting name not exist - " + settingName)
				.isTrue();
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(EXPECTED_SETTING_RESULT_CSS, settingName))));
	}

	/**
	 * Verifies Defined setting Value.
	 *
	 * @param storeCode    the store code
	 * @param settingValue String
	 */
	public void verifyDefinedSettingValue(final String storeCode, final String settingValue) {
		assertThat(getDriver().findElement(By.cssSelector(String.format(DEFINED_CONTEXT_NAME_ROW_VALUE_CSS, storeCode)
				+ String.format(DEFINED_CONTEXT_NAME_COLUMN_VALUE_CSS, storeCode))).getText())
				.as("Unexpected Defined Value for Store Data Policy " + storeCode)
				.isEqualTo(settingValue);
	}

}
