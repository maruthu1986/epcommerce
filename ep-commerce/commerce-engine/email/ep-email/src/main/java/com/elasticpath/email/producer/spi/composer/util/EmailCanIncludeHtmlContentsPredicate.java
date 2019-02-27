/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util;

import java.util.function.Predicate;

import com.elasticpath.email.domain.EmailProperties;

/**
 * Determines whether or not HTML emails may be sent. If not, email bodies must be sent containing plain text only.
 */
public interface EmailCanIncludeHtmlContentsPredicate extends Predicate<EmailProperties> {
}
