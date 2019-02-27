/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.impl;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.builder.ShippingCalculationResultBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.impl.ShippingCalculationResultBuilderImpl;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingCalculationResultImpl;
import com.elasticpath.shipping.connectivity.service.selector.PricedShippingCalculationPluginSelector;
import com.elasticpath.shipping.connectivity.service.selector.UnpricedShippingCalculationPluginSelector;
import com.elasticpath.shipping.connectivity.spi.ShippingCalculationPlugin;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingCostCalculationCapability;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingOptionListCapability;

/**
 * Tests {@link ShippingCalculationServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingCalculationServiceImplTest {

	private static final String STORE_CODE = "storeCode";
	private static final String DISPLAY_NAME = "testDisplayName";
	private static final String SHIPPING_OPTION_CODE = "testShippingOptionCode";

	@InjectMocks
	private ShippingCalculationServiceImpl shippingCalculationServiceImpl;

	@Mock
	private UnpricedShippingCalculationPluginSelector mockUnpricedShippingCalculationPluginSelector;
	@Mock
	private PricedShippingCalculationPluginSelector mockPricedShippingCalculationPluginSelector;
	@Mock
	private Supplier<ShippingCalculationResultBuilder> mockShippingCalculationResultBuilderSupplier;
	@Mock
	private List<ShippableItem> mockUnpricedShippableItems;
	@Mock
	private List<PricedShippableItem> mockPricedShippableItems;
	@Mock
	private ShippingAddress mockDestinationAddress;
	@Mock
	private ShippableItemContainer<ShippableItem> mockUnpricedShippableItemContainer;
	@Mock
	private PricedShippableItemContainer<PricedShippableItem> mockPricedShippableItemContainer;
	@Mock
	private ShippingCalculationPlugin mockShippingCalculationPlugin;
	@Mock
	private ShippingOptionListCapability mockShippingOptionListCapability;
	@Mock
	private ShippingCostCalculationCapability mockShippingCostCalculationCapability;
	@Mock
	private ShippingOption mockShippingOption;

	private List<ShippingOption> shippingOptions;

	@Before
	public void setUp() {

		// since multiple supplier interfaces, need to manual wire it into the test target implementation.
		shippingCalculationServiceImpl.setShippingCalculationResultBuilderSupplier(mockShippingCalculationResultBuilderSupplier);

		when(mockShippingCalculationResultBuilderSupplier.get()).thenAnswer((Answer<ShippingCalculationResultBuilderImpl>) invocationOnMock -> {
			final ShippingCalculationResultBuilderImpl instance = new ShippingCalculationResultBuilderImpl();
			instance.setInstanceSupplier(ShippingCalculationResultImpl::new);
			return instance;
		});

		when(mockShippingCalculationPlugin.hasCapability(ShippingOptionListCapability.class)).thenReturn(true);
		when(mockShippingCalculationPlugin.hasCapability(ShippingCostCalculationCapability.class)).thenReturn(true);
		when(mockShippingCalculationPlugin.getCapability(ShippingOptionListCapability.class)).thenReturn(mockShippingOptionListCapability);
		when(mockShippingCalculationPlugin.getCapability(ShippingCostCalculationCapability.class)).thenReturn(mockShippingCostCalculationCapability);

		doReturn(mockUnpricedShippableItems).when(mockUnpricedShippableItemContainer).getShippableItems();
		when(mockUnpricedShippableItemContainer.getDestinationAddress()).thenReturn(mockDestinationAddress);
		when(mockUnpricedShippableItemContainer.getStoreCode()).thenReturn(STORE_CODE);

		doReturn(mockPricedShippableItems).when(mockPricedShippableItemContainer).getShippableItems();
		when(mockPricedShippableItemContainer.getDestinationAddress()).thenReturn(mockDestinationAddress);
		when(mockPricedShippableItemContainer.getStoreCode()).thenReturn(STORE_CODE);

		when(mockUnpricedShippingCalculationPluginSelector.getUnpricedShippingCalculationPlugin(
				mockUnpricedShippableItems, mockDestinationAddress, STORE_CODE)).thenReturn(mockShippingCalculationPlugin);
		when(mockShippingOptionListCapability.getUnpricedShippingOptions(mockUnpricedShippableItemContainer))
				.thenReturn(Collections.singletonList(mockShippingOption));

		when(mockShippingOption.getCode()).thenReturn(SHIPPING_OPTION_CODE);
		when(mockShippingOption.getDisplayName(any(Locale.class))).thenReturn(Optional.of(DISPLAY_NAME));
		shippingOptions = Collections.singletonList(mockShippingOption);
	}

	@Test
	public void testGetUnpricedShippingOptions() {

		final ShippingCalculationResult result = shippingCalculationServiceImpl.getUnpricedShippingOptions(mockUnpricedShippableItemContainer);

		verify(mockUnpricedShippingCalculationPluginSelector).getUnpricedShippingCalculationPlugin(
				mockUnpricedShippableItems, mockDestinationAddress, STORE_CODE);
		verify(mockShippingOptionListCapability).getUnpricedShippingOptions(mockUnpricedShippableItemContainer);

		assertThat(result.getAvailableShippingOptions()).isEqualTo(shippingOptions);
	}

	@Test
	public void testGetUnpricedShippingOptionsIfMissingDestinationAddress() {

		when(mockUnpricedShippableItemContainer.getDestinationAddress()).thenReturn(null);

		final ShippingCalculationResult result = shippingCalculationServiceImpl.getUnpricedShippingOptions(mockUnpricedShippableItemContainer);

		assertThat(result.getAvailableShippingOptions()).isEmpty();
	}

	@Test
	public void testGetPricedShippingOptions() {
		when(mockPricedShippingCalculationPluginSelector.getPricedShippingCalculationPlugin(
				mockPricedShippableItems, mockDestinationAddress, STORE_CODE)).thenReturn(mockShippingCalculationPlugin);
		when(mockShippingCostCalculationCapability.getPricedShippingOptions(mockPricedShippableItemContainer))
				.thenReturn(Collections.singletonList(mockShippingOption));

		final ShippingCalculationResult result = shippingCalculationServiceImpl.getPricedShippingOptions(mockPricedShippableItemContainer);

		verify(mockPricedShippingCalculationPluginSelector).getPricedShippingCalculationPlugin(
				mockPricedShippableItems, mockDestinationAddress, STORE_CODE);
		verify(mockShippingCostCalculationCapability).getPricedShippingOptions(mockPricedShippableItemContainer);

		assertThat(result.getAvailableShippingOptions()).isEqualTo(shippingOptions);
	}

	@Test
	public void testGetUnpricedShippingOptionsIfProviderMissing() {
		when(mockUnpricedShippingCalculationPluginSelector.getUnpricedShippingCalculationPlugin(
				mockUnpricedShippableItems, mockDestinationAddress, STORE_CODE)).thenReturn(null);

		final ShippingCalculationResult result = shippingCalculationServiceImpl.getUnpricedShippingOptions(mockUnpricedShippableItemContainer);

		verify(mockUnpricedShippingCalculationPluginSelector).getUnpricedShippingCalculationPlugin(mockUnpricedShippableItems,
				mockDestinationAddress,
				STORE_CODE);
		verify(mockShippingOptionListCapability, never()).getUnpricedShippingOptions(mockUnpricedShippableItemContainer);

		assertThat(result.isSuccessful()).isFalse();
		assertThat(isEmpty(result.getAvailableShippingOptions())).isTrue();
	}

	@Test
	public void testGetPricedShippingOptionsIfProviderMissing() {
		when(mockPricedShippingCalculationPluginSelector.getPricedShippingCalculationPlugin(
				mockPricedShippableItems, mockDestinationAddress, STORE_CODE)).thenReturn(null);

		final ShippingCalculationResult pricedShippingOptions = shippingCalculationServiceImpl
				.getPricedShippingOptions(mockPricedShippableItemContainer);

		verify(mockPricedShippingCalculationPluginSelector).getPricedShippingCalculationPlugin(
				mockPricedShippableItems, mockDestinationAddress, STORE_CODE);
		verify(mockShippingCostCalculationCapability, never()).getPricedShippingOptions(mockPricedShippableItemContainer);

		assertThat(pricedShippingOptions.isSuccessful()).isFalse();
		assertThat(isEmpty(pricedShippingOptions.getAvailableShippingOptions())).isTrue();
	}
}
