/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.shipping.predicates;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.ConnectException;

import org.junit.Test;

/**
 * Unit test for {@link DefaultShippingOptionResultExceptionLogPredicateImpl}.
 */
public class DefaultShippingOptionResultExceptionLogPredicateTest {
	private final DefaultShippingOptionResultExceptionLogPredicateImpl predicateUnderTest =
			new DefaultShippingOptionResultExceptionLogPredicateImpl();

	@Test
	public void verifyTestWithConnectExceptionReturnsFalse() {
		assertThat(predicateUnderTest.test(new ConnectException())).isFalse();
	}

	@Test
	public void verifyTestWithNonConnectExceptionReturnsTrue() {
		assertThat(predicateUnderTest.test(new IllegalStateException())).isTrue();
	}
}
