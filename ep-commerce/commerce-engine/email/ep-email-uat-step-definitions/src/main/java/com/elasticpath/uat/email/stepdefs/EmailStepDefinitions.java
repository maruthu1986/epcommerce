/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.uat.email.stepdefs;

import static org.apache.camel.builder.Builder.header;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.velocity.tools.generic.DateTool;
import org.jvnet.mock_javamail.Mailbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.cucumber.testexecutionlisteners.CucumberDatabaseTestExecutionListener;
import com.elasticpath.cucumber.testexecutionlisteners.CucumberJmsRegistrationTestExecutionListener;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.test.support.EmailEnabler;
import com.elasticpath.email.test.support.EmailSendingMockInterceptor;
import com.elasticpath.email.util.EmailContentUtil;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for email functionality.
 */
@TestExecutionListeners(listeners = {
		CucumberJmsRegistrationTestExecutionListener.class,
		CucumberDatabaseTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class
})
public class EmailStepDefinitions {

	private static final String DEFAULT_DATE_FORMAT_STRING = "MMMM d, yyyy";

	private static final long MAX_SECONDS_TO_WAIT_FOR_EMAIL = 20;

	@Autowired
	private EmailEnabler emailEnabler;

	@Autowired
	private ScenarioContextValueHolder<Map<String, Message>> emailMessagesHolder;

	@Autowired
	private EmailSendingMockInterceptor emailSendingMockInterceptor;

	@Autowired
	@Qualifier("emailSendingCommandHolder")
	private ScenarioContextValueHolder<Runnable> emailSendingCommandHolder;

	@Autowired
	@Qualifier("emailDtoHolder")
	private ScenarioContextValueHolder<EmailDto> emailDtoHolder;

	@After
	public void tearDown() {
		Mailbox.clearAll();
	}

	@Given("^email sending is enabled")
	public void enableEmail() {
		emailEnabler.setEmailEnabledSettingDefaultValue(true);
	}

	@Given("^email sending is disabled")
	public void disableEmailSettings() {
		emailEnabler.setEmailEnabledSettingDefaultValue(false);
	}

	@Given("^email sending is updated to be enabled")
	public void disableEmailValue() throws Throwable {
		emailEnabler.setEmailEnabledSettingsValue(true);
	}

	@Then("^(?:.+) should (?:receive|exist) (\\d+) email(?:\\(s\\))? in (?:my|their|the) (.+) inbox$")
	public void verifyEmailsReceived(final int expectedNumberOfEmails, final String recipientEmailAddress) throws Exception {
		final NotifyBuilder notifyBuilder = emailSendingMockInterceptor.createNotifyBuilderForEmailSendingMockInterceptor()
				.filter(header("to").contains(recipientEmailAddress))
				.whenDone(expectedNumberOfEmails)
				.create();

		// Execute the code that will result in an email being sent. To avoid a race condition we can't
		// have that code run before the notify builder is created.
		emailSendingCommandHolder.get().run();

		assertTrue("Timed out waiting for email to be sent",
				   notifyBuilder.matches(MAX_SECONDS_TO_WAIT_FOR_EMAIL, TimeUnit.SECONDS));

		final Mailbox messages = Mailbox.get(recipientEmailAddress);
		assertEquals("Mailbox contains an unexpected number of email messages", expectedNumberOfEmails, messages.size());

		final Map<String, Message> emailMessageMap = new HashMap<>(messages.size());
		for (final Message message : messages) {
			emailMessageMap.put(message.getSubject(), message);

			final Object content = message.getContent();
			assertThat(content)
					.as("Email content should be a MimeMultipart (for HTML & text) or String (for text only) instance")
					.isInstanceOfAny(MimeMultipart.class, String.class);
		}

		if (emailDtoHolder.get() != null && emailMessageMap.size() == 1) {
			verifyEmailMatchesExpected(emailDtoHolder.get(), emailMessageMap.values().iterator().next());
		}

		emailMessagesHolder.set(emailMessageMap);
	}

	@Then("^the subject of(?: one of)? the emails? should be \"([^\"]*)\"$")
	public void verifyEmailSubject(final String expectedSubject) throws Exception {
		assertThat(emailMessagesHolder.get())
				.as("No such email found")
				.containsKey(expectedSubject);
	}

	@Then("^the subject of(?: one of)? the emails? should contain \"([^\"]*)\"$")
	public void verifyEmailSubjectSubstring(final String expectedSubjectSubstring) throws Exception {
		for (final Message message : emailMessagesHolder.get().values()) {
			if (message.getSubject().contains(expectedSubjectSubstring)) {

				// Since we're identifying this email by a substring of its subject, we're never going to examine its
				// full subject name in another step definition - we're going to refer to it by this substring.
				// We must clear out the map entry for the whole subject and replace it with the identifying part that we care about.
				emailMessagesHolder.get().put(message.getSubject(), null);
				emailMessagesHolder.get().put(expectedSubjectSubstring, message);

				return;
			}
		}

		fail("No such email found");
	}

	@Then("^the(?: \"(.+)\")? email should contain today's date$")
	public void verifyEmailContainsCurrentDate(final String emailSubject) throws Throwable {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));
		assertThat(emailContents)
				.as("The email contents should include today's date")
				.contains(new DateTool().get(DEFAULT_DATE_FORMAT_STRING));
	}

	@Then("^the(?: \"(.+)\")? email should contain an attachment with name (.+)$")
	public void verifyMessageContainsAttachment(final String emailSubject, final String filename) throws Throwable {
		final Message message = getEmailMessageBySubject(emailSubject, emailMessagesHolder.get());

		final Object content = message.getContent();

		assertThat(content)
				.as("The content of emails containing an attachment should be a MimeMultipart instance")
				.isInstanceOf(MimeMultipart.class);

		final Collection<BodyPart> bodyParts = EmailContentUtil.findBodyPartsByDisposition((MimeMultipart) content, Part.ATTACHMENT);

		assertThat(bodyParts)
				.extracting(this::getBodyPartFileName)
				.contains(filename);
	}

	@Then("^the(?: \"(.+)\")? email should contain an attachment with the contents of file (.+)$")
	public void verifyMessageContainsAttachmentContents(final String emailSubject, final String filename) throws Throwable {
		final URL resource = getClass().getClassLoader().getResource(filename);

		assertThat(resource)
				.as("Could not find classpath resource [" + filename + "]")
				.isNotNull();

		try (InputStream expectedContentInputStream = resource.openStream()) {

			final Message message = getEmailMessageBySubject(emailSubject, emailMessagesHolder.get());

			final Object content = message.getContent();

			assertThat(content)
					.as("The content of emails containing an attachment should be a MimeMultipart instance")
					.isInstanceOf(MimeMultipart.class);

			final Collection<BodyPart> bodyParts = EmailContentUtil.findBodyPartsByDisposition((MimeMultipart) content, Part.ATTACHMENT);

			bodyParts.stream()
					.filter(bodyPart -> Objects.equals(filename, getBodyPartFileName(bodyPart)))
					.map(this::getBodyPartInputStream)
					.forEach(attachmentContents -> assertThat(attachmentContents)
							.as("Unexpected attachment contents")
							.hasSameContentAs(expectedContentInputStream));
		}
	}

	/**
	 * <p>Convenience method to assist in retrieving a particular message by subject.</p>
	 * <p>Step Definitions for examining one of many emails are expected to be identified by email subject, in the following format:</p>
	 * <ul>
	 * <li>[Given] the <em>property</em> of the "Foo" email is ___</li>
	 * <li>[Given] the <em>property</em> of the "Bar" email is ___</li>
	 * <li>etc.</li>
	 * </ul>
	 * <p>It is also permitted to omit the "Subject" element when expecting only one email, i.e.</p>
	 * <ul>
	 * <li>"[Given] the <em>property</em> of the email is ___"</li>
	 * </ul>
	 * <p>This method will return the corresponding Message identified by subject, or the first email found if subject is omitted.</p>
	 *
	 * @param emailSubject the subject of the email to retrieve
	 * @param emailMessageMap the map of emails to inspect
	 * @return the corresponding Message
	 */
	public static Message getEmailMessageBySubject(final String emailSubject, final Map<String, Message> emailMessageMap) {
		if (emailSubject == null) {
			return Iterables.find(emailMessageMap.values(), new MessagePredicate());
		}

		return emailMessageMap.get(emailSubject);
	}

	/**
	 * Convenience method to return the contents of the given {@link javax.mail.Message} as a String.  This accommodates both simple text/plain
	 * emails as well as complex {@link MimeMultipart} messages used in text/html emails.
	 *
	 * @param message the Message
	 * @return a String containing the Message's contents
	 * @throws IOException in case of errors while reading the input MimeMultipart contents
	 * @throws MessagingException in case of inconsistent MimeMultipart contents
	 */
	public static String getContents(final Message message) throws IOException, MessagingException {
		final Object content = message.getContent();

		if (content instanceof String) {
			return (String) content;
		}

		assertTrue("Non-String Email content should be a MimeMultipart instance", content instanceof MimeMultipart);

		final String bodyPartContent = EmailContentUtil.findBodyPartContentsByContentType((MimeMultipart) content, "text/html");

		assertNotNull("No text/html body part found", bodyPartContent);

		return bodyPartContent;
	}

	private void verifyEmailMatchesExpected(final EmailDto expectedEmailMessage, final Message actualEmailMessage) throws MessagingException,
			IOException {
		assertEquals("Unexpected email subject", expectedEmailMessage.getSubject(), actualEmailMessage.getSubject());

		final Address[] actualFroms = actualEmailMessage.getFrom();
		assertEquals("Unexpected number of 'from' addresses", 1, actualFroms.length);
		assertEquals("Unexpected email from address", expectedEmailMessage.getFrom(), actualFroms[0].toString());

		verifyAddressListMatchesExpected(expectedEmailMessage.getTo(), Message.RecipientType.TO, actualEmailMessage);
		verifyAddressListMatchesExpected(expectedEmailMessage.getCc(), Message.RecipientType.CC, actualEmailMessage);
		verifyAddressListMatchesExpected(expectedEmailMessage.getBcc(), Message.RecipientType.BCC, actualEmailMessage);

		if (expectedEmailMessage.getHtmlBody() != null) {
			// Getting an HTML section from a Mail message is pretty hard.
			final Object content = actualEmailMessage.getContent();
			assertTrue("Expected email to contain a MimeMultipart message", content instanceof MimeMultipart);

			final BodyPart bodyPart = EmailContentUtil.findBodyPartByContentType((MimeMultipart) content, "text/html");

			assertEquals("Unexpected email content type", expectedEmailMessage.getContentType(), bodyPart.getContentType());
			assertEquals("Unexpected email contents", expectedEmailMessage.getHtmlBody(), bodyPart.getContent().toString());
		} else if (expectedEmailMessage.getTextBody() != null) {
			assertEquals("Unexpected email content type", expectedEmailMessage.getContentType(), actualEmailMessage.getContentType());
			assertEquals("Unexpected email contents", expectedEmailMessage.getTextBody(), actualEmailMessage.getContent());
		}
	}

	private void verifyAddressListMatchesExpected(final Collection<String> expectedAddresses, final Message.RecipientType recipientType,
												  final Message actualMessage) throws MessagingException {
		final Address[] actualAddresses = actualMessage.getRecipients(recipientType);
		if (actualAddresses == null) {
			assertTrue("No '" + recipientType + "' addresses set, but expected " + expectedAddresses.size(), expectedAddresses.isEmpty());
			return;
		}

		assertEquals("Unexpected number of '" + recipientType + "' addresses", expectedAddresses.size(), actualAddresses.length);

		for (final Address actualAddress : actualAddresses) {
			assertTrue("Unexpected '" + recipientType + "' address [" + actualAddress + "]",
					   expectedAddresses.contains(actualAddress.toString()));
		}
	}

	/**
	 * Predicate used for finding a message that has content.
	 */
	private static class MessagePredicate implements Predicate<Message> {
		@Override
		public boolean apply(final Message message) {
			boolean goodMessage;
			try {
				goodMessage = message != null && message.getContent() != null;
			} catch (IOException | MessagingException e) {
				goodMessage = false;
			}

			return goodMessage;
		}
	}

	private String getBodyPartFileName(final Part part) {
		try {
			return part.getFileName();
		} catch (final MessagingException e) {
			throw new EpSystemException(e.getMessage(), e);
		}
	}

	private InputStream getBodyPartInputStream(final Part part) {
		try {
			return part.getInputStream();
		} catch (IOException | MessagingException e) {
			throw new EpSystemException(e.getMessage(), e);
		}
	}

}
