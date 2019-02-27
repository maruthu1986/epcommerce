/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;

import com.google.common.testing.EqualsTester;
import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.money.Money;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingOptionImpl;

/**
 * Tests {@link ShippingOptionBuilderImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionBuilderImplTest {

	private static final String CARRIER_CODE = "carrierCode";
	private static final String CARRIER_DISPLAY_NAME = "carrierDisplayName";
	private static final String CODE = "code";
	private static final String DISPLAY_NAME = "displayName";
	private static final String DESCRIPTION = "description";
	private static final Money SHIPPING_COST = Money.valueOf(BigDecimal.ONE, Currency.getInstance("USD"));
	private static final String BUILD_SHOULD_RETURN_NEW_INSTANCE = "Each ShippingOption returned from build() should be new instance";
	public static final Locale LOCALE = Locale.CANADA;

	private static final String EST_EARLIEST_DELIVERY_DATE_STRING = "2000-01-01";
	private static final String EST_LATEST_DELIVERY_DATE_STRING = "2001-01-01";
	private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final LocalDate EST_EARLIEST_DELIVERY_DATE = LocalDate.parse(EST_EARLIEST_DELIVERY_DATE_STRING, LOCAL_DATE_FORMATTER);
	private static final LocalDate EST_LATEST_DELIVERY_DATE = LocalDate.parse(EST_LATEST_DELIVERY_DATE_STRING, LOCAL_DATE_FORMATTER);
	private static final String FIELD_KEY = "testKey";
	private static final String FIELD_VALUE = "testValue";

	private ShippingOptionBuilderImpl builderUnderTest;
	private ShippingOption expectedShippingOption;

	@Before
	public void setUp() {
		builderUnderTest = new ShippingOptionBuilderImpl();
		builderUnderTest.setInstanceSupplier(ShippingOptionImpl::new);

		expectedShippingOption = buildExpectedShippingOption();
	}

	@Test
	public void testBuild() {

		final ShippingOption actualShippingOption = builderUnderTest.withCarrierCode(CARRIER_CODE)
				.withCarrierDisplayName(CARRIER_DISPLAY_NAME)
				.withCode(CODE)
				.withDisplayNames(Maps.newHashMap(LOCALE, DISPLAY_NAME))
				.withDescription(DESCRIPTION)
				.withShippingCost(SHIPPING_COST)
				.withEstimatedEarliestDeliveryDate(EST_EARLIEST_DELIVERY_DATE)
				.withEstimatedLatestDeliveryDate(EST_LATEST_DELIVERY_DATE)
				.withFields(Maps.newHashMap(FIELD_KEY, FIELD_VALUE))
				.build();

		assertThat(actualShippingOption).isEqualTo(this.expectedShippingOption);
	}

	@Test
	public void testBuildFrom() {

		final ShippingOption actualShippingOption = builderUnderTest.from(expectedShippingOption).build();

		assertThat(actualShippingOption).isEqualTo(expectedShippingOption);
	}

	@Test
	public void testBuildReturnsNewInstanceEachTime() {
		final ShippingOption result1 = builderUnderTest.build();
		final ShippingOption result2 = builderUnderTest.build();
		final ShippingOption result3 = builderUnderTest.build();

		assertThat(result1).as(BUILD_SHOULD_RETURN_NEW_INSTANCE).isNotSameAs(result2);
		assertThat(result1).as(BUILD_SHOULD_RETURN_NEW_INSTANCE).isNotSameAs(result3);
		assertThat(result2).as(BUILD_SHOULD_RETURN_NEW_INSTANCE).isNotSameAs(result3);
	}

	@Test
	public void testBuildMaintainsPreviousState() {

		final ShippingOption result1 = builderUnderTest.withCarrierCode(CARRIER_CODE)
				.withCarrierDisplayName(CARRIER_DISPLAY_NAME)
				.withCode(CODE)
				.withDisplayNames(Maps.newHashMap(LOCALE, DISPLAY_NAME))
				.withDescription(DESCRIPTION)
				.withShippingCost(SHIPPING_COST)
				.withEstimatedLatestDeliveryDate(EST_EARLIEST_DELIVERY_DATE)
				.withEstimatedLatestDeliveryDate(EST_LATEST_DELIVERY_DATE)
				.build();

		final ShippingOption result2 = builderUnderTest.build();

		final ShippingOption result3 = builderUnderTest.build();

		new EqualsTester().addEqualityGroup(result1, result2, result3).testEquals();

	}

	@Test
	public void testBuildAllowsStateToChangeForEachBuild() {

		final ShippingOption result1 = builderUnderTest.withCarrierCode(CARRIER_CODE)
				.withCarrierDisplayName(CARRIER_DISPLAY_NAME)
				.withCode(CODE)
				.withDisplayNames(Maps.newHashMap(LOCALE, DISPLAY_NAME))
				.withDescription(DESCRIPTION)
				.withShippingCost(SHIPPING_COST)
				.build();

		final ShippingOption result2 = builderUnderTest
				.withDisplayNames(Maps.newHashMap(LOCALE, "DIFFERENT DISPLAY_NAME"))
				.build();

		assertThat(result1).as("Changing impl state after build() should create a mutated instance on next build()").isNotEqualTo(result2);
	}

	private ShippingOption buildExpectedShippingOption() {

		final ShippingOptionImpl newShippingOption = new ShippingOptionImpl();
		newShippingOption.setCarrierCode(CARRIER_CODE);
		newShippingOption.setCarrierDisplayName(CARRIER_DISPLAY_NAME);
		newShippingOption.setCode(CODE);
		newShippingOption.setDisplayName(LOCALE, DISPLAY_NAME);
		newShippingOption.setDescription(DESCRIPTION);
		newShippingOption.setShippingCost(SHIPPING_COST);
		newShippingOption.setFields(Maps.newHashMap(FIELD_KEY, FIELD_VALUE));
		newShippingOption.setEstimatedEarliestDeliveryDate(EST_EARLIEST_DELIVERY_DATE);
		newShippingOption.setEstimatedLatestDeliveryDate(EST_LATEST_DELIVERY_DATE);

		return newShippingOption;

	}
}
