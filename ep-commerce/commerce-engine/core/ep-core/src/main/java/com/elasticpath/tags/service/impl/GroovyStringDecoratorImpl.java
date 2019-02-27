/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tags.service.impl;

/**
 * Decorates a String for use in the groovy script engine.
 */
public class GroovyStringDecoratorImpl extends AbstractDecorator {

	/**
	 * Constructor.
	 * @param value given value for decoration.
	 */
	public GroovyStringDecoratorImpl(final Object value) {
		super(value);
	}
	
	@Override
	public String decorate() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append('\'');
		stringBuilder.append(getValue().toString().replace("'", "\\'"));
		stringBuilder.append('\'');
		return stringBuilder.toString();
	}
	
}
