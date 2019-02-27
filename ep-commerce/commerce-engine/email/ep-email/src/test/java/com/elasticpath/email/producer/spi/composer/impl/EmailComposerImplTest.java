/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.EmailDtoTestData;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.domain.impl.EmailPropertiesImpl;
import com.elasticpath.email.producer.spi.composer.TemplateRenderer;
import com.elasticpath.email.producer.spi.composer.util.EmailCompositionConfiguration;
import com.elasticpath.email.producer.spi.composer.util.EmailCompositionConfigurationFactory;
import com.elasticpath.email.producer.spi.composer.util.EmailContextFactory;
import com.elasticpath.email.producer.spi.composer.util.TemplatePathResolver;
import com.elasticpath.email.producer.spi.composer.util.impl.EmailCompositionConfigurationImpl;

/**
 * Test class for {@link EmailComposerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailComposerImplTest {

	private static final String HTML_TEMPLATE_FILE = "email/foo.html.vm";
	private static final String TEXT_TEMPLATE_FILE = "email/foo.txt.vm";
	private static final String HTML_CONTENTS = "<html><body>HTML Contents</body></html>";
	private static final String TEXT_CONTENTS = "Plain text contents";
	private static final String HTML_CONTENT_TYPE = "text/html";
	private static final String TEXT_CONTENT_TYPE = "text/plain";

	@Mock
	private EmailCompositionConfigurationFactory configurationFactory;

	@Mock
	private EmailContextFactory emailContextFactory;

	@Mock
	private TemplateRenderer templateRenderer;

	private Store store;

	@InjectMocks
	private EmailComposerImpl emailComposer;

	@Before
	public void setUp() {
		store = new StoreImpl();
		store.setContentEncoding(EmailDtoTestData.CHARSET);
	}

	@Test
	public void verifyEmailConstructedWithOnlyHtmlContents() throws Exception {
		final EmailProperties emailProperties = new EmailPropertiesImpl();

		final boolean canSendHtmlEmails = true;

		givenEmailCompositionConfigForEmailProperties(emailProperties, null, null, HTML_TEMPLATE_FILE, null, canSendHtmlEmails, null);
		givenTemplateRendersToString(null, null, HTML_TEMPLATE_FILE, emailProperties, HTML_CONTENTS);

		final EmailDto emailDto = emailComposer.composeMessage(emailProperties);

		assertThat(emailDto.getHtmlBody())
				.isEqualTo(HTML_CONTENTS);

		assertThat(emailDto.getContentType())
				.isEqualTo(HTML_CONTENT_TYPE);
	}

	@Test
	public void verifyEmailConstructedWithHtmlContentsThemedAndEncodedByStore() throws Exception {
		final String storeCode = "MYSTORE";
		store.setCode(storeCode);

		final EmailProperties emailProperties = new EmailPropertiesImpl();
		emailProperties.setStoreCode(storeCode);

		final boolean canSendHtmlEmails = true;

		givenEmailCompositionConfigForEmailProperties(emailProperties, null, null, HTML_TEMPLATE_FILE, null, canSendHtmlEmails, store);
		givenTemplateRendersToString(storeCode, store, HTML_TEMPLATE_FILE, emailProperties, HTML_CONTENTS);

		final EmailDto emailDto = emailComposer.composeMessage(emailProperties);

		assertThat(emailDto.getHtmlBody())
				.isEqualTo(HTML_CONTENTS);

		assertThat(emailDto.getContentType())
				.isEqualTo(EmailDtoTestData.HTML_CONTENT_TYPE); // includes charset info
	}

	@Test
	public void verifyEmailConstructedWithOnlyTextContents() throws Exception {
		final EmailProperties emailProperties = new EmailPropertiesImpl();

		givenEmailCompositionConfigForEmailProperties(emailProperties, null, null, null, TEXT_TEMPLATE_FILE, true, null);
		givenTemplateRendersToString(null, null, TEXT_TEMPLATE_FILE, emailProperties, TEXT_CONTENTS);

		final EmailDto emailDto = emailComposer.composeMessage(emailProperties);

		assertThat(emailDto.getTextBody())
				.isEqualTo(TEXT_CONTENTS);

		assertThat(emailDto.getHtmlBody())
				.isNull();

		assertThat(emailDto.getContentType())
				.isEqualTo(TEXT_CONTENT_TYPE);
	}

	@Test
	public void verifyEmailConstructedWithTextContentsThemedAndEncodedByStore() throws Exception {
		final String storeCode = "MYSTORE";
		store.setCode(storeCode);

		final EmailProperties emailProperties = new EmailPropertiesImpl();
		emailProperties.setStoreCode(storeCode);

		givenEmailCompositionConfigForEmailProperties(emailProperties, null, null, null, TEXT_TEMPLATE_FILE, true, store);
		givenTemplateRendersToString(storeCode, store, TEXT_TEMPLATE_FILE, emailProperties, TEXT_CONTENTS);

		final EmailDto emailDto = emailComposer.composeMessage(emailProperties);

		assertThat(emailDto.getTextBody())
				.isEqualTo(TEXT_CONTENTS);

		assertThat(emailDto.getContentType())
				.isEqualTo(EmailDtoTestData.PLAIN_TEXT_CONTENT_TYPE); // includes charset info

		assertThat(emailDto.getHtmlBody())
				.isNull();
	}

	@Test
	public void verifyEmailConstructedWithBothHtmlAndTextContents() throws Exception {
		final EmailProperties emailProperties = new EmailPropertiesImpl();

		givenEmailCompositionConfigForEmailProperties(emailProperties, null, null, HTML_TEMPLATE_FILE, TEXT_TEMPLATE_FILE, true, null);

		givenTemplateRendersToString(null, null, HTML_TEMPLATE_FILE, emailProperties, HTML_CONTENTS);
		givenTemplateRendersToString(null, null, TEXT_TEMPLATE_FILE, emailProperties, TEXT_CONTENTS);

		final EmailDto emailDto = emailComposer.composeMessage(emailProperties);

		assertThat(emailDto.getTextBody())
				.isEqualTo(TEXT_CONTENTS);

		assertThat(emailDto.getHtmlBody())
				.isEqualTo(HTML_CONTENTS);

		assertThat(emailDto.getContentType())
				.isEqualTo(HTML_CONTENT_TYPE);
	}

	@Test
	public void verifyEmailConstructedWithOnlyTextContentsWhenHtmlIsDisallowed() throws Exception {
		final EmailProperties emailProperties = new EmailPropertiesImpl();

		final boolean canSendHtmlEmails = false;

		givenEmailCompositionConfigForEmailProperties(emailProperties, null, null, HTML_TEMPLATE_FILE, TEXT_TEMPLATE_FILE, canSendHtmlEmails, null);

		givenTemplateRendersToString(null, null, HTML_TEMPLATE_FILE, emailProperties, HTML_CONTENTS);
		givenTemplateRendersToString(null, null, TEXT_TEMPLATE_FILE, emailProperties, TEXT_CONTENTS);

		final EmailDto emailDto = emailComposer.composeMessage(emailProperties);

		assertThat(emailDto.getTextBody())
				.isEqualTo(TEXT_CONTENTS);

		assertThat(emailDto.getHtmlBody())
				.isNull();
	}

	@Test
	public void verifyEmailConstructedWithSubject() throws Exception {
		final String subject = "Re: Fw: Thought you would enjoy this, love mum";

		final EmailProperties emailProperties = new EmailPropertiesImpl();

		givenEmailCompositionConfigForEmailProperties(emailProperties, null, subject, null, null, true, null);

		final EmailDto emailDto = emailComposer.composeMessage(emailProperties);

		assertThat(emailDto.getSubject())
				.isEqualTo(subject);
	}

	@Test
	public void verifyEmailConstructedWithRecipientAddress() throws Exception {
		final String recipientAddress = "recipient@elasticpath.com";

		final EmailProperties emailProperties = new EmailPropertiesImpl();
		emailProperties.setRecipientAddress(recipientAddress);

		givenEmailCompositionConfigForEmailProperties(emailProperties, null, null, null, null, true, null);

		final EmailDto emailDto = emailComposer.composeMessage(emailProperties);

		assertThat(emailDto.getTo())
				.containsExactly(recipientAddress);
	}

	@Test
	public void verifyEmailConstructedWithSendFromAddress() throws Exception {
		final String sendFromAddress = "sender@elasticpath.com";

		final EmailProperties emailProperties = new EmailPropertiesImpl();

		givenEmailCompositionConfigForEmailProperties(emailProperties, sendFromAddress, null, null, null, true, null);

		final EmailDto emailDto = emailComposer.composeMessage(emailProperties);

		assertThat(emailDto.getFrom())
				.isEqualTo(sendFromAddress);
	}

	private void givenTemplateRendersToString(final String storeCode,
											  final Store store,
											  final String templateFile,
											  final EmailProperties emailProperties,
											  final String templateOutput) {
		final Map<String, Object> templateResources = Maps.newHashMap();

		when(emailContextFactory.createVelocityContext(store, emailProperties)).thenReturn(templateResources);
		when(templateRenderer.renderTemplate(templateFile, storeCode, templateResources))
				.thenReturn(templateOutput);
	}

	private EmailCompositionConfiguration givenEmailCompositionConfigForEmailProperties(final EmailProperties emailProperties,
																						final String emailSentFromAddress, final String emailSubject,
																						final String htmlTemplateFile,
																						final String textTemplate,
																						final Boolean canSendHtmlEmails,
																						final Store store) {
		emailProperties.setHtmlTemplate(htmlTemplateFile);
		emailProperties.setTextTemplate(textTemplate);

		final TemplatePathResolver templateResolver = template ->
				(template.equals(htmlTemplateFile) ? htmlTemplateFile : textTemplate);

		final EmailCompositionConfiguration emailCompositionConfig =
				new EmailCompositionConfigurationImpl(emailProperties,
						() -> emailSentFromAddress,
						emailProps -> emailSubject,
						templateResolver,
						emailProps -> canSendHtmlEmails,
						store);

		when(configurationFactory.create(emailProperties)).thenReturn(emailCompositionConfig);

		return emailCompositionConfig;
	}

}