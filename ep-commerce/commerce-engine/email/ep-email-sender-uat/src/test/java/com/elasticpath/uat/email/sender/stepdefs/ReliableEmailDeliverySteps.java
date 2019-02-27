/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.email.sender.stepdefs;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.apache.camel.Endpoint;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.processor.RedeliveryPolicy;
import org.jvnet.mock_javamail.Mailbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Steps for the reliable email delivery feature.
 */
public class ReliableEmailDeliverySteps {

	@Autowired
	@Qualifier("ep-email-sender")
	private ModelCamelContext camelContext;

	@Autowired
	@Qualifier("epEmailSenderEmailSendingEndpoint")
	private Endpoint emailSendingEndpoint;

	@Autowired
	@Qualifier("emailSendingRedeliveryPolicy")
	private RedeliveryPolicy redeliveryPolicy;

	@Autowired
	@Qualifier("emailSendingCommandHolder")
	private ScenarioContextValueHolder<Runnable> emailSendingCommandHolder;

	private Mailbox mailbox;
	private int maxRetryAttempts;
	private long retryIntervalSeconds;

	@Given("^the SMTP server is available$")
	public void verifySmtpServerAccessible() throws Throwable {
		// email sending is enabled by default
	}

	@Given("^the email sending service is configured to retry delivery every (\\d+) seconds up to a maximum of (\\d+) attempts$")
	public void configureEmailSendingRetryValues(final long retryIntervalSeconds, final int maxRetryAttempts) throws Throwable {
		this.maxRetryAttempts = maxRetryAttempts;
		this.retryIntervalSeconds = retryIntervalSeconds;

		redeliveryPolicy.setMaximumRedeliveryDelay(retryIntervalSeconds * 1000); // 1000 milliseconds per second
		redeliveryPolicy.setMaximumRedeliveries(maxRetryAttempts);

		camelContext.stopRoute("ep-email-sender");
		camelContext.startRoute("ep-email-sender");
	}

	@Given("^the SMTP server is unable to deliver messages to (.+)")
	public void makeSmtpServerUnableToSendTo(final String recipientEmailAddress) throws Throwable {
		mailbox = Mailbox.get(recipientEmailAddress);
		mailbox.setError(true);
	}

	@Then("^when the SMTP server becomes available again within the retry window$")
	public void reenableSmtpServer() throws Throwable {
		mailbox.setError(false);
	}

	@And("^all retries are exhausted$")
	public void waitForRetriesToExpire() throws Throwable {
		final MockEndpoint assertionMock = camelContext.getEndpoint("mock:assert", MockEndpoint.class);

		 // header populated by Camel and incremented on each retry
		assertionMock.expectedHeaderReceived("org.apache.camel.redeliveryCount", maxRetryAttempts);
		assertionMock.expectedMinimumMessageCount(1);

		final NotifyBuilder failureNotify = new NotifyBuilder(camelContext)
				.from(emailSendingEndpoint.getEndpointUri())
				.whenDoneSatisfied(assertionMock)
				.create();

		emailSendingCommandHolder.get().run();

		// The very first attempt (prior to error) does not count within the max retry count.
		//
		// Thus, a configuration of 2 retries looks like
		// 		original attempt
		//		+        retry 1
		// 		+        retry 2
		//		________________
		// 	  = 3 attempts total
		//
		// So why do we add the additional 2 seconds at the end?  Well, because
		// without it, the conditions are not met.  :(  Camel's NotifyBuilder is notoriously
		// flaky and inconsistent.  In this case the extra seconds make it happy, and if it's
		// not happy, we're not happy.
		final long maxRetryDuration = (maxRetryAttempts + 1) * retryIntervalSeconds + 2;

		assertTrue("Routing conditions not met within " + maxRetryDuration + " seconds",
				   failureNotify.matches(maxRetryDuration, TimeUnit.SECONDS));
	}

}