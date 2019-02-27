/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email;

import static com.elasticpath.email.EmailDtoTestData.ATTACHMENT_DATA_1;
import static com.elasticpath.email.EmailDtoTestData.ATTACHMENT_DATA_2;
import static com.elasticpath.email.EmailDtoTestData.ATTACHMENT_URI_1;
import static com.elasticpath.email.EmailDtoTestData.ATTACHMENT_URI_2;
import static com.elasticpath.email.EmailDtoTestData.BCC_1;
import static com.elasticpath.email.EmailDtoTestData.BCC_2;
import static com.elasticpath.email.EmailDtoTestData.CC_1;
import static com.elasticpath.email.EmailDtoTestData.CC_2;
import static com.elasticpath.email.EmailDtoTestData.FROM_COMBINED;
import static com.elasticpath.email.EmailDtoTestData.HTML_CONTENT_TYPE;
import static com.elasticpath.email.EmailDtoTestData.MESSAGE_HTML;
import static com.elasticpath.email.EmailDtoTestData.MESSAGE_PLAIN_TEXT;
import static com.elasticpath.email.EmailDtoTestData.REPLY_TO_1;
import static com.elasticpath.email.EmailDtoTestData.REPLY_TO_2;
import static com.elasticpath.email.EmailDtoTestData.SUBJECT;
import static com.elasticpath.email.EmailDtoTestData.TO_1;
import static com.elasticpath.email.EmailDtoTestData.TO_2;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import org.junit.Rule;
import org.junit.Test;

import org.assertj.core.api.JUnitSoftAssertions;

/**
 * Test class that verifies {@link EmailDto} instances can be constructed from its builder.
 */
public class EmailDtoTest {

	@Rule
	public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

	private final Consumer<EmailDto> fieldsPopulatedAssertions = emailDto -> {
		softly.assertThat(emailDto.getContentType()).isEqualTo(HTML_CONTENT_TYPE);
		softly.assertThat(emailDto.getFrom()).isEqualTo(FROM_COMBINED);
		softly.assertThat(emailDto.getSubject()).isEqualTo(SUBJECT);
		softly.assertThat(emailDto.getTo()).contains(TO_1, TO_2);
		softly.assertThat(emailDto.getCc()).contains(CC_1, CC_2);
		softly.assertThat(emailDto.getBcc()).contains(BCC_1, BCC_2);
		softly.assertThat(emailDto.getReplyTo()).contains(REPLY_TO_1, REPLY_TO_2);
		softly.assertThat(emailDto.getTextBody()).isEqualTo(MESSAGE_PLAIN_TEXT);
		softly.assertThat(emailDto.getHtmlBody()).isEqualTo(MESSAGE_HTML);
	};

	@Test
	public void verifyBuilderBuildsHtmlEmailWithAllFields() throws Exception {
		final EmailDto emailDto = EmailDtoTestData.htmlEmailDtoBuilder().build();

		assertThat(emailDto).satisfies(fieldsPopulatedAssertions);
	}

	@Test
	public void verifyBuilderBuildsHtmlEmailFromPrototype() throws Exception {
		final EmailDto emailDtoPrototype = EmailDtoTestData.htmlEmailDtoBuilder().build();
		final EmailDto emailDto = EmailDto.builder().fromPrototype(emailDtoPrototype).build();

		assertThat(emailDto).satisfies(fieldsPopulatedAssertions);
	}

	@Test
	public void verifyBuilderAddsAttachments() throws Exception {
		final EmailDto.Builder<?> builder = EmailDto.builder();
		final EmailDto emailDto = EmailDtoTestData.withAttachments(builder)
				.build();

		softly.assertThat(emailDto.getAttachmentUrls()).contains(ATTACHMENT_URI_1, ATTACHMENT_URI_2);
		softly.assertThat(emailDto.getAttachments()).contains(ATTACHMENT_DATA_1, ATTACHMENT_DATA_2);
	}

}