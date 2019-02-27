/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util;

import java.util.Locale;
import java.util.Optional;

/**
 * Resolves store-theme-specific messages.
 */
@FunctionalInterface
public interface ThemedMessageResolver {

	/**
	 * Resolves a store-theme-specific message.
	 *
	 * @param storeCode   the store's code
	 * @param messageCode the message code
	 * @param locale      the locale in which the message should be rendered
	 * @return the message resolved from the code, if one can be found
	 */
	Optional<String> getMessage(String storeCode, String messageCode, Locale locale);

}
