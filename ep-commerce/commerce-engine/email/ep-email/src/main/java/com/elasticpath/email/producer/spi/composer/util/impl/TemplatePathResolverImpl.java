/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import java.io.File;

import com.elasticpath.email.producer.spi.composer.util.TemplatePathResolver;

/**
 * Default implementation of {@link TemplatePathResolver}.
 */
public class TemplatePathResolverImpl implements TemplatePathResolver {

	/**
	 * The directory within which all email templates reside.
	 */
	static final String BASE_EMAIL_TEMPLATE_DIR = "email";

	@Override
	public String apply(final String filename) {
		return BASE_EMAIL_TEMPLATE_DIR + File.separator + filename + ".vm";
	}

}
