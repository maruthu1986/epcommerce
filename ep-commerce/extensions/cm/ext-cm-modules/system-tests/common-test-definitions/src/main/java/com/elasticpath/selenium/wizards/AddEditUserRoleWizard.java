package com.elasticpath.selenium.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.elasticpath.selenium.domainobjects.KeyValue;
import com.elasticpath.selenium.util.Constants;

/**
 * Add and Edit User Role Wizard.
 */
public class AddEditUserRoleWizard extends AbstractWizard {

	private static final String ROLE_WIZARD_PARENT_CSS = "div[automation-id^='com.elasticpath.cmclient.admin.users.AdminUsersMessages']"
			+ "[widget-type='Shell'] ";
	private static final String ROLE_NAME_INPUT_CSS = "div[automation-id='com.elasticpath.cmclient.admin.users.AdminUsersMessages"
			+ ".RoleDetails_RoleName'][widget-type='Text'] > input";
	private static final String ROLE_DESCRIPTION_TEXTAREA_CSS = "div[automation-id='com.elasticpath.cmclient.admin.users.AdminUsersMessages"
			+ ".RoleDetails_Description'][widget-type='Text'] > textarea";
	private static final String AVAILABLE_PERMISSION_PARENT_CSS = ROLE_WIZARD_PARENT_CSS + "div[appearance-id='tree'] ";
	private static final String AVAILABLE_PERMISSION_COLUMN_CSS = AVAILABLE_PERMISSION_PARENT_CSS + "div[column-id='%s']";
	private static final String PERMISSION_EXPAND_ICON_CSS = ROLE_WIZARD_PARENT_CSS + " div[row-id='%s'] div[expand-icon]";
	private static final String PERMISSION_EXPAND_ICONS_XPATH
			= "((//div[@appearance-id='tree']//div[@seeable='true'])[2]//div[not(contains(@style,'display: none'))]"
			+ "//div[not(contains(@style,'display: none'))]//div[@expand-icon][not(contains(@style,'display: none'))])";
	private static final String PERMISSION_EXPAND_SINGLE_ICON_XPATH = "(" + PERMISSION_EXPAND_ICONS_XPATH + ")[%s]";
	private static final String ADD_BUTTON_CSS = ROLE_WIZARD_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.button_Add']";
	private static final String REMOVE_BUTTON_CSS = ROLE_WIZARD_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.button_Remove']";
	private static final String CANCEL_BUTTON_CSS = ROLE_WIZARD_PARENT_CSS + "div[widget-id='Cancel'][seeable='true']";
	private static final String ASSIGNED_PERMISSION_COLUMN_CSS = "div[appearance-id='tree'] div[column-id='%s']";

	private final WebDriver driver;


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddEditUserRoleWizard(final WebDriver driver) {
		super(driver);
		this.driver = driver;
	}

	/**
	 * Enters role name.
	 *
	 * @param roleName the role name
	 */
	public void enterRoleName(final String roleName) {
		clearAndType(ROLE_NAME_INPUT_CSS, roleName);
	}

	/**
	 * Enters role description.
	 *
	 * @param roleDescription the role description
	 */
	public void enterRoleDescription(final String roleDescription) {
		clearAndType(ROLE_DESCRIPTION_TEXTAREA_CSS, roleDescription);
	}

	/**
	 * Selects available permission.
	 *
	 * @param availablePermission the available permission
	 */
	public void selectAvailablePermission(final String availablePermission) {
		click(String.format(AVAILABLE_PERMISSION_COLUMN_CSS, availablePermission));
	}

	/**
	 * Selects available sub-permission.
	 *
	 * @param subPermission the sub permission
	 */
	public void selectSubPermission(final String subPermission) {
		click(By.cssSelector(String.format(AVAILABLE_PERMISSION_COLUMN_CSS, subPermission)));
	}

	/**
	 * Clicks permission expand icon.
	 *
	 * @param availablePermission the available permission
	 */
	public void clickPermissionExpandIcon(final String availablePermission) {
		moveFocusToElement(driver.findElement(By.cssSelector(String.format(PERMISSION_EXPAND_ICON_CSS, availablePermission))));
		click(String.format(PERMISSION_EXPAND_ICON_CSS, availablePermission));
	}

	/**
	 * Clicks the add '>' button.
	 */
	public void clickAddButton() {
		clickButton(ADD_BUTTON_CSS, "Add >");
	}

	/**
	 * Verifies assigned permissions.
	 *
	 * @param assignedPermission the assigned permissions
	 */
	public void verifyAssignedPermission(final String assignedPermission) {
		String[] assignedPermissionArray = assignedPermission.split(",");
		for (String permission : assignedPermissionArray) {
			assertThat(isElementPresent(By.cssSelector(String.format(ASSIGNED_PERMISSION_COLUMN_CSS, permission.trim()))))
					.as("Permission '" + permission + "' is not in Assigned Permission column as expected")
					.isTrue();
		}
	}

	/**
	 * Verifies assigned permissions.
	 *
	 * @param keyValueList the list of KeyValue class
	 */
	public void verifyAssignedPermission(final List<KeyValue> keyValueList) {
		for (KeyValue keyValue : keyValueList) {
			verifyAssignedPermission(keyValue.getValue());
		}
	}

	/**
	 * Selects assigned permission.
	 *
	 * @param assignedPermission the sub permission
	 */
	public void selectAssignedPermission(final String assignedPermission) {
		click(By.cssSelector(String.format(ASSIGNED_PERMISSION_COLUMN_CSS, assignedPermission)));
	}

	/**
	 * Clicks the remove '<' button.
	 */
	public void clickRemoveButton() {
		clickButton(REMOVE_BUTTON_CSS, "Remove <");
	}

	/**
	 * Verifies permission is not in the assigned permission column.
	 *
	 * @param permission the removed permission
	 */
	public void verifyAssignedPermissionIsRemoved(final String permission) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(isElementPresent(By.cssSelector(String.format(ASSIGNED_PERMISSION_COLUMN_CSS, permission))))
				.as("Permission '" + permission + "' should not be in Assigned Permission column")
				.isFalse();
		setWebDriverImplicitWaitToDefault();

	}

	/**
	 * Clicks the cancel button.
	 */
	public void clickCancelButton() {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		if (isElementPresent(By.cssSelector(CANCEL_BUTTON_CSS))) {
			clickButton(CANCEL_BUTTON_CSS, "Cancel Button");
		}
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Expands all grouped permissions.
	 */
	public void expandAssignedPermissions() {
		int visibleAssignedPermissions = driver.findElements(By.xpath(PERMISSION_EXPAND_ICONS_XPATH)).size();
		for (int i = 0; i < visibleAssignedPermissions; i++) {
			WebElement webElement = driver.findElement(By.xpath(String.format(PERMISSION_EXPAND_SINGLE_ICON_XPATH, String.valueOf(i + 1))));
			moveFocusToElement(webElement);
			click(webElement);
		}
	}
}
