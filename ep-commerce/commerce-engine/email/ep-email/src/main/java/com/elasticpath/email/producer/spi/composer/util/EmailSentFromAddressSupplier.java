/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util;

import java.util.function.Supplier;

/**
 * Provides a From address for an email.
 */
public interface EmailSentFromAddressSupplier extends Supplier<String> {
}
