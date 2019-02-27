/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.uat.email.sender.stepdefs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.net.URL;
import javax.activation.MimetypesFileTypeMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.spi.BrowsableEndpoint;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jvnet.mock_javamail.Mailbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.email.EmailDto;
import com.elasticpath.email.test.support.EmailSendingMockInterceptor;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Steps definition for publishing emails.
 */
public class EmailPublishingStepDefinitions {

	private static final String FROM = "Sender Sendingman <sender@elasticpath.com>";

	@Autowired
	@Qualifier("ep-email-sender")
	private ModelCamelContext camelContext;

	@Autowired
	private EmailSendingMockInterceptor emailSendingMockInterceptor;

	@Autowired
	@Qualifier("emailSendingCommandHolder")
	private ScenarioContextValueHolder<Runnable> emailSendingCommandHolder;

	@Autowired
	@Qualifier("emailDtoHolder")
	private ScenarioContextValueHolder<EmailDto> emailDtoHolder;

	private MockEndpoint mockSmtpEndpoint;

	private EmailDto.Builder<?> emailDtoBuilder;
	private String emailMessageJson;

	@Before
	public void setUp() throws Exception {
		mockSmtpEndpoint = emailSendingMockInterceptor.wireTapEmailSending();
	}

	@After
	public void tearDown() {
		Mailbox.clearAll();
	}

	@Given("^an email message to be delivered$")
	public void createEmailMessage() throws Throwable {
		emailDtoBuilder = EmailDto.builder()
				// These properties are substituted by Camel if omitted, so we define sensible defaults in order to avoid being surprised.
				.withContentType("text/plain")
				.withFrom(FROM);
	}

	@Given("^the email contains an attachment of file (.+)$")
	public void addAttachmentFile(final String url) throws Throwable {
		final URL resource = getClass().getClassLoader().getResource(url);

		assertThat(resource)
				.as("Could not find classpath resource [" + url + "]")
				.isNotNull();

		emailDtoBuilder = emailDtoBuilder.addAttachmentUrl(resource.toString());
	}

	@Given("^the email contains an attachment of URL (.+)$")
	public void addAttachmentUrl(final String url) throws Throwable {
		final URL resource = new URL(url);

		emailDtoBuilder = emailDtoBuilder.addAttachmentUrl(resource.toString());
	}

	@Given("^the email contains an attachment with the contents of file (.+)$")
	public void addAttachmentContents(final String filename) throws Throwable {
		final URL resource = getClass().getClassLoader().getResource(filename);

		assertThat(resource)
				.as("Could not find classpath resource [" + filename + "]")
				.isNotNull();

		try (InputStream inputStream = resource.openStream()) {
			final byte[] resourceContents = IOUtils.toByteArray(inputStream);
			final String fileName = FilenameUtils.getName(resource.getPath());

			emailDtoBuilder = emailDtoBuilder.addAttachment(fileName, resourceContents, new MimetypesFileTypeMap().getContentType(filename));
		}
	}

	@When("^a message representing an email with recipient (.+) is published to the (.+) queue$")
	public void publishSampleEmailMessage(final String recipientEmailAddress, final String queueName) throws Throwable {
		emailDtoBuilder = EmailDto.builder()
				.withFrom(FROM)
				.withSubject("Re: subject")
				.withContentType("text/plain")
				.withTextBody("Message contents");

		publishEmailMessage(recipientEmailAddress, queueName);
	}

	@When("^the email with recipient (.+) is published to the (.+) queue$")
	public void publishEmailMessage(final String recipientEmailAddress, final String queueName) throws Throwable {
		emailDtoBuilder = emailDtoBuilder.withTo(recipientEmailAddress);

		final EmailDto emailDto = emailDtoBuilder.build();
		emailDtoHolder.set(emailDto);
		emailMessageJson = createJsonFrom(emailDto);

		emailSendingCommandHolder.set(() -> camelContext.createProducerTemplate().sendBody(jms(queueName), emailMessageJson));
	}

	private String createJsonFrom(final EmailDto emailMessage) {
		try {
			return new ObjectMapper().writeValueAsString(emailMessage);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Unable to marshal email message to JSON.", e);
		}
	}

	@Then("^no email is delivered$")
	public void verifyNoEmailDelivered() throws Throwable {
		mockSmtpEndpoint.expectedMessageCount(0);
		mockSmtpEndpoint.assertIsSatisfied();
	}

	@Then("^the message is delivered to the (.+) queue$")
	public void verifyMessageDeliveredToQueue(final String queueName) throws Throwable {
		final BrowsableEndpoint endpoint = camelContext.getEndpoint(jms(queueName), BrowsableEndpoint.class);

		final Exchange exchange = endpoint.createPollingConsumer().receive(2000);
		assertNotNull("Message not sent to the " + queueName + " queue.", exchange);
		assertEquals("Unexpected message contents on the " + queueName + " queue", emailMessageJson, exchange.getIn().getBody());
	}

	@When("^a message that does not represent a valid email message is published to the (.+) queue$")
	public void publishMalformedEmailMessage(final String queueName) throws Throwable {
		emailMessageJson = "{\"foo\":\"bar\"}";

		camelContext.createProducerTemplate().sendBody(jms(queueName), emailMessageJson);
	}

	private String jms(final String queueName) {
		return "jms:" + queueName;
	}

}
