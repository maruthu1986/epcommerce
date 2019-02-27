/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer;

import java.util.Map;

/**
 * Produces the output result of executing a template with specified parameters.
 */
@FunctionalInterface
public interface TemplateRenderer {

	/**
	 * Returns the output result of executing a template with specified parameters.
	 *
	 * @param template          the template to execute
	 * @param storeCode         the store code that determines the theme of the template
	 * @param templateResources the parameters that will populate the template
	 * @return the output result of executing a template with specified parameters
	 */
	String renderTemplate(String template, String storeCode, Map<String, Object> templateResources);

}
