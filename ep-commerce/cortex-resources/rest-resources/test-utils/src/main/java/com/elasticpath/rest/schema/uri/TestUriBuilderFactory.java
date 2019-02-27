/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * Factory for quickly creating mocked builders whose methods will, by default, return the builder.
 */
public final class TestUriBuilderFactory {

	private TestUriBuilderFactory() {
		// Static class
	}

	/**
	 * Create a mocked builder with the given interface and return value.
	 *
	 * @param builderClass the builder class
	 * @param buildValue the return value to be stubbed onto the {@link UriBuilder#build()} method.
	 * @param <B> builder type
	 * @return a mocked builder
	 */
	public static <B extends UriBuilder> B mockUriBuilder(final Class<B> builderClass, final String buildValue) {
		@SuppressWarnings("unchecked")
		Answer<B> answerWithSelf = invocation -> (B) invocation.getMock();
		B builder = Mockito.mock(builderClass, answerWithSelf);
		Mockito.doReturn(buildValue).when(builder).build();
		return builder;
	}

}
