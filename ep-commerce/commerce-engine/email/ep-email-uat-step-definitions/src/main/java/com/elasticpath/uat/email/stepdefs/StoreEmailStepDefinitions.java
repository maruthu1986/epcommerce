/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.email.stepdefs;

import static org.assertj.core.api.Assertions.assertThat;

import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getContents;
import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getEmailMessageBySubject;

import java.util.Map;
import javax.mail.Message;

import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for Store email functionality.
 */
public class StoreEmailStepDefinitions {

	@Autowired
	private ScenarioContextValueHolder<Map<String, Message>> emailMessagesHolder;

	@Autowired
	private ScenarioContextValueHolder<SimpleStoreScenario> storeScenarioHolder;

	@Then("^the(?: \"(.+)\")? email should contain the Store URL$")
	public void verifyEmailContainsStoreUrl(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));
		assertThat(emailContents)
				.as("The email contents should include the Store URL")
				.contains(storeScenarioHolder.get().getStore().getUrl());
	}

}
