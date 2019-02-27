/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.BrandLocalizedPropertyValueImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/** Test cases for {@link ShippingServiceLevelImpl}. */
public class ShippingServiceLevelImplTest {

	private ShippingServiceLevelImpl shippingServiceLevel;

	private static final Locale DEFAULT_LOCALE = Locale.US;
	private static final String DISPLAYNAME_DEFAULT_LOCALE = "defaultDisplayName";
	private static final Locale OTHER_LOCALE = Locale.GERMANY;
	private static final String DISPLAYNAME_OTHER_LOCALE = "otherDisplayName";

	@Before
	public void setUp() throws Exception {
		this.shippingServiceLevel = new ShippingServiceLevelImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl.getShippingRegion()'.
	 */
	@Test
	public void testGetSetShippingRegion() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		shippingServiceLevel.setShippingRegion(shippingRegion);
		assertThat(shippingServiceLevel.getShippingRegion()).isEqualTo(shippingRegion);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl.getStore()'.
	 */
	@Test
	public void testGetSetStore() {
		final Store store = new StoreImpl();
		store.setCode("some code");
		shippingServiceLevel.setStore(store);
		assertThat(shippingServiceLevel.getStore()).isEqualTo(store);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl.getShippingCostCalculationMethod()'.
	 */
	@Test
	public void testGetSetShippingCostCalculationMethod() {
		final ShippingCostCalculationMethod shippingCostCalculationMethod = new FixedBaseAndOrderTotalPercentageMethodImpl();
		shippingServiceLevel.setShippingCostCalculationMethod(shippingCostCalculationMethod);
		assertThat(shippingServiceLevel.getShippingCostCalculationMethod()).isEqualTo(shippingCostCalculationMethod);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl.getCarrier()'.
	 */
	@Test
	public void testGetSetCarrier() {
		final String testCarrier = "Fed Ex";
		shippingServiceLevel.setCarrier(testCarrier);
		assertThat(shippingServiceLevel.getCarrier()).isEqualTo(testCarrier);
	}

	private LocalizedProperties createLocalizedPropertiesWithDisplayNameInDefaultLocale() {
		//give it a reference to the Utility class so that it can broaden the locale
		LocalizedProperties localizedProperties = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 7789997879611928448L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}

			@Override
			public Utility getUtility() {
				return new UtilityImpl();
			}
		};
		localizedProperties.setValue(ShippingServiceLevel.LOCALIZED_PROPERTY_NAME, DEFAULT_LOCALE, DISPLAYNAME_DEFAULT_LOCALE);
		return localizedProperties;
	}

	private LocalizedProperties createLocalizedPropertiesWithDisplayNameInDefaultLocaleAndOtherLocale() {
		LocalizedProperties localizedProperties = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 406419152977594590L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		};
		localizedProperties.setValue(ShippingServiceLevel.LOCALIZED_PROPERTY_NAME, DEFAULT_LOCALE, DISPLAYNAME_DEFAULT_LOCALE);
		localizedProperties.setValue(ShippingServiceLevel.LOCALIZED_PROPERTY_NAME, OTHER_LOCALE, DISPLAYNAME_OTHER_LOCALE);
		return localizedProperties;
	}

	private ShippingServiceLevelImpl createShippingServiceLevelImplWithDisplayNameDefaultLocaleOnly() {
		return createShippingServiceLevelImplForTesting(createLocalizedPropertiesWithDisplayNameInDefaultLocale());
	}

	private ShippingServiceLevelImpl createShippingServiceLevelImplWithDisplayNameInBothLocales() {
		return createShippingServiceLevelImplForTesting(createLocalizedPropertiesWithDisplayNameInDefaultLocaleAndOtherLocale());
	}

	private ShippingServiceLevelImpl createShippingServiceLevelImplForTesting(final LocalizedProperties localizedProperties) {
		return new ShippingServiceLevelImpl() {
			private static final long serialVersionUID = 6032980036419946630L;

			@Override
			public LocalizedProperties getLocalizedProperties() {
				return localizedProperties;
			}

			@Override
			public Locale getStoreDefaultLocale() {
				return DEFAULT_LOCALE;
			}
		};
	}

	/**
	 * Test that getDisplayName falls back if necessary, but not if it has been forbidden.
	 */
	@Test
	public void testGetDisplayNameFallsBackIfNecessaryButNotIfForbidden() {
		ShippingServiceLevelImpl shippingServiceLevel = createShippingServiceLevelImplWithDisplayNameDefaultLocaleOnly();
		assertThat(shippingServiceLevel.getDisplayName(OTHER_LOCALE, true))
			.as("Should fall back if necessary")
			.isEqualTo(DISPLAYNAME_DEFAULT_LOCALE);
		assertThat(shippingServiceLevel.getDisplayName(OTHER_LOCALE, false))
			.as("Should not fall back if it's forbidden")
			.isNull();
	}

	/**
	 * Test that getDisplayName doesn't fall back if a match is found
	 * for the requested locale.
	 */
	@Test
	public void testGetDisplayNameDoesNotFallBackIfNotNecessary() {
		ShippingServiceLevelImpl shippingServiceLevel = createShippingServiceLevelImplWithDisplayNameInBothLocales();
		assertThat(shippingServiceLevel.getDisplayName(OTHER_LOCALE, true))
			.as("Should not fall back if not necessary")
			.isEqualTo(DISPLAYNAME_OTHER_LOCALE);
	}

	/**
	 */
	@Test
	public void testIsApplicableReturnsFalseWhenInactive() {
		ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setEnabled(false);

		String anyStoreCode = "doesn't matter store code";
		ShippingAddress anyAddress = mock(ShippingAddress.class, "doesn't matter address");
		assertThat(shippingServiceLevel.isApplicable(anyStoreCode, anyAddress)).isFalse();
	}

	/**
	 */
	@Test
	public void testIsApplicableReturnsFalseWhenStoreCodeMismatch() {
		ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setEnabled(true);
		final Store store = createStore("test store code");
		shippingServiceLevel.setStore(store);

		ShippingAddress anyAddress = mock(ShippingAddress.class, "doesn't matter address");
		assertThat(shippingServiceLevel.isApplicable("non-matching store code", anyAddress)).isFalse();

		verify(store).getCode();
	}

	/**
	 */
	@Test
	public void testIsApplicableReturnsFalseWhenAddressNotInRegion() {
		ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setEnabled(true);

		final String storeCode = "test store code";
		final Store store = createStore(storeCode);
		shippingServiceLevel.setStore(store);

		final ShippingRegion shippingRegion = createShippingRegion(false);
		shippingServiceLevel.setShippingRegion(shippingRegion);

		ShippingAddress address = createAddress("US", "WA");
		assertThat(shippingServiceLevel.isApplicable(storeCode, address)).isFalse();

		verify(store).getCode();
		verify(shippingRegion).isInShippingRegion(any(ShippingAddress.class));
	}

	/**
	 */
	@Test
	public void testIsApplicableReturnsTrueWhenEnabledMatchingStoreAndInRegion() {
		ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setEnabled(true);

		final String storeCode = "test store code";
		final Store store = createStore(storeCode);
		shippingServiceLevel.setStore(store);

		final ShippingRegion shippingRegion = createShippingRegion(true);
		shippingServiceLevel.setShippingRegion(shippingRegion);

		ShippingAddress address = createAddress("US", "WA");
		assertThat(shippingServiceLevel.isApplicable(storeCode, address)).isTrue();

		verify(store).getCode();
		verify(shippingRegion).isInShippingRegion(any(ShippingAddress.class));

	}

	private Store createStore(final String storeCode) {
		final Store store = mock(Store.class);
		when(store.getCode()).thenReturn(storeCode);
		return store;
	}

	private ShippingAddress createAddress(final String country, final String subCountry) {
		final ShippingAddress address = mock(ShippingAddress.class);
		when(address.getCountry()).thenReturn(country);
		when(address.getSubCountry()).thenReturn(subCountry);
		return address;
	}

	private ShippingRegion createShippingRegion(final boolean inShippingRegion) {
		final ShippingRegion shippingRegion = mock(ShippingRegion.class);

		when(shippingRegion.isInShippingRegion(any(ShippingAddress.class))).thenReturn(inShippingRegion);
		return shippingRegion;
	}

}
