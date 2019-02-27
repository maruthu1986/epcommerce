package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create User Dialog.
 */
public class CreateUserDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String CREATE_USER_PARENT_CSS = "div[widget-id='Create User'][widget-type='Shell'] ";
	private static final String USER_NAME_INPUT_CSS = CREATE_USER_PARENT_CSS + "div[widget-id='User Name'] input";
	private static final String FIRST_NAME_INPUT_CSS = CREATE_USER_PARENT_CSS + "div[widget-id='First Name'] input";
	private static final String LAST_NAME_INPUT_CSS = CREATE_USER_PARENT_CSS + "div[widget-id='Last Name'] input";
	private static final String EMAIL_ADDRESS = CREATE_USER_PARENT_CSS + "div[widget-id='Email Address'] input";
	private static final String FINISH_BUTTON_CSS = CREATE_USER_PARENT_CSS + "div[widget-id='Finish']";
	private static final String NEXT_BUTTON_CSS = CREATE_USER_PARENT_CSS + "div[widget-id='Next >']";
	private static final String AVAILABLE_ROLES_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.admin.users.AdminUsersMessages"
			+ ".RoleAssignment_AvailableRoles'][seeable='true'][widget-type='Table'] ";
	private static final String AVAILABLE_ROLES_COLUMN_CSS = AVAILABLE_ROLES_PARENT_CSS + "div[column-id='%s']";
	private static final String MOVE_RIGHT_BUTTON_CSS = CREATE_USER_PARENT_CSS + "div[widget-id='>']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreateUserDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs user name.
	 *
	 * @param userName String
	 */
	public void enterUserName(final String userName) {
		clearAndType(USER_NAME_INPUT_CSS, userName);

	}

	/**
	 * Inputs first name.
	 *
	 * @param firstName String
	 */
	public void enterFirstName(final String firstName) {
		clearAndType(FIRST_NAME_INPUT_CSS, firstName);
	}

	/**
	 * Inputs last name.
	 *
	 * @param lastName String
	 */
	public void enterLastName(final String lastName) {
		clearAndType(LAST_NAME_INPUT_CSS, lastName);
	}

	/**
	 * Inputs email.
	 *
	 * @param emailAddress String
	 */
	public void enterEmailAddress(final String emailAddress) {
		clearAndType(EMAIL_ADDRESS, emailAddress);
	}

	/**
	 * Clicks finish button.
	 */
	public void clickFinishButton() {
		clickButton(FINISH_BUTTON_CSS, "Finish");
		waitTillElementDisappears(By.cssSelector(CREATE_USER_PARENT_CSS));
	}

	/**
	 * Clicks next button.
	 */
	public void clickNextButton() {
		clickButton(NEXT_BUTTON_CSS, "Next");
	}

	/**
	 * Clicks move right button.
	 */
	public void clickMoveRightButton() {
		clickButton(MOVE_RIGHT_BUTTON_CSS, "Move Right");
	}

	/**
	 * Selects role from available roles list.
	 *
	 * @param role String
	 */
	public void selectAvailableRole(final String role) {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(AVAILABLE_ROLES_PARENT_CSS));
		assertThat(selectItemInDialog(AVAILABLE_ROLES_PARENT_CSS, AVAILABLE_ROLES_COLUMN_CSS, role, "Available Roles"))
				.as("Unable to find role - " + role)
				.isTrue();
	}

	/**
	 * Checks if the dialog is present.
	 *
	 * @return boolean
	 */
	public boolean isDialogPresent() {
		return isElementPresent(By.cssSelector(CREATE_USER_PARENT_CSS));
	}
}
