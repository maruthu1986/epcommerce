/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.builder.impl;

import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.impl.ShippingOptionResultImpl;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingOptionImpl;

/**
 * Tests {@link ShippingOptionResultBuilderImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionResultBuilderImplTest {

	private static final String BUILD_SHOULD_RETURN_NEW_INSTANCE = "Each ShippingOptionResult returned from build() should be new instance";
	private static final String BUILD_SHOULD_RETURN_EQUAL_INSTANCES = "Consecutive calls to build() should return equal but distinct objects";

	private List<ShippingOption> shippingOptions;

	@Mock
	private ShippingCalculationResult.ErrorInformation errorInformation;

	private ShippingOptionResultBuilderImpl builderUnderTest;

	@Before
	public void setUp() {
		builderUnderTest = new ShippingOptionResultBuilderImpl();
		builderUnderTest.setInstanceSupplier(ShippingOptionResultImpl::new);

		shippingOptions = singletonList(new ShippingOptionImpl());
	}

	@Test
	public void testBuild() {
		final ShippingOptionResultImpl expectedShippingOptionResult = new ShippingOptionResultImpl();
		expectedShippingOptionResult.setAvailableShippingOptions(shippingOptions);
		expectedShippingOptionResult.setErrorInformation(errorInformation);

		final ShippingOptionResult actualShippingOptionResult = builderUnderTest
				.withShippingOptions(shippingOptions)
				.withErrorInformation(errorInformation)
				.build();

		assertThat(expectedShippingOptionResult).isEqualTo(actualShippingOptionResult);
	}

	@Test
	public void testBuildReturnsNewInstanceEachTime() {
		final ShippingOptionResult result1 = builderUnderTest.build();
		final ShippingOptionResult result2 = builderUnderTest.build();
		final ShippingOptionResult result3 = builderUnderTest.build();

		assertThat(result1).as(BUILD_SHOULD_RETURN_NEW_INSTANCE).isNotSameAs(result2);
		assertThat(result1).as(BUILD_SHOULD_RETURN_NEW_INSTANCE).isNotSameAs(result3);
		assertThat(result2).as(BUILD_SHOULD_RETURN_NEW_INSTANCE).isNotSameAs(result3);
	}

	@Test
	public void testBuildMaintainsPreviousState() {
		final ShippingOptionResultImpl expectedShippingOptionResult = new ShippingOptionResultImpl();
		expectedShippingOptionResult.setAvailableShippingOptions(shippingOptions);
		expectedShippingOptionResult.setErrorInformation(errorInformation);

		final ShippingOptionResult result1 = builderUnderTest
				.withShippingOptions(shippingOptions)
				.withErrorInformation(errorInformation)
				.build();

		final ShippingOptionResult result2 = builderUnderTest.build();

		final ShippingOptionResult result3 = builderUnderTest.build();

		assertThat(result1).as(BUILD_SHOULD_RETURN_EQUAL_INSTANCES).isEqualTo(result2);
		assertThat(result1).as(BUILD_SHOULD_RETURN_EQUAL_INSTANCES).isEqualTo(result3);
	}

	@Test
	public void testBuildAllowsStateToChangeForEachBuild() {
		final ShippingOptionResultImpl expectedShippingOptionResult = new ShippingOptionResultImpl();
		expectedShippingOptionResult.setAvailableShippingOptions(shippingOptions);

		final ShippingOptionResult result1 = builderUnderTest
				.withShippingOptions(shippingOptions)
				.build();

		final ShippingOptionResult result2 = builderUnderTest
				.withShippingOptions(null)
				.withErrorInformation(errorInformation)
				.build();

		assertThat(result1).as("Changing impl state after build() should create a mutated instance on next build()").isNotEqualTo(result2);
	}
}
