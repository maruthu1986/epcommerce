/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email.camel.typeconverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.camel.Converter;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import com.elasticpath.email.EmailDto;
import com.elasticpath.email.util.EmailContentUtil;

/**
 * Allows Apache Camel to convert between {@link Email} and {@link EmailDto} instances.
 */
@Converter
public class EmailDtoTypeConverter {

	/**
	 * Convert an {@link Email} to an {@link EmailDto} instance.
	 * 
	 * @param email the email to convert
	 * @return an {@link EmailDto} instance corresponding to the input {@code email}
	 * @throws EmailException in case of errors while reading the input {@code email}, for example if it is not correctly populated
	 * @throws IOException in case of errors while reading the input {@code email} contents
	 * @throws MessagingException in case of inconsistent {@code email} contents
	 */
	@Converter
	public EmailDto convertToEmailDto(final Email email) throws EmailException, IOException, MessagingException {
		final EmailDto.Builder<?> builder = createEmailDtoBuilder()
				.withFrom(email.getFromAddress().toString().replaceAll("\"", ""))
				.withSubject(email.getSubject())
				.withTo(convertToStrings(email.getToAddresses()))
				.withCc(convertToStrings(email.getCcAddresses()))
				.withBcc(convertToStrings(email.getBccAddresses()))
				.withReplyTo(convertToStrings(email.getReplyToAddresses()));

		final MimeMessage message = getMimeMessage(email);

		final Object content = message.getContent();

		if (content.getClass().isAssignableFrom(MimeMultipart.class)) {
			final BodyPart textBodyPart = EmailContentUtil.findBodyPartByContentType((MimeMultipart) content, "text/plain");
			if (textBodyPart != null) {
				builder.withTextBody(textBodyPart.getContent().toString());
				builder.withContentType(textBodyPart.getContentType());
			}

			final BodyPart htmlBodyPart = EmailContentUtil.findBodyPartByContentType((MimeMultipart) content, "text/html");
			if (htmlBodyPart != null) {
				builder.withHtmlBody(htmlBodyPart.getContent().toString());
				builder.withContentType(htmlBodyPart.getContentType());
			}
		} else {
			final String plainTextContent = message.getContent().toString();
			builder.withTextBody(plainTextContent);
			builder.withContentType(message.getContentType());
		}

		return builder.build();
	}

	/**
	 * Retrieve a {@link MimeMessage} from the given {@link Email}.
	 * 
	 * @param email the email
	 * @return a {@link MimeMessage} created from the given {@link Email}
	 * @throws EmailException in case of error retrieving the {@link MimeMessage}
	 * @throws MessagingException in case of error retrieving the {@link MimeMessage}
	 */
	protected MimeMessage getMimeMessage(final Email email) throws EmailException, MessagingException {
		email.setHostName("localhost"); // not used when sending emails; a value is required for the message content to be generated.
		email.buildMimeMessage();

		final MimeMessage mimeMessage = email.getMimeMessage();
		mimeMessage.saveChanges();
		return mimeMessage;
	}

	private List<String> convertToStrings(final List<InternetAddress> addresses) {
		final List<String> addressStrings = new ArrayList<>(addresses.size());

		for (final InternetAddress address : addresses) {
			addressStrings.add(address.toString());
		}

		return addressStrings;
	}

	/**
	 * Factory method to create a new {@link EmailDto} instance.
	 * 
	 * @return a new {@link EmailDto} instance
	 */
	protected EmailDto.Builder<?> createEmailDtoBuilder() {
		return EmailDto.builder();
	}

}
