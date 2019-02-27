/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.util.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Test;

import com.elasticpath.email.util.EmailContentUtil;

/**
 * Test class for {@link EmailContentUtil}.
 */
public class EmailContentUtilTest {

	private static final String TEXT_HTML = "text/html";
	private static final String TEXT_PLAIN = "text/plain";

	private static final String HTML_MESSAGE = "<html><body>This is an HTML message</body></html>";
	private static final String TEXT_MESSAGE = "This is a plaintext message.";

	private static final String UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_HTML = "Unexpected email contents for content type \"text/html\"";
	private static final String UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_PLAIN = "Unexpected email contents for content type \"text/plain\"";

	/** These must resolve to actual sample attachment filenames, probably located in src/test/resource. */
	private static final String ATTACHMENT1_FILENAME = "attachment1.txt";
	private static final String ATTACHMENT2_FILENAME = "attachment2.txt";

	@Test
	public void verifyNullReturnedWhenNoSuchContentTypeForBodyPart() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(TEXT_MESSAGE, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final BodyPart bodyPart = EmailContentUtil.findBodyPartByContentType(mimeMultipart, "no/such");
		assertNull("Expected null returned for unknown content type", bodyPart);
	}

	@Test
	public void verifyNullReturnedWhenNoSuchContentTypeForContent() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(TEXT_MESSAGE, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final String contents = EmailContentUtil.findBodyPartContentsByContentType(mimeMultipart, "no/such");
		assertNull("Expected null returned for unknown content type", contents);
	}

	@Test
	public void verifyUtilFindsPlainTextBodyInMultipartMessage() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(TEXT_MESSAGE, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final BodyPart bodyPart = EmailContentUtil.findBodyPartByContentType(mimeMultipart, TEXT_PLAIN);
		assertEquals(UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_PLAIN, TEXT_MESSAGE, bodyPart.getContent());
	}

	@Test
	public void verifyUtilFindsPlainTextContentInMultipartMessage() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(TEXT_MESSAGE, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final String contents = EmailContentUtil.findBodyPartContentsByContentType(mimeMultipart, TEXT_PLAIN);
		assertEquals(UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_PLAIN, TEXT_MESSAGE, contents);
	}

	@Test
	public void verifyUtilFindsHtmlTextBodyInMultipartMessage() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(TEXT_MESSAGE, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final BodyPart bodyPart = EmailContentUtil.findBodyPartByContentType(mimeMultipart, TEXT_HTML);
		assertEquals(UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_HTML, HTML_MESSAGE, bodyPart.getContent());
	}

	@Test
	public void verifyUtilFindsHtmlTextContentInMultipartMessage() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(TEXT_MESSAGE, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final String contents = EmailContentUtil.findBodyPartContentsByContentType(mimeMultipart, TEXT_HTML);
		assertEquals(UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_HTML, HTML_MESSAGE, contents);
	}

	@Test
	public void verifyUtilFindsHtmlTextBodyInSingleMessageTypeMessage() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(null, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final BodyPart bodyPart = EmailContentUtil.findBodyPartByContentType(mimeMultipart, TEXT_HTML);
		assertEquals(UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_HTML, HTML_MESSAGE, bodyPart.getContent());
	}

	@Test
	public void verifyUtilFindsHtmlTextContentInSingleMessageTypeMessage() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(null, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final String contents = EmailContentUtil.findBodyPartContentsByContentType(mimeMultipart, TEXT_HTML);
		assertEquals(UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_HTML, HTML_MESSAGE, contents);
	}

	@Test
	public void verifyUtilFindsAttachment() throws Exception {
		final HtmlEmail email = createHtmlEmail(null, HTML_MESSAGE);
		email.attach(createEmailAttachmentWithFilename(ATTACHMENT1_FILENAME));
		email.attach(createEmailAttachmentWithFilename(ATTACHMENT2_FILENAME));

		final MimeMessage mimeMessage = getMimeMessage(email);
		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final Collection<BodyPart> attachmentBodyParts = EmailContentUtil.findBodyPartsByDisposition(mimeMultipart, Part.ATTACHMENT);

		assertThat(attachmentBodyParts)
				.extracting(this::getBodyPartFileName)
				.contains(ATTACHMENT1_FILENAME, ATTACHMENT2_FILENAME);
	}

	@Test
	public void verifyEmptySetReturnedWhenNoAttachment() throws Exception {
		final HtmlEmail email = createHtmlEmail(null, HTML_MESSAGE);
		final MimeMessage mimeMessage = getMimeMessage(email);
		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final Collection<BodyPart> attachmentBodyParts = EmailContentUtil.findBodyPartsByDisposition(mimeMultipart, Part.ATTACHMENT);

		assertThat(attachmentBodyParts).isEmpty();
	}

	private HtmlEmail createHtmlEmail(final String textMessage, final String htmlMessage) throws EmailException, AddressException {
		final HtmlEmail email = new HtmlEmail();
		if (textMessage != null) {
			email.setTextMsg(textMessage);
		}

		if (htmlMessage != null) {
			email.setHtmlMsg(htmlMessage);
		}

		email.setFrom("from@from.com");
		email.setTo(Collections.singletonList(new InternetAddress("to@to.foo")));
		email.setHostName("localhost");
		return email;
	}

	private MimeMessage createMimeMessage(final String textMessage, final String htmlMessage) throws EmailException, MessagingException {
		return getMimeMessage(createHtmlEmail(textMessage, htmlMessage));
	}

	private MimeMessage getMimeMessage(final Email email) throws EmailException, MessagingException {
		email.buildMimeMessage();

		final MimeMessage mimeMessage = email.getMimeMessage();
		mimeMessage.saveChanges();

		return mimeMessage;
	}

	private MimeMultipart getMimeMultipart(final MimeMessage mimeMessage) throws IOException, MessagingException {
		final Object emailContent = mimeMessage.getContent();
		assertTrue("Email content should be of type MimeMultipart for an email with both HTML and plain text content",
				emailContent instanceof MimeMultipart);

		return (MimeMultipart) emailContent;
	}

	private EmailAttachment createEmailAttachmentWithFilename(final String attachmentFilename) {
		final EmailAttachment attachment = new EmailAttachment();

		attachment.setDisposition(Part.ATTACHMENT);
		attachment.setName(attachmentFilename);

		final URL resource = getClass().getClassLoader().getResource(attachmentFilename);

		assertThat(resource)
				.as("Error: test attachment file not found on classpath.")
				.isNotNull();

		final String absoluteFilename = resource.getFile();

		attachment.setPath(absoluteFilename);

		return attachment;
	}

	private String getBodyPartFileName(final Part part) {
		try {
			return part.getFileName();
		} catch (final MessagingException e) {
			return null;
		}
	}

}