/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email.camel.typeconverter;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.junit.Test;

import com.elasticpath.email.EmailDto;

/**
 * Test class for {@link com.elasticpath.email.camel.typeconverter.EmailDtoTypeConverter}.
 */
public class EmailDtoTypeConverterTest {

	private static final String CONTENT_TYPE_HTML = "text/html";
	private static final String CONTENT_TYPE_PLAIN = "text/plain";
	private static final String FROM_NAME = "Sender Sendingman";
	private static final String FROM_ADDRESS = "sender@elasticpath.com";
	private static final String FROM_COMBINED = FROM_NAME + " <" + FROM_ADDRESS + ">";
	private static final String SUBJECT = "Subject";
	private static final String TO_1 = "recipient-one@elasticpath.com";
	private static final String TO_2 = "recipient-two@elasticpath.com";
	private static final String CC_1 = "cc-recipient-one@elasticpath.com";
	private static final String CC_2 = "cc-recipient-two@elasticpath.com";
	private static final String BCC_1 = "bcc-recipient-one@elasticpath.com";
	private static final String BCC_2 = "bcc-recipient-two@elasticpath.com";
	private static final String REPLY_TO_1 = "senders-PA@elasticpath.com";
	private static final String REPLY_TO_2 = "senders-PAs-PA@elasticpath.com";
	private static final String MESSAGE_PLAIN_TEXT = "This is an email message.";
	private static final String MESSAGE_HTML = "<html><body>This is an email message.</body></html>";

	@Test
	public void verifyPlainEmailCanBeConvertedToEmailDto() throws Exception {
		final SimpleEmail inputEmail = new SimpleEmail();

		setCommonFields(inputEmail);
		inputEmail.setMsg(MESSAGE_PLAIN_TEXT);

		final EmailDto convertedDto = new EmailDtoTypeConverter().convertToEmailDto(inputEmail);

		verifyConversionOfCommonFields(convertedDto);
		assertThat("Content type field incorrectly converted", convertedDto.getContentType(), startsWith(CONTENT_TYPE_PLAIN));
	}

	@Test
	public void verifyHtmlEmailCanBeConvertedToEmailDto() throws Exception {
		final HtmlEmail inputEmail = new HtmlEmail();

		setCommonFields(inputEmail);
		inputEmail.setTextMsg(MESSAGE_PLAIN_TEXT);
		inputEmail.setHtmlMsg(MESSAGE_HTML);

		final EmailDto convertedDto = new EmailDtoTypeConverter().convertToEmailDto(inputEmail);

		verifyConversionOfCommonFields(convertedDto);
		assertThat("Content type field incorrectly converted", convertedDto.getContentType(), startsWith(CONTENT_TYPE_HTML));
		assertEquals("HTML message body field incorrectly converted", MESSAGE_HTML, convertedDto.getHtmlBody());
	}

	private void setCommonFields(final Email inputEmail) throws EmailException {
		inputEmail.setFrom(FROM_ADDRESS, FROM_NAME);
		inputEmail.setSubject(SUBJECT);
		inputEmail.addTo(TO_1);
		inputEmail.addTo(TO_2);
		inputEmail.addCc(CC_1);
		inputEmail.addCc(CC_2);
		inputEmail.addBcc(BCC_1);
		inputEmail.addBcc(BCC_2);
		inputEmail.addReplyTo(REPLY_TO_1);
		inputEmail.addReplyTo(REPLY_TO_2);
	}

	private void verifyConversionOfCommonFields(final EmailDto convertedDto) {
		assertEquals("From field incorrectly converted", FROM_COMBINED, convertedDto.getFrom());
		assertEquals("Subject field incorrectly converted", SUBJECT, convertedDto.getSubject());
		assertEquals("Recipient list field incorrectly converted", Arrays.asList(TO_1, TO_2), convertedDto.getTo());
		assertEquals("CC list field incorrectly converted", Arrays.asList(CC_1, CC_2), convertedDto.getCc());
		assertEquals("BCC list field incorrectly converted", Arrays.asList(BCC_1, BCC_2), convertedDto.getBcc());
		assertEquals("Reply-to list field incorrectly converted", Arrays.asList(REPLY_TO_1, REPLY_TO_2), convertedDto.getReplyTo());
		assertEquals("Plain text message body field incorrectly converted", MESSAGE_PLAIN_TEXT, convertedDto.getTextBody());
	}

}