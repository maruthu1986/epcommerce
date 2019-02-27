/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import org.apache.commons.lang3.BooleanUtils;

import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.producer.spi.composer.util.EmailCanIncludeHtmlContentsPredicate;

/**
 * Default implementation of {@link EmailCanIncludeHtmlContentsPredicate}.
 */
public class EmailCanIncludeHtmlContentsPredicateImpl implements EmailCanIncludeHtmlContentsPredicate {

	// This is a terrible variable name that matches the terribly-named Setting definition at COMMERCE/SYSTEM/EMAIL/emailTextTemplateEnabled
	private final Boolean emailTextTemplateEnabled;

	/**
	 * Constructor.
	 *
	 * @param emailTextTemplateEnabled indicates whether to use the plain text template for sending store emails
	 */
	public EmailCanIncludeHtmlContentsPredicateImpl(final Boolean emailTextTemplateEnabled) {
		this.emailTextTemplateEnabled = emailTextTemplateEnabled;
	}

	@Override
	public boolean test(final EmailProperties emailProperties) {
		return BooleanUtils.isNotTrue(emailProperties.isTextOnly()) && !emailTextTemplateEnabled;
	}

}
