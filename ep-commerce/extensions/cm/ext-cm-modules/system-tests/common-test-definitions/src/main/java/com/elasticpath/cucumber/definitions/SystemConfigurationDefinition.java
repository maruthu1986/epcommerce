package com.elasticpath.cucumber.definitions;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.resultspane.SystemConfigurationResultPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ConfigurationActionToolbar;

/**
 * System Configuration step definitions.
 */
public class SystemConfigurationDefinition {
	private SystemConfigurationResultPane systemConfigurationResultPane;
	private final ConfigurationActionToolbar configurationActionToolbar;

	/**
	 * Constructor.
	 */
	public SystemConfigurationDefinition() {
		configurationActionToolbar = new ConfigurationActionToolbar(SetUp.getDriver());
		systemConfigurationResultPane = new SystemConfigurationResultPane(SetUp.getDriver());
	}

	/**
	 * Open System Configuration.
	 */
	@When("^I go to System Configuration$")
	public void openSystemConfiguration() {
		systemConfigurationResultPane = configurationActionToolbar.clickSystemConfiguration();
	}

	/**
	 * Enter setting name.
	 *
	 * @param settingName String
	 */
	@When("^I enter setting name (.+) in filter$")
	public void enterSettingName(final String settingName) {
		systemConfigurationResultPane.enterSettingNameFilterInput(settingName);
	}

	/**
	 * Verify setting name exists.
	 *
	 * @param settingName String
	 */
	@Then("^I should see setting (.+) in the filter result$")
	public void selectSettingName(final String settingName) {
		systemConfigurationResultPane.selectSettingName(settingName);
	}

	/**
	 * Verify setting Defined values.
	 *
	 * @param storeCode    the store code
	 * @param settingValue String
	 */
	@Then("^Defined Value for Store (.+) is (.+)$")
	public void verifyDefinedSettingValue(final String storeCode, final String settingValue) {
		systemConfigurationResultPane.maximizeSystemConfigurationWindow();
		systemConfigurationResultPane.verifyDefinedSettingValue(storeCode, settingValue);
		systemConfigurationResultPane.restoreSystemConfigurationWindow();
	}

}
