package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.CM;
import com.elasticpath.selenium.setup.PublishEnvSetUp;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.util.Constants;

/**
 * Sign In Page.
 */
public class SignInDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String USERNAME_INPUT_CSS = "div[widget-id='User ID'] input";
	private static final String PASSWORD_INPUT_CSS = "div[widget-id='Password'] input";
	private static final String SIGN_IN_BUTTON_CSS = "div[widget-id='Sign In']";
	private static final String ERROR_MSG_XPATH = "//div[text()= 'Authentication failed. Check your user ID and password, then try again"
			+ ".'][contains(@style, 'overflow: hidden')]";
	private static final String POST_LOGIN_WINDOW_CSS = "[window-id=post-login-window]";
	private static final String DISABLED_USER_DIALOG_CSS = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".EpLoginDialog_ErrorTitle_ServerCommunication']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public SignInDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Enters username value.
	 *
	 * @param username the username.
	 */
	public void enterUsername(final String username) {
		assertThat(getWaitDriver().waitForWindowToMaximize())
				.as("Browser is not maximized as expected")
				.isTrue();
		clearAndType(USERNAME_INPUT_CSS, username);
	}

	/**
	 * Enters password value.
	 *
	 * @param password the password.
	 */
	public void enterPassword(final String password) {
		clearAndType(PASSWORD_INPUT_CSS, password);
	}

	/**
	 * Clicks on Sign In.
	 *
	 * @return the CM.
	 */
	public CM clickSignIn() {
		clickButton(SIGN_IN_BUTTON_CSS, "Sign In");
		return new CM(getDriver());
	}

	/**
	 * Verifies signin failed.
	 */
	public void verifySignInFailed() {
		assertThat(isElementPresent(By.xpath(ERROR_MSG_XPATH)))
				.as("Expected sign in error message is not present.")
				.isTrue();
	}

	/**
	 * Verifies user is signed in successfully by checking that user is on
	 * the post login window.
	 */
	public void verifySignInSuccessful() {
		assertThat(getDriver().findElement(By.cssSelector(POST_LOGIN_WINDOW_CSS))).isNotNull();
	}


	/**
	 * Sign in and verify post login screen is displayed.
	 *
	 * @param username the username.
	 * @param password the password.
	 */
	public void performSignIn(final String username, final String password) {
		signIn(username, password);
		verifySignInSuccessful();
	}

	/**
	 * Sign in.
	 *
	 * @param username the username.
	 * @param password the password.
	 */
	public void performSignInWithInvalidCredentials(final String username, final String password) {
		new CM(SetUp.getDriver()).openCM();
		signIn(username, password);
	}

	/**
	 * Signs into CM with given username and password.
	 *
	 * @param username user name
	 * @param password password
	 */
	public void signIn(final String username, final String password) {
		enterUsername(username);
		enterPassword(password);
		clickSignIn();
	}

	/**
	 * Sign in.
	 *
	 * @param username the username.
	 * @param password the password.
	 */
	public void initialSignIn(final String username, final String password) {
		new CM(SetUp.getDriver()).openCM();
		performSignIn(username, password);
	}

	/**
	 * Sign in to Publish.
	 *
	 * @param username the username.
	 * @param password the password.
	 */
	public void signInToPublish(final String username, final String password) {
		new CM(PublishEnvSetUp.getDriver()).openPublishCM();
		performSignIn(username, password);
	}

	/**
	 * Verify disabled user dialog is present.
	 */
	public void verifyDisabledUserDialogIsPresent() {
		int count = 0;
		while (!isElementPresent(By.cssSelector(DISABLED_USER_DIALOG_CSS)) && count < Constants.RETRY_COUNTER_3) {
			sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			count++;
		}
		assertThat(isElementPresent(By.cssSelector(DISABLED_USER_DIALOG_CSS))).as("User is Disabled dialog is not present.").isTrue();
		new ConfirmDialog(SetUp.getDriver()).clickOKButton("com.elasticpath.cmclient.core.CoreMessages"
				+ ".EpLoginDialog_ErrorTitle_ServerCommunication");
	}

}
