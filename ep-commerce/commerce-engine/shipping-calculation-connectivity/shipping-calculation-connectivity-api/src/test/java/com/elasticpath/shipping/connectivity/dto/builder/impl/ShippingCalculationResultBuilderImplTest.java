/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.impl;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingCalculationResultImpl;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingOptionImpl;

/**
 * Tests {@link ShippingCalculationResultBuilderImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingCalculationResultBuilderImplTest {

	private static final String BUILD_SHOULD_RETURN_NEW_INSTANCE = "Each ShippingCalculationResult returned from build() should be new instance";
	private static final String BUILD_SHOULD_RETURN_EQUAL_INSTANCES = "Consecutive calls to build() should return equal but distinct objects";

	private ShippingCalculationResultBuilderImpl builderUnderTest;

	private List<ShippingOption> shippingOptions;

	@Mock
	private ShippingCalculationResult.ErrorInformation errorInformation;


	@Before
	public void setUp() {
		builderUnderTest = new ShippingCalculationResultBuilderImpl();
		builderUnderTest.setInstanceSupplier(ShippingCalculationResultImpl::new);

		shippingOptions = singletonList(new ShippingOptionImpl());
	}

	@Test
	public void testBuild() {
		final ShippingCalculationResultImpl expectedShippingCalculationResult = new ShippingCalculationResultImpl();
		expectedShippingCalculationResult.setAvailableShippingOptions(shippingOptions);
		expectedShippingCalculationResult.setErrorInformation(errorInformation);

		final ShippingCalculationResult actualShippingCalculationResult = builderUnderTest
				.withShippingOptions(shippingOptions)
				.withErrorInformation(errorInformation)
				.build();

		assertThat(expectedShippingCalculationResult).isEqualTo(actualShippingCalculationResult);
	}

	@Test
	public void testBuildReturnsNewInstanceEachTime() {
		final ShippingCalculationResult result1 = builderUnderTest.build();
		final ShippingCalculationResult result2 = builderUnderTest.build();
		final ShippingCalculationResult result3 = builderUnderTest.build();

		assertThat(result1).as(BUILD_SHOULD_RETURN_NEW_INSTANCE).isNotSameAs(result2);
		assertThat(result2).as(BUILD_SHOULD_RETURN_NEW_INSTANCE).isNotSameAs(result3);
		assertThat(result2).as(BUILD_SHOULD_RETURN_NEW_INSTANCE).isNotSameAs(result3);
	}

	@Test
	public void testBuildMaintainsPreviousState() {
		final ShippingCalculationResultImpl expectedShippingCalculationResult = new ShippingCalculationResultImpl();
		expectedShippingCalculationResult.setAvailableShippingOptions(shippingOptions);
		expectedShippingCalculationResult.setErrorInformation(errorInformation);

		final ShippingCalculationResult result1 = builderUnderTest
				.withShippingOptions(shippingOptions)
				.withErrorInformation(errorInformation)
				.build();

		final ShippingCalculationResult result2 = builderUnderTest.build();

		final ShippingCalculationResult result3 = builderUnderTest.build();

		assertThat(result1).as(BUILD_SHOULD_RETURN_EQUAL_INSTANCES).isEqualTo(result2);
		assertThat(result1).as(BUILD_SHOULD_RETURN_EQUAL_INSTANCES).isEqualTo(result3);
	}

	@Test
	public void testBuildAllowsStateToChangeForEachBuild() {
		final ShippingCalculationResultImpl expectedShippingCalculationResult = new ShippingCalculationResultImpl();
		expectedShippingCalculationResult.setAvailableShippingOptions(shippingOptions);

		final ShippingCalculationResult result1 = builderUnderTest
				.withShippingOptions(shippingOptions)
				.build();

		final ShippingCalculationResult result2 = builderUnderTest
				.withShippingOptions(null)
				.withErrorInformation(errorInformation)
				.build();

		assertThat(result1).as("Changing impl state after build() should create a mutated instance on next build()").isNotEqualTo(result2);
	}
}
