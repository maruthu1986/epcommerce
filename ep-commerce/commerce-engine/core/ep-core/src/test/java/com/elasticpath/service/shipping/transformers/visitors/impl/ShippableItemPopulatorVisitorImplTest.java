/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.impl;

import static java.lang.String.format;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemBuilderPopulator;

/**
 * Unit test for {@link ShippableItemPopulatorVisitorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippableItemPopulatorVisitorImplTest {
	private static final String SHOPPING_ITEM_GUID = "SHOPPING_ITEM_GUID";
	private static final String SKU_GUID = "SKU_GUID";

	private static final int QUANTITY = 5;

	private static final BigDecimal WEIGHT = BigDecimal.ONE;
	private static final BigDecimal HEIGHT = BigDecimal.TEN;
	private static final BigDecimal WIDTH = BigDecimal.valueOf(100);
	private static final BigDecimal LENGTH = BigDecimal.valueOf(1000);

	@Mock
	private ShoppingItem shoppingItem;

	@Mock
	private ShippableItemBuilderPopulator populator;

	@Mock
	private ProductSku productSku;

	@Mock
	private ProductSkuLookup productSkuLookup;

	@InjectMocks
	private ShippableItemPopulatorVisitorImpl objectUnderTest;

	@Before
	public void setUp() {
		when(shoppingItem.getGuid()).thenReturn(SHOPPING_ITEM_GUID);
		when(shoppingItem.getSkuGuid()).thenReturn(SKU_GUID);
		when(shoppingItem.getQuantity()).thenReturn(QUANTITY);

		when(productSku.getWeight()).thenReturn(WEIGHT);
		when(productSku.getHeight()).thenReturn(HEIGHT);
		when(productSku.getWidth()).thenReturn(WIDTH);
		when(productSku.getLength()).thenReturn(LENGTH);

		when(productSkuLookup.findByGuid(SKU_GUID)).thenReturn(productSku);
	}

	@Test
	public void verifyVisitorPopulatesAllFields() {
		when(populator.withSkuGuid(SKU_GUID)).thenReturn(populator);
		when(populator.withQuantity(QUANTITY)).thenReturn(populator);
		when(populator.withWeight(WEIGHT)).thenReturn(populator);
		when(populator.withHeight(HEIGHT)).thenReturn(populator);
		when(populator.withWidth(WIDTH)).thenReturn(populator);
		when(populator.withLength(LENGTH)).thenReturn(populator);

		objectUnderTest.accept(shoppingItem, populator);

		verify(populator).withSkuGuid(SKU_GUID);
		verify(populator).withQuantity(QUANTITY);
		verify(populator).withWeight(WEIGHT);
		verify(populator).withHeight(HEIGHT);
		verify(populator).withWidth(WIDTH);
		verify(populator).withLength(LENGTH);
	}

	@Test
	public void verifyIllegalArgumentExceptionThrownWhenNoProductSkuFound() {
		when(productSkuLookup.findByGuid(SKU_GUID)).thenReturn(null);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> objectUnderTest.accept(shoppingItem, populator))
				.withMessage(format("Cannot find corresponding ProductSku with GUID '%s' for ShoppingItem with GUID '%s'",
									SKU_GUID, SHOPPING_ITEM_GUID));
	}
}
