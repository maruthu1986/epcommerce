/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.transformer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.function.Supplier;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.money.Money;
import com.elasticpath.service.shipping.impl.ShippingOptionTransformerImpl;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.builder.ShippingOptionBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.impl.ShippingOptionBuilderImpl;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingOptionImpl;

/**
 * Tests {@link ShippingOptionTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionTransformerImplTest {

	private static final Locale LOCALE = Locale.US;
	private static final String CARRIER_CODE = "carrierCode";
	private static final String CODE = "code";
	private static final String DISPLAY_NAME = "displayName";
	private static final Money SHIPPING_COST = Money.valueOf(BigDecimal.ONE, Currency.getInstance(Locale.US));

	@InjectMocks
	private ShippingOptionTransformerImpl shippingOptionTransformerImpl;

	@Mock
	private ShippingServiceLevel mockShippingServiceLevel;
	@Mock
	private Supplier<ShippingOptionBuilder> mockShippingOptionBuilderSupplier;

	@Mock
	private Supplier<ShippingOptionImpl> mockShippingOptionSupplier;

	@Before
	public void setUp() {
		shippingOptionTransformerImpl.setShippingOptionBuilderSupplier(mockShippingOptionBuilderSupplier);
		when(mockShippingOptionBuilderSupplier.get()).thenAnswer((Answer<ShippingOptionBuilderImpl>) mock -> {

			final ShippingOptionBuilderImpl shippingOptionBuilder = new ShippingOptionBuilderImpl();
			shippingOptionBuilder.setInstanceSupplier(mockShippingOptionSupplier);
			return shippingOptionBuilder;
		});
		when(mockShippingOptionSupplier.get()).thenAnswer((Answer<ShippingOptionImpl>) mock -> new ShippingOptionImpl());
	}

	@Test
	public void testTransform() {
		when(mockShippingServiceLevel.getCode()).thenReturn(CODE);
		when(mockShippingServiceLevel.getDisplayName(LOCALE, true)).thenReturn(DISPLAY_NAME);
		when(mockShippingServiceLevel.getCarrier()).thenReturn(CARRIER_CODE);

		final ShippingOption transformedShippingOption = shippingOptionTransformerImpl.transform(
				mockShippingServiceLevel, () -> SHIPPING_COST, LOCALE);

		final SoftAssertions softly = new SoftAssertions();
		softly.assertThat(transformedShippingOption.getCode()).isEqualTo(CODE);
		softly.assertThat(transformedShippingOption.getDisplayName(LOCALE)).isPresent().contains(DISPLAY_NAME);
		softly.assertThat(transformedShippingOption.getCarrierCode()).isPresent().contains(CARRIER_CODE);
		softly.assertThat(transformedShippingOption.getCarrierDisplayName()).isPresent().contains(CARRIER_CODE);
		softly.assertThat(transformedShippingOption.getShippingCost()).isPresent().contains(SHIPPING_COST);
		softly.assertAll();

	}

	@Test
	public void testTransformWithoutCarrierCode() {
		when(mockShippingServiceLevel.getCode()).thenReturn(CODE);
		when(mockShippingServiceLevel.getDisplayName(LOCALE, true)).thenReturn(DISPLAY_NAME);

		final ShippingOption transformedShippingOption = shippingOptionTransformerImpl.transform(mockShippingServiceLevel, () -> null, LOCALE);
		assertThat(transformedShippingOption.getCarrierCode()).isEmpty();
		assertThat(transformedShippingOption.getCarrierDisplayName()).isEmpty();
	}
}
