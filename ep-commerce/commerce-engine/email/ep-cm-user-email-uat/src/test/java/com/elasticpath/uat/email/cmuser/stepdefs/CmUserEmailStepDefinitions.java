/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.email.cmuser.stepdefs;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getContents;
import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getEmailMessageBySubject;

import java.util.Map;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.builder.CmUserBuilder;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for verifying CM User emails.
 */
public class CmUserEmailStepDefinitions {

	@Autowired
	private ScenarioContextValueHolder<Map<String, Message>> emailMessagesHolder;

	@Autowired
	private ScenarioContextValueHolder<CmUserBuilder> cmUserBuilderHolder;

	@Autowired
	private CmUserService cmUserService;

	private static final String OPTIONAL_EMAIL_SUBJECT = "(?: \"(.+)\")?";

	@Then("^the" + OPTIONAL_EMAIL_SUBJECT + " email should contain the CM User's name$")
	public void verifyEmailContainsCmUserName(final String emailSubject) throws Exception {

		final Message message = getEmailMessageBySubject(emailSubject, emailMessagesHolder.get());
		final CmUser cmUser = getCmUser(getRecipientEmail(message));
		final String emailContents = getContents(message);
		assertThat("The email contents should include the CM User's first name", emailContents, containsString(cmUser.getFirstName()));
		assertThat("The email contents should include the CM User's last name", emailContents, containsString(cmUser.getLastName()));
	}

	@Then("^the" + OPTIONAL_EMAIL_SUBJECT + " email should contain the CM User's username$")
	public void verifyEmailContainsCmUserUsername(final String emailSubject) throws Exception {

		final Message message = getEmailMessageBySubject(emailSubject, emailMessagesHolder.get());
		final CmUser cmUser = getCmUser(getRecipientEmail(message));
		assertThat("The email contents should include the CM User's login User ID", getContents(message), containsString(cmUser.getUsername()));
	}

	private CmUser getCmUser(final String email) {
		return cmUserService.findByEmail(email);
	}

	private String getRecipientEmail(final Message message) throws MessagingException {
		final Address[] addresses = message.getAllRecipients();
		// there should be exactly one address; none of the CM User emails are sent to multiple people
		final InternetAddress address = (InternetAddress) addresses[0];
		return address.getAddress();
	}

}
