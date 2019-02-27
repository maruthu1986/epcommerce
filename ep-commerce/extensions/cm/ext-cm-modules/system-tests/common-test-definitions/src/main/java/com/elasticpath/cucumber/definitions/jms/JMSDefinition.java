package com.elasticpath.cucumber.definitions.jms;

import cucumber.api.java.en.Then;

import com.elasticpath.jms.cucumber.definitions.RawJsonDefinitions;
import com.elasticpath.selenium.editor.ChangeSetEditor;
import com.elasticpath.selenium.setup.SetUp;

/**
 * JMS steps.
 */
public class JMSDefinition {

	private final RawJsonDefinitions rawJsonDefinitions;
	private final ChangeSetEditor changeSetEditor;

	/**
	 * Constructor.
	 * @param rawJsonDefinitions RawJsonDefinitions
	 */
	public JMSDefinition(final RawJsonDefinitions rawJsonDefinitions) {
		this.rawJsonDefinitions = rawJsonDefinitions;
		this.changeSetEditor = new ChangeSetEditor(SetUp.getDriver());
	}

	/**
	 * Verifies JMS json value.
	 *
	 * @param key the key
	 **/
	@Then("^the json (.+) value should be same as change set guid$")
	public void createChangeSetAndOpenEditor(final String key) {
		rawJsonDefinitions.verifyJsonValues(key, changeSetEditor.getChangeSetGuid());
	}

}
