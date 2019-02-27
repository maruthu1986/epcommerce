package com.elasticpath.cucumber.definitions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import com.elaticpath.selenium.util.ExtDBConnector;
import cucumber.api.java.en.Then;

/**
 * DB Connection Example Definition.
 */
public class DBConnectionExampleDefinition {

	private final UsersDefinition usersDefinition;

	/**
	 * Constructor.
	 *
	 * @param usersDefinition UserDefinition object injection
	 */
	public DBConnectionExampleDefinition(final UsersDefinition usersDefinition) {
		this.usersDefinition = usersDefinition;
	}

	/**
	 * Verify user status.
	 *
	 * @param expectedStatus the expected status (0 or 1)
	 */
	@Then("^the (?:newly created|disabled) user status should be (.+) in the database$")
	public void verifyUserStatus(final int expectedStatus) {
		ExtDBConnector dbConnector = new ExtDBConnector();
		try {
			int status = dbConnector.checkUserStatus(usersDefinition.getNewUserName());
			assertThat(status).as("Actual User status is not equal to expected user status").isEqualTo(expectedStatus);
		} catch (SQLException e) {
			fail("SQL exception occured \n Message: " + e.getMessage() + "\n" + e.toString());
		}

	}

}
