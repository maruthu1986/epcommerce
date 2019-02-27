package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.selenium.wizards.AddEditUserRoleWizard;

/**
 * Uer Roles Result Pane.
 */
public class UserRolesResultPane extends AbstractPageObject {

	private static final String ROLE_TABLE_PARENT_CSS = "div[widget-id='Role'][widget-type='Table'] ";
	private static final String ROLE_COLUMN_CSS = ROLE_TABLE_PARENT_CSS + "div[column-id='%s']";
	private static final String USER_ROLE_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.admin.users.AdminUsersMessages.%s']"
			+ "[seeable='true']";
	private static final String CREATE_USER_ROLE_BUTTON_CSS = String.format(USER_ROLE_BUTTON_CSS, "CreateRole");
	private static final String EDIT_USER_ROLE_BUTTON_CSS = String.format(USER_ROLE_BUTTON_CSS, "EditRole");
	private static final String DELETE_USER_ROLE_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.admin.users.AdminUsersMessages"
			+ ".DeleteRole'][seeable='true']";
	private static final String DELETE_ROLE_CONFIRM_DIALOG_CSS = "com.elasticpath.cmclient.admin.users.AdminUsersMessages.DeleteRole";
	private final WebDriver driver;


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public UserRolesResultPane(final WebDriver driver) {
		super(driver);
		this.driver = driver;
	}

	/**
	 * Clicks Create User Role button.
	 *
	 * @return AddEditUserRoleWizard
	 */
	public AddEditUserRoleWizard clickCreateUserRoleButton() {
		clickButton(CREATE_USER_ROLE_BUTTON_CSS, "Create User Role");
		return new AddEditUserRoleWizard(driver);
	}

	/**
	 * Clicks Edit User Role button.
	 *
	 * @return AddEditUserRoleWizard
	 */
	public AddEditUserRoleWizard clickEditUserRoleButton() {
		clickButton(EDIT_USER_ROLE_BUTTON_CSS, "Edit User Role");
		return new AddEditUserRoleWizard(driver);
	}

	/**
	 * Clicks Delete User Role button.
	 */
	public void clickDeleteUserRoleButton() {
		clickButton(DELETE_USER_ROLE_BUTTON_CSS, "Delete User Role");
		new ConfirmDialog(driver).clickYesButton(DELETE_ROLE_CONFIRM_DIALOG_CSS);
	}

	/**
	 * Selects user role.
	 *
	 * @param userRole the user role
	 */
	public void selectUserRole(final String userRole) {
		assertThat(selectItemInCenterPaneWithoutPagination(ROLE_TABLE_PARENT_CSS, ROLE_COLUMN_CSS, userRole, "Role"))
				.as("User role '" + userRole + "' is not in the list as expected")
				.isTrue();
	}

	/**
	 * Verifies given user role exists.
	 *
	 * @param expectedUserRole the user role
	 */
	public void verifyUserRoleExists(final String expectedUserRole) {
		selectUserRole(expectedUserRole);
	}

	/**
	 * Verifies given user doesn't exist.
	 *
	 * @param userRole the user role
	 */
	public void verifyUserRoleDoesNotExist(final String userRole) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(verifyItemIsNotInCenterPaneWithoutPagination(ROLE_TABLE_PARENT_CSS, ROLE_COLUMN_CSS, userRole, "Role"))
				.as("User role '" + userRole + "' should not be in the list as expected")
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

}
