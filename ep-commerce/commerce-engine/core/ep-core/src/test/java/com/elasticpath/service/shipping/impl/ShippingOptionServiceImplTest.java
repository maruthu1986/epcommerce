/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.builder.ShippingOptionResultBuilder;
import com.elasticpath.service.shipping.builder.impl.ShippingOptionResultBuilderImpl;
import com.elasticpath.service.shipping.transformers.ShippableItemContainerTransformer;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;

/**
 * Unit test for {@link ShippingOptionServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionServiceImplTest {

	private static final String SHOULD_NEVER_RETURN_NULL = "ShippingOptionService should never return a null ShippingOptionResult";

	@InjectMocks
	private ShippingOptionServiceImpl target;

	@Mock
	private ShippingCalculationService mockShippingCalculationService;
	@Mock
	private ShippableItemContainer<ShippableItem> mockShippableItemContainer;
	@Mock
	private ShippingCalculationResult mockUnpricedShippingCalculationResult;
	@Mock
	private ShippingOption mockShippingOption;
	@Mock
	private ShippableItemContainerTransformer mockShippableItemContainerAdapter;

	@Mock
	private ShoppingCart mockShoppingCart;

	private List<ShippingOption> shippingOptions;

	@Before
	public void setUp() {
		shippingOptions = singletonList(mockShippingOption);

		target.setShippingOptionResultBuilderSupplier(this::createShippingOptionResultBuilder);

		when(mockShippableItemContainerAdapter.apply(any(ShoppingCart.class))).thenReturn(mockShippableItemContainer);
	}

	private ShippingOptionResultBuilder createShippingOptionResultBuilder() {
		final ShippingOptionResultBuilderImpl builder = new ShippingOptionResultBuilderImpl();
		builder.setInstanceSupplier(ShippingOptionResultImpl::new);
		return builder;
	}

	@Test
	public void testGetShippingOptionsForCart() {
		when(mockUnpricedShippingCalculationResult.getAvailableShippingOptions()).thenReturn(shippingOptions);
		when(mockUnpricedShippingCalculationResult.getErrorInformation()).thenReturn(empty());
		when(mockShippingCalculationService.getUnpricedShippingOptions(mockShippableItemContainer))
				.thenReturn(mockUnpricedShippingCalculationResult);

		final ShippingOptionResult shippingOptionResult = target.getShippingOptions(mockShoppingCart);

		verify(mockShippingCalculationService).getUnpricedShippingOptions(mockShippableItemContainer);

		assertThat(shippingOptionResult).as(SHOULD_NEVER_RETURN_NULL).isNotNull();
		assertThat(shippingOptions).isEqualTo(shippingOptionResult.getAvailableShippingOptions());
	}

	@Test
	public void testGetShippingOptions() {
		when(mockUnpricedShippingCalculationResult.getAvailableShippingOptions()).thenReturn(shippingOptions);
		when(mockUnpricedShippingCalculationResult.getErrorInformation()).thenReturn(empty());
		when(mockShippingCalculationService.getUnpricedShippingOptions(eq(mockShippableItemContainer)))
				.thenReturn(mockUnpricedShippingCalculationResult);

		final ShippingOptionResult shippingOptionResult = target.getShippingOptions(mockShoppingCart);

		verify(mockShippingCalculationService).getUnpricedShippingOptions(eq(mockShippableItemContainer));

		assertThat(shippingOptionResult).as(SHOULD_NEVER_RETURN_NULL).isNotNull();
		assertThat(shippingOptions).isEqualTo(shippingOptionResult.getAvailableShippingOptions());
	}


	@Test
	public void testGetDefaultShippingOption() {
		ShippingOption mockShippingOption1 = mock(ShippingOption.class);
		ShippingOption mockShippingOption2 = mock(ShippingOption.class);

		final Optional<ShippingOption> defaultShippingOption = target.getDefaultShippingOption(asList(mockShippingOption1, mockShippingOption2));

		assertThat(defaultShippingOption).isPresent().contains(mockShippingOption1);
	}

	@Test
	public void testGetDefaultShippingOptionNoOptionsAvailable() {
		final Optional<ShippingOption> defaultShippingOption = target.getDefaultShippingOption(Collections.emptyList());

		assertThat(defaultShippingOption).isNotPresent();
	}
}
