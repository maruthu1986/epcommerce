/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.money.Money;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Tests {@link ShippingOptionImpl}.
 */
public class ShippingOptionImplTest {

	private static final String CARRIER_CODE = "testCarrierCode";
	private static final String CARRIER_DISPLAY_NAME = "testCarrierDisplayName";
	private static final String CODE = "testCode";
	private static final String DESCRIPTION = "testDescription";
	private static final String OBJECT = "testObject";
	private static final String KEY = "testKey";
	private static final ImmutableMap<String, Object> FIELDS = ImmutableMap.of(KEY, OBJECT);
	private static final Locale LOCALE = Locale.CANADA;
	private static final Locale SECOND_LOCALE = Locale.US;
	private static final Money COST = Money.valueOf(BigDecimal.TEN, Currency.getInstance(LOCALE));
	private static final String DISPLAY_NAME = "testDisplayName";
	private static final ImmutableMap<Locale, String> DISPLAY_NAMES = ImmutableMap.of(LOCALE, DISPLAY_NAME);
	private static final String SECOND_DISPLAY_NAME = "testOtherDisplayName";
	private static final String OTHER_CARRIER_CODE = "otherCarrierCode";
	private static final String OTHER_CARRIER_DISPLAY_NAME = "otherCarrierDisplayName";
	private static final String OTHER_CODE = "otherCode";
	private static final String OTHER_DESCRIPTION = "otherDescription";
	private static final String OTHER_KEY = "otherKey";
	private static final String OTHER_OBJECT = "otherObject";
	private static final ImmutableMap<String, Object> OTHER_FIELDS = ImmutableMap.of(OTHER_KEY, OTHER_OBJECT);
	private static final String OTHER_DISPLAY_NAME = "otherDisplayName";
	private static final ImmutableMap<Locale, String> OTHER_DISPLAY_NAMES = ImmutableMap.of(Locale.ENGLISH, OTHER_DISPLAY_NAME);
	private static final String OTHER_SECOND_DISPLAY_NAME = "otherSecondDisplayName";
	private static final Money OTHER_COST = Money.valueOf(BigDecimal.ONE, Currency.getInstance(Locale.US));
	private static final Locale OTHER_SECOND_LOCALE = Locale.FRANCE;
	private static final String KEY_2 = "testKey2";
	private static final String OBJECT_2 = "testObject2";
	private static final Locale LOCALE_2 = Locale.CHINA;
	private static final String DISPLAY_NAME_2 = "testDisplayName2";
	private static final String EST_EARLIEST_DELIVERY_DATE_STRING = "2000-01-01";
	private static final String EST_LATEST_DELIVERY_DATE_STRING = "2001-01-01";
	private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final LocalDate EST_EARLIEST_DELIVERY_DATE = LocalDate.parse(EST_EARLIEST_DELIVERY_DATE_STRING, LOCAL_DATE_FORMATTER);
	private static final LocalDate OTHER_EST_EARLIEST_DELIVERY_DATE = LocalDate.parse("2000-02-01", LOCAL_DATE_FORMATTER);
	private static final LocalDate EST_LATEST_DELIVERY_DATE = LocalDate.parse(EST_LATEST_DELIVERY_DATE_STRING, LOCAL_DATE_FORMATTER);
	private static final LocalDate OTHER_EST_LATEST_DELIVERY_DATE = LocalDate.parse("2001-02-01", LOCAL_DATE_FORMATTER);

	private ShippingOptionImpl shippingOption;

	@Before
	public void setUp() {

		shippingOption = buildEmpty();

	}

	@Test
	public void testShippingCost() {

		shippingOption.setShippingCost(COST);
		assertThat(shippingOption.getShippingCost().orElse(null)).isEqualTo(COST);

	}

	@Test
	public void testDescription() {

		shippingOption.setDescription(DESCRIPTION);
		assertThat(shippingOption.getDescription().orElse(null)).isEqualTo(DESCRIPTION);

	}

	@Test
	public void testCode() {

		shippingOption.setCode(CODE);
		assertThat(shippingOption.getCode()).isEqualTo(CODE);

	}

	@Test
	public void testCarrierCode() {

		shippingOption.setCarrierCode(CARRIER_CODE);
		assertThat(shippingOption.getCarrierCode().orElse(null)).isEqualTo(CARRIER_CODE);

	}

	@Test
	public void testCarrierDisplayName() {

		shippingOption.setCarrierDisplayName(CARRIER_DISPLAY_NAME);
		assertThat(shippingOption.getCarrierDisplayName().orElse(null)).isEqualTo(CARRIER_DISPLAY_NAME);

	}


	@Test
	public void testDisplayName() {

		final Map<Locale, String> displayNames = ImmutableMap.of(LOCALE, DISPLAY_NAME);
		shippingOption.setDisplayNames(displayNames);
		assertThat(shippingOption.getDisplayNames()).isEqualTo(displayNames);

	}

	@Test
	public void testFields() {

		final Map<String, Object> fields = ImmutableMap.of(KEY, OBJECT);
		shippingOption.setFields(fields);
		assertThat(shippingOption.getFields()).isEqualTo(fields);

	}

	@Test
	public void testEquals() {
		new EqualsTester()
				.addEqualityGroup(shippingOption, buildEmpty())
				.addEqualityGroup(buildDefault())
				.addEqualityGroup(build(CARRIER_CODE, null, null, null, null, null, null, null, null, null, null))
				.addEqualityGroup(build(null, CARRIER_DISPLAY_NAME, null, null, null, null, null, null, null, null, null))
				.addEqualityGroup(build(null, null, CODE, null, null, null, null, null, null, null, null))
				.addEqualityGroup(build(null, null, null, DESCRIPTION, null, null, null, null, null, null, null))
				.addEqualityGroup(build(null, null, null, null, ImmutableMap.of(KEY, OBJECT), null, null, null, null, null, null))
				.addEqualityGroup(build(null, null, null, null, null, ImmutableMap.of(LOCALE, DISPLAY_NAME), null, null, null, null, null))
				.addEqualityGroup(build(null, null, null, null, null, null, SECOND_LOCALE, null, null, null, null))
				.addEqualityGroup(build(null, null, null, null, null, null, null, SECOND_DISPLAY_NAME, null, null, null))
				.addEqualityGroup(build(null, null, null, null, null, null, null, null, COST, null, null))
				.addEqualityGroup(build(null, null, null, null, null, null, null, null, null, EST_EARLIEST_DELIVERY_DATE, null))
				.addEqualityGroup(build(null, null, null, null, null, null, null, null, null, null, EST_LATEST_DELIVERY_DATE))
				.addEqualityGroup(build(OTHER_CARRIER_CODE, CARRIER_DISPLAY_NAME, CODE, DESCRIPTION, FIELDS, DISPLAY_NAMES, SECOND_LOCALE,
						SECOND_DISPLAY_NAME, COST,
						EST_EARLIEST_DELIVERY_DATE,
						EST_LATEST_DELIVERY_DATE))
				.addEqualityGroup(build(CARRIER_CODE, OTHER_CARRIER_DISPLAY_NAME, CODE, DESCRIPTION, FIELDS, DISPLAY_NAMES, SECOND_LOCALE,
						SECOND_DISPLAY_NAME, COST,
						EST_EARLIEST_DELIVERY_DATE,
						EST_LATEST_DELIVERY_DATE))
				.addEqualityGroup(build(CARRIER_CODE, CARRIER_DISPLAY_NAME, OTHER_CODE, DESCRIPTION, FIELDS, DISPLAY_NAMES, SECOND_LOCALE,
						SECOND_DISPLAY_NAME, COST,
						EST_EARLIEST_DELIVERY_DATE,
						EST_LATEST_DELIVERY_DATE))
				.addEqualityGroup(build(CARRIER_CODE, CARRIER_DISPLAY_NAME, CODE, OTHER_DESCRIPTION, FIELDS, DISPLAY_NAMES, SECOND_LOCALE,
						SECOND_DISPLAY_NAME, COST,
						EST_EARLIEST_DELIVERY_DATE,
						EST_LATEST_DELIVERY_DATE))
				.addEqualityGroup(build(CARRIER_CODE, CARRIER_DISPLAY_NAME, CODE, DESCRIPTION, OTHER_FIELDS, DISPLAY_NAMES, SECOND_LOCALE,
						SECOND_DISPLAY_NAME, COST,
						EST_EARLIEST_DELIVERY_DATE,
						EST_LATEST_DELIVERY_DATE))
				.addEqualityGroup(build(CARRIER_CODE, CARRIER_DISPLAY_NAME, CODE, DESCRIPTION, FIELDS, OTHER_DISPLAY_NAMES, SECOND_LOCALE,
						SECOND_DISPLAY_NAME, COST,
						EST_EARLIEST_DELIVERY_DATE,
						EST_LATEST_DELIVERY_DATE))
				.addEqualityGroup(build(CARRIER_CODE, CARRIER_DISPLAY_NAME, CODE, DESCRIPTION, FIELDS, DISPLAY_NAMES, OTHER_SECOND_LOCALE,
						SECOND_DISPLAY_NAME, COST,
						EST_EARLIEST_DELIVERY_DATE,
						EST_LATEST_DELIVERY_DATE))
				.addEqualityGroup(build(CARRIER_CODE, CARRIER_DISPLAY_NAME, CODE, DESCRIPTION, FIELDS, DISPLAY_NAMES, SECOND_LOCALE,
						OTHER_SECOND_DISPLAY_NAME, COST,
						EST_EARLIEST_DELIVERY_DATE,
						EST_LATEST_DELIVERY_DATE))
				.addEqualityGroup(build(CARRIER_CODE, CARRIER_DISPLAY_NAME, CODE, DESCRIPTION, FIELDS, DISPLAY_NAMES, SECOND_LOCALE,
						SECOND_DISPLAY_NAME, OTHER_COST,
						EST_EARLIEST_DELIVERY_DATE,
						EST_LATEST_DELIVERY_DATE))
				.addEqualityGroup(build(CARRIER_CODE, CARRIER_DISPLAY_NAME, CODE, DESCRIPTION, FIELDS, DISPLAY_NAMES, SECOND_LOCALE,
						SECOND_DISPLAY_NAME, COST,
						OTHER_EST_EARLIEST_DELIVERY_DATE,
						EST_LATEST_DELIVERY_DATE))
				.addEqualityGroup(build(CARRIER_CODE, CARRIER_DISPLAY_NAME, CODE, DESCRIPTION, FIELDS, DISPLAY_NAMES, SECOND_LOCALE,
						SECOND_DISPLAY_NAME, COST,
						EST_EARLIEST_DELIVERY_DATE,
						OTHER_EST_LATEST_DELIVERY_DATE))
				.testEquals();
	}

	@Test
	public void testToString() {

		final ShippingOption shippingOption = buildEmpty();

		final String resultToString = shippingOption.toString();

		assertThat(resultToString).isNotEmpty();

	}

	@Test
	public void testToStringWithDefaultValue() {

		final ShippingOption shippingOption = buildDefault();

		final String resultToString = shippingOption.toString();

		assertThat(resultToString).isNotEmpty();
		assertThat(resultToString).contains(CARRIER_CODE, CARRIER_DISPLAY_NAME, CODE, DESCRIPTION, KEY, OBJECT, LOCALE.toString(), COST.toString(),
			DISPLAY_NAME, SECOND_DISPLAY_NAME, SECOND_LOCALE.toString(), EST_EARLIEST_DELIVERY_DATE_STRING, EST_LATEST_DELIVERY_DATE_STRING);

	}

	private ShippingOptionImpl buildEmpty() {
		return build(null, null, null, null, null, null, null, null, null, null, null);
	}

	private ShippingOptionImpl buildDefault() {
		return build(CARRIER_CODE,
				CARRIER_DISPLAY_NAME,
				CODE, DESCRIPTION,
				ImmutableMap.of(KEY, OBJECT, KEY_2, OBJECT_2),
				ImmutableMap.of(LOCALE, DISPLAY_NAME, LOCALE_2, DISPLAY_NAME_2),
				SECOND_LOCALE,
				SECOND_DISPLAY_NAME, COST,
				EST_EARLIEST_DELIVERY_DATE,
				EST_LATEST_DELIVERY_DATE);
	}

	@SuppressWarnings({"checkstyle:parameternumber", "PMD.ExcessiveParameterList"})
	private ShippingOptionImpl build(final String carrierCode,
									 final String carrierDisplayName,
									 final String code,
									 final String description,
									 final Map<String, Object> fields,
									 final Map<Locale, String> displayNames,
									 final Locale locale,
									 final String displayName,
									 final Money cost,
									 final LocalDate estEarliestDeliveryDate,
									 final LocalDate estLatestDeliveryDate) {

		final ShippingOptionImpl shippingOption = new ShippingOptionImpl();

		shippingOption.setCarrierCode(carrierCode);
		shippingOption.setCarrierDisplayName(carrierDisplayName);
		shippingOption.setCode(code);
		shippingOption.setDescription(description);
		shippingOption.setFields(fields);
		shippingOption.setDisplayNames(displayNames);
		shippingOption.setDisplayName(locale, displayName);
		shippingOption.setShippingCost(cost);
		shippingOption.setEstimatedEarliestDeliveryDate(estEarliestDeliveryDate);
		shippingOption.setEstimatedLatestDeliveryDate(estLatestDeliveryDate);
		return shippingOption;
	}

}
