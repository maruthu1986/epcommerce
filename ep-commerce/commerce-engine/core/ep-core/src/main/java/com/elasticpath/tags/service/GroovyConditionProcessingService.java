/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tags.service;

import groovy.lang.Script;

import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * Processes conditions strings to produce a groovy script.
 */
public interface GroovyConditionProcessingService {
	/**
	 * Apply any preprocessing and caching necessary for condition scripts.
	 * A groovy shell instance is used to parse the script.
	 *
	 * @param condition the condition to process
	 * @return groovy script object for the condition
	 * @throws Exception - throws etopxception if future task fails
	 */
	Script preprocess(ConditionalExpression condition) throws Exception;
}
