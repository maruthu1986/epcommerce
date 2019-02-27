/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.impl;

import java.util.Map;
import java.util.Optional;

import com.elasticpath.domain.store.Store;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.email.producer.spi.composer.TemplateRenderer;
import com.elasticpath.email.producer.spi.composer.util.EmailCompositionConfiguration;
import com.elasticpath.email.producer.spi.composer.util.EmailCompositionConfigurationFactory;
import com.elasticpath.email.producer.spi.composer.util.EmailContextFactory;

/**
 * Default implementation of {@link EmailComposer}.
 */
public class EmailComposerImpl implements EmailComposer {

	private static final String HTML_TYPE = "text/html";
	private static final String PLAIN_TEXT_TYPE = "text/plain";

	private EmailCompositionConfigurationFactory emailCompositionConfigurationFactory;

	private EmailContextFactory emailContextFactory;

	private TemplateRenderer templateRenderer;

	@Override
	public EmailDto composeMessage(final EmailProperties emailProperties) {
		final EmailCompositionConfiguration compositionConfiguration = getEmailCompositionConfigurationFactory().create(emailProperties);

		final Optional<Store> storeOptional = compositionConfiguration.getStore();
		final Optional<String> storeCodeOptional = storeOptional.map(Store::getCode);

		final Map<String, Object> templateResources =
				emailContextFactory.createVelocityContext(storeOptional.orElse(null), emailProperties);

		EmailDto.Builder<?> builder = EmailDto.builder()
				.withFrom(compositionConfiguration.getSendFromAddress())
				.withTo(compositionConfiguration.getRecipientAddress())
				.withSubject(compositionConfiguration.getEmailSubject());

		final Optional<String> htmlTemplateOptional = compositionConfiguration.getHtmlTemplate();
		final Optional<String> textTemplateOptional = compositionConfiguration.getTextTemplate();

		if (textTemplateOptional.isPresent()) {
			final String textContents = getTemplateRenderer().renderTemplate(textTemplateOptional.get(), storeCodeOptional.orElse(null),
					templateResources);

			builder = builder
					.withTextBody(textContents)
					.withContentType(getPlainTextContentType(compositionConfiguration));
		}

		if (htmlTemplateOptional.isPresent() && compositionConfiguration.htmlEmailModeEnabled()) {
			final String htmlContents =
					getTemplateRenderer().renderTemplate(htmlTemplateOptional.get(), storeCodeOptional.orElse(null), templateResources);

			builder = builder
					.withHtmlBody(htmlContents)
					.withContentType(getHtmlContentType(compositionConfiguration));
		}

		return builder.build();
	}

	/**
	 * Returns a Content Type value for plain text emails, including a specified character encoding if set.
	 *
	 * @param compositionConfiguration the composition configuration
	 * @return a content type value
	 */
	protected String getPlainTextContentType(final EmailCompositionConfiguration compositionConfiguration) {
		final Optional<String> characterEncoding = compositionConfiguration.getCharacterEncoding();

		return characterEncoding
				.map(encoding -> joinContentTypeAndCharset(PLAIN_TEXT_TYPE, encoding))
				.orElse(PLAIN_TEXT_TYPE);
	}

	/**
	 * Returns a Content Type value for HTML emails, including a specified character encoding if set.
	 *
	 * @param compositionConfiguration the composition configuration
	 * @return a content type value
	 */
	protected String getHtmlContentType(final EmailCompositionConfiguration compositionConfiguration) {
		final Optional<String> characterEncoding = compositionConfiguration.getCharacterEncoding();

		return characterEncoding
				.map(encoding -> joinContentTypeAndCharset(HTML_TYPE, encoding))
				.orElse(HTML_TYPE);
	}

	private String joinContentTypeAndCharset(final String contentType, final String charset) {
		return contentType + "; charset=" + charset;
	}

	protected EmailCompositionConfigurationFactory getEmailCompositionConfigurationFactory() {
		return emailCompositionConfigurationFactory;
	}

	public void setEmailCompositionConfigurationFactory(final EmailCompositionConfigurationFactory emailCompositionConfigurationFactory) {
		this.emailCompositionConfigurationFactory = emailCompositionConfigurationFactory;
	}

	protected TemplateRenderer getTemplateRenderer() {
		return templateRenderer;
	}

	public void setTemplateRenderer(final TemplateRenderer templateRenderer) {
		this.templateRenderer = templateRenderer;
	}

	protected EmailContextFactory getEmailContextFactory() {
		return emailContextFactory;
	}

	public void setEmailContextFactory(final EmailContextFactory emailContextFactory) {
		this.emailContextFactory = emailContextFactory;
	}

}
