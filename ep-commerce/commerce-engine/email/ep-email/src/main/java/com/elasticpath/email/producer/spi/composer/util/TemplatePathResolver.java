/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util;

import java.util.function.Function;

/**
 * Produces a template path from a given filename.
 */
public interface TemplatePathResolver extends Function<String, String> {
}
