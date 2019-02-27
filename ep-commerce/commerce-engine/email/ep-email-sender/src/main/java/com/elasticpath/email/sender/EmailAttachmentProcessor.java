/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.sender;

import java.net.MalformedURLException;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apache.camel.Exchange;
import org.apache.camel.ExpectedBodyTypeException;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.FilenameUtils;

import com.elasticpath.commons.exception.EmailSendException;
import com.elasticpath.email.EmailDto;

/**
 * Processor that allows attachments to be added to an email.
 */
public class EmailAttachmentProcessor implements Processor {

	@Override
	public void process(final Exchange exchange) throws Exception {
		final Message message = exchange.getIn();
		final Object body = message.getBody();

		if (!(body instanceof EmailDto)) {
			throw new ExpectedBodyTypeException(exchange, EmailDto.class);
		}

		final EmailDto emailDto = (EmailDto) body;

		emailDto.getAttachmentUrls()
				.forEach(attachmentUrl -> addAttachment(message, attachmentUrl));

		emailDto.getAttachments()
				.forEach(attachment -> addAttachment(message,
													 attachment.getFilename(),
													 attachment.getData(),
													 attachment.getMimeType()));
	}

	/**
	 * Attaches a file at a given URL to the email to be sent.
	 *
	 * @param message the message on the {@link Exchange}
	 * @param attachmentUrl the Url of the file to attach
	 */
	protected void addAttachment(final Message message, final String attachmentUrl) {
		try {
			final URL url = new URL(attachmentUrl);

			final String fileName = FilenameUtils.getName(url.getPath());

			message.addAttachment(fileName, new DataHandler(new URLDataSource(url)));
		} catch (final MalformedURLException e) {
			throw new EmailSendException("Unable to process attachment [" + attachmentUrl + "]", e);
		}
	}

	/**
	 * Attaches a file to the email to be sent.
	 *
	 * @param message the message on the {@link Exchange}
	 * @param attachmentFilename the name of the file to attach
	 * @param attachmentData the name of the file to attach
	 * @param mimeType the MIME type of the attachment
	 */
	protected void addAttachment(final Message message, final String attachmentFilename, final byte[] attachmentData, final String mimeType) {
		message.addAttachment(attachmentFilename, new DataHandler(new ByteArrayDataSource(attachmentData, mimeType)));
	}

}
