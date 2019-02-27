package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateEditProfileAttributeDialog;
import com.elasticpath.selenium.util.Constants;

/**
 * Profile Attribute pane.
 */
public class ProfileAttributePane extends AbstractPageObject {

	private static final String PROFILE_ATTRIBUTE_LIST_PARENT_CSS = "div[widget-id='Attribute List'][widget-type='Table'] ";
	private static final String PROFILE_ATTRIBUTE_LIST_CSS = PROFILE_ATTRIBUTE_LIST_PARENT_CSS + "div[column-id='%s']";
	private static final String CREATE_PROFILE_ATTRIBUTE_BUTTON_CSS = "div[widget-id='Create Attribute'][seeable='true']";
	private static final String DELETE_PROFILE_ATTRIBUTE_BUTTON_CSS = "div[widget-id='Delete Attribute'][seeable='true']";
	private static final String EDIT_PROFILE_ATTRIBUTE_BUTTON_CSS = "div[widget-id='Edit Attribute'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ProfileAttributePane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies if Profile Attribute exists.
	 *
	 * @param profileAttributeName String
	 */
	public void verifyProfileAttributeExists(final String profileAttributeName) {
		assertThat(selectItemInCenterPaneWithoutPagination(PROFILE_ATTRIBUTE_LIST_PARENT_CSS, PROFILE_ATTRIBUTE_LIST_CSS, profileAttributeName,
				"Attribute Name"))
				.as("Profile Attribute does not exist in the list - " + profileAttributeName)
				.isTrue();
	}

	/**
	 * Verifies Profile Attribute is not in the list.
	 *
	 * @param profileAttributeName String
	 */
	public void verifyProfileAttributeIsNotInList(final String profileAttributeName) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(verifyItemIsNotInCenterPaneWithoutPagination(PROFILE_ATTRIBUTE_LIST_PARENT_CSS, PROFILE_ATTRIBUTE_LIST_CSS, profileAttributeName,
				"Attribute Name"))
				.as("Delete failed, warehouse does is still in the list - " + profileAttributeName)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Selects and deletes the Profile Attribute.
	 *
	 * @param profileAttributeName String
	 */
	public void deleteProfileAttribute(final String profileAttributeName) {
		verifyProfileAttributeExists(profileAttributeName);
		clickDeleteProfileAttributeButton();
		new ConfirmDialog(getDriver()).clickOKButton("AdminCustomersMessages.DeleteProfileAttributeTitle");
	}

	/**
	 * Clicks Create Profile Attribute button.
	 *
	 * @return Profile Atrribute pane
	 */
	public CreateEditProfileAttributeDialog clickCreateProfileAttributeButton() {
		final String dialogName = "Create";
		clickButton(CREATE_PROFILE_ATTRIBUTE_BUTTON_CSS, "Create Attribute", String.format(CreateEditProfileAttributeDialog
				.CREATE_EDIT_PROFILE_ATTRIBUTE_DIALOG_CSS_TEMPLATE, dialogName));
		return new CreateEditProfileAttributeDialog(getDriver(), dialogName);
	}

	/**
	 * Clicks Delete Profile Atrribute button.
	 */
	public void clickDeleteProfileAttributeButton() {
		clickButton(DELETE_PROFILE_ATTRIBUTE_BUTTON_CSS, "Delete Attribute");
	}

	/**
	 * Selects and Edits the Profile Attribute.
	 *
	 * @param profileAttributeName String
	 * @return CreateEditProfileAttributeDialog
	 */
	public CreateEditProfileAttributeDialog clickEditProfileAttributeButton(final String profileAttributeName) {
		verifyProfileAttributeExists(profileAttributeName);
		final String dialogName = "Edit";
		clickButton(EDIT_PROFILE_ATTRIBUTE_BUTTON_CSS, "Edit Attribute", String.format(CreateEditProfileAttributeDialog
				.CREATE_EDIT_PROFILE_ATTRIBUTE_DIALOG_CSS_TEMPLATE, dialogName));
		return new CreateEditProfileAttributeDialog(getDriver(), dialogName);
	}

}
