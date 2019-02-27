/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util;

import java.util.function.Function;

import com.elasticpath.email.domain.EmailProperties;

/**
 * Selects an appropriate email subject from a given {@link EmailProperties}.
 */
public interface EmailSubjectResolver extends Function<EmailProperties, String> {
}
