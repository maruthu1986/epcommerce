package com.elasticpath.cucumber.definitions;

import java.util.Map;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.CreateEditProfileAttributeDialog;
import com.elasticpath.selenium.resultspane.ProfileAttributePane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ConfigurationActionToolbar;
import com.elasticpath.selenium.util.Utility;

/**
 * Warehouse step definitions.
 */
public class ProfileAttributeDefinition {
	private final ConfigurationActionToolbar configurationActionToolbar;
	private ProfileAttributePane profileAttributePane;
	private CreateEditProfileAttributeDialog createEditProfileAttributeDialog;
	private String profileAttributeName;

	/**
	 * Constructor.
	 */
	public ProfileAttributeDefinition() {
		configurationActionToolbar = new ConfigurationActionToolbar(SetUp.getDriver());
	}

	/**
	 * Click user roles.
	 */
	@When("^I go to Profile Attributes")
	public void clickUserRoles() {
		profileAttributePane = configurationActionToolbar.clickProfileAttributes();
	}

	/**
	 * Create Profile Attribute.
	 *
	 * @param profileAttributeMap the profileAttribute map.
	 */
	@When("^I create profile attribute with following values$")
	public void createProfileAttribute(final Map<String, String> profileAttributeMap) {
		createEditProfileAttributeDialog = profileAttributePane.clickCreateProfileAttributeButton();
		String profileAttributeKey = "pt" + Utility.getRandomUUID();
		this.profileAttributeName = profileAttributeMap.get("attribute name") + "_" + profileAttributeKey;
		createEditProfileAttributeDialog.enterProfileAttributeKey(profileAttributeKey);
		createEditProfileAttributeDialog.enterProfileAttributeName(this.profileAttributeName);
		createEditProfileAttributeDialog.selectProfileAttributeType(profileAttributeMap.get("type"));
		createEditProfileAttributeDialog.clickSaveButton();
	}

	/**
	 * Delete new profile attribute.
	 */
	@And("^I delete newly created profile attribute$")
	public void deleteNewProfileAttribute() {
		profileAttributePane.deleteProfileAttribute(this.profileAttributeName);
	}

	/**
	 * Verify new profile attribute no longer exists.
	 */
	@Then("^newly created profile attribute no longer exists$")
	public void verifyNewWarehouseProfileAttributeIsDeleted() {
		profileAttributePane.verifyProfileAttributeIsNotInList(this.profileAttributeName);
	}

	/**
	 * Verify new profile attribute exists.
	 */
	@When("^the new profile attribute name should exist in the list$")
	public void verifyNewProfileAttributeExists() {
		profileAttributePane.verifyProfileAttributeExists(this.profileAttributeName);
	}

	/**
	 * Editing the new profile attribute name.
	 *
	 * @param newProfileAttributeName editing the profile attribute name to another
	 */
	@When("^I edit newly created profile attribute name to (.+)$")
	public void editProfileAttributeName(final String newProfileAttributeName) {
		createEditProfileAttributeDialog = profileAttributePane.clickEditProfileAttributeButton(this.profileAttributeName);
		this.profileAttributeName = newProfileAttributeName + " " + Utility.getRandomUUID();
		createEditProfileAttributeDialog.enterProfileAttributeName(this.profileAttributeName);
		createEditProfileAttributeDialog.clickSaveButton();
	}
}
