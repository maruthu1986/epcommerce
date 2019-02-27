package com.elasticpath.cucumber.definitions;

import java.util.List;

import cucumber.api.java.en.Then;

import com.elasticpath.selenium.dialogs.ErrorDialog;
import com.elasticpath.selenium.dialogs.ValidationErrorsDialog;
import com.elasticpath.selenium.setup.SetUp;

/**
 * Common definitions.
 */
public class CommonDefinition {

	/**
	 * Verify Error message.
	 *
	 * @param errMsg the error message.
	 */
	@Then("^I should see error (.+)$")
	public void verifyErrorMessage(final String errMsg) {
		new ValidationErrorsDialog(SetUp.getDriver()).verifyErrorMessage(errMsg);
	}

	/**
	 * Verify error message.
	 *
	 * @param errMsgList the list of messages.
	 */
	@Then("^I should see following error messages?$")
	public void verifyErrorMessages(final List<String> errMsgList) {
		new ValidationErrorsDialog(SetUp.getDriver()).verifyAllErrorMessages(errMsgList);
	}

	/**
	 * Verify Error message.
	 *
	 * @param errMsg the error message.
	 */
	@Then("^I should see the following error: (.+)$")
	public void verifyGenericErrorMessage(final String errMsg) {
		new ErrorDialog(SetUp.getDriver()).verifyErrorMessage(errMsg);
	}

}
