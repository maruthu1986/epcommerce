/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Tests for {@link TestUriBuilderFactory}.
 */
public final class TestUriBuilderFactoryTest {

	private static final String EXAMPLE_URI = "exampleUri";

	@Test
	public void testUniqueBuilders() {
		ExampleUriBuilder uriBuilder1 = TestUriBuilderFactory.mockUriBuilder(ExampleUriBuilder.class, EXAMPLE_URI);
		ExampleUriBuilder uriBuilder2 = TestUriBuilderFactory.mockUriBuilder(ExampleUriBuilder.class, EXAMPLE_URI);
		
		assertNotEquals("No two generated URI builders should be the same.", uriBuilder1, uriBuilder2);
	}

	@Test
	public void testSelfReturnValueConsistency() {
		ExampleUriBuilder uriBuilder = TestUriBuilderFactory.mockUriBuilder(ExampleUriBuilder.class, EXAMPLE_URI);
		ExampleUriBuilder returnValueA = uriBuilder.setFieldA();
		ExampleUriBuilder returnValueB = uriBuilder.setFieldB();
		ExampleUriBuilder returnValueC = uriBuilder.setFieldC();
		
		assertEquals("Setter A should return the builder on which it was called.", uriBuilder, returnValueA);
		assertEquals("Setters A and B should return the same builder.", returnValueA, returnValueB);
		assertEquals("Setters B and C should return the same builder.", returnValueB, returnValueC);
	}

	/**
	 * Example extension of UriBuilder for testing purposes.
	 */
	private interface ExampleUriBuilder extends UriBuilder {

		ExampleUriBuilder setFieldA();

		ExampleUriBuilder setFieldB();

		ExampleUriBuilder setFieldC();

	}

}
