/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import java.util.Optional;

import com.elasticpath.domain.store.Store;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.producer.spi.composer.util.EmailCanIncludeHtmlContentsPredicate;
import com.elasticpath.email.producer.spi.composer.util.EmailCompositionConfiguration;
import com.elasticpath.email.producer.spi.composer.util.EmailSentFromAddressSupplier;
import com.elasticpath.email.producer.spi.composer.util.EmailSubjectResolver;
import com.elasticpath.email.producer.spi.composer.util.TemplatePathResolver;

/**
 * Default implementation of {@link EmailCompositionConfiguration}.
 */
public class EmailCompositionConfigurationImpl implements EmailCompositionConfiguration {

	private final EmailProperties emailProperties;
	private final EmailSentFromAddressSupplier emailSentFromAddressSupplier;
	private final EmailSubjectResolver emailSubjectResolver;
	private final TemplatePathResolver templatePathResolver;
	private final EmailCanIncludeHtmlContentsPredicate emailCanIncludeHtmlContentsPredicate;
	private final Store store;

	/**
	 * Constructor.
	 *
	 * @param emailProperties                      the email properties on which the configuration is based
	 * @param emailSentFromAddressSupplier         supplies sent-from email addresses
	 * @param emailSubjectResolver                 supplies email subjects
	 * @param templatePathResolver                 provides template paths
	 * @param emailCanIncludeHtmlContentsPredicate indicates whether or not emails can be sent with HTML contents
	 */
	public EmailCompositionConfigurationImpl(final EmailProperties emailProperties,
											 final EmailSentFromAddressSupplier emailSentFromAddressSupplier,
											 final EmailSubjectResolver emailSubjectResolver,
											 final TemplatePathResolver templatePathResolver,
											 final EmailCanIncludeHtmlContentsPredicate emailCanIncludeHtmlContentsPredicate) {
		this(emailProperties, emailSentFromAddressSupplier, emailSubjectResolver, templatePathResolver, emailCanIncludeHtmlContentsPredicate, null);
	}

	/**
	 * Constructor.
	 *
	 * @param emailProperties                      the email properties on which the configuration is based
	 * @param emailSentFromAddressSupplier         supplies sent-from email addresses
	 * @param emailSubjectResolver                 supplies email subjects
	 * @param templatePathResolver                 provides template paths
	 * @param emailCanIncludeHtmlContentsPredicate indicates whether or not emails can be sent with HTML contents
	 * @param store                                the store; used in certain properties that may be store-specific
	 */
	public EmailCompositionConfigurationImpl(final EmailProperties emailProperties,
											 final EmailSentFromAddressSupplier emailSentFromAddressSupplier,
											 final EmailSubjectResolver emailSubjectResolver,
											 final TemplatePathResolver templatePathResolver,
											 final EmailCanIncludeHtmlContentsPredicate emailCanIncludeHtmlContentsPredicate,
											 final Store store) {
		this.emailProperties = emailProperties;
		this.emailSentFromAddressSupplier = emailSentFromAddressSupplier;
		this.emailSubjectResolver = emailSubjectResolver;
		this.templatePathResolver = templatePathResolver;
		this.emailCanIncludeHtmlContentsPredicate = emailCanIncludeHtmlContentsPredicate;
		this.store = store;
	}

	/**
	 * Returns the Store used when composing the email, if applicable.
	 *
	 * @return an optional Store
	 */
	@Override
	public Optional<Store> getStore() {
		return Optional.ofNullable(store);
	}

	/**
	 * Returns the email address from which the email should be sent.
	 *
	 * @return the email address from which the email should be sent
	 */
	@Override
	public String getSendFromAddress() {
		return emailSentFromAddressSupplier.get();
	}

	@Override
	public String getRecipientAddress() {
		return emailProperties.getRecipientAddress();
	}

	/**
	 * Returns the subject of the email.
	 *
	 * @return the subject of the email
	 */
	@Override
	public String getEmailSubject() {
		return emailSubjectResolver.apply(emailProperties);
	}

	@Override
	public Optional<String> getCharacterEncoding() {
		return getStore().map(Store::getContentEncoding);
	}

	/**
	 * Returns the relative path and filename of the template used to render HTML email contents.
	 *
	 * @return the relative path and filename of the template used to render HTML email contents
	 */
	@Override
	public Optional<String> getHtmlTemplate() {
		return Optional.ofNullable(emailProperties.getHtmlTemplate())
				.map(templatePathResolver);
	}

	/**
	 * Returns the relative path and filename of the template used to render plain text email contents.
	 *
	 * @return the relative path and filename of the template used to render plain text email contents
	 */
	@Override
	public Optional<String> getTextTemplate() {
		return Optional.ofNullable(emailProperties.getTextTemplate())
				.map(templatePathResolver);
	}

	/**
	 * Indicates whether or not the email can be sent with HTML contents.
	 *
	 * @return true if the email can be sent with HTML contents
	 */
	@Override
	public boolean htmlEmailModeEnabled() {
		return emailCanIncludeHtmlContentsPredicate.test(emailProperties);
	}

}
