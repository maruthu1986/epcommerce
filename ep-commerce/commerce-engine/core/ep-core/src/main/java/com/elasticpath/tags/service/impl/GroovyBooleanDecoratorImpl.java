/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tags.service.impl;

/**
 * Decorates a Boolean for use in the groovy script engine.
 */
public class GroovyBooleanDecoratorImpl extends AbstractDecorator {

	/**
	 * Constructor.
	 * @param value given value for decoration.
	 */
	public GroovyBooleanDecoratorImpl(final Object value) {
		super(value);
	}
	
	@Override
	public String decorate() {
		if (Boolean.parseBoolean(getValue().toString())) {
			return Boolean.TRUE.toString();
		}
		return Boolean.FALSE.toString();
	}
	
}
