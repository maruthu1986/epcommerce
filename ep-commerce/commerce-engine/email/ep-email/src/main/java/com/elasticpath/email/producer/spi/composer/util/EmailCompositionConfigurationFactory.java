/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util;

import com.elasticpath.email.domain.EmailProperties;

/**
 * Factory that creates {@link EmailCompositionConfiguration} instances.
 */
public interface EmailCompositionConfigurationFactory {

	/**
	 * Creates a new {@link EmailCompositionConfiguration} instance.
	 *
	 * @param emailProperties the email properties on which the configuration is based
	 * @return a new EmailCompositionConfiguration instance
	 */
	EmailCompositionConfiguration create(EmailProperties emailProperties);

}
