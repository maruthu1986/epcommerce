package com.elasticpath.cucumber.definitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import com.elasticpath.selenium.dialogs.SignInDialog;
import com.elasticpath.selenium.domainobjects.DST;
import com.elasticpath.selenium.framework.util.PropertyManager;
import com.elasticpath.selenium.setup.PublishEnvSetUp;
import com.elasticpath.selenium.setup.SetUp;

/**
 * Sign In steps.
 */
public class SignInDefinition {
	private final SignInDialog signInDialog;
	private final String adminID;
	private final String adminPassword;

	/**
	 * Constructor.
	 */
	public SignInDefinition() {
		signInDialog = new SignInDialog(SetUp.getDriver());
		adminID = PropertyManager.getInstance().getProperty("admin.id");
		adminPassword = PropertyManager.getInstance().getProperty("cm.password");
	}

	/**
	 * Sign In CM as admin user.
	 */
	@Given("^I sign in to CM as admin user$")
	public void signInAsAdmin() {
		signInDialog.initialSignIn(adminID, adminPassword);
	}

	/**
	 * Sign In Author CM as admin user.
	 */
	@Given("^I sign in to the author environment CM as admin user$")
	public void signInToAuthor() {
		new DST();
		signInDialog.initialSignIn(adminID, adminPassword);
	}

	/**
	 * Sign In to publish environment CM as admin user.
	 */
	@Given("^I sign in to the publish environment CM as admin user$")
	public void signInToPublishServerAsAdmin() {
		new SignInDialog(PublishEnvSetUp.getDriver()).signInToPublish(adminID, adminPassword);
	}

	/**
	 * Sign In CM as CSR user.
	 */
	@Given("^I sign in to CM as CSR user$")
	public void signInAsCSR() {
		String csrID = PropertyManager.getInstance().getProperty("csr.id");
		String csrPassword = PropertyManager.getInstance().getProperty("csr.password");

		signInDialog.initialSignIn(csrID, csrPassword);
	}

	/**
	 * Sign In CM per given credentials.
	 *
	 * @param username the username.
	 * @param password the password.
	 */
	@Given("^I attempt to sign in with invalid credentials to CM as (.*) with password (.+)$")
	public void attemptSignInAsUser(final String username, final String password) {
		signInDialog.performSignInWithInvalidCredentials(username, password);
	}

	/**
	 * Sign in to CM per given credentials and verify successfully signed in.
	 *
	 * @param username the username.
	 * @param password the password.
	 */
	@Then("^I sign in to CM as (.*) with password (.+)$")
	public void signInAsUser(final String username, final String password) {
		signInDialog.initialSignIn(username, password);
	}


	/**
	 * Sign in to CM per given credentials and verify successfully signed in.
	 *
	 * @param username the username.
	 * @param password the password.
	 */
	@Then("^I attempt sign in with disabled user (.*) with password (.+)$")
	public void attemptSignInWithDisabledUser(final String username, final String password) {
		signInDialog.signIn(username, password);
	}

	/**
	 * Sign in to CM again after logging out and verify successfully signed in.
	 *
	 * @param username the username.
	 * @param password the password.
	 */
	@Then("^I (?:should be able to sign in again|sign in again) to CM as (.*) with password (.+)$")
	public void signInAgain(final String username, final String password) {
		signInDialog.performSignIn(username, password);
	}

	/**
	 * Verify Sign in Error.
	 */
	@Then("^I should not be able to sign in$")
	public void verifySignInError() {
		signInDialog.verifySignInFailed();
	}

}
