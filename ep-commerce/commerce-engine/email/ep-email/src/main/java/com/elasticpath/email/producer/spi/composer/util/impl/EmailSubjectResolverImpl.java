/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.producer.spi.composer.util.EmailSubjectResolver;
import com.elasticpath.email.producer.spi.composer.util.ThemedMessageResolver;

/**
 * Default implementation link 	{@link EmailSubjectResolver}.
 */
public class EmailSubjectResolverImpl implements EmailSubjectResolver {

	private final ThemedMessageResolver themedMessageResolver;

	/**
	 * Constructor.
	 *
	 * @param themedMessageResolver the themed message resolver
	 */
	public EmailSubjectResolverImpl(final ThemedMessageResolver themedMessageResolver) {
		this.themedMessageResolver = themedMessageResolver;
	}

	@Override
	public String apply(final EmailProperties emailProperties) {
		return themedMessageResolver.getMessage(emailProperties.getStoreCode(),
				emailProperties.getLocaleDependentSubjectKey(),
				emailProperties.getEmailLocale())
				.orElse(emailProperties.getDefaultSubject());
	}

}
