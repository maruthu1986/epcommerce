package com.elasticpath.selenium.navigations;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.resultspane.UsersResultPane;

/**
 * User search.
 */
public class UserSearch extends AbstractNavigation {

	private static final String USER_SEARCH_BUTTON_CSS = "div[widget-id='Search'][seeable='true']";
	private static final String USER_NAME_TEXT_FIELD = "div[automation-id='com.elasticpath.cmclient.admin.users.AdminUsersMessages"
			+ ".SearchView_Search_Label_UserName'][widget-type='Text'] > input";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public UserSearch(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks on search button.
	 *
	 * @return UsersResultPane
	 */
	public UsersResultPane clickSearchButton() {
		clickButton(USER_SEARCH_BUTTON_CSS, "Search");
		return new UsersResultPane(getDriver());
	}

	/**
	 * Enters user name in 'User Name' text field.
	 *
	 * @param userName user name
	 */
	public void enterUserName(final String userName) {
		clearAndType(USER_NAME_TEXT_FIELD, userName);
	}

	/**
	 * Enters first name in 'First Name' text field.
	 *
	 * @param firstName first name
	 */
	public void enterFirstName(final String firstName) {
		clearAndType(USER_NAME_TEXT_FIELD, firstName);
	}
}
