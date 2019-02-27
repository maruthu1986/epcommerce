/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.uat.stepdefs;

import static org.apache.camel.builder.Builder.header;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.apache.camel.builder.NotifyBuilder;
import org.jvnet.mock_javamail.Mailbox;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.builder.CmUserBuilder;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.email.test.support.EmailSendingMockInterceptor;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for CM User-based functionality.
 */
public class CmUserStepDefinitions {

	@Autowired
	private ScenarioContextValueHolder<CmUserBuilder> cmUserBuilderHolder;

	@Autowired
	private CmUserBuilder cmUserBuilder;

	@Autowired
	private CmUserService cmUserService;

	@Autowired
	private EmailSendingMockInterceptor emailSendingMockInterceptor;

	@Autowired
	private ScenarioContextValueHolder<Runnable> emailSendingCommandHolder;

	private static final long MAX_SECONDS_TO_WAIT_FOR_EMAIL = 20;

	@Before(order = 1100)
	public void initialiseCmUserBuilderHolder() {
		cmUserBuilderHolder.set(cmUserBuilder);
	}

	@Given("^(?:the CM User's|my) role is (.+)$")
	public void setCmUserRole(final String roleName) {
		cmUserBuilderHolder.set(cmUserBuilderHolder.get().withRole(roleName));
	}

	@When("^a new CM User is created with the email address (.+)$")
	public void createNewCmUser(final String emailAddress) throws Exception {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			setCmUserEmailAddress(emailAddress);
			setCmUserUsername("newCmUser");
			final CmUser cmUserToAdd = cmUserBuilderHolder.get().build();
			cmUserService.add(cmUserToAdd);
		});
	}

	@Given("^(?:I am|there exists) a CM User with the email address (.+)$")
	public void cmUserExists(final String emailAddress) throws Throwable {
		setCmUserEmailAddress(emailAddress);
		final CmUser cmUserToAdd = cmUserBuilderHolder.get().build();

		final NotifyBuilder notifyBuilder = emailSendingMockInterceptor.createNotifyBuilderForEmailSendingMockInterceptor()
				.filter(header("to").contains(emailAddress)).whenDone(1).create();

		cmUserService.add(cmUserToAdd);

		// wait for the email to go through
		assertTrue("Timed out waiting for email to be sent", notifyBuilder.matches(MAX_SECONDS_TO_WAIT_FOR_EMAIL, TimeUnit.SECONDS));

		// Clear the messages. This Given defines the case in which the user already exists, so scenarios using it should not need to account for an
		// extra CM User Created email.
		final Mailbox messages = Mailbox.get(emailAddress);
		messages.clear();

	}

	@When("^(?:the CM User|I) changes? (?:his|her|their|my) CM password to \"(.+)\"$")
	public void cmUserChangesPassword(final String newPassword) throws Throwable {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			final String cmUserEmail = cmUserBuilder.build().getEmail();
			final CmUser cmUser = cmUserService.findByEmail(cmUserEmail);
			cmUserService.sendPasswordChangedEvent(cmUser.getGuid(), newPassword);
		});
	}

	@When("^(?:the CM User|I) requests? a CM password reset$")
	public void cmUserRequestsPasswordReset() {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			final String cmUserEmail = cmUserBuilder.build().getEmail();
			cmUserService.resetUserPassword(cmUserEmail);
		});
	}

	private void setCmUserEmailAddress(final String cmUserEmailAddress) {
		cmUserBuilderHolder.set(cmUserBuilderHolder.get().withEmail(cmUserEmailAddress));
	}

	private void setCmUserUsername(final String username) {
		cmUserBuilderHolder.set(cmUserBuilderHolder.get().withUserName(username));
	}

}
