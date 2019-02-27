/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tags.service.impl;

import org.junit.Test;

/**
 * AbstractDecorator test.
 */
public class AbstractDecoratorTest {
	
	/**
	 * Test that AbstractDecorator will not work with null values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAbstractDecoratorNullValue() {
		new AbstractDecorator(null) {
			@Override
			public String decorate() {
				return null;
			}
		};
	}

}
