package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateUserDialog;
import com.elasticpath.selenium.navigations.UserSearch;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.wizards.EditUser;

/**
 * User Result Pane.
 */
public class UsersResultPane extends AbstractPageObject {
	private static final String CREATE_USER_BUTTON_CSS = "div[widget-id='Create User'][widget-type='ToolItem'][seeable='true']";
	private static final String DISABLE_USER_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.admin.users.AdminUsersMessages"
			+ ".DisableUser'][widget-type='ToolItem']";
	private static final String USER_SEARCH_RESULT_PARENT_CSS = "div[widget-id='User List'][widget-type='Table'] ";
	private static final String USER_SEARCH_RESULT_LIST = "div[column-id='%s']";
	private final UserSearch userSearch;
	private static final int SLEEP_COUNT = 4;
	private static final int SLEEP_TIME = 500;

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public UsersResultPane(final WebDriver driver) {
		super(driver);
		this.userSearch = new UserSearch(driver);
	}

	/**
	 * Open create user dialog.
	 *
	 * @return CreateUserDialog String
	 */
	public CreateUserDialog clickCreateUserButton() {
		clickButton(CREATE_USER_BUTTON_CSS, "Create User", CreateUserDialog.CREATE_USER_PARENT_CSS);
		return new CreateUserDialog(getDriver());
	}

	/**
	 * Click Disable User Button.
	 */
	public void disableUser() {
		clickButton(DISABLE_USER_BUTTON_CSS, "Disable User");
		new ConfirmDialog(getDriver()).clickYesButton("com.elasticpath.cmclient.admin.users.AdminUsersMessages.DisableUser");
	}


	/**
	 * Set user's status to Active.
	 *
	 * @param userName user name
	 */
	public void enableUser(final String userName) {
		openEditUserWizard(userName);
		final EditUser editUserWizard = new EditUser(SetUp.getDriver());
		editUserWizard.setUserStatus("Active");
		editUserWizard.clickFinish();
	}

	/**
	 * Is user in the list.
	 *
	 * @param userName user to search for
	 * @return true/false
	 */
	public boolean isUserInList(final String userName) {
		return selectItemInCenterPane(USER_SEARCH_RESULT_PARENT_CSS, USER_SEARCH_RESULT_LIST,
				userName, "User Name");
	}

	/**
	 * Is user in the list.
	 *
	 * @param userName   user to search for
	 * @param columnName column name
	 * @return true/false
	 */
	public boolean isUserInList(final String userName, final String columnName) {
		return selectItemInCenterPane(USER_SEARCH_RESULT_PARENT_CSS, USER_SEARCH_RESULT_LIST,
				userName, columnName);
	}

	/**
	 * Verify user by the given username exists in list.
	 *
	 * @param userName the username.
	 */
	public void verifyUserExistsInList(final String userName) {
		userSearch.clickSearchButton();
		boolean isUserInList = isUserInList(userName);

		int count = 0;
		while (!isUserInList && count < SLEEP_COUNT) {
			sleep(SLEEP_TIME);
			userSearch.clickSearchButton();
			isUserInList = isUserInList(userName);
			count++;
		}

		assertThat(isUserInList)
				.as("User with userName " + userName + "does not exist in search result - ")
				.isTrue();
	}

	/**
	 * Verify user exists in list using the given column name and column value to verify.
	 *
	 * @param columnValue the column value to find.
	 * @param columnName  column name
	 */
	public void verifyUserExistsInList(final String columnValue, final String columnName) {
		boolean isUserInList = isUserInList(columnValue, columnName);

		int count = 0;
		while (!isUserInList && count < SLEEP_COUNT) {
			sleep(SLEEP_TIME);
			isUserInList = isUserInList(columnValue, columnName);
			count++;
		}

		assertThat(isUserInList)
				.as("User with columnValue " + columnValue + "does not exist in search result - ")
				.isTrue();
	}

	/**
	 * Select user from list and double click to edit user.
	 *
	 * @param userName user name.
	 */
	public void openEditUserWizard(final String userName) {
		verifyUserExistsInList(userName);
		doubleClick(getSelectedElement(), EditUser.EDIT_USER_PARENT_CSS);
	}

}
