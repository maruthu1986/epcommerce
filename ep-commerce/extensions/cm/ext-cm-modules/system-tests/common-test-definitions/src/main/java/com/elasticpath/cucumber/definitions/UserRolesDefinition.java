package com.elasticpath.cucumber.definitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.domainobjects.KeyValue;
import com.elasticpath.selenium.resultspane.UserRolesResultPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ConfigurationActionToolbar;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.selenium.util.Utility;
import com.elasticpath.selenium.wizards.AddEditUserRoleWizard;

/**
 * User Roles step definitions.
 */
public class UserRolesDefinition {
	private final ConfigurationActionToolbar configurationActionToolbar;
	private UserRolesResultPane userRolesResultPane;
	private AddEditUserRoleWizard addEditUserRoleWizard;
	private String userRoleName;
	private String removedPermission;
	private boolean isUserRoleCreated;
	private boolean runCleanupUserRole = true;

	/**
	 * Constructor.
	 */
	public UserRolesDefinition() {
		configurationActionToolbar = new ConfigurationActionToolbar(SetUp.getDriver());
	}

	/**
	 * Clicks user roles link.
	 */
	@When("^I go to User Roles$")
	public void clickUserRolesLink() {
		userRolesResultPane = configurationActionToolbar.clickUserRoles();
	}

	/**
	 * Verifies user roles exists.
	 *
	 * @param expectedRole the expected role.
	 */
	@Then("^User role (.+) exists$")
	public void verifyUserRoleExists(final String expectedRole) {
		userRolesResultPane.verifyUserRoleExists(expectedRole);
	}

	/**
	 * Verifies newly created user role exists.
	 */
	@Then("^I should see the newly created user role in the list$")
	public void verifyNewlyCreatedUserRoleExists() {
		verifyUserRoleExists(this.userRoleName);
	}

	/**
	 * Verify User Role link is present.
	 */
	@And("^I can view User Roles link")
	public void verifyUserRolesLinkIsPresent() {
		configurationActionToolbar.verifyUserRolesLinkIsPresent();
	}

	/**
	 * Selects user role.
	 *
	 * @param userRole the user role
	 */
	@When("^I select user role (.+)$")
	public void selectUserRole(final String userRole) {
		userRolesResultPane.selectUserRole(userRole);
	}

	/**
	 * Clicks the create user role button.
	 */
	@And("^I click the create user role button$")
	public void clickAddUserRoleButton() {
		addEditUserRoleWizard = userRolesResultPane.clickCreateUserRoleButton();
	}

	/**
	 * Clicks the edit user role button.
	 */
	@And("^I click the edit user role button$")
	public void clickEditUserRoleButton() {
		addEditUserRoleWizard = userRolesResultPane.clickEditUserRoleButton();
	}

	/**
	 * Clicks the delete user role button.
	 */
	@And("^I click the delete user role button$")
	public void clickDeleteUserRoleButton() {
		userRolesResultPane.clickDeleteUserRoleButton();
	}

	/**
	 * Selects available permission.
	 *
	 * @param availablePermission the available permission
	 */
	@And("^I select available permission (.+)$")
	public void selectAvailablePermission(final String availablePermission) {
		addEditUserRoleWizard.selectAvailablePermission(availablePermission);
	}

	/**
	 * Creates new user role.
	 *
	 * @param keyValueList list of available permissions
	 */
	@And("^I create a new user role with following permissions?$")
	public void createNewUserRole(final List<KeyValue> keyValueList) {
		createUserRole(keyValueList);
		viewUserRoleAssignedPermissions();
		addEditUserRoleWizard.expandAssignedPermissions();
		addEditUserRoleWizard.verifyAssignedPermission(keyValueList);
		addEditUserRoleWizard.clickFinish();
	}

	/**
	 * Removes assigned permission.
	 *
	 * @param assignedPermission the assigned permission
	 */
	@And("^I remove assigned permission (.+)$")
	public void removeAssignedPermission(final String assignedPermission) {
		this.removedPermission = assignedPermission;
		viewUserRoleAssignedPermissions();
		addEditUserRoleWizard.expandAssignedPermissions();
		addEditUserRoleWizard.selectAssignedPermission(assignedPermission);
		addEditUserRoleWizard.clickRemoveButton();
		addEditUserRoleWizard.clickFinish();
	}

	/**
	 * Verifies assigned permissions.
	 *
	 * @param permissionList the list of permissions
	 */
	@Then("^user role should contain following assigned permissions?$")
	public void verifyAssignedPermission(final List<String> permissionList) {
		viewUserRoleAssignedPermissions();
		addEditUserRoleWizard.expandAssignedPermissions();
		for (String permission : permissionList) {
			addEditUserRoleWizard.verifyAssignedPermission(permission);
		}
		addEditUserRoleWizard.clickFinish();
	}

	/**
	 * Verifies removed permission is not in the list.
	 */
	@Then("^user role should not contain the removed permission$")
	public void verifyPermissionIsRemoved() {
		viewUserRoleAssignedPermissions();
		addEditUserRoleWizard.expandAssignedPermissions();
		addEditUserRoleWizard.verifyAssignedPermissionIsRemoved(this.removedPermission);
		addEditUserRoleWizard.clickFinish();
	}

	/**
	 * Verifies removed permission is not in the list.
	 */
	@When("^I delete the (?:newly created|existing) user role$")
	public void deleteLatestUserRole() {
		selectUserRole(this.userRoleName);
		clickDeleteUserRoleButton();
	}

	/**
	 * Verifies user role is deleted.
	 */
	@Then("^user role is deleted$")
	public void verifyUserRoleIsDeleted() {
		userRolesResultPane.verifyUserRoleDoesNotExist(this.userRoleName);
		this.runCleanupUserRole = false;
	}

	/**
	 * Creates user role.
	 *
	 * @param keyValueList the list of KeyValue class
	 */
	@Given("^there is an existing user role with following permissions?$")
	public void existingUserRole(final List<KeyValue> keyValueList) {
		createUserRole(keyValueList);
	}

	/**
	 * Creates user role.
	 *
	 * @param keyValueList the list of KeyValue class
	 */
	private void createUserRole(final List<KeyValue> keyValueList) {
		isUserRoleCreated = false;
		clickUserRolesLink();
		clickAddUserRoleButton();

		userRoleName = "Test Role " + Utility.getRandomUUID().substring(0, Constants.UUID_END_INDEX);
		addEditUserRoleWizard.enterRoleName(userRoleName);
		addEditUserRoleWizard.enterRoleDescription("Test Role Description");
		addEditUserRoleWizard.clickNextInDialog();

		for (KeyValue keyValue : keyValueList) {
			if (keyValue.getValue() == null || keyValue.getValue().length() <= 0) {
				assertThat(false)
						.as("Please provide sub permission for " + keyValue.getKey())
						.isTrue();
			} else {
				addEditUserRoleWizard.clickPermissionExpandIcon(keyValue.getKey());
				String[] valueArray = keyValue.getValue().split(",");
				for (String value : valueArray) {
					addEditUserRoleWizard.selectSubPermission(value.trim());
					addEditUserRoleWizard.clickAddButton();
				}
			}

			addEditUserRoleWizard.clickPermissionExpandIcon(keyValue.getKey());
		}
		addEditUserRoleWizard.expandAssignedPermissions();
		addEditUserRoleWizard.verifyAssignedPermission(keyValueList);
		addEditUserRoleWizard.clickFinish();
		isUserRoleCreated = true;
	}

	/**
	 * Selects user role, clicks edit role button and clicks the next button in wizard.
	 */
	private void viewUserRoleAssignedPermissions() {
		selectUserRole(this.userRoleName);
		clickEditUserRoleButton();
		addEditUserRoleWizard.clickNextInDialog();
	}

	/**
	 * Deletes new user role.
	 */
	@After("@cleanupUserRole")
	public void userRoleCleanup() {
		addEditUserRoleWizard.clickCancelButton();
		if (isUserRoleCreated) {
			if (runCleanupUserRole) {
				clickUserRolesLink();
				selectUserRole(this.userRoleName);
				clickDeleteUserRoleButton();
				userRolesResultPane.verifyUserRoleDoesNotExist(this.userRoleName);
			}
		} else {
			assertThat(false)
					.as("Unable to create user role")
					.isTrue();
		}
	}
}
