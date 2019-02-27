/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.impl;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.when;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * Test cases for {@link ShippableItemContainerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippableItemContainerImplTest {

	private static final String STORE_CODE = "testStoreCode";
	private static final Currency CURRENCY = Currency.getInstance(Locale.CANADA);
	private static final Locale LOCALE = Locale.CANADA;
	private static final String SHIPPABLE_ITEM_STRING = "testShippableItemString";
	private static final String ORIGIN_SHIPPING_ADDRESS_STRING = "testOriginShippingAddressString";
	private static final String DEST_SHIPPING_ADDRESS_STRING = "testDestShippingAddressString";
	private static final String KEY_1 = "testKey1";
	private static final String VALUE_1 = "testValue1";

	private ShippableItemContainerImpl<ShippableItem> shippableItemContainer;

	@Mock
	private ShippingAddress mockOriginShippingAddress;

	@Mock
	private ShippingAddress mockDestShippingAddress;

	@Mock
	private ShippableItem mockShippableItem;

	private List<ShippableItem> shippableItems;

	@Before
	public void setUp() {

		shippableItemContainer = buildEmpty();
		shippableItems = singletonList(mockShippableItem);

		when(mockShippableItem.toString()).thenReturn(SHIPPABLE_ITEM_STRING);
		when(mockOriginShippingAddress.toString()).thenReturn(ORIGIN_SHIPPING_ADDRESS_STRING);
		when(mockDestShippingAddress.toString()).thenReturn(DEST_SHIPPING_ADDRESS_STRING);

	}

	@Test
	public void testStoreCode() {

		shippableItemContainer.setStoreCode(STORE_CODE);
		assertThat(shippableItemContainer.getStoreCode()).isEqualTo(STORE_CODE);

	}

	@Test
	public void testCurrency() {

		shippableItemContainer.setCurrency(CURRENCY);
		assertThat(shippableItemContainer.getCurrency()).isEqualTo(CURRENCY);

	}

	@Test
	public void testLocale() {

		shippableItemContainer.setLocale(LOCALE);
		assertThat(shippableItemContainer.getLocale()).isEqualTo(LOCALE);

	}

	@Test
	public void testOriginAddress() {

		shippableItemContainer.setOriginAddress(mockOriginShippingAddress);
		assertThat(shippableItemContainer.getOriginAddress()).isEqualTo(mockOriginShippingAddress);

	}

	@Test
	public void testDestinationAddress() {

		shippableItemContainer.setDestinationAddress(mockOriginShippingAddress);
		assertThat(shippableItemContainer.getDestinationAddress()).isEqualTo(mockOriginShippingAddress);

	}

	@Test
	public void testShippableItems() {

		shippableItemContainer.setShippableItems(shippableItems);
		assertThat(shippableItemContainer.getShippableItems()).isEqualTo(shippableItems);

	}

	@Test
	public void testFields() {
		shippableItemContainer.setFields(ImmutableMap.of(KEY_1, VALUE_1));
		assertThat(shippableItemContainer.getFields()).containsExactly(entry(KEY_1, VALUE_1));
	}

	@Test
	public void testEmptyFields() {
		shippableItemContainer.setFields(null);
		assertThat(shippableItemContainer.getFields()).isEmpty();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testUnmodifiableFields() {
		shippableItemContainer.getFields().put(KEY_1, VALUE_1);
	}

	@Test
	public void testField() {
		shippableItemContainer.setField(KEY_1, VALUE_1);
		assertThat(shippableItemContainer.getFields()).containsExactly(entry(KEY_1, VALUE_1));
	}

	@Test
	public void testToString() {

		final ShippableItemContainer<ShippableItem> shippableItemContainer = build(STORE_CODE, LOCALE, CURRENCY,
				shippableItems, mockOriginShippingAddress, mockDestShippingAddress);

		final String resultToString = shippableItemContainer.toString();

		assertThat(resultToString).contains(STORE_CODE, LOCALE.getCountry(), CURRENCY.toString(), ORIGIN_SHIPPING_ADDRESS_STRING,
			DEST_SHIPPING_ADDRESS_STRING, SHIPPABLE_ITEM_STRING);

	}

	@Test
	public void testEquals() {
		new EqualsTester()
				.addEqualityGroup(buildEmpty(), shippableItemContainer)
				.addEqualityGroup(buildDefault())
				.addEqualityGroup(build(STORE_CODE, null, null, null, null, null))
				.addEqualityGroup(build(null, LOCALE, null, null, null, null))
				.addEqualityGroup(build(null, null, CURRENCY, null, null, null))
				.addEqualityGroup(build(null, null, null, shippableItems, null, null))
				.addEqualityGroup(build(null, null, null, null, mockOriginShippingAddress, null))
				.addEqualityGroup(build(null, null, null, null, null, mockDestShippingAddress))
				.testEquals();
	}

	private ShippableItemContainerImpl<ShippableItem> buildEmpty() {

		return build(null, null, null, null, null, null);

	}

	private ShippableItemContainerImpl<ShippableItem> buildDefault() {

		return build(STORE_CODE, LOCALE, CURRENCY, shippableItems, mockOriginShippingAddress, mockDestShippingAddress);

	}

	private ShippableItemContainerImpl<ShippableItem> build(final String storeCode, final Locale locale,
															final Currency currency, final List<ShippableItem> shippableItems,
															final ShippingAddress originAddress, final ShippingAddress destAddress) {

		final ShippableItemContainerImpl<ShippableItem> shippableItemContainer = new ShippableItemContainerImpl<>();

		shippableItemContainer.setStoreCode(storeCode);
		shippableItemContainer.setLocale(locale);
		shippableItemContainer.setCurrency(currency);
		shippableItemContainer.setShippableItems(shippableItems);
		shippableItemContainer.setOriginAddress(originAddress);
		shippableItemContainer.setDestinationAddress(destAddress);

		return shippableItemContainer;

	}

}
