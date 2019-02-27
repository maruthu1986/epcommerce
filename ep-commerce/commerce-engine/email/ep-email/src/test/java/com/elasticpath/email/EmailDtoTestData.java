/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email;

/**
 * Generates Email Test data for unit testing purposes.
 */
public final class EmailDtoTestData {

	/**
	 * A sample Character set.
	 */
	public static final String CHARSET = "UTF-8";

	/**
	 * Content Type for HTML emails.
	 */
	public static final String HTML_CONTENT_TYPE = "text/html; charset=" + CHARSET;

	/**
	 * Content Type for plain-text emails.
	 */
	public static final String PLAIN_TEXT_CONTENT_TYPE = "text/plain; charset=" + CHARSET;

	/**
	 * A sample name of the email sender.
	 */
	public static final String FROM_NAME = "Sender Sendingman";

	/**
	 * A sample email address of the email sender.
	 */
	public static final String FROM_ADDRESS = "sender@elasticpath.com";

	/**
	 * The full identity of the email sender.
	 */
	public static final String FROM_COMBINED = FROM_NAME + " <" + FROM_ADDRESS + ">";

	/**
	 * A sample email subject.
	 */
	public static final String SUBJECT = "Re: Subject";

	/**
	 * A sample recipient email address.
	 */
	public static final String TO_1 = "recipient-one@elasticpath.com";

	/**
	 * A sample recipient email address.
	 */
	public static final String TO_2 = "recipient-two@elasticpath.com";

	/**
	 * A sample recipient email address.
	 */
	public static final String CC_1 = "cc-recipient-one@elasticpath.com";

	/**
	 * A sample recipient email address.
	 */
	public static final String CC_2 = "cc-recipient-two@elasticpath.com";

	/**
	 * A sample recipient email address.
	 */
	public static final String BCC_1 = "bcc-recipient-one@elasticpath.com";

	/**
	 * A sample recipient email address.
	 */
	public static final String BCC_2 = "bcc-recipient-two@elasticpath.com";

	/**
	 * A sample recipient reply-to email address.
	 */
	public static final String REPLY_TO_1 = "senders-PA@elasticpath.com";

	/**
	 * A sample recipient reply-to email address.
	 */
	public static final String REPLY_TO_2 = "senders-PAs-PA@elasticpath.com";

	/**
	 * A sample plain-text email body.
	 */
	public static final String MESSAGE_PLAIN_TEXT = "This is an email message.";

	/**
	 * A sample HTML email body.
	 */
	public static final String MESSAGE_HTML = "<html><body>This is an email message.</body></html>";

	/**
	 * The URI of a sample attachment.
	 */
	public static final String ATTACHMENT_URI_1 = "classpath:/foo.txt";

	/**
	 * The URI of a sample attachment.
	 */
	public static final String ATTACHMENT_URI_2 = "classpath:/bar.txt";

	/**
	 * A sample attachment.
	 */
	public static final EmailAttachmentDto ATTACHMENT_DATA_1 = new EmailAttachmentDto("baz.bin", "Baz".getBytes(), "text/plain");

	/**
	 * A sample attachment.
	 */
	public static final EmailAttachmentDto ATTACHMENT_DATA_2 = new EmailAttachmentDto("qux.dat", "Quz".getBytes(), "text/plain");

	/**
	 * Private constructor.
	 */
	private EmailDtoTestData() {
		// cannot be instantiated
	}

	/**
	 * Returns an EmailDto Builder configured with a standard set of values for a text/plain email.
	 *
	 * @return an EmailDto Builder configured with a standard set of values
	 */
	public static EmailDto.Builder<?> plainTextEmailDtoBuilder() {
		return EmailDto.builder()
				.withContentType(PLAIN_TEXT_CONTENT_TYPE)
				.withFrom(FROM_COMBINED)
				.withSubject(SUBJECT)
				.withTo(TO_1, TO_2)
				.withCc(CC_1, CC_2)
				.withBcc(BCC_1, BCC_2)
				.withReplyTo(REPLY_TO_1, REPLY_TO_2)
				.withTextBody(MESSAGE_PLAIN_TEXT);
	}

	/**
	 * Returns an EmailDto Builder configured with a standard set of values for a text/html email.
	 *
	 * @return an EmailDto Builder configured with a standard set of values
	 */
	public static EmailDto.Builder<?> htmlEmailDtoBuilder() {
		return plainTextEmailDtoBuilder()
				.withContentType(HTML_CONTENT_TYPE)
				.withHtmlBody(MESSAGE_HTML);
	}

	/**
	 * Returns an EmailDto Builder with sample attachments.
	 *
	 * @param fromBuilder the builder onto which to add the sample attachments
	 * @return an EmailDto Builder configured with sample attachments
	 */
	public static EmailDto.Builder<?> withAttachments(final EmailDto.Builder<?> fromBuilder) {
		return fromBuilder
				.withAttachmentUrls(ATTACHMENT_URI_1, ATTACHMENT_URI_2)
				.withAttachments(ATTACHMENT_DATA_1, ATTACHMENT_DATA_2);
	}

}