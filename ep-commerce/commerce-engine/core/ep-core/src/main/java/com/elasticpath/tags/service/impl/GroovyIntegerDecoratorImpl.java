/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tags.service.impl;

/**
 * 
 * Integer decorator.
 *
 */
public class GroovyIntegerDecoratorImpl extends AbstractGroovyNumberDecorator {

	/**
	 * Constructor. 
	 * @param value given value.
	 */
	GroovyIntegerDecoratorImpl(final Object value) {
		super(value);
	}

	@Override
	protected String getGroovyTypeSuffix() {
		return "i";
	}


}
