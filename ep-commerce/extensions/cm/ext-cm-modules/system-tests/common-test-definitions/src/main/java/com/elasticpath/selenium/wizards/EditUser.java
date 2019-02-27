package com.elasticpath.selenium.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

/**
 * Edit User Wizard.
 */
public class EditUser extends AbstractWizard {

	/**
	 * EDIT USER WIZARD PARENT CSS.
	 */
	public static final String EDIT_USER_PARENT_CSS
			= "div[automation-id*='com.elasticpath.cmclient.admin.users.AdminUsersMessages.EditUser'][widget-type='Shell'] ";
	private static final String USER_NAME_INPUT_CSS = EDIT_USER_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.admin.users.AdminUsersMessages.UserName'] > input";
	private static final String FIRST_NAME_INPUT_CSS = EDIT_USER_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.admin.users.AdminUsersMessages.FirstName'] > input";
	private static final String STATUS_COMBO_BOX_CSS = EDIT_USER_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.admin.users.AdminUsersMessages.Status'][widget-type='CCombo'] ";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public EditUser(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs user name.
	 *
	 * @param username the user name
	 */
	public void enterUserName(final String username) {
		clearAndType(USER_NAME_INPUT_CSS, username);
	}

	/**
	 * Inputs first name.
	 *
	 * @param firstname the first name
	 */
	public void enterFirstName(final String firstname) {
		clearAndType(FIRST_NAME_INPUT_CSS, firstname);
	}

	/**
	 * Sets the user status.
	 *
	 * @param status user status
	 */
	public void setUserStatus(final String status) {
		assertThat(selectComboBoxItem(STATUS_COMBO_BOX_CSS, status))
				.as("Unable to find status - " + status)
				.isTrue();

	}
}
